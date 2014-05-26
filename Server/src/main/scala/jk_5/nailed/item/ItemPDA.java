package jk_5.nailed.item;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ItemPDA extends Item {

    public ItemPDA() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setUnlocalizedName("nailed.pda");
    }

    public ServerMachine getMachine(Player player) {
        int instId = 666;
        World world = player.getCurrentMap().getWorld();
        ServerMachine machine = ServerMachine.REGISTRY.get(instId);
        if(machine == null){
            int id = MachineRegistry.getNextId();
            player.setPdaID(id);
            machine = new ServerMachine(world, id, instId, Terminal.PDAWIDTH, Terminal.PDAHEIGHT);
            ServerMachine.REGISTRY.add(id, machine);
        }
        if(machine.getWorld() != world){
            machine.setWorld(world);
        }
        return machine;
    }

    @Override
    public void onUpdate(ItemStack item, World world, Entity entity, int slot, boolean selected) {
        if(!world.isRemote && entity instanceof EntityPlayer){
            this.getMachine(NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) entity)).update();
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ServerMachine machine = this.getMachine(NailedAPI.getPlayerRegistry().getPlayer(player));
        machine.turnOn();
        machine.terminalChanged = true;
        return stack;
    }
}
