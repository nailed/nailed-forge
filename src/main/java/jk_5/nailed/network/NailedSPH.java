package jk_5.nailed.network;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IServerPacketHandler;
import com.google.common.base.Preconditions;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.server.ProxyCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetServerHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.network.ForgePacket;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedSPH implements IServerPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, NetServerHandler netServerHandler, EntityPlayerMP entityPlayerMP) {
        switch (Packets.fromID(packet.getType())){
            case STATEMITTER_STAT:
                TileEntity tile = entityPlayerMP.worldObj.getBlockTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
                if(tile == null || !(tile instanceof TileEntityStatEmitter)) return;
                ((TileEntityStatEmitter) tile).readGuiData(packet);
            case PORTALCONTROLLER_DESTINATION:
                TileEntity tile1 = entityPlayerMP.worldObj.getBlockTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
                if(tile1 == null || !(tile1 instanceof TileEntityPortalController)) return;
                ((TileEntityPortalController) tile1).readGuiData(packet);
            default: break;
        }
    }

    public static void sendNotification(EntityPlayer player, String text){
        Preconditions.checkNotNull(player);
        Packets.NOTIFICATION.newPacket()
                .writeByte(0)
                .writeString(text)
                .sendToPlayer(player);
    }

    public static void sendNotification(EntityPlayer player, String text, int color){
        Preconditions.checkNotNull(player);
        Packets.NOTIFICATION.newPacket()
                .writeByte(1)
                .writeString(text)
                .writeInt(color)
                .sendToPlayer(player);
    }

    public static void sendNotification(EntityPlayer player, String text, int color, ResourceLocation image){
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(image);
        Packets.NOTIFICATION.newPacket()
                .writeByte(2)
                .writeString(text)
                .writeString(image.getResourceDomain())
                .writeString(image.getResourcePath())
                .writeInt(color)
                .sendToPlayer(player);
    }

    public static void broadcastNotification(String text){
        Packets.NOTIFICATION.newPacket()
                .writeByte(0)
                .writeString(text)
                .sendToClients();
    }

    public static void broadcastNotification(String text, int color){
        Packets.NOTIFICATION.newPacket()
                .writeByte(1)
                .writeString(text)
                .writeInt(color)
                .sendToClients();
    }

    public static void broadcastNotification(String text, int color, ResourceLocation image){
        Preconditions.checkNotNull(image);
        Packets.NOTIFICATION.newPacket()
                .writeByte(2)
                .writeString(text)
                .writeString(image.getResourceDomain())
                .writeString(image.getResourcePath())
                .writeInt(color)
                .sendToClients();
    }

    public static void sendRegisterDimension(EntityPlayer player, int dimension, int provider){
        PacketDispatcher.sendPacketToPlayer(ForgePacket.makePacketSet(new DimensionRegisterPacket(dimension, provider))[0], (Player) player);
    }

    public static void broadcastRegisterDimension(int dimension){
        PacketDispatcher.sendPacketToAllPlayers(ForgePacket.makePacketSet(new DimensionRegisterPacket(dimension, ProxyCommon.providerID))[0]);
    }

    public static void sendTimeUpdate(EntityPlayer player, String data){
        Packets.TIME_UPDATE.newPacket().writeString(data).sendToPlayer(player);
    }

    public static void broadcastTimeUpdate(String data){
        Packets.TIME_UPDATE.newPacket().writeString(data).sendToClients();
    }
}
