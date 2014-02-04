package jk_5.nailed.encryption;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

/**
 * No description given
 *
 * @author jk-5
 */
public class NativeCipher implements NailedCipher {

    private final NativeCipherImpl nativeCipher = new NativeCipherImpl();
    private long pointer;
    private boolean encrypt;
    private byte[] iv;

    private static boolean loaded = false;

    public static boolean load(){
        if(!loaded){
            try{
                System.loadLibrary("nativecipher");
                //System.load("~/Development/Modding/Nailed/nailed-forge/Native/src/main/c/libnativecipher.jnilib");
                loaded = true;
                System.out.println("Successfully loaded native cipher");
            }catch(Throwable t){
                t.printStackTrace();
                System.out.println("Failed loading native cipher");
            }
        }
        return loaded;
    }

    @Override
    public void init(boolean encrypt, SecretKey key) throws GeneralSecurityException{
        if(this.pointer != 0){
            this.nativeCipher.free(this.pointer);
        }
        this.encrypt = encrypt;
        this.iv = key.getEncoded();
        this.pointer = this.nativeCipher.init(key.getEncoded());
    }

    @Override
    public void free(){
        if(this.pointer != 0){
            this.nativeCipher.free(this.pointer);
            this.pointer = 0;
        }
    }

    @Override
    public void cipher(ByteBuf in, ByteBuf out) throws GeneralSecurityException{
        in.memoryAddress();
        out.memoryAddress();
        Preconditions.checkState(this.pointer != 0, "Invalid pointer to AES key!");
        Preconditions.checkState(this.iv != null, "Invalid IV!");

        int length = in.readableBytes(); //Amount of bytes to cipher
        out.ensureWritable(length); //In AES CFB-8, the number of input bytes is equal to the number of output bytes

        this.nativeCipher.cipher(this.encrypt, this.pointer, this.iv, in.memoryAddress() + in.readerIndex(), out.memoryAddress() + out.writerIndex(), length);

        in.readerIndex(in.writerIndex());
        out.writerIndex(out.writerIndex() + length);
    }

    @Override
    public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws GeneralSecurityException{
        int readable = in.readableBytes();
        ByteBuf out = ctx.alloc().directBuffer(readable);
        this.cipher(in, out);
        return out;
    }
}
