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

    private String team;

    @Override
    public void injectArguments(String args) {
        this.team = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionResetExperience(this.team);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team != null){
            for(Player player : team.getMembers()){
                player.getEntity().addExperienceLevel(-1000000);
            }
        }
    }
}
