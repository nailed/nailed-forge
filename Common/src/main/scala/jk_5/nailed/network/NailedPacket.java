package jk_5.nailed.network;

import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;

import cpw.mods.fml.common.network.ByteBufUtils;

import jk_5.nailed.map.RenderPoint;
import jk_5.nailed.map.teamlist.TeamInfo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RenderList extends NailedPacket {
        public int func = 0;
        public List<RenderPoint[]> points = Lists.newArrayList();

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeByte(func);
            buffer.writeInt(points.size());
            for(RenderPoint[] pPoints : points){
                buffer.writeByte(pPoints.length);
                for(RenderPoint point : pPoints){
                    buffer.writeDouble(point.getX());
                    buffer.writeDouble(point.getY());
                    buffer.writeDouble(point.getZ());
                    buffer.writeInt(point.getColor());
                    buffer.writeFloat(point.getSize());
                }
            }
        }

        public void decode(ByteBuf buffer){
            this.points = Lists.newArrayList();
            this.func = buffer.readByte();
            int size = buffer.readInt();
            for(int x = 0; x < size; x++){
                RenderPoint[] tPoints = new RenderPoint[buffer.readByte()];
                for(int y = 0; y < tPoints.length; y++){
                    tPoints[y] = new RenderPoint(buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readInt(),buffer.readFloat());
                }
            }
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamInformation extends NailedPacket {

        public boolean display;
        public List<TeamInfo> teams;

        public void encode(ByteBuf buffer){
            buffer.writeBoolean(this.display);
            if(this.display){
                ByteBufUtils.writeVarShort(buffer, teams.size());
                for(TeamInfo team : teams){
                    team.write(buffer);
                }
            }
        }

        public void decode(ByteBuf buffer){
            this.display = buffer.readBoolean();
            if(this.display){
                int size = ByteBufUtils.readVarShort(buffer);
                this.teams = Lists.newArrayListWithCapacity(size);
                for(int i = 0; i < size; i++){
                    teams.add(TeamInfo.read(buffer));
                }
            }
        }
    }
}
