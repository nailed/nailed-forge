package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.world.EnumDifficulty
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandDifficulty extends ScalaCommand {

  override val name = "difficulty"
  override val usage = "/difficulty <peaceful/easy/normal/hard> - Sets the world difficulty"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length != 1) throw new WrongUsageException("/difficulty <peaceful/easy/normal/hard>")

    val s = args(0)
    val newDifficulty = if(s.equalsIgnoreCase("peaceful") || s.equalsIgnoreCase("p") || s.equalsIgnoreCase("0")){
      EnumDifficulty.PEACEFUL
    }else if(s.equalsIgnoreCase("easy") || s.equalsIgnoreCase("e") || s.equals("1")){
      EnumDifficulty.EASY
    }else if(s.equalsIgnoreCase("normal") || s.equalsIgnoreCase("n") || s.equals("2")){
      EnumDifficulty.NORMAL
    }else if(s.equalsIgnoreCase("hard") || s.equalsIgnoreCase("h") || s.equals("3")){
      EnumDifficulty.HARD
    }else throw new WrongUsageException("/difficulty <peaceful/easy/normal/hard>")

    map.getWorld.difficultySetting = newDifficulty
    map.getWorld.setAllowedSpawnTypes(newDifficulty != EnumDifficulty.PEACEFUL, true)

    val msg = new ChatComponentText("Successfully changed difficulty")
    msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
    sender.addChatMessage(msg)
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] = {
    if(args.length == 1) getOptions(args, "peaceful", "easy", "normal", "hard") else null
  }
}
