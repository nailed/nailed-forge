package jk_5.nailed.client.network;

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
            //We can only receive this packet.
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
            //NOOP
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuiReturnDataPacket extends NailedPacket {

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
            //NOOP
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuiOpen extends NailedPacket {

        public int x, y, z;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            //NOOP
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
    public static class TileEntityData extends NailedPacket {

        public int x, y, z;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){
            //NOOP
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
    public static class TimeUpdate extends NailedPacket {

        public boolean display;
        public String data;

        @Override
        public void encode(ByteBuf buffer){
            //Receive-only
        }

        @Override
        public void decode(ByteBuf buffer){
            this.display = buffer.readBoolean();

            int len = buffer.readInt();
            this.data = buffer.toString(buffer.readerIndex(), len, Charsets.UTF_8);
            buffer.readerIndex(buffer.readerIndex() + len);
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
            this.username = ByteBufUtils.readUTF8String(buffer);
            this.isSkin = buffer.readBoolean();
            this.isUrl = buffer.readBoolean();

            int len = buffer.readInt();
            this.skin = buffer.toString(buffer.readerIndex(), len, Charsets.UTF_8);
            buffer.readerIndex(buffer.readerIndex() + len);
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

        }

        @Override
        public void decode(ByteBuf buffer){
            this.skinName = ByteBufUtils.readUTF8String(buffer);
            this.isCape = buffer.readBoolean();
            this.data = buffer.copy();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapData extends NailedPacket {

        public int dimId;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer){

        }

        @Override
        public void decode(ByteBuf buffer){
            this.dimId = buffer.readInt();
            this.data = buffer.copy();
        }
    }
}
