package jk_5.worldeditcui.render.vector

/**
 * No description given
 *
 * @author jk-5
 */
object Vector3D {
  @inline def subtract(a: Vector3D, b: Vector3D) = new Vector3D(a.getX - b.getX, a.getY - b.getY, a.getZ - b.getZ)
  @inline def add(a: Vector3D, b: Vector3D) = new Vector3D(a.getX + b.getX, a.getY + b.getY, a.getZ + b.getZ)
}
case class Vector3D(private val x: Float, private val y: Float, private val z: Float) {
  def getX = this.x
  def getY = this.y
  def getZ = this.z

  @inline def add(that: Vector3D) = Vector3D.add(this, that)
  @inline def subtract(that: Vector3D) = Vector3D.subtract(this, that)
}
