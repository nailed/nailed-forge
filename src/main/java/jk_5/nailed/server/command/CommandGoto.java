package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.NailedTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGoto extends CommandBase {

    @Override
    public String getCommandName(){
        return "goto";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "/goto <mapname> - Go to another map";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args.length == 0) throw new WrongUsageException("/goto <mapname>");
        Map map = MapLoader.instance().getMapFromName(args[0]);
        if(map == null) {
            try{
                map = MapLoader.instance().getMap(Integer.parseInt(args[0]));
            }catch(NumberFormatException e){
                throw new CommandException("That map does not exist");
            }
        }
        if(map == null) throw new CommandException("That map does not exist");
        MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) sender, map.getID(), new NailedTeleporter(map));
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

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
