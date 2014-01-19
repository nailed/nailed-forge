package jk_5.nailed.client.network.handlers;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.network.NailedPacket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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

        BufferedImage image = ImageIO.read(new ByteBufInputStream(msg.data));
        ImageIO.write(image, "PNG", dest);
    }
}
