package jk_5.nailed.blocks;

import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.gui.EnumGui;
import jk_5.nailed.network.Packets;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
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
        this.setBlockUnbreakable();
    }

    public TileEntity createNewTileEntity(World world){
        return new TileEntityStatEmitter();
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) return 0;
        return ((TileEntityStatEmitter) tile).isSignalEnabled() ? 15 : 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) return true;
        Player player = PlayerRegistry.instance().getPlayer(entity.username);
        if(player != null && player.isOp()){
            PacketDispatcher.sendPacketToPlayer(world.getBlockTileEntity(x, y, z).getDescriptionPacket(), (cpw.mods.fml.common.network.Player) entity);
            Packets.OPEN_GUI.newPacket().writeInt(EnumGui.STATEMITTER.getGuiID()).writeCoord(x, y, z).sendToPlayer(entity);
        }else{
            entity.addChatMessage("You need to be an OP to do that");
        }
        return true;
    }
}
