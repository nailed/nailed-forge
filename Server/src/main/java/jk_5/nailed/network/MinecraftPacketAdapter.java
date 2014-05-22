package jk_5.nailed.network;

import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

import io.netty.buffer.*;
import io.netty.channel.*;

import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.storage.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.block.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.sign.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.network.packets.*;
import jk_5.nailed.util.*;

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
     * Adapt inbound packets
     *
     * @param ctx ChannelHandlerContext
     * @param msg The inbound packet
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof C12PacketUpdateSign){
            C12PacketUpdateSign packet = (C12PacketUpdateSign) msg;
            SignCommandHandler handler = NailedAPI.getMapLoader().getMap(player.worldObj).getSignCommandHandler();
            handler.onSignAdded(packet.field_149590_d, packet.field_149593_a, packet.field_149591_b, packet.field_149592_c);
        }/*else if(msg instanceof C0CPacketInput){
            C0CPacketInput packet = (C0CPacketInput) msg;
            if(packet.func_149618_e()){ //Jump
                ElevatorHelper.onJump(player);
            }else if(packet.func_149617_f()){ //Sneak
                ElevatorHelper.onSneak(player);
            }
        }*/
        ctx.fireChannelRead(msg);
    }

    /**
     * Adapt outbound packets
     *
     * @param ctx     ChannelHandlerContext
     * @param msg     The outbound packet
     * @param promise The promise of the packet
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
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
                    }else{
                        return;
                    }
                }
            }
        }else if(msg instanceof S33PacketUpdateSign){
            S33PacketUpdateSign signPacket = (S33PacketUpdateSign) msg;
            String[] lines = signPacket.field_149349_d;
            if("$mappack".equalsIgnoreCase(lines[0])){
                Map map = NailedAPI.getMapLoader().getMap(player.worldObj);
                Sign sign = map.getSignCommandHandler().getSign(signPacket.field_149352_a, signPacket.field_149350_b, signPacket.field_149351_c);
                if(sign == null){
                    ctx.write(msg, promise);
                    return;
                }
                ctx.write(sign.getUpdatePacket(), promise);
                return;
            }
        }else if(msg instanceof S38PacketPlayerListItem){
            S38PacketPlayerListItem playerList = (S38PacketPlayerListItem) msg;
            Player pPlayer = NailedAPI.getPlayerRegistry().getPlayerByUsername(playerList.field_149126_a);
            if(playerList.field_149124_b){
                Player nPlayer = NailedAPI.getPlayerRegistry().getPlayer(player);
                if(!nPlayer.getPlayersVisible().contains(pPlayer)){
                    return;
                }else{
                    msg = new S38PacketPlayerListItem(pPlayer.getChatPrefix(), playerList.field_149124_b, playerList.field_149125_c);
                    ctx.write(msg, promise);
                    return;
                }
            }
        }else if(msg instanceof S2FPacketSetSlot){
            S2FPacketSetSlot setSlot = (S2FPacketSetSlot) msg;
            if(NailedAPI.getPlayerRegistry().getPlayer(player).getClient() != PlayerClient.NAILED && setSlot.field_149178_c != null){
                setSlot.field_149178_c = tryReplaceforClient(setSlot.field_149178_c);
            }
            ctx.write(setSlot, promise);
            return;
        }else if(msg instanceof S30PacketWindowItems){
            S30PacketWindowItems windowItems = (S30PacketWindowItems) msg;
            if(NailedAPI.getPlayerRegistry().getPlayer(player).getClient() != PlayerClient.NAILED){
                ItemStack[] array = windowItems.field_148913_b;
                for(int f = 0; f < array.length; ++f){
                    if(array[f] != null){
                        array[f] = tryReplaceforClient(array[f]);
                    }
                }
                windowItems.field_148913_b = array;
            }
            ctx.write(windowItems, promise);
            return;
        }else if(msg instanceof CustomChunkPacket){
            Player nPlayer = NailedAPI.getPlayerRegistry().getPlayer(player);

            CustomChunkPacket ccPacket = (CustomChunkPacket) msg;
            Chunk chunk = ccPacket.chunk;
            boolean groundUpCont = ccPacket.groundUpCont;
            ByteBuf buf = Unpooled.buffer();
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeVarIntToBuffer(33); // 33 = S21
            buffer.writeVarIntToBuffer(chunk.xPosition);
            buffer.writeVarIntToBuffer(chunk.zPosition);
            buffer.writeBoolean(groundUpCont);
            Extracted extracted = extractData(chunk, ccPacket.groundUpCont, ccPacket.i, nPlayer.getClient() == PlayerClient.NAILED);
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
            }finally{
                deflater.end();
            }
            buffer.writeInt(length);
            buffer.writeBytes(deflated, 0, length);

            ctx.write(buf, promise);
            return;
        }else if(msg instanceof CustomBulkChunkPacket){
            Player nplayer = NailedAPI.getPlayerRegistry().getPlayer(player);
            ByteBuf buf = Unpooled.buffer();
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeVarIntToBuffer(38); // 38 = S26

            List<Chunk> chunks = ((CustomBulkChunkPacket) msg).getChunks();
            int i = chunks.size();
            int[] xArray = new int[i];
            int[] zArray = new int[i];
            int[] bitmaskArray = new int[i];
            int[] addBitmapArray = new int[i];
            byte[][] chunkdata = new byte[i][];
            boolean groundUpCont = !chunks.isEmpty() && chunks.get(0).worldObj.provider.hasNoSky;

            int j = 0;
            int k;
            for(k = 0; k < i; ++k){
                Chunk chunk = chunks.get(k);
                Extracted extracted = extractData(chunk, true, 65535, nplayer.getClient() == PlayerClient.NAILED);
                j += extracted.aBlock.length;
                xArray[k] = chunk.xPosition;
                zArray[k] = chunk.zPosition;
                bitmaskArray[k] = extracted.bitmask;
                addBitmapArray[k] = extracted.addBitmap;
                chunkdata[k] = extracted.aBlock;
            }

            byte[] data = new byte[j];
            int offset = 0;
            for(k = 0; k < chunkdata.length; k++){
                System.arraycopy(chunkdata[k], 0, data, offset, chunkdata[k].length);
                offset += chunkdata[k].length;
            }
            Deflater deflater = new Deflater(-1);
            int length = 0;
            byte[] deflated;
            try{
                deflater.setInput(data, 0, data.length);
                deflater.finish();
                deflated = new byte[data.length];
                length = deflater.deflate(deflated);
            }finally{
                deflater.end();
            }
            buffer.writeShort(xArray.length);
            buffer.writeInt(length);
            buffer.writeBoolean(groundUpCont);
            buffer.writeBytes(deflated);

            for(k = 0; k < xArray.length; ++k){
                buffer.writeInt(xArray[k]);
                buffer.writeInt(zArray[k]);
                buffer.writeShort((short) (bitmaskArray[k] & 65535));
                buffer.writeShort((short) (addBitmapArray[k] & 65535));
            }

            ctx.write(buf, promise);
        }
        ctx.write(msg, promise);
    }

    public Extracted extractData(Chunk chunk, boolean groundUpCont, int bitmap, boolean isNailed) {
        int j = 0;
        ExtendedBlockStorage[] aExtendedBlockStorage = chunk.getBlockStorageArray();
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[aExtendedBlockStorage.length];

        int k = 0;
        MinecraftPacketAdapter.Extracted extracted = new MinecraftPacketAdapter.Extracted();
        byte[] abyte = new byte[196864];

        if(groundUpCont){
            chunk.sendUpdates = true;
        }

        int l;

        if(!isNailed){
            for(l = 0; l < aExtendedBlockStorage.length; ++l){
                ExtendedBlockStorage array = aExtendedBlockStorage[l];
                ExtendedBlockStorage pExtendedBlockStorage = new ExtendedBlockStorage(array.getYLocation(), true);

                pExtendedBlockStorage.setBlockLSBArray(array.getBlockLSBArray());
                pExtendedBlockStorage.setBlocklightArray(array.getBlocklightArray());
                pExtendedBlockStorage.setBlockMetadataArray(array.getMetadataArray());
                pExtendedBlockStorage.setBlockMSBArray(array.getBlockMSBArray());
                pExtendedBlockStorage.setSkylightArray(array.getSkylightArray());
                aextendedblockstorage[l] = pExtendedBlockStorage;
            }

            for(ExtendedBlockStorage extendedBlockStorage : aextendedblockstorage){
                if(extendedBlockStorage != null){
                    for(int x = 0; x < 16; ++x){
                        for(int y = 0; y < 16; ++y){
                            for(int z = 0; z < 16; ++z){
                                Block block = extendedBlockStorage.getBlockByExtId(x, y, z);
                                if(block instanceof INailedBlock){
                                    extendedBlockStorage.func_150818_a(x, y, z, ((INailedBlock) block).getReplacementBlock());
                                    extendedBlockStorage.setExtBlockMetadata(x, y, z, ((INailedBlock) block).getReplacementMetadata());
                                }
                            }
                        }
                    }
                }
            }
        }

        for(l = 0; l < aextendedblockstorage.length; ++l){
            if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0){
                extracted.bitmask |= 1 << l;

                if(aextendedblockstorage[l].getBlockMSBArray() != null){
                    extracted.addBitmap |= 1 << l;
                    ++k;
                }
            }
        }

        for(l = 0; l < aextendedblockstorage.length; ++l){
            if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0){
                byte[] abyte1 = aextendedblockstorage[l].getBlockLSBArray();
                System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
                j += abyte1.length;
            }
        }

        NibbleArray nibblearray;

        for(l = 0; l < aextendedblockstorage.length; ++l){
            if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0){
                nibblearray = aextendedblockstorage[l].getMetadataArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        for(l = 0; l < aextendedblockstorage.length; ++l){
            if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0){
                nibblearray = aextendedblockstorage[l].getBlocklightArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        if(!chunk.worldObj.provider.hasNoSky){
            for(l = 0; l < aextendedblockstorage.length; ++l){
                if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && (bitmap & 1 << l) != 0){
                    nibblearray = aextendedblockstorage[l].getSkylightArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if(k > 0){
            for(l = 0; l < aextendedblockstorage.length; ++l){
                if(aextendedblockstorage[l] != null && (!groundUpCont || !aextendedblockstorage[l].isEmpty()) && aextendedblockstorage[l].getBlockMSBArray() != null && (bitmap & 1 << l) != 0){
                    nibblearray = aextendedblockstorage[l].getBlockMSBArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if(groundUpCont){
            byte[] abyte2 = chunk.getBiomeArray();
            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        extracted.aBlock = new byte[j];
        System.arraycopy(abyte, 0, extracted.aBlock, 0, j);
        return extracted;
    }

    public ItemStack tryReplaceforClient(ItemStack stack) {
        if(stack == null){
            return null;
        }
        if(stack.field_151002_e instanceof ItemBlock && ((ItemBlock) stack.field_151002_e).field_150939_a instanceof INailedBlock){
            Item item = stack.field_151002_e;
            INailedBlock nailedBlock = (INailedBlock) ((ItemBlock) item).field_150939_a;
            ItemBlock newItem = new ItemBlock(nailedBlock.getReplacementBlock());
            ItemStack newItemStack = new ItemStack(newItem, stack.stackSize, nailedBlock.getReplacementMetadata());
            newItemStack.setStackDisplayName(((Block) nailedBlock).getUnlocalizedName());
            return newItemStack;
        }
        return stack;
    }

    public static class Extracted {

        public byte[] aBlock;
        public int bitmask;
        public int addBitmap;
    }
}
