package jk_5.nailed.server.command;

import jk_5.nailed.map.teleport.TeleportHelper;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

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
            MapLoader.instance().newMapServerFor(MapLoader.instance().getMappack(type));
        }else if(args[0].equals("tp")){
            Map map = MapLoader.instance().getMap(Integer.parseInt(args[1]));
            TeleportHelper.travelEntity(sender.getEntityWorld(), (EntityPlayerMP) sender, map.getSpawnTeleport());
        }
    }
}
