package jk_5.nailed.map.lua

import jk_5.nailed.{NailedLog, api}
import scala.collection.{immutable, mutable}
import scala.collection.convert.WrapAsJava._
import jk_5.nailed.api.lua.{LimitReachedException, Machine, Context, Arguments}
import java.lang.reflect.{Modifier, Method, InvocationTargetException}
import org.luaj.vm2.{LuaValue, Varargs}
import jk_5.nailed.map.lua.ScalaClosure._
import java.io.{IOException, FileNotFoundException}

/**
 * No description given
 *
 * @author jk-5
 */
object NewLuaConverter {

  private val cache = mutable.Map.empty[Class[_], immutable.Map[String, LuaMethodCallback]]

  def callbacks(owner: Any) = cache.getOrElseUpdate(owner.getClass, analyze(owner))

  def asLua(callbacks: immutable.Map[String, LuaMethodCallback], machine: Machine, errorCheck: () => Unit) = {
    val ret = LuaValue.tableOf()

    callbacks.foreach(callback => {
      ret.set(callback._1, (args: Varargs) => {
        val params = toSimpleJavaObjects(args)
        try{
          machine.invoke(callback._1, params.toArray) match {
            case results: Array[_] => LuaValue.varargsOf(Array(LuaValue.TRUE) ++ results.map(toLuaValue))
            case _ => LuaValue.TRUE
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
    })
    ret
  }

  private def analyze(owner: Any) = {
    val callbacks = mutable.Map.empty[String, LuaMethodCallback]
    val whitelists = mutable.Buffer.empty[Set[String]]
    val seeds = Seq(owner.getClass: Class[_])
    val whitelist = whitelists.reduceOption(_.intersect(_)).getOrElse(Set.empty[String])
    def shouldAdd(name: String) = !callbacks.contains(name) && (whitelist.isEmpty || whitelist.contains(name))
    for(seed <- seeds){
      var c: Class[_] = seed
      while (c != classOf[Object]) {
        val ms = c.getDeclaredMethods
        ms.filter(_.isAnnotationPresent(classOf[api.lua.LuaMethod])).foreach(m =>
          if(m.getParameterTypes.size != 2 || (m.getParameterTypes()(0) != classOf[Context]) || m.getParameterTypes()(1) != classOf[Arguments]) {
            NailedLog.error("Invalid use of LuaMethod annotation on %s.%s: invalid argument types or count.".format(m.getDeclaringClass.getName, m.getName))
          }else if (m.getReturnType != classOf[Array[AnyRef]]) {
            NailedLog.error("Invalid use of LuaMethod annotation on %s.%s: invalid return type.".format(m.getDeclaringClass.getName, m.getName))
          }else if (!Modifier.isPublic(m.getModifiers)) {
            NailedLog.error("Invalid use of LuaMethod annotation on %s.%s: method must be public.".format(m.getDeclaringClass.getName, m.getName))
          }else{
            val a = m.getAnnotation[api.lua.LuaMethod](classOf[api.lua.LuaMethod])
            val name = if(a.value != null && a.value.trim != "") a.value else m.getName
            if(shouldAdd(name)){
              callbacks += name -> new ReflectiveLuaMethodCallback(m, a.direct, a.limit, a.doc)
            }
          }
        )

        c = c.getSuperclass
      }
    }
    callbacks.toMap
  }

  abstract class LuaMethodCallback(val direct: Boolean, val limit: Int, val doc: String = "") {
    def apply(instance: Any, context: Context, args: Arguments): Array[AnyRef]
  }

  class ReflectiveLuaMethodCallback(val method: Method, direct: Boolean, limit: Int, doc: String) extends LuaMethodCallback(direct, limit, doc) {
    override def apply(instance: Any, context: Context, args: Arguments) = try {
      method.invoke(instance, context, args).asInstanceOf[Array[AnyRef]]
    }catch{
      case e: InvocationTargetException => throw e.getCause
    }
  }

  class VarArgs(val args: Seq[AnyRef]) extends Arguments {
    def iterator() = args.iterator

    def count() = args.length

    def checkAny(index: Int) = {
      checkIndex(index, "value")
      args(index) match {
        case Unit | None => null
        case arg => arg
      }
    }

    def checkBoolean(index: Int) = {
      checkIndex(index, "boolean")
      args(index) match {
        case value: java.lang.Boolean => value
        case value => throw typeError(index, value, "boolean")
      }
    }

    def checkDouble(index: Int) = {
      checkIndex(index, "number")
      args(index) match {
        case value: java.lang.Double => value
        case value => throw typeError(index, value, "number")
      }
    }

    def checkInteger(index: Int) = {
      checkIndex(index, "number")
      args(index) match {
        case value: java.lang.Double => value.intValue
        case value => throw typeError(index, value, "number")
      }
    }

    def checkString(index: Int) = {
      checkIndex(index, "string")
      args(index) match {
        case value: java.lang.String => value
        case value: Array[Byte] => new String(value, "UTF-8")
        case value => throw typeError(index, value, "string")
      }
    }

    def checkByteArray(index: Int) = {
      checkIndex(index, "string")
      args(index) match {
        case value: java.lang.String => value.getBytes("UTF-8")
        case value: Array[Byte] => value
        case value => throw typeError(index, value, "string")
      }
    }

    def checkTable(index: Int) = {
      checkIndex(index, "table")
      args(index) match {
        case value: java.util.Map[_, _] => value
        case value: Map[_, _] => value
        case value: mutable.Map[_, _] => value
        case value => throw typeError(index, value, "table")
      }
    }

    def isBoolean(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.lang.Boolean => true
        case _ => false
      })

    def isDouble(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.lang.Double => true
        case _ => false
      })

    def isInteger(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.lang.Integer => true
        case value: java.lang.Double => true
        case _ => false
      })

    def isString(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.lang.String => true
        case value: Array[Byte] => true
        case _ => false
      })

    def isByteArray(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.lang.String => true
        case value: Array[Byte] => true
        case _ => false
      })

    def isTable(index: Int) =
      index >= 0 && index < count && (args(index) match {
        case value: java.util.Map[_, _] => true
        case value: Map[_, _] => true
        case value: mutable.Map[_, _] => true
        case _ => false
      })

    private def checkIndex(index: Int, name: String) =
      if (index < 0) throw new IndexOutOfBoundsException()
      else if (args.length <= index) throw new IllegalArgumentException(
        "bad arguments #%d (%s expected, got no value)".
          format(index + 1, name))

    private def typeError(index: Int, have: AnyRef, want: String) =
      new IllegalArgumentException(
        "bad argument #%d (%s expected, got %s)".
          format(index + 1, want, typeName(have)))

    private def typeName(value: AnyRef): String = value match {
      case null | Unit | None => "nil"
      case _: java.lang.Boolean => "boolean"
      case _: java.lang.Double => "double"
      case _: java.lang.String => "string"
      case _: Array[Byte] => "string"
      case value: java.util.Map[_, _] => "table"
      case value: Map[_, _] => "table"
      case value: mutable.Map[_, _] => "table"
      case _ => value.getClass.getSimpleName
    }
  }
}
