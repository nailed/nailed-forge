package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.effect.fireworks.Firework;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFirework extends NailedCommand {

    @Override
    public String getCommandName(){
        return "firework";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        ChunkCoordinates coords = sender.getPlayerCoordinates();
        map.getWorld().spawnEntityInWorld(new EntityFireworkRocket(map.getWorld(), coords.posX, coords.posY, coords.posZ, Firework.getItemStack(0xFF0000)));
    }
}
