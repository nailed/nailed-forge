package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.mappack.Spawnpoint;
import jk_5.nailed.players.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionSpreadPlayersToRandomSpawns implements IInstruction {

    @Override
    public void injectArguments(String args){

    }

    @Override
    public void execute(GameController controller){
        for(Player player : controller.getMap().getPlayers()){
            Spawnpoint spawn = controller.getMap().getRandomSpawnpoint();
            player.getEntity().setLocationAndAngles(spawn.posX + 0.5, spawn.posY, spawn.posZ + 0.5, spawn.yaw, spawn.pitch);
        }
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionSpreadPlayersToRandomSpawns();
    }
}
