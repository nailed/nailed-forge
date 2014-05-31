package jk_5.worldeditcui.network.packet

/**
 * No description given
 *
 * @author jk-5
 */
abstract class Packet(val minArgs: Int, val maxArgs: Int) {

  private var args: Array[String] = _

  final def processPacket(args: Array[String]){
    this.args = args
    if((this.maxArgs == this.minArgs && this.args.length != this.maxArgs) || (this.maxArgs != this.minArgs && (this.args.length > this.maxArgs || this.args.length < this.minArgs))){
      throw new PacketException("Received invalid number of arguments")
    }else{
      this.process()
    }
  }

  def process()

  def getInt(index: Int) = Integer.parseInt(this.args(index))
  def getDouble(index: Int) = java.lang.Double.parseDouble(this.args(index))
  def getString(index: Int) = this.args(index)
}

class PacketException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)
