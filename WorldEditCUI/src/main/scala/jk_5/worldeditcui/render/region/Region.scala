package jk_5.worldeditcui.render.region

import jk_5.worldeditcui.render.region.RegionType.RegionType

/**
 * No description given
 *
 * @author jk-5
 */
abstract class Region {

  def initialize() = {}
  def render()
  def setCuboidPoint(id: Int, x: Int, y: Int, z: Int) = {}
  def setPolygonPoint(id: Int, x: Int, z: Int) = {}
  def setEllipsoidCenter(x: Int, y: Int, z: Int) = {}
  def setEllipsoidRadii(x: Double, y: Double, z: Double) = {}
  def setMinMax(min: Int, max: Int) = {}
  def setCylinderCenter(x: Int, y: Int, z: Int) = {}
  def setCylinderRadius(x: Double, z: Double) = {}
  def regionType: RegionType
}
