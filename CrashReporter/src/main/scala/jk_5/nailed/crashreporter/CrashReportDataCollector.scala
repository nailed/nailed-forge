package jk_5.nailed.crashreporter

import com.google.gson.{JsonPrimitive, JsonArray, GsonBuilder, JsonObject}
import net.minecraft.launchwrapper.{ITweaker, Launch}
import java.util
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CrashReportDataCollector {

  lazy val tweakList = Launch.blackboard.get("Tweaks").asInstanceOf[util.List[ITweaker]]
  val wrappedPluginClass = Class.forName("cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper")
  val wrappedPluginInstanceField = {
    val f = wrappedPluginClass.getDeclaredField("coreModInstance")
    f.setAccessible(true)
    f
  }

  def populate(location: String): JsonObject = {
    val ret = new JsonObject
    addTweakers(ret)
    addTransformers(ret)
    ret
  }

  def addTweakers(obj: JsonObject){
    val array = new JsonArray
    def add(tweaker: ITweaker){
      val name = tweaker.getClass.getName
      if(name == "cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper"){
        val inst = wrappedPluginInstanceField.get(tweaker)
        array.add(new JsonPrimitive(inst.getClass.getName))
      }else{
        array.add(new JsonPrimitive(name))
      }
    }
    tweakList.foreach(add)
    obj.add("tweakers", array)
  }

  def addTransformers(obj: JsonObject){

  }

  def main(args: Array[String]){
    println(new GsonBuilder().setPrettyPrinting().create().toJson(this.populate("test")))
  }
}
