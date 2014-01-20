package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.network.NailedPacket;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * No description given
 *
 * @author jk-5
 */
public class StoreSkinHandler extends SimpleChannelInboundHandler<NailedPacket.StoreSkin> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.StoreSkin msg) throws Exception{
        File dest = new File("skincache", msg.skinName + ".png");
        dest.getParentFile().mkdirs();
        FileChannel channel = new FileOutputStream(dest).getChannel();
        channel.write(msg.data.nioBuffer());
        channel.close();
        msg.data.release();
    }
}
