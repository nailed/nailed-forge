package jk_5.nailed.server.command

import java.text.DecimalFormat
import net.minecraft.command.{CommandBase, ICommandSender}
import jk_5.nailed.api.map.Map
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.DimensionManager
import net.minecraft.util.{EnumChatFormatting, ChatComponentText, IChatComponent}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandTps extends ScalaCommand {

  final val timeFormatter = new DecimalFormat("########0.000")

  val name = "tps"
  val usage = "/tps - Shows the current tps of worlds on the server"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val server = MinecraftServer.getServer
    var dim = 0
    var summary = true
    if(args.length > 1){
      dim = CommandBase.parseInt(sender, args(1))
      summary = true
    }
    if(summary){
      val meanTime = mean(server.tickTimeArray) * 1.0E-6D
      val meanTps = Math.min(1000.0 / meanTime, 20)
      sender.addChatMessage(this.getComponent("Overall", meanTime, meanTps))
      for(dim <- DimensionManager.getIDs){
        val worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D
        val worldTPS = Math.min(1000.0 / worldTickTime, 20)
        sender.addChatMessage(this.getComponent("Dim " + dim, worldTickTime, worldTPS))
      }
    }else{
      val worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D
      val worldTPS = Math.min(1000.0 / worldTickTime, 20)
      sender.addChatMessage(this.getComponent("Dim " + dim, worldTickTime, worldTPS))
    }
  }

  private def getComponent(prefix: String, tickTime: Double, tps: Double): IChatComponent = {
    val ret = new ChatComponentText(prefix + ": ")
    var com1 = new ChatComponentText("TPS: " + timeFormatter.format(tps))
    if(tps != 20) com1.getChatStyle.setColor(EnumChatFormatting.RED)
    ret.appendSibling(com1)
    com1 = new ChatComponentText(" Tick Time: " + timeFormatter.format(tickTime) + "ms")
    if(tickTime > 45) com1.getChatStyle.setColor(EnumChatFormatting.RED)
    else if(tickTime > 35) com1.getChatStyle.setColor(EnumChatFormatting.GOLD)
    ret.appendSibling(com1)
    val percent = (tps / 20) * 100
    com1 = new ChatComponentText(" (" + timeFormatter.format(percent) + "%)")
    ret.appendSibling(com1)
    ret
  }

  private def mean(values: Array[Long]) = {
    var sum = 0L
    for(v <- values) sum += v
    sum / values.length
  }
}
