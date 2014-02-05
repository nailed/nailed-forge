package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.Spawnpoint;
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
public class InstructionSetSpawnpoint implements IInstruction {

    private Spawnpoint spawnpoint;
    private String team;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 4);
        this.spawnpoint = new Spawnpoint(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        this.team = data[0];
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetSpawnpoint(this.spawnpoint, this.team);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team == null) return;
        team.setSpawnpoint(this.spawnpoint);
    }
}
