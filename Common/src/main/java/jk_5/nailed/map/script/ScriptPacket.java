package jk_5.nailed.map.script;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.network.NailedPacket;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacket extends NailedPacket {

    @NoArgsConstructor
    public static class RemoveTerminal extends ScriptPacket {

        @Override public void encode(ByteBuf buffer){}
        @Override public void decode(ByteBuf buffer){}
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTerminal extends ScriptPacket {

        public ByteBuf data;

        public boolean additionalMasks = false;

        public int width;
        public int height;
        public int cursorX;
        public int cursorY;
        public int colors;
        public int lineChangeMask;
        public int colorLineChangeMask;
        public int lineChangeMask1 = 0;
        public int colorLineChangeMask1 = 0;
        public int lineChangeMask2 = 0;
        public int colorLineChangeMask2 = 0;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.width);
            buffer.writeInt(this.height);
            buffer.writeInt(this.cursorX);
            buffer.writeInt(this.cursorY);
            buffer.writeInt(this.colors);
            buffer.writeBoolean(this.additionalMasks);
            buffer.writeInt(this.lineChangeMask);
            buffer.writeInt(this.colorLineChangeMask);
            if(this.additionalMasks){
                buffer.writeInt(this.lineChangeMask1);
                buffer.writeInt(this.colorLineChangeMask1);
                buffer.writeInt(this.lineChangeMask2);
                buffer.writeInt(this.colorLineChangeMask2);
            }
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.width = buffer.readInt();
            this.height = buffer.readInt();
            this.cursorX = buffer.readInt();
            this.cursorY = buffer.readInt();
            this.colors = buffer.readInt();
            this.additionalMasks = buffer.readBoolean();
            this.lineChangeMask = buffer.readInt();
            this.colorLineChangeMask = buffer.readInt();
            if(this.additionalMasks){
                this.lineChangeMask1 = buffer.readInt();
                this.colorLineChangeMask1 = buffer.readInt();
                this.lineChangeMask2 = buffer.readInt();
                this.colorLineChangeMask2 = buffer.readInt();
            }
            this.data = buffer.slice();
        }
    }

    @NoArgsConstructor
    public static class RequestTerminalUpdate extends ScriptPacket {

        @Override public void encode(ByteBuf buffer){}
        @Override public void decode(ByteBuf buffer){}
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientKeyTyped extends ScriptPacket {

        public int key;
        public char ch;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.key);
            buffer.writeChar(this.ch);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.key = buffer.readInt();
            this.ch = buffer.readChar();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientMouseClicked extends ScriptPacket {

        public int x, y, button;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.x);
            buffer.writeInt(this.y);
            buffer.writeInt(this.button);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.button = buffer.readInt();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientStringTyped extends ScriptPacket {

        public String data;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.data = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Interrupt extends ScriptPacket {

        public int data;

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeByte(this.data);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.data = buffer.readByte();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientEvent extends ScriptPacket {

        public String event;

        @Override
        public void encode(ByteBuf buffer){
            ByteBufUtils.writeUTF8String(buffer, this.event);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.event = ByteBufUtils.readUTF8String(buffer);
        }
    }
}
