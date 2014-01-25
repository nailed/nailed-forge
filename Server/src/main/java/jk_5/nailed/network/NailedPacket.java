package jk_5.nailed.network;

import com.google.common.base.Charsets;
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
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementEvent extends NailedPacket{

        public int x, y, z;
        public byte type;

        @Override
        public void encode(ByteBuf buffer){
            //Receive-only
        }

        @Override
        public void decode(ByteBuf buffer){
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.type = buffer.readByte();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuiReturnDataPacket extends NailedPacket {

        public int x, y, z;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            //Receive-only
        }

        @Override
        public void decode(ByteBuf buffer){
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.data = buffer.copy();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuiOpen extends NailedPacket {

        public int x, y, z;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.x);
            buffer.writeInt(this.y);
            buffer.writeInt(this.z);
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TileEntityData extends NailedPacket {

        public int x, y, z;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.x);
            buffer.writeInt(this.y);
            buffer.writeInt(this.z);
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeUpdate extends NailedPacket {

        public boolean display;
        public String data;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeBoolean(this.display);

            byte[] utf8Bytes = this.data.getBytes(Charsets.UTF_8);
            buffer.writeInt(utf8Bytes.length);
            buffer.writeBytes(utf8Bytes);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerSkin extends NailedPacket {

        public String username;
        public boolean isSkin;
        public boolean isUrl;
        public String skin;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.username);
            buffer.writeBoolean(this.isSkin);
            buffer.writeBoolean(this.isUrl);

            byte[] utf8Bytes = this.skin.getBytes(Charsets.UTF_8);
            buffer.writeInt(utf8Bytes.length);
            buffer.writeBytes(utf8Bytes);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreSkin extends NailedPacket {

        public String skinName;
        public boolean isCape;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.skinName);
            buffer.writeBoolean(this.isCape);
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapData extends NailedPacket {

        public int dimId;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.dimId);
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            //Send-only
        }
    }
}
