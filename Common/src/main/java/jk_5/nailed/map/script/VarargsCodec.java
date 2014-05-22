package jk_5.nailed.map.script;

import io.netty.buffer.*;

import cpw.mods.fml.common.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class VarargsCodec {

    private VarargsCodec(){

    }

    public static void writeObjects(ByteBuf buffer, Object... data){
        if(data == null){
            buffer.writeShort(0);
            return;
        }
        buffer.writeShort(data.length);
        for(Object o : data){
            if(o instanceof String){
                buffer.writeByte(0);
                ByteBufUtils.writeUTF8String(buffer, (String) o);
            }else if(o instanceof Integer){
                buffer.writeByte(1);
                buffer.writeInt((Integer) o);
            }
        }
    }

    public static Object[] readObjects(ByteBuf buffer){
        int length = buffer.readShort();
        Object[] ret = new Object[length];
        for(int i = 0; i < length; i++){
            int type = buffer.readByte();
            if(type == 0){
                ret[i] = ByteBufUtils.readUTF8String(buffer);
            }else if(type == 1){
                ret[i] = buffer.readInt();
            }
        }
        return ret;
    }
}
