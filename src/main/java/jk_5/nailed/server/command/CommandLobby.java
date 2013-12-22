package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.NailedTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandLobby extends CommandBase {

    @Override
    public String getCommandName(){
        return "lobby";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/lobby - Teleports you to the lobby";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        Map map = MapLoader.instance().getMap(0);
        MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) sender, map.getID(), new NailedTeleporter(map));
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
