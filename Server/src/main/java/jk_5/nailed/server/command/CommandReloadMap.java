package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMap extends NailedCommand {

    public CommandReloadMap(){
        super("reloadmap");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map currentMap, String[] args){
        if(args.length == 0) throw new WrongUsageException("/reloadmap <mapname>");
        Map map = null;
        for(Map m : NailedAPI.getMapLoader().getMaps()){
            if(args[0].equalsIgnoreCase(m.getSaveFileName())){
                map = m;
                break;
            }
        }
        if(map == null) throw new CommandException("Map does not exist");
        map.reloadFromMappack();

        IChatComponent component = new ChatComponentText("Reloaded map " + map.getSaveFileName());
        component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Go to the map")));
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        sender.addChatMessage(component);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args){
        if(args.length != 1) return Arrays.asList();
        List<String> ret = Lists.newArrayList();
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, ret);
    }
}
