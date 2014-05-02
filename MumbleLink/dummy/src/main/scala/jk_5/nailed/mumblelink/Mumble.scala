package jk_5.nailed.mumblelink

/**
 * No description given
 *
 * @author jk-5
 */
object Mumble {

  //TODO: Better native loading
  System.loadLibrary("MumbleLink")

  var inited = false

  @native def update (fAvatarPosition: Array[Float], fAvatarFront: Array[Float], fAvatarTop: Array[Float], name: String, description: String, fCameraPosition: Array[Float], fCameraFront: Array[Float], fCameraTop: Array[Float], identity: String, context: String): Int
  @native def init(): Int

  def tryInit(): Boolean = {
    this.inited = this.init() == 0
    this.inited
  }
}
