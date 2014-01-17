package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.players.Team;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionSetSpawnpoint implements IInstruction {

    private ChunkCoordinates coordinates;
    private String team;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 4);
        this.coordinates = new ChunkCoordinates(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        this.team = data[0];
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetSpawnpoint(this.coordinates, this.team);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team == null) return;
        team.setSpawnPoint(this.coordinates);
    }
}
