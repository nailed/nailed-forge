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
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.data = buffer.slice();
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
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.data = buffer.slice();
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
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.data = buffer.slice();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeUpdate extends NailedPacket {

        public String data;

        @Override
        public void encode(ByteBuf buffer){
            byte[] utf8Bytes = this.data.getBytes(Charsets.UTF_8);
            buffer.writeInt(utf8Bytes.length);
            buffer.writeBytes(utf8Bytes);
        }

        @Override
        public void decode(ByteBuf buffer){
            int len = buffer.readInt();
            this.data = buffer.toString(buffer.readerIndex(), len, Charsets.UTF_8);
            buffer.readerIndex(buffer.readerIndex() + len);
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
            this.dimId = buffer.readInt();
            this.data = buffer.slice();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Particle extends NailedPacket {

        public double x, y, z;
        public String name;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeDouble(this.x);
            buffer.writeDouble(this.y);
            buffer.writeDouble(this.z);
            ByteBufUtils.writeUTF8String(buffer, this.name);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.x = buffer.readDouble();
            this.y = buffer.readDouble();
            this.z = buffer.readDouble();
            this.name = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class FPSSummary extends NailedPacket {

        public int fps;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.fps);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.fps = buffer.readInt();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenTerminalGui extends NailedPacket {

        public int instanceId;
        public int width;
        public int height;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.instanceId);
            buffer.writeInt(this.width);
            buffer.writeInt(this.height);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.instanceId = buffer.readInt();
            this.width = buffer.readInt();
            this.height = buffer.readInt();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditMode extends NailedPacket {

        public boolean enable;
        public ByteBuf buffer;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeBoolean(this.enable);
            if(this.enable){
                buffer.writeBytes(this.buffer);
            }
        }

        @Override
        public void decode(ByteBuf buffer){
            this.enable = buffer.readBoolean();
            if(this.enable){
                this.buffer = buffer.slice();
            }
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterAchievement extends NailedPacket {

        public boolean enable;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeBoolean(this.enable);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.enable = buffer.readBoolean();
        }
    }

    @NoArgsConstructor
    public static class CheckClientUpdates extends NailedPacket {

        @Override
        public void encode(ByteBuf buffer){

        }

        @Override
        public void decode(ByteBuf buffer){

        }
    }

    @NoArgsConstructor
    public static class DisplayLogin extends NailedPacket {

        @Override
        public void encode(ByteBuf buffer){

        }

        @Override
        public void decode(ByteBuf buffer){

        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login extends NailedPacket {

        public String username;
        public String password;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.username);
            ByteBufUtils.writeUTF8String(buffer, this.password);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.username = ByteBufUtils.readUTF8String(buffer);
            this.password = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse extends NailedPacket {

        public int state;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeByte(state);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.state = buffer.readByte();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldStatus extends NailedPacket {

        public int field;
        public String content;
        public int status;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeByte(this.field);
            ByteBufUtils.writeUTF8String(buffer, this.content);
            buffer.writeByte(this.status);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.field = buffer.readByte();
            this.content = ByteBufUtils.readUTF8String(buffer);
            this.status = buffer.readByte();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAccount extends NailedPacket {

        public String username;
        public String email;
        public String name;
        public String password;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.username);
            ByteBufUtils.writeUTF8String(buffer, this.email);
            ByteBufUtils.writeUTF8String(buffer, this.name);
            ByteBufUtils.writeUTF8String(buffer, this.password);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.username = ByteBufUtils.readUTF8String(buffer);
            this.email = ByteBufUtils.readUTF8String(buffer);
            this.name = ByteBufUtils.readUTF8String(buffer);
            this.password = ByteBufUtils.readUTF8String(buffer);
        }
    }
}
