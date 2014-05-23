package jk_5.nailed.map.script;

import io.netty.buffer.*;

import cpw.mods.fml.common.network.*;

import jk_5.nailed.network.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacket extends NailedPacket {

    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueEvent extends ScriptPacket {

        public int instanceId;
        public String eventName;
        public Object[] data;

        @Override
        public void encode(ByteBuf buffer) {
            buffer.writeInt(this.instanceId);
            ByteBufUtils.writeUTF8String(buffer, this.eventName);
            VarargsCodec.writeObjects(buffer, this.data);
        }

        @Override
        public void decode(ByteBuf buffer) {
            this.instanceId = buffer.readInt();
            this.eventName = ByteBufUtils.readUTF8String(buffer);
            this.data = VarargsCodec.readObjects(buffer);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class StateEvent extends ScriptPacket {

        public int instanceId;
        public int operation;

        @Override
        public void encode(ByteBuf buffer) {
            buffer.writeInt(this.instanceId);
            buffer.writeByte(this.operation);
        }

        @Override
        public void decode(ByteBuf buffer) {
            this.instanceId = buffer.readInt();
            this.operation = buffer.readByte();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMachine extends ScriptPacket {

        public int instanceId;
        public ByteBuf data;

        @Override
        public void encode(ByteBuf buffer) {
            buffer.writeInt(this.instanceId);
            buffer.writeBytes(this.data);
        }

        @Override
        public void decode(ByteBuf buffer) {
            this.instanceId = buffer.readInt();
            this.data = buffer.slice();
        }
    }
}
