package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.team.Team;
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
public class InstructionResetExperience implements IInstruction {

    private String target;

    @Override
    public void injectArguments(String args) {
        this.target = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionResetExperience(this.target);
    }

    @Override
    public void execute(GameController controller) {
        if(this.target.equals("@a")){
            for(Player player : controller.getMap().getPlayers()){
                player.getEntity().addExperienceLevel(-1000000);
            }
        }else{
            Team team = controller.getMap().getTeamManager().getTeam(this.target);
            if(team != null){
                for(Player player : team.getMembers()){
                    player.getEntity().addExperienceLevel(-1000000);
                }
            }
        }
    }
}
