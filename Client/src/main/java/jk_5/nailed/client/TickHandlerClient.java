package jk_5.nailed.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.client.blocks.tileentity.NailedTileEntity;
import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.client.network.NailedPacket;
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
    private int ellapsedTicks = 0;

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            NailedTileEntity target = this.getTileEntityUnderPlayer(player);
            if (target != null) {
                if (player.movementInput.jump && !wasJumping){
                    ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(target.field_145851_c, target.field_145848_d, target.field_145849_e, (byte) 0));
                }
                if (player.movementInput.sneak && !wasSneaking) {
                    ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(target.field_145851_c, target.field_145848_d, target.field_145849_e, (byte) 1));
                }
            }
            wasJumping = player.movementInput.jump;
            wasSneaking = player.movementInput.sneak;
        }
        if(this.ellapsedTicks >= 40){
            ClientNetworkHandler.sendPacketToServer(new NailedPacket.FPSSummary(Minecraft.debugFPS));
            this.ellapsedTicks = -1;
        }
        this.ellapsedTicks++;
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
