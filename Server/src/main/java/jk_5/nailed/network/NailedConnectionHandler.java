package jk_5.nailed.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.NailedServer;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import net.minecraftforge.common.network.ForgeMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedConnectionHandler {

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(event.player.worldObj.provider instanceof NailedWorldProvider){
            //TODO: send data?
        }
        for(Map map : MapLoader.instance().getMaps()){
            if(map.getID() >= -1 && map.getID() <= 1){
                continue;
            }
            ForgeMessage.DimensionRegisterMessage packet = new ForgeMessage.DimensionRegisterMessage(map.getID(), NailedServer.getProviderID());
            EmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.player);
            channel.writeOutbound(packet);
        }

        ByteBuf buf = Unpooled.buffer();
        try{
            BufferedImage image = ImageIO.read(new File("testskin.png"));
            ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.StoreSkin("test", false, buf), event.player);
        }catch(IOException e){
            buf.release();
        }
    }
}
