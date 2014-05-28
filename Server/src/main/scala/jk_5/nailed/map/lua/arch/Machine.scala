package jk_5.nailed.map.lua.arch

import java.io
import java.lang.reflect.Constructor
import java.util.concurrent.Callable
import net.minecraft.client.Minecraft
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.DimensionManager
import scala.collection.mutable
import jk_5.nailed.{NailedLog, api}
import jk_5.nailed.api.lua._
import jk_5.nailed.map.lua.ThreadPoolFactory
import scala.Some

/**
 * No description given
 *
 * @author jk-5
 */
class Machine(constructor: Constructor[_ <: Architecture]) extends api.lua.Machine with Runnable {

  val component = ComponentApi.newComponent(this)

  val tmp = if (Settings.get.tmpSize > 0) {
    Option(FileSystem.asManagedEnvironment(FileSystem.
      fromMemory(Settings.get.tmpSize * 1024), "tmpfs"))
  } else None

  val architecture = constructor.newInstance(this)

  private[component] val state = mutable.Stack(Machine.State.Stopped)

  private val _components = mutable.Set.empty[Component]

  private val addedComponents = mutable.Set.empty[Component]

  private val _users = mutable.Set.empty[String]

  private val signals = mutable.Queue.empty[Machine.Signal]

  private val callCounts = mutable.Map.empty[String, mutable.Map[String, Int]]

  // ----------------------------------------------------------------------- //

  private var timeStarted = 0L // Game-world time [ms] for os.uptime().

  var worldTime = 0L // Game-world time for os.time().

  private var cpuTotal = 0L // Pseudo-real-world time [ns] for os.clock().

  private var cpuStart = 0L // Pseudo-real-world time [ns] for os.clock().

  private var remainIdle = 0 // Ticks left to sleep before resuming.

  private var remainingPause = 0 // Ticks left to wait before resuming.

  private var message: Option[String] = None // For error messages.

  // ----------------------------------------------------------------------- //

  override def components = scala.collection.convert.WrapAsJava.setAsJavaSet(_components)

  def lastError = message.orNull

  override def upTime() = (worldTime - timeStarted) / 20.0

  override def cpuTime = (cpuTotal + (System.nanoTime() - cpuStart)) * 10e-10

  // ----------------------------------------------------------------------- //

  override def canInteract(player: String) = true //TODO: Permission check

  override def isRunning = state.synchronized(state.top != Machine.State.Stopped && state.top != Machine.State.Stopping)

  override def isPaused = state.synchronized(state.top == Machine.State.Paused && remainingPause > 0)

  override def start() = state.synchronized(state.top match {
    case Machine.State.Stopped =>
      processAddedComponents()
      init() && {
        switchTo(Machine.State.Starting)
        timeStarted = this.map().getWorld.getTotalWorldTime
        //node.sendToReachable("computer.started") //TODO
        true
      }
    case Machine.State.Paused if remainingPause > 0 =>
      remainingPause = 0
      true
    case Machine.State.Stopping =>
      switchTo(Machine.State.Restarting)
      true
    case _ =>
      false
  })

  override def pause(seconds: Double): Boolean = {
    val ticksToPause = math.max((seconds * 20).toInt, 0)
    def shouldPause(state: Machine.State.Value) = state match {
      case Machine.State.Stopping | Machine.State.Stopped => false
      case Machine.State.Paused if ticksToPause <= remainingPause => false
      case _ => true
    }
    if (shouldPause(state.synchronized(state.top))) {
      // Check again when we get the lock, might have changed since.
      Machine.this.synchronized(state.synchronized(if (shouldPause(state.top)) {
        if (state.top != Machine.State.Paused) {
          assert(!state.contains(Machine.State.Paused))
          state.push(Machine.State.Paused)
        }
        remainingPause = ticksToPause
        return true
      }))
    }
    false
  }

  override def stop() = state.synchronized(state.top match {
    case Machine.State.Stopped | Machine.State.Stopping =>
      false
    case _ =>
      state.push(Machine.State.Stopping)
      true
  })

  override def crash(message: String) = {
    this.message = Option(message)
    stop()
  }

  override def signal(name: String, args: AnyRef*) = state.synchronized(state.top match {
    case Machine.State.Stopped | Machine.State.Stopping => false
    case _ => signals.synchronized {
      if (signals.size >= 256) false
      else if (args == null) {
        signals.enqueue(new Machine.Signal(name, Array.empty))
        true
      }
      else {
        signals.enqueue(new Machine.Signal(name, args.map {
          case null | Unit | None => null
          case arg: java.lang.Boolean => arg
          case arg: java.lang.Byte => Double.box(arg.doubleValue)
          case arg: java.lang.Character => Double.box(arg.toDouble)
          case arg: java.lang.Short => Double.box(arg.doubleValue)
          case arg: java.lang.Integer => Double.box(arg.doubleValue)
          case arg: java.lang.Long => Double.box(arg.doubleValue)
          case arg: java.lang.Float => Double.box(arg.doubleValue)
          case arg: java.lang.Double => arg
          case arg: java.lang.String => arg
          case arg: Array[Byte] => arg
          case arg: Map[_, _] if arg.isEmpty || arg.head._1.isInstanceOf[String] && arg.head._2.isInstanceOf[String] => arg
          case arg =>
            NailedLog.warn("Trying to push signal with an unsupported argument of type " + arg.getClass.getName)
            null
        }.toArray[AnyRef]))
        true
      }
    }
  })

  override def popSignal(): Machine.Signal = signals.synchronized(if (signals.isEmpty) null else signals.dequeue())

  override def invoke(address: String, method: String, args: Array[AnyRef]) =
    Option(node.network.node(address)) match {
      case Some(component: server.network.Component) if component.canBeSeenFrom(node) || component == node =>
        val direct = component.isDirect(method)
        if (direct && architecture.isInitialized) callCounts.synchronized {
          val limit = component.limit(method)
          val counts = callCounts.getOrElseUpdate(component.address, mutable.Map.empty[String, Int])
          val count = counts.getOrElseUpdate(method, 0)
          if (count >= limit) {
            throw new LimitReachedException()
          }
          counts(method) += 1
        }
        component.invoke(method, this, args: _*)
      case _ => throw new IllegalArgumentException("no such component")
    }

  override def documentation(address: String, method: String) = Option(node.network.node(address)) match {
    case Some(component: server.network.Component) if component.canBeSeenFrom(node) || component == node => component.doc(method)
    case _ => throw new IllegalArgumentException("no such component")
  }

  // ----------------------------------------------------------------------- //

  @LuaMethod(doc = """function():boolean -- Starts the computer. Returns true if the state changed.""")
  def start(context: Context, args: Arguments): Array[AnyRef] = result(!isPaused && start())

  @LuaMethod(doc = """function():boolean -- Stops the computer. Returns true if the state changed.""")
  def stop(context: Context, args: Arguments): Array[AnyRef] = result(stop())

  @LuaMethod(direct = true, doc = """function():boolean -- Returns whether the computer is running.""")
  def isRunning(context: Context, args: Arguments): Array[AnyRef] = result(isRunning)

  // ----------------------------------------------------------------------- //

  override def update() = if (state.synchronized(state.top != Machine.State.Stopped)) {
    // Add components that were added since the last update to the actual list
    // of components if we can see them. We use this delayed approach to avoid
    // issues with components that have a visibility lower than their
    // reachability, because in that case if they get connected in the wrong
    // order we wouldn't add them (since they'd be invisible in their connect
    // message, and only become visible with a later node-to-node connection,
    // but that wouldn't trigger a connect message anymore due to the higher
    // reachability).
    processAddedComponents()

    // Update world time for time().
    worldTime = this.map.getWorld.getTotalWorldTime

    // We can have rollbacks from '/time set'. Avoid getting negative uptimes.
    timeStarted = math.min(timeStarted, worldTime)

    if (remainIdle > 0) {
      remainIdle -= 1
    }

    // Reset direct call limits.
    callCounts.synchronized(if (callCounts.size > 0) callCounts.clear())

    // Check if we should switch states. These are all the states in which we're
    // guaranteed that the executor thread isn't running anymore.
    state.synchronized(state.top match {
      // Booting up.
      case Machine.State.Starting =>
        switchTo(Machine.State.Yielded)
      // Computer is rebooting.
      case Machine.State.Restarting =>
        close()
        /*if (Settings.get.eraseTmpOnReboot) {
          tmp.foreach(_.node.remove()) // To force deleting contents.
          tmp.foreach(tmp => node.connect(tmp.node))
        }
        node.sendToReachable("computer.stopped")*/ //TODO
        start()
      // Resume from pauses based on sleep or signal underflow.
      case Machine.State.Sleeping if remainIdle <= 0 || !signals.isEmpty =>
        switchTo(Machine.State.Yielded)
      // Resume in case we paused  because the game was paused.
      case Machine.State.Paused =>
        if (remainingPause > 0) {
          remainingPause -= 1
        }else{
          state.pop()
          switchTo(state.top) // Trigger execution if necessary.
        }
      // Perform a synchronized call (message sending).
      case Machine.State.SynchronizedCall =>
        // Clear direct call limits again, just to be on the safe side...
        // Theoretically it'd be possible for the executor to do some direct
        // calls between the clear and the state check, which could in turn
        // make this synchronized call fail due the limit still being maxed.
        callCounts.clear()
        // We switch into running state, since we'll behave as though the call
        // were performed from our executor thread.
        switchTo(Machine.State.Running)
        try {
          architecture.runSynchronized()
          // Check if the callback called pause() or stop().
          state.top match {
            case Machine.State.Running =>
              switchTo(Machine.State.SynchronizedReturn)
            case Machine.State.Paused =>
              state.pop() // Paused
              state.pop() // Running, no switchTo to avoid new future.
              state.push(Machine.State.SynchronizedReturn)
              state.push(Machine.State.Paused)
            case Machine.State.Stopping => // Nothing to do, we'll die anyway.
            case _ => throw new AssertionError()
          }
        }catch{
          case e: java.lang.Error if e.getMessage == "not enough memory" => crash("Out of memory")
          case e: Throwable =>
            NailedLog.warn("Faulty architecture implementation for synchronized calls.", e)
            crash("Internal VM error")
        }

        assert(state.top != Machine.State.Running)
      case _ => // Nothing special to do, just avoid match errors.
    })

    // Finally check if we should stop the computer. We cannot lock the state
    // because we may have to wait for the executor thread to finish, which
    // might turn into a deadlock depending on where it currently is.
    state.synchronized(state.top) match {
      // Computer is shutting down.
      case Machine.State.Stopping => Machine.this.synchronized(state.synchronized {
        close()
        /*tmp.foreach(_.node.remove()) // To force deleting contents.
        if (node.network != null) {
          tmp.foreach(tmp => node.connect(tmp.node))
        }
        node.sendToReachable("computer.stopped")*/ //TODO
      })
      case _ =>
    }
  }

  // ----------------------------------------------------------------------- //

  def addComponent(component: Component) {
    if(!_components.contains(component)){
      addedComponents += component
    }
  }

  def removeComponent(component: Component) {
    if(_components.contains(component)){
      _components.synchronized(_components -= component)
      signal("component_removed", component.name)
    }
    addedComponents -= component
  }

  private def processAddedComponents(){
    if(addedComponents.size > 0){
      for(component <- addedComponents){
        _components.synchronized(_components += component)
        // Skip the signal if we're not initialized yet, since we'd generate a
        // duplicate in the startup script otherwise.
        if(architecture.isInitialized){
          signal("component_added", component.name)
        }
      }
      addedComponents.clear()
    }
  }

  // ----------------------------------------------------------------------- //

  private def init(): Boolean = {
    // Reset error state.
    message = None

    // Clear any left-over signals from a previous run.
    signals.clear()

    // Connect the ROM and `/tmp` node to our owner. We're not in a network in
    // case we're loading, which is why we have to check it here.
    if (node.network != null) {
      tmp.foreach(fs => node.connect(fs.node))
      rom.foreach(fs => node.connect(fs.node))
    }

    try {
      return architecture.initialize()
    }
    catch {
      case ex: Throwable =>
        NailedLog.warn("Failed initializing computer", ex)
        close()
    }
    false
  }

  private def close() = state.synchronized(
    if (state.size == 0 || state.top != Machine.State.Stopped) {
      state.clear()
      state.push(Machine.State.Stopped)
      architecture.close()
      signals.clear()
      timeStarted = 0
      cpuTotal = 0
      cpuStart = 0
      remainIdle = 0
    })

  // ----------------------------------------------------------------------- //

  private def switchTo(value: Machine.State.Value) = {
    val result = state.pop()
    state.push(value)
    if (value == Machine.State.Yielded || value == Machine.State.SynchronizedReturn) {
      remainIdle = 0
      Machine.threadPool.submit(this)
    }

    result
  }

  private def isGamePaused = !MinecraftServer.getServer.isDedicatedServer && (MinecraftServer.getServer match {
    case integrated: IntegratedServer => Minecraft.getMinecraft.isGamePaused
    case _ => false
  })

  // This is a really high level lock that we only use for saving and loading.
  override def run(): Unit = Machine.this.synchronized {
    val isSynchronizedReturn = state.synchronized {
      if (state.top != Machine.State.Yielded &&
        state.top != Machine.State.SynchronizedReturn) {
        return
      }
      // See if the game appears to be paused, in which case we also pause.
      if (isGamePaused) {
        state.push(Machine.State.Paused)
        return
      }
      switchTo(Machine.State.Running) == Machine.State.SynchronizedReturn
    }

    cpuStart = System.nanoTime()

    try {
      val result = architecture.runThreaded(isSynchronizedReturn)

      // Check if someone called pause() or stop() in the meantime.
      state.synchronized {
        state.top match {
          case Machine.State.Running =>
            result match {
              case result: ExecutionResult.Sleep =>
                signals.synchronized {
                  // Immediately check for signals to allow processing more than one
                  // signal per game tick.
                  if (signals.isEmpty && result.ticks > 0) {
                    switchTo(Machine.State.Sleeping)
                    remainIdle = result.ticks
                  } else {
                    switchTo(Machine.State.Yielded)
                  }
                }
              case result: ExecutionResult.SynchronizedCall =>
                switchTo(Machine.State.SynchronizedCall)
              case result: ExecutionResult.Shutdown =>
                if (result.reboot) {
                  switchTo(Machine.State.Restarting)
                }
                else {
                  switchTo(Machine.State.Stopping)
                }
              case result: ExecutionResult.Error =>
                crash(result.message)
            }
          case Machine.State.Paused =>
            state.pop() // Paused
            state.pop() // Running, no switchTo to avoid new future.
            state.push(Machine.State.Yielded)
            state.push(Machine.State.Paused)
          case Machine.State.Stopping => // Nothing to do, we'll die anyway.
          case _ => throw new AssertionError("Invalid state in executor post-processing.")
        }
        assert(state.top != Machine.State.Running)
      }
    }catch{
      case e: Throwable =>
        NailedLog.warn("Architecture's runThreaded threw an error. This should never happen!", e)
        crash("Internal VM error")
    }
    // Keep track of time spent executing the computer.
    cpuTotal += System.nanoTime() - cpuStart
  }
}

object Machine extends MachineAPI {
  val checked = mutable.Set.empty[Class[_ <: Architecture]]

  var roms = mutable.Map.empty[Class[_ <: Architecture], mutable.LinkedHashMap[String, Callable[fs.FileSystem]]]

  override def add(architecture: Class[_ <: Architecture]) {
    if (!checked.contains(architecture)) {
      try {
        architecture.getConstructor(classOf[machine.Machine])
      }
      catch {
        case t: Throwable => throw new IllegalArgumentException("Architecture does not have required constructor.")
      }
      checked += architecture
      roms += architecture -> mutable.LinkedHashMap.empty[String, Callable[fs.FileSystem]]
    }
  }

  override def addRomResource(architecture: Class[_ <: Architecture], resource: Callable[fs.FileSystem], name: String) {
    add(architecture)
    val rom = roms(architecture)
    if (rom.contains(name)) {
      throw new IllegalArgumentException(s"A file system with the name '$name' is already registered.")
    }
    rom += name -> resource
  }

  override def architectures() = scala.collection.convert.WrapAsJava.asJavaIterable(checked)

  override def create(architecture: Class[_ <: Architecture]) = {
    add(architecture)
    val rom = new CompositeReadOnlyFileSystem(roms(architecture))
    val instance = new Machine(Option(FileSystem.asManagedEnvironment(rom, "rom")),
      architecture.getConstructor(classOf[machine.Machine]))
    val romPath = "rom/" + instance.architecture.name
    try {
      val path = new io.File(DimensionManager.getCurrentSaveRootDirectory, Settings.savePath + romPath)
      if ((path.exists || path.mkdirs()) && path.isDirectory && !rom.parts.contains(romPath)) {
        rom.parts += romPath -> FileSystem.fromSaveDirectory(romPath, 0, false)
      }
      else {
        NailedLog.warn(s"Failed mounting user ROM override '$romPath'. It is either not a directory or another mod registered a ROM resource with that name.")
      }
    }
    catch {
      case t: Throwable => NailedLog.warn(s"Failed mounting user ROM override '$romPath'.", t)
    }
    instance
  }

  /** Possible states of the computer, and in particular its executor. */
  private[component] object State extends Enumeration {
    /** The computer is not running right now and there is no Lua state. */
    val Stopped = Value("Stopped")

    /** Booting up, doing the first run to initialize the kernel and libs. */
    val Starting = Value("Starting")

    /** Computer is currently rebooting. */
    val Restarting = Value("Restarting")

    /** The computer is currently shutting down. */
    val Stopping = Value("Stopping")

    /** The computer is paused and waiting for the game to resume. */
    val Paused = Value("Paused")

    /** The computer executor is waiting for a synchronized call to be made. */
    val SynchronizedCall = Value("SynchronizedCall")

    /** The computer should resume with the result of a synchronized call. */
    val SynchronizedReturn = Value("SynchronizedReturn")

    /** The computer will resume as soon as possible. */
    val Yielded = Value("Yielded")

    /** The computer is yielding for a longer amount of time. */
    val Sleeping = Value("Sleeping")

    /** The computer is up and running, executing Lua code. */
    val Running = Value("Running")
  }

  /** Signals are messages sent to the Lua state from Java asynchronously. */
  private[component] class Signal(val name: String, val args: Array[AnyRef]) extends machine.Signal

  private val threadPool = ThreadPoolFactory.create(4)
}
