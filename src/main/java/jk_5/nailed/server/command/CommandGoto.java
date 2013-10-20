package jk_5.nailed.server.command;

import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGoto extends CommandBase {

    @Override
    public String getCommandName(){
        return "map";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Go to another map";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args[0].equals("create")){
            String type = args[1];
            MapLoader.instance().getMappack(type).createMap();
        }else if(args[0].equals("tp")){
            MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) sender, Integer.parseInt(args[1]));
        }
    }
}
