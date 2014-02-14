package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
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

    private String target;

    @Override
    public void injectArguments(String args) {
        this.target = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionClearInventory(this.target);
    }

    @Override
    public void execute(GameController controller) {
        if(this.target.equals("@a")){
            for(Player player : controller.getMap().getPlayers()){
                EntityPlayer ent = player.getEntity();
                ent.inventory.clearInventory(null, -1);
                ent.inventoryContainer.detectAndSendChanges();
                //if(!ent.capabilities.isCreativeMode) ent.updateHeldItem();
            }
        }else{
            Team team = controller.getMap().getTeamManager().getTeam(this.target);
            if(team == null) return;
            for(Player player : team.getMembers()){
                EntityPlayer ent = player.getEntity();
                ent.inventory.clearInventory(null, -1);
                ent.inventoryContainer.detectAndSendChanges();
                //if(!ent.capabilities.isCreativeMode) ent.updateHeldItem();
            }
        }
    }
}
