package jk_5.worldeditcui.render.vector

/**
 * No description given
 *
 * @author jk-5
 */
object Vector2D {
  @inline def subtract(a: Vector2D, b: Vector2D) = new Vector2D(a.getX - b.getX, a.getY - b.getY)
  @inline def add(a: Vector2D, b: Vector2D) = new Vector2D(a.getX + b.getX, a.getY + b.getY)
  @inline def to3D(o: Vector2D, y: Float) = new Vector3D(o.x, y, o.z)
}
case class Vector2D(protected val x: Float, protected val z: Float) {
  def getX = this.x
  def getY = this.z

  @inline def add(that: Vector2D) = Vector2D.add(this, that)
  @inline def subtract(that: Vector2D) = Vector2D.subtract(this, that)
  @inline def to3D(y: Float) = Vector2D.to3D(this, y)
}
