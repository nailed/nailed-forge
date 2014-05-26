package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGoto extends NailedCommand {

    public CommandGoto() {
        super("goto");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("/goto <mapname>");
        }
        Map dest = NailedAPI.getMapLoader().getMap(args[0]);
        if(dest == null){
            try{
                dest = NailedAPI.getMapLoader().getMap(Integer.parseInt(args[0]));
            }catch(NumberFormatException e){
                throw new CommandException("That map does not exist");
            }
        }
        if(dest == null){
            throw new CommandException("That map does not exist");
        }
        sender.teleportToMap(dest);
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length != 1){
            return null;
        }
        List<String> ret = Lists.newArrayList();
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return getOptions(args, ret);
    }
}
