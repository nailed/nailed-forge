package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.players.Player;
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
public class InstructionSetHealth implements IInstruction {

    private String team;
    private int health;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 2);
        this.team = data[0];
        this.health = Integer.parseInt(data[1]);
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetHealth(this.team, this.health);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team == null) return;
        for(Player player : team.getMembers()){
            player.getEntity().setHealth(this.health);
        }
    }
}