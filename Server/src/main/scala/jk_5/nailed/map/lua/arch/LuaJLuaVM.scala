package jk_5.nailed.map.lua.arch

import jk_5.nailed.{NailedLog, api}
import jk_5.nailed.api.lua.{LimitReachedException, ExecutionResult, Architecture}
import org.luaj.vm3._
import jk_5.nailed.map.lua.ScalaClosure._
import scala.collection.convert.WrapAsScala._
import jk_5.nailed.map.lua.ScalaClosure
import scala.Some
import org.luaj.vm3.lib.jse.JsePlatform
import jk_5.nailed.util.GameTimeFormatter
import java.io.{IOException, FileNotFoundException}

/**
 * No description given
 *
 * @author jk-5
 */
class LuaJLuaVM(val machine: api.lua.Machine) extends Architecture {

  private var lua: Globals = _
  private var thread: LuaThread = _
  private var synchronizedCall: LuaFunction = _
  private var synchronizedResult: LuaValue = _
  private var doneWithInitRun = false

  val name = "LuaJ"

  private def components = machine.components //TODO

  override def isInitialized = doneWithInitRun

  override def runSynchronized() {
    synchronizedResult = synchronizedCall.call()
    synchronizedCall = null
  }

  override def runThreaded(isSynchronizedReturn: Boolean) = {
    try{
      // Resume the Lua state and remember the number of results we get.
      val results = if(isSynchronizedReturn){
        // If we were doing a synchronized call, continue where we left off.
        val result = thread.resume(synchronizedResult)
        synchronizedResult = null
        result
      }else{
        if(!doneWithInitRun) {
          // We're doing the initialization run.
          val result = thread.resume(LuaValue.NONE)
          // Mark as done *after* we ran, to avoid switching to synchronized
          // calls when we actually need direct ones in the init phase.
          doneWithInitRun = true
          // We expect to get nothing here, if we do we had an error.
          if (result.narg == 1) {
            // Fake zero sleep to avoid stopping if there are no signals.
            LuaValue.varargsOf(LuaValue.TRUE, LuaValue.valueOf(0))
          } else {
            LuaValue.NONE
          }
        }else machine.popSignal() match {
          case signal if signal != null =>
            thread.resume(LuaValue.varargsOf(Array(LuaValue.valueOf(signal.name)) ++ signal.args.map(ScalaClosure.toLuaValue)))
          case _ =>
            thread.resume(LuaValue.NONE)
        }
      }

      // Check if the kernel is still alive.
      if (thread.state.status == LuaThread.STATUS_SUSPENDED) {
        // If we get one function it must be a wrapper for a synchronized
        // call. The protocol is that a closure is pushed that is then called
        // from the main server thread, and returns a table, which is in turn
        // passed to the originating coroutine.yield().
        if (results.narg == 2 && results.isfunction(2)) {
          synchronizedCall = results.checkfunction(2)
          new ExecutionResult.SynchronizedCall()
        }
        // Check if we are shutting down, and if so if we're rebooting. This
        // is signalled by boolean values, where `false` means shut down,
        // `true` means reboot (i.e shutdown then start again).
        else if (results.narg == 2 && results.`type`(2) == LuaValue.TBOOLEAN) {
          new ExecutionResult.Shutdown(results.toboolean(2))
        }
        else {
          // If we have a single number, that's how long we may wait before
          // resuming the state again. Note that the sleep may be interrupted
          // early if a signal arrives in the meantime. If we have something
          // else we just process the next signal or wait for one.
          val ticks = if (results.narg == 2 && results.isnumber(2)) (results.todouble(2) * 20).toInt else Int.MaxValue
          new ExecutionResult.Sleep(ticks)
        }
      }
      // The kernel thread returned. If it threw we'd be in the catch below.
      else {
        // We're expecting the result of a pcall, if anything, so boolean + (result | string).
        if (results.`type`(2) != LuaValue.TBOOLEAN || !(results.isstring(3) || results.isnil(3))) {
          NailedLog.warn("Kernel returned unexpected results")
        }
        // The pcall *should* never return normally... but check for it nonetheless.
        if (results.toboolean(1)) {
          NailedLog.warn("Kernel stopped unexpectedly")
          new ExecutionResult.Shutdown(false)
        }else{
          val error = results.tojstring(3)
          if (error != null) new ExecutionResult.Error(error)
          else new ExecutionResult.Error("unknown error")
        }
      }
    }catch {
      case e: LuaError =>
        NailedLog.warn("Kernel crashed. This is a bug!", e)
        new ExecutionResult.Error("kernel panic: this is a bug, check your log file and report it")
      case e: Throwable =>
        NailedLog.warn("Unexpected error in kernel. This is a bug!", e)
        new ExecutionResult.Error("kernel panic: this is a bug, check your log file and report it")
    }
  }

  // ----------------------------------------------------------------------- //

  override def initialize() = {
    lua = JsePlatform.debugGlobals()
    lua.set("package", LuaValue.NIL)
    lua.set("io", LuaValue.NIL)
    lua.set("luajava", LuaValue.NIL)

    // Prepare table for os stuff.
    val os = LuaValue.tableOf()
    lua.set("os", os)

    // Remove some other functions we don't need and are dangerous.
    lua.set("dofile", LuaValue.NIL)
    lua.set("loadfile", LuaValue.NIL)

    // Provide some better Unicode support.
    val unicode = LuaValue.tableOf()

    unicode.set("lower", (args: Varargs) => LuaValue.valueOf(args.checkjstring(1).toLowerCase))
    unicode.set("upper", (args: Varargs) => LuaValue.valueOf(args.checkjstring(1).toUpperCase))
    unicode.set("char", (args: Varargs) => LuaValue.valueOf(String.valueOf((1 to args.narg).map(args.checkint).map(_.toChar).toArray)))
    unicode.set("len", (args: Varargs) => LuaValue.valueOf(args.checkjstring(1).length))
    unicode.set("reverse", (args: Varargs) => LuaValue.valueOf(args.checkjstring(1).reverse))

    unicode.set("sub", (args: Varargs) => {
      val string = args.checkjstring(1)
      val start = math.max(0, args.checkint(2) match {
        case i if i < 0 => string.length + i
        case i => i - 1
      })
      val end =
        if (args.narg > 2) math.min(string.length, args.checkint(3) match {
          case i if i < 0 => string.length + i + 1
          case i => i
        })
        else string.length
      if (end <= start) LuaValue.valueOf("")
      else LuaValue.valueOf(string.substring(start, end))
    })

    lua.set("unicode", unicode)

    os.set("clock", (_: Varargs) => LuaValue.valueOf(machine.cpuTime()))

    // Date formatting function.
    os.set("date", (args: Varargs) => {
      val format = if (args.narg > 0 && args.isstring(1)) args.tojstring(1) else "%d/%m/%y %H:%M:%S"
      val time = if (args.narg > 1 && args.isnumber(2)) args.todouble(2) * 1000 / 60 / 60 else machine.map.getWorld.getTotalWorldTime + 5000

      val dt = GameTimeFormatter.parse(time)
      def fmt(format: String) = {
        if (format == "*t") {
          val table = LuaValue.tableOf(0, 8)
          table.set("year", LuaValue.valueOf(dt.year))
          table.set("month", LuaValue.valueOf(dt.month))
          table.set("day", LuaValue.valueOf(dt.day))
          table.set("hour", LuaValue.valueOf(dt.hour))
          table.set("min", LuaValue.valueOf(dt.minute))
          table.set("sec", LuaValue.valueOf(dt.second))
          table.set("wday", LuaValue.valueOf(dt.weekDay))
          table.set("yday", LuaValue.valueOf(dt.yearDay))
          table
        }else{
          LuaValue.valueOf(GameTimeFormatter.format(format, dt))
        }
      }

      // Just ignore the allowed leading '!', Minecraft has no time zones...
      if(format.startsWith("!"))
        fmt(format.substring(1))
      else
        fmt(format)
    })

    // Return ingame time for os.time().
    os.set("time", (args: Varargs) => {
      if (args.isnoneornil(1)) {
        // Game time is in ticks, so that each day has 24000 ticks, meaning
        // one hour is game time divided by one thousand. Also, Minecraft
        // starts days at 6 o'clock, versus the 1 o'clock of timestamps so we
        // add those five hours. Thus:
        // timestamp = (time + 5000) * 60[kh] * 60[km] / 1000[s]
        LuaValue.valueOf((machine.map().getWorld.getTotalWorldTime + 5000) * 60 * 60 / 1000)
      }else{
        val table = args.checktable(1)

        def getField(key: String, d: Int) = {
          val res = table.get(key)
          if (!res.isint())
            if (d < 0) throw new Exception("field '" + key + "' missing in date table")
            else d
          else res.toint()
        }

        val sec = getField("sec", 0)
        val min = getField("min", 0)
        val hour = getField("hour", 12)
        val mday = getField("day", -1)
        val mon = getField("month", -1)
        val year = getField("year", -1)

        val time = GameTimeFormatter.mktime(year, mon, mday, hour, min, sec)
        if(time == null) LuaValue.NIL else LuaValue.valueOf(time: Int)
      }
    })

    // Allow getting the real world time for timeouts.
    os.set("realTime", (_: Varargs) => LuaValue.valueOf(System.currentTimeMillis() / 1000.0))

    // The time the computer has been running, as opposed to the CPU time.
    // World time is in ticks, and each second has 20 ticks. Since we
    // want uptime() to return real seconds, though, we'll divide it
    // accordingly.
    os.set("uptime", (_: Varargs) => LuaValue.valueOf(machine.upTime()))

    os.set("pushSignal", (args: Varargs) => LuaValue.valueOf(machine.signal(args.checkjstring(1), toSimpleJavaObjects(args, 2): _*)))

    // And its ROM address.
    os.set("romAddress", (_: Varargs) => Option(machine.romAddress) match {
      case Some(address) => LuaValue.valueOf(address)
      case _ => LuaValue.NIL
    })

    // How long programs may run without yielding before we stop them.
    lua.set("timeout", LuaValue.valueOf(5))

    // Component interaction stuff.
    val component = LuaValue.tableOf()

    component.set("list", (args: Varargs) => components.synchronized {
      val filter = if (args.isstring(1)) Option(args.tojstring(1)) else None
      val table = LuaValue.tableOf(0, components.size)
      for ((address, name) <- components) {
        if (filter.isEmpty || name.contains(filter.get)) {
          table.set(address, name)
        }
      }
      table
    })

    component.set("type", (args: Varargs) => components.synchronized {
      components.get(args.checkjstring(1)) match {
        case name: String =>
          LuaValue.valueOf(name)
        case _ =>
          LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf("no such component"))
      }
    })

    //TODO: Fix this!
    /*component.set("methods", (args: Varargs) => {
      Option(node.network.node(args.checkjstring(1))) match {
        case Some(component: server.network.Component) if component.canBeSeenFrom(node) || component == node =>
          val table = LuaValue.tableOf()
          for (method <- component.methods()) {
            table.set(method, LuaValue.valueOf(component.isDirect(method)))
          }
          table
        case _ =>
          LuaValue.varargsOf(LuaValue.NIL, LuaValue.valueOf("no such component"))
      }
    })*/

    component.set("invoke", (args: Varargs) => {
      val address = args.checkjstring(1)
      val method = args.checkjstring(2)
      val params = toSimpleJavaObjects(args, 3)
      try {
        machine.invoke(address, method, params.toArray) match {
          case results: Array[_] =>
            LuaValue.varargsOf(Array(LuaValue.TRUE) ++ results.map(toLuaValue))
          case _ =>
            LuaValue.TRUE
        }
      }catch{
        case _: LimitReachedException => LuaValue.NONE
        case e: IllegalArgumentException if e.getMessage != null =>
          LuaValue.varargsOf(LuaValue.FALSE, LuaValue.valueOf(e.getMessage))
        case e: Throwable if e.getMessage != null =>
          LuaValue.varargsOf(LuaValue.TRUE, LuaValue.NIL, LuaValue.valueOf(e.getMessage))
        case _: IndexOutOfBoundsException =>
          LuaValue.varargsOf(LuaValue.FALSE, LuaValue.valueOf("index out of bounds"))
        case _: IllegalArgumentException =>
          LuaValue.varargsOf(LuaValue.FALSE, LuaValue.valueOf("bad argument"))
        case _: NoSuchMethodException =>
          LuaValue.varargsOf(LuaValue.FALSE, LuaValue.valueOf("no such method"))
        case _: FileNotFoundException =>
          LuaValue.varargsOf(LuaValue.TRUE, LuaValue.NIL, LuaValue.valueOf("file not found"))
        case _: SecurityException =>
          LuaValue.varargsOf(LuaValue.TRUE, LuaValue.NIL, LuaValue.valueOf("access denied"))
        case _: IOException =>
          LuaValue.varargsOf(LuaValue.TRUE, LuaValue.NIL, LuaValue.valueOf("i/o error"))
        case e: Throwable =>
          NailedLog.warn("Unexpected error in Lua callback", e)
          LuaValue.varargsOf(LuaValue.TRUE, LuaValue.NIL, LuaValue.valueOf("unknown error"))
      }
    })

    lua.set("component", component)

    val kernel = lua.load(classOf[Machine].getResourceAsStream("/assets/nailed/lua/kernel.lua"), "=kernel", "t", lua)
    thread = new LuaThread(lua, kernel) // Left as the first value on the stack.

    true
  }

  override def close() = {
    lua = null
    thread = null
    synchronizedCall = null
    synchronizedResult = null
    doneWithInitRun = false
  }
}
