package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.effect.FireworkRandomizer;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFirework extends NailedCommand {

    public CommandFirework(){
        super("firework");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        ChunkCoordinates coords = sender.getPlayerCoordinates();
        FireworkRandomizer.getRandomEffect().toFirework().spawnInWorld(map, coords.posX, coords.posY, coords.posZ);
    }
}
