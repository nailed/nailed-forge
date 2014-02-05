package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.world.WorldSettings;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionSetGamemode implements IInstruction {

    private String team;
    private WorldSettings.GameType gamemode;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 2);
        this.team = data[0];
        this.gamemode = WorldSettings.GameType.getByID(Integer.parseInt(data[1]));
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetGamemode(this.team, this.gamemode);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        if(team == null) return;
        for(Player player : team.getMembers()){
            player.getEntity().setGameType(this.gamemode);
        }
    }
}
