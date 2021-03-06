package jk_5.nailed.ipc.codec;

import java.util.*;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.handler.codec.*;

import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class VarintFrameCodec extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLength = msg.readableBytes();
        int headerLength = PacketUtils.varIntSize(bodyLength);
        out.ensureWritable(headerLength + bodyLength);
        PacketUtils.writeVarInt(bodyLength, out);
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        final byte[] buf = new byte[3];
        for(int i = 0; i < buf.length; i++){
            if(!in.isReadable()){
                in.resetReaderIndex();
                return;
            }
            buf[i] = in.readByte();
            if(buf[i] >= 0){
                int length = PacketUtils.readVarInt(Unpooled.wrappedBuffer(buf));
                if(in.readableBytes() < length){
                    in.resetReaderIndex();
                    return;
                }else{
                    out.add(in.readBytes(length));
                    return;
                }
            }
        }
        throw new CorruptedFrameException("Length wider than 21-bit");
    }
}
