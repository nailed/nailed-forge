package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.event.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGamerule extends NailedCommand {

    public CommandGamerule() {
        super("gamerule");
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "commands.gamerule.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        GameRules gameRules = map.getWorld().getGameRules();
        if(args.length == 0){
            IChatComponent base = new ChatComponentText("");
            IChatComponent comp = new ChatComponentText("Available gamerules: ");
            comp.getChatStyle().setColor(EnumChatFormatting.GREEN);
            base.appendSibling(comp);
            boolean first = true;
            for(String rule : gameRules.getRules()){
                if(!first){
                    base.appendText(", ");
                }else{
                    first = false;
                }
                IChatComponent c = new ChatComponentText(rule);
                c.getChatStyle().setColor(EnumChatFormatting.RESET);
                IChatComponent tooltip = new ChatComponentText(rule + " = " + gameRules.getGameRuleStringValue(rule));
                c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
                base.appendSibling(c);
            }
            Utils.minifyChatComponent(base); //Stupid vanilla minecraft
            sender.addChatMessage(base);
        }else if(args.length == 1){
            if(gameRules.hasRule(args[0])){
                sender.addChatMessage(new ChatComponentText(args[0] + " = " + gameRules.getGameRuleStringValue(args[0])));
            }else{
                IChatComponent comp = new ChatComponentText("No gamerule called \"" + args[0] + "\" exists");
                comp.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(comp);
            }
        }else if(args.length == 2){
            if(gameRules.hasRule(args[0])){
                gameRules.setOrCreateGameRule(args[0], args[1]);
                sender.addChatMessage(new ChatComponentText("Gamerule " + args[0] + " changed to " + args[1]));
            }else{
                IChatComponent comp = new ChatComponentText("No gamerule called \"" + args[0] + "\" exists");
                comp.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(comp);
            }
        }else{
            throw new WrongUsageException("commands.gamerule.usage");
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        GameRules gameRules = sender.getEntityWorld().getGameRules();
        if(args.length == 1){
            return getOptions(args, gameRules.getRules());
        }else if(args.length == 2){
            return getOptions(args, "true", "false");
        }
        return null;
    }
}