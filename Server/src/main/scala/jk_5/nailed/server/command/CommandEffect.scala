package jk_5.nailed.server.command

import scala.collection.{mutable, immutable}
import net.minecraft.potion.{PotionEffect, Potion}
import net.minecraft.command.{CommandBase, ICommandSender}
import jk_5.nailed.api.map.Map
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import java.util.Locale

/**
 * No description given
 *
 * @author jk-5
 */
object CommandEffect extends ScalaCommand {

  override val name = "effect"
  override val usage = "/effect <player> <clear/potionName> [duration] - Adds a potion effect to a player"

  final val names = immutable.HashMap(
    "speed" -> Potion.moveSpeed,
    "slowness" -> Potion.moveSlowdown,
    "haste" -> Potion.digSpeed,
    "miningfatigue" -> Potion.digSlowdown,
    "strength" -> Potion.damageBoost,
    "instanthealth" -> Potion.heal,
    "harming" -> Potion.harm,
    "jumpboost" -> Potion.jump,
    "confusion" -> Potion.confusion,
    "regeneration" -> Potion.regeneration,
    "resistance" -> Potion.resistance,
    "fireresistance" -> Potion.fireResistance,
    "waterbreathing" -> Potion.waterBreathing,
    "invisibility" -> Potion.invisibility,
    "blindness" -> Potion.blindness,
    "nightvision" -> Potion.nightVision,
    "hunger" -> Potion.hunger,
    "weakness" -> Potion.weakness,
    "poison" -> Potion.poison,
    "wither" -> Potion.wither,
    "healthboost" -> Potion.field_76434_w,
    "absorption" -> Potion.field_76444_x,
    "saturation" -> Potion.field_76443_y
  )

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val targets = getPlayersList(sender, args(0))
    if(args(1).equalsIgnoreCase("clear")){
      targets.foreach(_.clearActivePotions())

      val msg = new ChatComponentText(s"Cleared all potion effects of ${targets.length} player" + (if(targets.length == 1) "" else "s"))
      msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
      sender.addChatMessage(msg)
    }else{
      var effect = names.get(args(1).toLowerCase(Locale.US))
      effect = Option(effect.getOrElse(Potion.potionTypes(CommandBase.parseIntWithMin(sender, args(1), 1))))
      if(effect.isEmpty) throw new CommandException("Unknown potion effect '%s'", args(1) toString)

      var length = 1
      if(args.length > 2){
        if(args(2).equalsIgnoreCase("infinite")){
          length = 1000000
        }else{
          length = CommandBase.parseIntBounded(sender, args(2), 0, 1000000)
          if(!effect.get.isInstant) length *= 20
        }
      }

      val level = if(args.length > 3) CommandBase.parseIntBounded(sender, args(3), 0, 255) else 0
      if(length == 0){
        targets.foreach(_.removePotionEffect(effect.get.getId))

        val msg = new ChatComponentText(s"Removed ${effect.get.getName} from ${targets.length} player" + (if(targets.length == 1) "" else "s"))
        msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
        sender.addChatMessage(msg)
      }else{
        targets.foreach(_.addPotionEffect(new PotionEffect(effect.get.getId, length, level)))

        val msg = new ChatComponentText(s"Added ${effect.get.getName} (Level ${level + 1}) to ${targets.length} player" + (if(targets.length == 1) "" else "s"))
        msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
        sender.addChatMessage(msg)
      }
    }
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] = args.length match {
    case 1 => getUsernameOptions(args)
    case 2 =>
      val opts = mutable.ArrayBuffer[String]("clear")
      opts ++= names.keys
      getOptions(args, opts)
    case 3 => getOptions(args, "infinite")
    case _ => null
  }
}
