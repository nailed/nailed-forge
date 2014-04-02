package jk_5.nailed.updater.json

import com.google.gson.{TypeAdapter, Gson, TypeAdapterFactory}
import java.util
import java.util.Locale
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
    for(c <- typ.getRawType.getEnumConstants.asInstanceOf[Array[T]]){
      map.put(c.toString.toLowerCase(util.Locale.US), c)
    }
    new TypeAdapter[T] {
      def read(reader: JsonReader): T = {
        if(reader.peek eq JsonToken.NULL){
          reader.nextNull()
          null
        }
        val name = reader.nextString
        if(name == null) return null.asInstanceOf[T]
        map.get(name.toLowerCase(Locale.US))
      }

      def write(writer: JsonWriter, value: T){
        if(value == null){
          writer.nullValue
        }else{
          writer.value(value.toString.toLowerCase(Locale.US))
        }
      }
    }
  }
}
