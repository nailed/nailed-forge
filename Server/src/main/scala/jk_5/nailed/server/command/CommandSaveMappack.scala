package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.{ChatComponentText, EnumChatFormatting}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSaveMappack extends ScalaCommand {

  val name = "savemappack"
  val usage = "/savemappack - Saves the current map to this mappack"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) = Option(map.getMappack) match {
    case Some(m) =>
      if(m.saveAsMappack(map)){
        val comp = new ChatComponentText("Saved to mappack " + map.getMappack.getMappackMetadata.getName)
        comp.getChatStyle.setColor(EnumChatFormatting.GREEN)
        sender.addChatMessage(comp)
      }else{
        val comp = new ChatComponentText("Operation not supported for this mappack")
        comp.getChatStyle.setColor(EnumChatFormatting.RED)
        sender.addChatMessage(comp)
      }
    case None => throw new CommandException("This map does not have a mappack. It can\'t be saved (we plan on adding support for this)")
  } //TODO: prompt the user for a name and save it as a new mappack on the second case
}
