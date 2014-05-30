package jk_5.nailed.server.command;

import java.util.*;
import javax.annotation.*;

import com.google.common.collect.*;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;

import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandEffect extends NailedCommand {

    private final ImmutableMap<String, Potion> names = ImmutableMap.<String, Potion>builder()
            .put("speed", Potion.moveSpeed)
            .put("slowness", Potion.moveSlowdown)
            .put("haste", Potion.digSpeed)
            .put("miningfatigue", Potion.digSlowdown)
            .put("strength", Potion.damageBoost)
            .put("instanthealth", Potion.heal)
            .put("harming", Potion.harm)
            .put("jumpboost", Potion.jump)
            .put("confusion", Potion.confusion)
            .put("regeneration", Potion.regeneration)
            .put("resistance", Potion.resistance)
            .put("fireresistance", Potion.fireResistance)
            .put("waterbreathing", Potion.waterBreathing)
            .put("invisibility", Potion.invisibility)
            .put("blindness", Potion.blindness)
            .put("nightvision", Potion.nightVision)
            .put("hunger", Potion.hunger)
            .put("weakness", Potion.weakness)
            .put("poison", Potion.poison)
            .put("wither", Potion.wither)
            .put("healthboost", Potion.field_76434_w)
            .put("absorption", Potion.field_76444_x)
            .put("saturation", Potion.field_76443_y)
    .build();

    public CommandEffect() {
        super("effect");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        EntityPlayerMP[] targets = NailedCommand.getPlayersList(sender, args[0]);
        if(args[1].equalsIgnoreCase("clear")){
            for(EntityPlayerMP player : targets){
                player.clearActivePotions();
            }
            IChatComponent response = new ChatComponentText("Cleared all potion effects of " + targets.length + " player" + (targets.length == 1 ? "" : "s"));
            response.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(response);
        }else{
            Potion effect = names.get(args[1].toLowerCase());
            if(effect == null){
                effect = Potion.potionTypes[CommandBase.parseIntWithMin(sender, args[1], 1)];
            }
            if(effect == null){
                throw new CommandException("Unknown potion effect '%s'", args[1]);
            }
            int length;
            if(args.length > 2){
                if(args[2].equalsIgnoreCase("infinite")){
                    length = 1000000;
                }else{
                    length = CommandBase.parseIntBounded(sender, args[2], 0, 1000000);
                    if(!effect.isInstant()){
                        length *= 20;
                    }
                }
            }else{
                length = 1;
            }
            int level = 0;
            if(args.length > 3){
                level = CommandBase.parseIntBounded(sender, args[3], 0, 255);
            }

            if(length == 0){
                for(EntityPlayerMP player : targets){
                    player.removePotionEffect(effect.getId());
                }
                IChatComponent response = new ChatComponentText("Removed " + effect.getName() + " from " + targets.length + " player" + (targets.length == 1 ? "" : "s"));
                response.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatMessage(response);
            }else{
                for(EntityPlayerMP player : targets){
                    PotionEffect e = new PotionEffect(effect.getId(), length, level);
                    player.addPotionEffect(e);
                }
                IChatComponent response = new ChatComponentText("Added " + effect.getName() + " (Level " + (level + 1) + ") to " + targets.length + " player" + (targets.length == 1 ? "" : "s"));
                response.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatMessage(response);
            }
        }
    }

    @Nullable
    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getUsernameOptions(args);
        }else if(args.length == 2){
            return getOptions(args, ImmutableList.<String>builder().add("clear").addAll(names.keySet()).build());
        }else if(args.length == 3){
            return getOptions(args, "infinite");
        }
        return null;
    }
}
