package jk_5.nailed.util

import scala.util.Random
import net.minecraft.util.ChatComponentText
import net.minecraft.network.ServerStatusResponse
import net.minecraft.server.MinecraftServer
import net.minecraft.network.status.server.S00PacketServerInfo
import io.netty.channel.Channel

/**
 * No description given
 *
 * @author jk-5
 */
object MotdManager {

  final val random = new Random
  final val messages = IndexedSeq(
    "Well, that escalated quickly!",
    "Let\'s go!",
    "Oh well...",
    "Hello world!",
    "That\'s me!",
    "Oh god why!?",
    "Oh i hate the teams!",
    "FUCK THIS SHIT!",
    "I hate you!",
    "Kill them all!",
    "Blow it up!",
    "Fix yo laggz bro!",
    "Where\'s the enderpearl?",
    "It\'s opensource!",
    "Gimme starfall!",
    ChatColor.MAGIC + "FUNKY SHIT!",
    "Now 99% bug-free!",
    "Using netty!",
    "Booo!",
    "1.7.2 now!"
  )

  def motdComponent = {
    val message = messages(random.nextInt(messages.size))
    new ChatComponentText(ChatColor.AQUA + "Nailed " + ChatColor.GOLD + "| " + ChatColor.WHITE + "Quakecraft is up and running again!\n" + ChatColor.GRAY + message)
  }

  def sendMotd(channel: Channel, host: String){
    val response = new ServerStatusResponse
    response.func_151319_a(MinecraftServer.getServer.func_147134_at.func_151318_b)
    response.func_151321_a(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("Nailed-1.7.2", protocolVersion))
    response.func_151315_a(motdComponent)
    response.func_151320_a(MinecraftServer.getServer.func_147134_at.func_151316_d)
    channel.writeAndFlush(new S00PacketServerInfo(response))
  }

  @inline private def protocolVersion = MinecraftServer.getServer.func_147134_at.func_151322_c.func_151304_b
}
