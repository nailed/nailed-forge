package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.Spawnpoint;
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
public class InstructionSetSpawnpoint implements IInstruction {

    private Spawnpoint spawnpoint;
    private String target;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 4);
        this.spawnpoint = new Spawnpoint(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        this.target = data[0];
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetSpawnpoint(this.spawnpoint, this.target);
    }

    @Override
    public void execute(GameController controller) {
        if(this.target.equals("@a")){
            for(Player player : controller.getMap().getPlayers()){
                //TODO: player only spawnpoints
                //player.getEntity().setHealth(this.health);
            }
        }else{
            Team team = controller.getMap().getTeamManager().getTeam(this.target);
            if(team == null) return;
            team.setSpawnpoint(this.spawnpoint);
        }
    }
}
