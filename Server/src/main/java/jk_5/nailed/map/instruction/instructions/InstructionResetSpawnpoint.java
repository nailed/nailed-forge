package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.team.Team;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionResetSpawnpoint implements IInstruction {

    private String team;

    @Override
    public void injectArguments(String args) {
        this.team = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionResetSpawnpoint(this.team);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team != null) team.setSpawnpoint(null);
    }
}
