package jk_5.nailed.client.updater

/**
 * No description given
 *
 * @author jk-5
 */
object UpdaterApi {
  var updaterInstance: Any = null
  var updaterClass: Class[_] = null
  val updaterInstalled: Boolean = try{
    val cl = Class.forName("jk_5.nailed.updater.UpdaterApiImpl$")
    val defined = cl != null
    if(defined){
      updaterInstance = cl.getDeclaredField("MODULE$").get(null)
      updaterClass = cl
      true
    }else false
  }catch{
    case e: Exception => false
  }

  def update(): Boolean = if(this.updaterInstalled){
    updaterClass.getDeclaredMethod("update").invoke(updaterInstance).asInstanceOf[java.lang.Boolean]
  }else false
}
