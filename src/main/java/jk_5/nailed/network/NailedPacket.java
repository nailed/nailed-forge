package jk_5.nailed.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedPacket {

    public abstract void encode(ByteBuf buffer);
    public abstract void decode(ByteBuf buffer);

    public static class NailedPacketNotification extends NailedPacket{

        public String message;
        public ResourceLocation icon = null;
        public int color = 0xFFFFFF;

        @Override
        public void encode(ByteBuf buffer){
            byte mode;
            if(this.icon == null && this.color == 0xFFFFFF){
                mode = 0;
            }else if(this.color == 0xFFFFFF){
                mode = 1;
            }else{
                mode = 2;
            }
            buffer.writeByte(mode);
            ByteBufUtils.writeUTF8String(buffer, this.message);
            if(mode >= 1){
                ByteBufUtils.writeUTF8String(buffer, this.icon.getResourceDomain());
                ByteBufUtils.writeUTF8String(buffer, this.icon.getResourcePath());
            }
            if(mode == 2){
                buffer.writeInt(this.color);
            }
        }

        @Override
        public void decode(ByteBuf buffer){
            int mode = buffer.readByte();
            this.message = ByteBufUtils.readUTF8String(buffer);
            if(mode >= 1){
                this.icon = new ResourceLocation(ByteBufUtils.readUTF8String(buffer), ByteBufUtils.readUTF8String(buffer));
            }
            if(mode == 2){
                this.color = buffer.readInt();
            }
        }
    }
}
