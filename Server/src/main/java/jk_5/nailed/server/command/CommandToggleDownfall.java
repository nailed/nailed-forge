package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandToggleDownfall extends NailedCommand {

    public CommandToggleDownfall() {
        super("toggledownfall");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            if(map instanceof NailedMap){
                ((NailedMap) map).markDataNeedsResync();
            }
            map.getWeatherController().toggleRain();
        }else if(args.length == 1){
            map = NailedAPI.getMapLoader().getMap(args[0]);
            if(map == null){
                try{
                    map = NailedAPI.getMapLoader().getMap(Integer.parseInt(args[0]));
                }catch(NumberFormatException e){
                    throw new CommandException("That map does not exist");
                }
            }
            if(map == null){
                throw new CommandException("That map does not exist");
            }
            if(map instanceof NailedMap){
                ((NailedMap) map).markDataNeedsResync();
            }
            map.getWeatherController().toggleRain();
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length != 1){
            return Arrays.asList();
        }
        List<String> ret = Lists.newArrayList();
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return getOptions(args, ret);
    }
}
