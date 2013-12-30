package jk_5.nailed.util.invsee;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

/**
 * No description given
 *
 * @author jk-5
 */
public class InventoryOtherPlayer extends InventoryBasic {

    @Getter private EntityPlayerMP owner;
    private EntityPlayerMP viewer;
    private boolean allowUpdate;

    public InventoryOtherPlayer(EntityPlayerMP owner, EntityPlayerMP viewer){
        super(owner.getCommandSenderName() + "\'s inventory", false, owner.inventory.mainInventory.length);
        this.owner = owner;
        this.viewer = viewer;
    }

    @Override
    public void openChest(){
        InvSeeTicker.register(this);
        this.allowUpdate = false;
        for (int id = 0; id < this.owner.inventory.mainInventory.length; ++id){
            this.setInventorySlotContents(id, this.owner.inventory.mainInventory[id]);
        }
        allowUpdate = true;
        super.openChest();
    }

    @Override
    public void closeChest(){
        InvSeeTicker.unregister(this);
        if(this.allowUpdate){
            for (int id = 0; id < this.owner.inventory.mainInventory.length; ++id){
                this.owner.inventory.mainInventory[id] = getStackInSlot(id);
            }
        }
        this.onInventoryChanged();
        super.closeChest();
    }

    @Override
    public void onInventoryChanged(){
        super.onInventoryChanged();
        if(this.allowUpdate){
            for (int id = 0; id < this.owner.inventory.mainInventory.length; ++id){
                this.owner.inventory.mainInventory[id] = this.getStackInSlot(id);
            }
        }
    }

    public void update(){
        this.allowUpdate = false;
        for (int id = 0; id < this.owner.inventory.mainInventory.length; ++id){
            setInventorySlotContents(id, this.owner.inventory.mainInventory[id]);
        }
        this.allowUpdate = true;
        this.onInventoryChanged();
    }
}
