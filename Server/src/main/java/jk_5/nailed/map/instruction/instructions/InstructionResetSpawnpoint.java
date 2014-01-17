package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.players.Team;
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
        if(team != null) team.setSpawnPoint(null);
    }
}
