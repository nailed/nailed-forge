package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTime extends NailedCommand {

    public CommandTime() {
        super("time");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        boolean hasWorld = map.getWorld() != null;
        if(args.length > 0){
            if("set".equals(args[0])){
                if(args.length > 1){
                    int target;
                    if("day".equals(args[1])){
                        target = 6000;
                    }else if("night".equals(args[1])){
                        target = 18000;
                    }else{
                        target = CommandBase.parseIntBounded(sender, args[1], 0, 23999);
                    }
                    if(!hasWorld && args.length == 3){
                        map = NailedAPI.getMapLoader().getMap(CommandBase.parseInt(sender, args[2]));
                    }
                    if(map != null){
                        map.getWorld().setWorldTime(target);
                    }
                }
            }else{
                try{
                    int number = CommandBase.parseInt(sender, args[0]);
                    map = NailedAPI.getMapLoader().getMap(number);
                    if(map != null){
                        IChatComponent component = new ChatComponentText("Current time in " + map.getSaveFileName() + ": " + map.getWorld().getWorldTime());
                        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                        sender.addChatMessage(component);
                    }
                }catch(NumberInvalidException e){
                    //NOOP
                }
            }
        }else if(hasWorld){
            IChatComponent component = new ChatComponentText("Current time in " + map.getSaveFileName() + ": " + map.getWorld().getWorldTime());
            component.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(component);
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "set");
        }else if(args.length == 2){
            return getOptions(args, "day", "night");
        }else{
            return null;
        }
    }
}
