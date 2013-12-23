package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Player;
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
public class CommandGoto extends NailedCommand {

    @Override
    public String getCommandName(){
        return "goto";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        if(args.length == 0) throw new WrongUsageException("/goto <mapname>");
        Map dest = MapLoader.instance().getMapFromName(args[0]);
        if(dest == null) {
            try{
                dest = MapLoader.instance().getMap(Integer.parseInt(args[0]));
            }catch(NumberFormatException e){
                throw new CommandException("That map does not exist");
            }
        }
        if(dest == null) throw new CommandException("That map does not exist");
        sender.teleportToMap(map);
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
