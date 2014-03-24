package jk_5.nailed.util

/**
 * No description given
 *
 * @author jk-5
 */
object ColorFormat {
  private val fgShift = 8
  private val bgMask = 0x000000FF

  private val colorBitFormat = new MultiBitFormat(3, 3, 2)

  def pack(foreground: Int, background: Int, format: ColorFormat = colorBitFormat) = ((format.deflate(foreground) << fgShift) | format.deflate(background)).toShort
  def unpackForeground(color: Short, format: ColorFormat = colorBitFormat) = format.inflate((color & 0xFFFF) >>> fgShift)
  def unpackBackground(color: Short, format: ColorFormat = colorBitFormat) = format.inflate(color & bgMask)
}

private abstract class ColorFormat {
  def inflate(value: Int): Int

  def deflate(value: Int): Int
}

class SingleBitFormat extends ColorFormat {
  def inflate(value: Int) = if (value == 0) 0x000000 else 0xFFFFFF

  def deflate(value: Int) = if (value == 0) 0 else 1
}

class MultiBitFormat(rBits: Int, gBits: Int, bBits: Int) extends ColorFormat {

  private val rMask32 = 0xFF0000
  private val gMask32 = 0x00FF00
  private val bMask32 = 0x0000FF
  private val rShift32 = 16
  private val gShift32 = 8
  private val bShift32 = 0

  def mask(nBits: Int) = 0xFFFFFFFF >>> (32 - nBits)

  private val bShift = 0
  private val gShift = bBits
  private val rShift = gShift + gBits

  private val bMask = mask(bBits) << bShift
  private val gMask = mask(gBits) << gShift
  private val rMask = mask(rBits) << rShift

  private val bScale = 255.0 / ((1 << bBits) - 1)
  private val gScale = 255.0 / ((1 << gBits) - 1)
  private val rScale = 255.0 / ((1 << rBits) - 1)

  def inflate(value: Int) = {
    val r = ((((value & rMask) >>> rShift) * rScale).toInt << rShift32) & rMask32
    val g = ((((value & gMask) >>> gShift) * gScale).toInt << gShift32) & gMask32
    val b = ((((value & bMask) >>> bShift) * bScale).toInt << bShift32) & bMask32
    r | g | b
  }

  def deflate(value: Int) = {
    val r = ((((value & rMask32) >>> rShift32) / rScale).toInt << rShift) & rMask
    val g = ((((value & gMask32) >>> gShift32) / gScale).toInt << gShift) & gMask
    val b = ((((value & bMask32) >>> bShift32) / bScale).toInt << bShift) & bMask
    r | g | b
  }
}
