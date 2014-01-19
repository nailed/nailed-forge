package jk_5.nailed.skinsync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class SkinSyncManager {

    @Getter private static final SkinSyncManager instance = new SkinSyncManager();

    public void sendSkinToClient(EntityPlayer player, File image, String name){
        ByteBuf buf = Unpooled.buffer();
        try{
            new FileInputStream(image).getChannel().write(buf.nioBuffer());
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.StoreSkin("test", false, buf), player);
        }catch(IOException e){
        }
        buf.release();
    }
}
