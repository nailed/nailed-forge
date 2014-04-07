package jk_5.nailed.client.network.handlers;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.NailedLog;
import jk_5.nailed.client.skinsync.SkinSync;
import jk_5.nailed.network.NailedPacket;

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
        NailedLog.info("Incoming {} data for {}", msg.isCape ? "cape" : "skin", msg.skinName);
        File dest = new File("skincache", (msg.isCape ? "cape_" : "skin_") + msg.skinName + ".png");
        dest.getParentFile().mkdirs();
        BufferedImage image = ImageIO.read(new ByteBufInputStream(msg.data));
        ImageIO.write(image, "PNG", dest);
        msg.data.release();
        if(msg.isCape){
            SkinSync.getInstance().cacheCapeData(msg.skinName, image);
        }else{
            SkinSync.getInstance().cacheSkinData(msg.skinName, image);
        }
        NailedLog.info("Stored {} data for {}", msg.isCape ? "cape" : "skin", msg.skinName);
    }
}
