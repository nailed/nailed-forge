package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.Team;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionClearInventory implements IInstruction {

    private String team;

    @Override
    public void injectArguments(String args) {
        this.team = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionClearInventory(this.team);
    }

    @Override
    public void execute(GameController controller) {
        Team team = controller.getMap().getTeamManager().getTeam(this.team);
        for(Player player : team.getMembers()){
            EntityPlayer ent = player.getEntity();
            ent.inventory.func_146027_a(null, -1);
            ent.inventoryContainer.detectAndSendChanges();
            //if(!ent.capabilities.isCreativeMode) ent.updateHeldItem();
        }
    }
}
