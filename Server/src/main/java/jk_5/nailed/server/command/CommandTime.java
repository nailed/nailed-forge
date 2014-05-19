package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTime extends NailedCommand {

    public CommandTime(){
        super("time");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        boolean hasWorld = map.getWorld() != null;
        if(args.length > 0){
            if(args[0].equals("set")){
                if(args.length > 1){
                    int target;
                    if(args[1].equals("day")) target = 6000;
                    else if(args[1].equals("night")) target = 18000;
                    else target = CommandBase.parseIntBounded(sender, args[1], 0, 23999);
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
