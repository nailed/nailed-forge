package jk_5.nailed.client.network;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class NailedPacketCodec extends FMLIndexedMessageToMessageCodec<NailedPacket> {

    public NailedPacketCodec(){
        this.addDiscriminator(0, NailedPacket.Notification.class);
        this.addDiscriminator(1, NailedPacket.MovementEvent.class);
        this.addDiscriminator(2, NailedPacket.GuiReturnDataPacket.class);
        this.addDiscriminator(3, NailedPacket.GuiOpen.class);
        this.addDiscriminator(4, NailedPacket.TileEntityData.class);
        this.addDiscriminator(5, NailedPacket.TimeUpdate.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, NailedPacket msg, ByteBuf target) throws Exception{
        msg.encode(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, NailedPacket msg){
        msg.decode(source);
    }
}