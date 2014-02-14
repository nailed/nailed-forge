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

    private String target;
    private WorldSettings.GameType gamemode;

    @Override
    public void injectArguments(String args) {
        String[] data = args.split(" ", 2);
        this.target = data[0];
        this.gamemode = WorldSettings.GameType.getByID(Integer.parseInt(data[1]));
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetGamemode(this.target, this.gamemode);
    }

    @Override
    public void execute(GameController controller) {
        if(this.target.equals("@a")){
            for(Player player : controller.getMap().getPlayers()){
                player.getEntity().setGameType(this.gamemode);
            }
        }else{
            Team team = controller.getMap().getTeamManager().getTeam(this.target);
            if(team == null) return;
            for(Player player : team.getMembers()){
                player.getEntity().setGameType(this.gamemode);
            }
        }
    }
}
