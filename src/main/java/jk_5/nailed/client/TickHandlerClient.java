package jk_5.nailed.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.blocks.tileentity.NailedTileEntity;
import jk_5.nailed.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class TickHandlerClient {

    private boolean wasJumping = false;
    private boolean wasSneaking = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            NailedTileEntity target = this.getTileEntityUnderPlayer(player);
            if (target != null) {
                if (player.movementInput.jump && !wasJumping) Packets.MOVEMENT_EVENT.newPacket().writeCoord(target.xCoord, target.yCoord, target.zCoord).writeByte(0).sendToServer();
                if (player.movementInput.sneak && !wasSneaking) Packets.MOVEMENT_EVENT.newPacket().writeCoord(target.xCoord, target.yCoord, target.zCoord).writeByte(1).sendToServer();
            }
            wasJumping = player.movementInput.jump;
            wasSneaking = player.movementInput.sneak;
        }
    }

    public NailedTileEntity getTileEntityUnderPlayer(EntityPlayer player){
        World world = Minecraft.getMinecraft().theWorld;
        if (world != null && player != null) {
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
            int z = MathHelper.floor_double(player.posZ);
            TileEntity te = world.func_147438_o(x, y, z);
            if (te instanceof NailedTileEntity) return (NailedTileEntity) te;
        }
        return null;
    }
}
