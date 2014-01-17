package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.players.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionTeleportAll implements IInstruction {

    private int destination;

    @Override
    public void injectArguments(String args){
        this.destination = Integer.parseInt(args);
    }

    @Override
    public void execute(GameController controller){
        Map destinationMap = MapLoader.instance().getMap(this.destination);
        for(Player player : controller.getMap().getPlayers()){
            player.teleportToMap(destinationMap);
        }
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionTeleportAll(this.destination);
    }
}
