package jk_5.nailed.server.command;

import jk_5.nailed.effect.fireworks.FireworkRed;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFirework extends CommandBase {

    @Override
    public String getCommandName(){
        return "firework";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        ChunkCoordinates coords = sender.getPlayerCoordinates();
        sender.getEntityWorld().spawnEntityInWorld(new EntityFireworkRocket(sender.getEntityWorld(), coords.posX, coords.posY, coords.posZ, FireworkRed.getItemStack(0x00FF00)));
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
