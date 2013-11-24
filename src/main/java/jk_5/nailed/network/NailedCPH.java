package jk_5.nailed.network;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;
import jk_5.nailed.client.render.NotificationRenderer;
import jk_5.nailed.client.render.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedCPH implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, NetClientHandler netClientHandler, Minecraft minecraft) {
        switch (Packets.fromID(packet.getType())){
            case NOTIFICATION:
                int mode = packet.readByte();
                if(mode == 0){
                    NotificationRenderer.addNotification(packet.readString());
                }else if(mode == 1){
                    NotificationRenderer.addNotification(packet.readString(), null, packet.readInt());
                }else if(mode == 2){
                    NotificationRenderer.addNotification(packet.readString(), new ResourceLocation(packet.readString(), packet.readString()), packet.readInt());
                }
                break;
            case TIME_UPDATE:
                RenderEventHandler.format = packet.readString();
                break;
            default: break;
        }
    }
}
