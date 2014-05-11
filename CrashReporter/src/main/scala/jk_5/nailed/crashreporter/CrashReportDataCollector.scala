package jk_5.nailed.crashreporter

import com.google.gson.{JsonPrimitive, JsonArray, GsonBuilder, JsonObject}
import net.minecraft.launchwrapper.{ITweaker, Launch}
import java.util
import scala.collection.JavaConversions._
import org.apache.logging.log4j.LogManager
import cpw.mods.fml.common.discovery.ASMDataTable
import com.google.common.collect.ImmutableList

/**
 * No description given
 *
 * @author jk-5
 */
object CrashReportDataCollector {

  lazy val tweakList = Launch.blackboard.get("Tweaks").asInstanceOf[util.List[ITweaker]]
  lazy val username = {
    val arglist = Launch.blackboard.get("launchArgs").asInstanceOf[util.HashMap[String, String]]
    val ret = if(arglist.containsKey("--username")) Option(arglist.get("--username")) else None
    if(ret.isDefined){
      logger.info("Found username: {}", ret.get)
    }else{
      logger.info("Was not able to find username")
    }
    ret
  }
  val logger = LogManager.getLogger
  val wrappedPluginClass = Class.forName("cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper")
  val wrappedPluginInstanceField = {
    val f = wrappedPluginClass.getDeclaredField("coreModInstance")
    f.setAccessible(true)
    f
  }

  def init(dataTable: ASMDataTable){
    /*val candidates = try {
      val packageMap: Multimap[String, ModCandidate] = ReflectionHelper.getPrivateValue(classOf[ASMDataTable], dataTable, "packageMap")
      if (packageMap != null) packageMap else ImmutableList.of[ModCandidate]()
    }catch{case e: Exception => ImmutableList.of[ModCandidate]()}
    */
  }

  def populate(location: String): JsonObject = {
    val ret = new JsonObject
    addTweakers(ret)
    addTransformers(ret)
    addUsername(ret)
    addJavaInfo(ret)
    addOsInfo(ret)
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
    val array = new JsonArray
    val cl = Launch.classLoader
    cl.getTransformers.foreach(c => array.add(new JsonPrimitive(c.getClass.getName)))
    obj.add("transformers", array)
  }

  def addUsername(obj: JsonObject){
    if(username.isEmpty){
      obj.addProperty("username", "[unknown]")
    }else if(CrashReporter.includeUsername){
      obj.addProperty("username", username.get)
    }else{
      obj.addProperty("username", "[hidden]")
    }
  }

  def addJavaInfo(obj: JsonObject){
    val o = new JsonObject
    obj.add("java", o)
    o.addProperty("name", System.getProperty("java.vm.name"))
    o.addProperty("version", System.getProperty("java.version"))
  }

  def addOsInfo(obj: JsonObject){
    val o = new JsonObject
    obj.add("os", o)
    o.addProperty("name", System.getProperty("os.name"))
    o.addProperty("arch", System.getProperty("os.arch"))
    o.addProperty("version", System.getProperty("os.version"))
  }

  def main(args: Array[String]){
    println(new GsonBuilder().setPrettyPrinting().create().toJson(this.populate("test")))
  }
}
