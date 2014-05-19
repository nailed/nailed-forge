package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGoto extends NailedCommand {

    public CommandGoto(){
        super("goto");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        if(args.length == 0) throw new WrongUsageException("/goto <mapname>");
        Map dest = NailedAPI.getMapLoader().getMap(args[0]);
        if(dest == null) {
            try{
                dest = NailedAPI.getMapLoader().getMap(Integer.parseInt(args[0]));
            }catch(NumberFormatException e){
                throw new CommandException("That map does not exist");
            }
        }
        if(dest == null) throw new CommandException("That map does not exist");
        sender.teleportToMap(dest);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings){
        if(strings.length != 1) return null;
        List<String> ret = Lists.newArrayList();
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            ret.add(map.getSaveFileName());
        }
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(strings, ret);
    }
}
