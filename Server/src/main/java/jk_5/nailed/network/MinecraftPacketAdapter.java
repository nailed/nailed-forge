package jk_5.nailed.network;

import com.jcraft.jogg.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.block.INailedBlock;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.sign.Sign;
import jk_5.nailed.api.map.sign.SignCommandHandler;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.blocks.NailedBlock;
import jk_5.nailed.network.packets.CustomBulkChunkPacket;
import jk_5.nailed.network.packets.CustomChunkPacket;
import jk_5.nailed.util.ChatColor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.concurrent.Semaphore;
import java.util.zip.Deflater;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftPacketAdapter extends ChannelDuplexHandler {

    private final EntityPlayerMP player;

    public MinecraftPacketAdapter(EntityPlayerMP player) {
        this.player = player;
    }

    /**
     * Adapt inbound CustomChunkPacket
     *
     * @param ctx ChannelHandlerContext
     * @param msg The inbound packet
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(msg instanceof C12PacketUpdateSign){
            C12PacketUpdateSign packet = (C12PacketUpdateSign) msg;
            NetworkManager manager = ctx.pipeline().get(NetworkManager.class);
            EntityPlayerMP player = ((NetHandlerPlayServer) manager.getNetHandler()).playerEntity;
            SignCommandHandler handler = NailedAPI.getMapLoader().getMap(player.worldObj).getSignCommandHandler();
            handler.onSignAdded(packet.field_149590_d, packet.field_149593_a, packet.field_149591_b, packet.field_149592_c);
        }
        ctx.fireChannelRead(msg);
    }

    /**
     * Adapt outbound CustomChunkPacket
     *
     * @param ctx ChannelHandlerContext
     * @param msg The outbound packet
     * @param promise The promise of the packet
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        NetworkManager manager = (NetworkManager) ctx.pipeline().get("packet_handler");
        NetHandlerPlayServer handler = (NetHandlerPlayServer) manager.getNetHandler();
        EntityPlayerMP player = handler.playerEntity;
        if(msg instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) msg;
            IChatComponent component = packet.field_148919_a;
            if(component instanceof ChatComponentTranslation){
                ChatComponentTranslation translation = (ChatComponentTranslation) component;
                String key = translation.getKey();
                if(key.startsWith("death.")){
                    String died = ChatColor.stripColor(((ChatComponentText) translation.getFormatArgs()[0]).getUnformattedTextForChat());
                    Player ply = NailedAPI.getPlayerRegistry().getPlayerByUsername(died);
                    if(ply == null){
                        ply = NailedAPI.getPlayerRegistry().getPlayerByUsername(died.substring(1));
                    }
                    EntityPlayerMP diedPlayer = ply.getEntity();
                    if(player.dimension == diedPlayer.dimension){
                        NailedLog.info("Sent death message for {} to {}", diedPlayer.getDisplayName().replace("%", "%%"), player.getDisplayName().replace("%", "%%"));
                    }else return;
                }
            }
        }else if(msg instanceof S33PacketUpdateSign){
            S33PacketUpdateSign signPacket = (S33PacketUpdateSign) msg;
            String[] lines = signPacket.field_149349_d;
            if(lines[0].equalsIgnoreCase("$mappack")){
                Map map = NailedAPI.getMapLoader().getMap(player.worldObj);
                Sign sign = map.getSignCommandHandler().getSign(signPacket.field_149352_a, signPacket.field_149350_b, signPacket.field_149351_c);
                if(sign == null){
                    ctx.write(msg, promise);
                    return;
                }
                ctx.write(sign.getUpdatePacket(), promise);
                return;
            }
        } else if(msg instanceof S38PacketPlayerListItem){
            S38PacketPlayerListItem playerList = (S38PacketPlayerListItem) msg;
            Player pPlayer = NailedAPI.getPlayerRegistry().getPlayerByUsername(playerList.field_149126_a);
            if (playerList.field_149124_b){
                Player nPlayer = NailedAPI.getPlayerRegistry().getPlayer(player);
                if(!nPlayer.getPlayersVisible().contains(pPlayer)){
                    return;
                } else {
                    msg = new S38PacketPlayerListItem(pPlayer.getChatPrefix(), playerList.field_149124_b, playerList.field_149125_c);
                    ctx.write(msg, promise);
                    return;
                }
            }
        } else if(msg instanceof CustomChunkPacket){
            Player nPlayer = NailedAPI.getPlayerRegistry().getPlayer(player);

            CustomChunkPacket ccPacket = ((CustomChunkPacket) msg);
            Chunk chunk = ccPacket.chunk;
            boolean groundUpCont = ccPacket.groundUpCont;
            ByteBuf buf = Unpooled.buffer();
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeVarIntToBuffer(38); // 38 = S26
            buffer.writeVarIntToBuffer(chunk.xPosition);
            buffer.writeVarIntToBuffer(chunk.zPosition);
            buffer.writeBoolean(groundUpCont);
            Extracted extracted = extractData(chunk, ccPacket.groundUpCont, ccPacket.i, nPlayer.isClient());
            buffer.writeShort(extracted.bitmask & 65535);
            buffer.writeShort(extracted.addBitmap & 65535);

            Semaphore deflateGate = new Semaphore(1);
            Deflater deflater = new Deflater(-1);
            byte[] deflated;
            int length = 0;
            try{
                deflater.setInput(extracted.aBlock, 0, extracted.aBlock.length);
                deflater.finish();
                deflated = new byte[extracted.aBlock.length];
                length = deflater.deflate(deflated);
            }
            finally{
                deflater.end();
            }
            buffer.writeInt(length);
            buffer.writeBytes(deflated, 0, length);

            ctx.write(buf, promise);
            return;
        } else if(msg instanceof CustomBulkChunkPacket) {
            ByteBuf buf = Unpooled.buffer();
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeVarIntToBuffer(33); //33 = S21. 38 = S26

            //buffer.writeIets, zoals je gewend bent en gebeurt in S21 en S26
        }
        ctx.write(msg, promise);
    }

    public Extracted extractData(Chunk chunk, boolean groundUpCont, int bitmap, boolean isClient) {
        int j = 0;
        ExtendedBlockStorage[] aExtendedBlockStorage = chunk.getBlockStorageArray();
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[aExtendedBlockStorage.length];
        System.arraycopy(aExtendedBlockStorage, 0, aextendedblockstorage, 0, aExtendedBlockStorage.length);
        int k = 0;
        MinecraftPacketAdapter.Extracted extracted = new MinecraftPacketAdapter.Extracted();
        byte[] abyte = new byte[196864];

        if (groundUpCont) {
            chunk.sendUpdates = true;
        }
        if (isClient){
            for (ExtendedBlockStorage extendedBlockStorage : aextendedblockstorage) {
                if (extendedBlockStorage != null) {
                    for (int x = 0; x < 16; ++x) {
                        for (int y = 0; y < 16; ++y) {
                            for (int z = 0; z < 16; ++z) {
                                Block block = extendedBlockStorage.getBlockByExtId(x, y, z);
                                if (block instanceof INailedBlock) {
                                    extendedBlockStorage.func_150818_a(x, y, z, ((INailedBlock) block).getReplacementBlock());
                                    extendedBlockStorage.setExtBlockMetadata(x, y, z, ((INailedBlock) block).getReplacementMetadata());
                                }
                            }
                        }
                    }
                }
            }
        }

        int l;

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0)
            {
                extracted.bitmask |= 1 << l;

                if (aextendedblockstorage[l].getBlockMSBArray() != null)
                {
                    extracted.addBitmap |= 1 << l;
                    ++k;
                }
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0)
            {
                byte[] abyte1 = aextendedblockstorage[l].getBlockLSBArray();
                System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
                j += abyte1.length;
            }
        }

        NibbleArray nibblearray;

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getMetadataArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getBlocklightArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        if (!chunk.worldObj.provider.hasNoSky)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getSkylightArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (k > 0)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && aextendedblockstorage[l].getBlockMSBArray() != null && (bitmap & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getBlockMSBArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (groundUpCont)
        {
            byte[] abyte2 = chunk.getBiomeArray();
            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        extracted.aBlock = new byte[j];
        System.arraycopy(abyte, 0, extracted.aBlock, 0, j);
        return extracted;
    }

    public static class Extracted{
        public byte[] aBlock;
        public int bitmask;
        public int addBitmap;
    }
}
