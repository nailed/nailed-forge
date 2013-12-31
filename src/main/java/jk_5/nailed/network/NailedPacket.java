package jk_5.nailed.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedPacket {

    public abstract void encode(ByteBuf buffer);
    public abstract void decode(ByteBuf buffer);

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Notification extends NailedPacket{

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

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementEvent extends NailedPacket{

        public int x, y, z;
        public byte type;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.x);
            buffer.writeInt(this.y);
            buffer.writeInt(this.z);
            buffer.writeByte(this.type);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.type = buffer.readByte();
        }
    }
}
