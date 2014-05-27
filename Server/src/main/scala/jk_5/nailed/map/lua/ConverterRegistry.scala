package jk_5.nailed.map.lua

import java.util
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import jk_5.nailed.api
import jk_5.nailed.NailedLog

/**
 * No description given
 *
 * @author jk-5
 */
object ConverterRegistry {

  val converters = ArrayBuffer.empty[api.lua.Converter]

  def convert(value: Array[AnyRef]) = if (value != null) value.map(convertRecursively) else null

  def convertRecursively(value: AnyRef): AnyRef = value match {
    case null | Unit | None => null
    case arg: java.lang.Boolean => arg
    case arg: java.lang.Byte => arg
    case arg: java.lang.Character => arg
    case arg: java.lang.Short => arg
    case arg: java.lang.Integer => arg
    case arg: java.lang.Long => arg
    case arg: java.lang.Float => arg
    case arg: java.lang.Double => arg
    case arg: java.lang.String => arg

    case arg: Array[Boolean] => arg
    case arg: Array[Byte] => arg
    case arg: Array[Character] => arg
    case arg: Array[Short] => arg
    case arg: Array[Integer] => arg
    case arg: Array[Long] => arg
    case arg: Array[Float] => arg
    case arg: Array[Double] => arg
    case arg: Array[String] => arg

    case arg: Array[_] => arg.map {
      case (value: AnyRef) => convertRecursively(value)
    }
    case arg: Map[_, _] => arg.collect {
      case (key: AnyRef, value: AnyRef) => convertRecursively(key) -> convertRecursively(value)
    }
    case arg: java.util.Map[_, _] => arg.collect {
      case (key: AnyRef, value: AnyRef) => convertRecursively(key) -> convertRecursively(value)
    }

    case arg =>
      val result = new util.HashMap[AnyRef, AnyRef]()
      converters.foreach(converter => try converter.convert(arg, result) catch {
        case t: Throwable => NailedLog.warn("Type converter threw an exception.", t)
      })
      if (result.isEmpty) null
      else result
  }
}
