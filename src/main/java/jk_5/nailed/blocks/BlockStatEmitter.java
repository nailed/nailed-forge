package jk_5.nailed.blocks;

import jk_5.nailed.NailedModContainer;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.gui.EnumGui;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockStatEmitter extends NailedBlock implements ITileEntityProvider {

    public BlockStatEmitter(){
        super("statEmitter", Material.circuits);
        this.isBlockContainer = true;
    }

    public TileEntity createNewTileEntity(World world){
        return new TileEntityStatEmitter();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ) {
        Player player = PlayerRegistry.instance().getPlayer(entity.username);
        if(player != null && player.isOp()){
            entity.openGui(NailedModContainer.getInstance(), EnumGui.STATEMITTER.getGuiID(), world, x, y, z);
        }else{
            entity.addChatMessage("You need to be an OP to do that");
        }
        return true;
    }
}
