package jk_5.nailed.updater.json

import com.google.gson.{TypeAdapter, Gson, TypeAdapterFactory}
import java.util
import com.google.gson.stream.{JsonWriter, JsonReader, JsonToken}
import com.google.gson.reflect.TypeToken

/**
 * No description given
 *
 * @author jk-5
 */
/**
 * No description given
 *
 * @author jk-5
 */
class EnumAdapterFactory extends TypeAdapterFactory {

  def create[T](gson: Gson, typ: TypeToken[T]): TypeAdapter[T] = {
    if(!typ.getRawType.isEnum) return null
    val map = new util.HashMap[String, T]()
    typ.getRawType.getEnumConstants.asInstanceOf[Array[T]].foreach(c => map.put(c.toString.toLowerCase, c))
    new TypeAdapter[T] {
      def read(reader: JsonReader): T = {
        if(reader.peek == JsonToken.NULL){
          reader.nextNull()
          null
        }
        val name = reader.nextString
        if(name == null) return null.asInstanceOf[T]
        map.get(name.toLowerCase)
      }
      def write(writer: JsonWriter, value: T){
        if(value == null) writer.nullValue
        else writer.value(value.toString.toLowerCase)
      }
    }
  }
}
