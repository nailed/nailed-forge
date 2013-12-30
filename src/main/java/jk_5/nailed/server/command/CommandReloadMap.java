package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMap extends NailedCommand {

    @Override
    public String getCommandName(){
        return "reloadmap";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map currentMap, String[] args){
        if(args.length == 0) throw new WrongUsageException("/reloadmap <mapname>");
        Map map = null;
        for(Map m : MapLoader.instance().getMaps()){
            if(args[0].equalsIgnoreCase(m.getSaveFileName())){
                map = m;
                break;
            }
        }
        if(map == null) throw new CommandException("Map does not exist");
        map.reloadFromMappack();
        //FIXME
        //sender.func_145747_a(new ChatComponentText("Reloaded map " + map.getSaveFileName()).setColor(EnumChatFormatting.GREEN));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args){
        if(args.length != 1) return Arrays.asList();
        List<String> ret = Lists.newArrayList();
        for(Map map : MapLoader.instance().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return getListOfStringsFromIterableMatchingLastWord(args, ret);
    }
}
