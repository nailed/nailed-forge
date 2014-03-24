package jk_5.nailed.client.item;

import jk_5.nailed.client.NailedClient;
import jk_5.nailed.client.gui.GuiTerminal;
import jk_5.nailed.client.scripting.ClientMachine;
import jk_5.nailed.map.script.Terminal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class ItemPDA extends Item {

    public ItemPDA(){
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setUnlocalizedName("nailed.pda");
        this.setCreativeTab(NailedClient.getCreativeTab());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerminal(this.getMachine(), Terminal.PDAWIDTH, Terminal.PDAHEIGHT));
        return par1ItemStack;
    }

    public ClientMachine getMachine(){
        int instId = 666;
        ClientMachine computer = NailedClient.getMachines().get(instId);
        if(computer == null){
            computer = new ClientMachine(instId);
            NailedClient.getMachines().add(instId, computer);
        }
        return computer;
    }
}
