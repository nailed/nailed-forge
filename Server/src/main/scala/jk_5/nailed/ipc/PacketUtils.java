package jk_5.nailed.ipc;

import io.netty.buffer.*;
import io.netty.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class PacketUtils {

    private PacketUtils(){

    }

    public static int readVarInt(ByteBuf input) {
        int out = 0;
        int bytes = 0;
        byte in;
        while(true){
            in = input.readByte();
            out |= (in & 127) << (bytes++ * 7);
            if(bytes > 5){
                throw new RuntimeException("VarInt too big");
            }
            if((in & 0x80) != 0x80){
                break;
            }
        }
        return out;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while(true){
            part = value & 0x7F;
            value >>>= 7;
            if(value != 0){
                part |= 0x80;
            }
            output.writeByte(part);
            if(value == 0){
                break;
            }
        }
    }

    public static int varIntSize(int varint) {
        if((varint & 0xFFFFFF80) == 0){
            return 1;
        }
        if((varint & 0xFFFFC000) == 0){
            return 2;
        }
        if((varint & 0xFFE00000) == 0){
            return 3;
        }
        if((varint & 0xF0000000) == 0){
            return 4;
        }
        return 5;
    }

    public static String readString(ByteBuf buffer) {
        int len = readVarInt(buffer);
        String str = buffer.toString(buffer.readerIndex(), len, CharsetUtil.UTF_8);
        buffer.readerIndex(buffer.readerIndex() + len);
        return str;
    }

    public static void writeString(String string, ByteBuf buffer) {
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        writeVarInt(bytes.length, buffer);
        buffer.writeBytes(bytes);
    }
}
