package jk_5.nailed.map.lua

import jk_5.nailed.{NailedLog, api}
import scala.collection.{mutable, immutable}
import scala.collection.convert.WrapAsJava._
import jk_5.nailed.api.lua.{Context, Arguments}
import java.lang.reflect.{Modifier, Method, InvocationTargetException}

/**
 * No description given
 *
 * @author jk-5
 */
trait Component extends api.lua.Component {

  def host: Any

  private lazy val callbacks = Component.callbacks(host)

  private lazy val hosts = callbacks.map {
    case (method, callback) => method -> Some(host)
  }

  def methods = callbacks.keySet

  def doc(name: String) = callbacks.get(name) match {
    case Some(callback) => callback.doc
    case _ => throw new NoSuchMethodException()
  }

  def invoke(method: String, context: Context, arguments: AnyRef*) =
    callbacks.get(method) match {
      case Some(callback) => hosts(method) match {
        case Some(environment) => ConverterRegistry.convert(callback(environment, context, new Component.VarArgs(Seq(arguments: _*))))
        case _ => throw new NoSuchMethodException()
      }
      case _ => throw new NoSuchMethodException()
    }

  def isDirect(method: String) =
    callbacks.get(method) match {
      case Some(callback) => callbacks(method).direct
      case _ => throw new NoSuchMethodException()
    }

  def limit(method: String) =
    callbacks.get(method) match {
      case Some(callback) => callbacks(method).limit
      case _ => throw new NoSuchMethodException()
    }
}

object Component {
  private val cache = mutable.Map.empty[Class[_], immutable.Map[String, LuaMethodCallback]]

  def callbacks(owner: Any) = owner match {
    case _ => cache.getOrElseUpdate(owner.getClass, analyze(owner))
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

  // ----------------------------------------------------------------------- //

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
