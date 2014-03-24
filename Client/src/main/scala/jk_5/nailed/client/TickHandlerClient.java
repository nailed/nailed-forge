package jk_5.nailed.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.client.blocks.NailedBlocks;
import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.config.ConfigFile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
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
    private static long totalTicks = 0;

    private final int sendFpsInterval;

    public TickHandlerClient(ConfigFile config){
        this.sendFpsInterval = config.getTag("sendFpsInterval").setComment("For tracking purposes the client sends its fps to the server. By default this is every 40 ticks (2 seconds). Set to -1 to disable").getIntValue(40);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;
        if(player != null && world != null) {
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
            int z = MathHelper.floor_double(player.posZ);
            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            if(block == NailedBlocks.stat && meta == 2){
                if(player.movementInput.jump && !wasJumping){
                    ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(x, y, z, (byte) 0));
                }
                if (player.movementInput.sneak && !wasSneaking) {
                    ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(x, y, z, (byte) 1));
                }
            }
            wasJumping = player.movementInput.jump;
            wasSneaking = player.movementInput.sneak;
        }
        if(this.sendFpsInterval > 0){
            if(this.ellapsedTicks >= this.sendFpsInterval){
                ClientNetworkHandler.sendPacketToServer(new NailedPacket.FPSSummary(Minecraft.debugFPS));
                this.ellapsedTicks = -1;
            }
            this.ellapsedTicks++;
        }
        totalTicks++;
    }

    public static boolean blinkOn(){
        return totalTicks / 6 % 2 == 0;
    }
}
