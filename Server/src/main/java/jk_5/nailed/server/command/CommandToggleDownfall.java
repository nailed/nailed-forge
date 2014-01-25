package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandToggleDownfall extends NailedCommand {

    @Override
    public String getCommandName(){
        return "toggledownfall";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public void process(ICommandSender sender, String[] args){
        Map map = null;
        if(args.length > 0){
            map = MapLoader.instance().getMapFromName(args[0]);
            if(map == null) {
                try{
                    map = MapLoader.instance().getMap(Integer.parseInt(args[0]));
                }catch(NumberFormatException e){
                    throw new CommandException("That map does not exist");
                }
            }
        }
        if(map == null){
            map = MapLoader.instance().getMap(sender.getEntityWorld());
        }
        if(map == null){
            throw new CommandException("Unknown map. Use /toggledownfall <mapname/mapid>");
        }
        map.markDataNeedsResync();
        map.getWeatherController().toggleRain();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings){
        if(strings.length != 1) return Arrays.asList();
        List<String> ret = Lists.newArrayList();
        for(Map map : MapLoader.instance().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return getListOfStringsFromIterableMatchingLastWord(strings, ret);
    }
}
