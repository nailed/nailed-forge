package jk_5.nailed.skinsync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class SkinSyncManager {

    @Getter private static final SkinSyncManager instance = new SkinSyncManager();

    public void sendSkinToClient(EntityPlayer player, File imgFile, String name){
        ByteBuf buf = Unpooled.buffer();
        try{
            BufferedImage image = ImageIO.read(imgFile);
            ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.StoreSkin("test", false, buf), player);
        }catch(IOException e){

        }
        buf.release();
    }

    public void setPlayerSkin(EntityPlayer player, String skinName){
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.PlayerSkin(player.func_146103_bH().getName(), true, false, skinName), player);
    }
}
