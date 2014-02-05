package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
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
        Map destinationMap = NailedAPI.getMapLoader().getMap(this.destination);
        for(Player player : controller.getMap().getPlayers()){
            player.teleportToMap(destinationMap);
        }
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionTeleportAll(this.destination);
    }
}
