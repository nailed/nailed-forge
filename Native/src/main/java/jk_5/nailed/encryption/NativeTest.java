package jk_5.nailed.encryption;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * No description given
 *
 * @author jk-5
 */
public class NativeTest {

    public static void main(String[] args) throws Exception {
        NativeCipher.load();
        NativeCipher cipher = new NativeCipher();

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();

        cipher.init(true, key);

        ByteBuf in = Unpooled.directBuffer();
        ByteBuf out = Unpooled.directBuffer();
        ByteBufUtils.writeUTF8String(in, "TEST");
        cipher.cipher(in, out);

        cipher.init(false, key);

        ByteBuf real = Unpooled.directBuffer();
        real.markReaderIndex();
        cipher.cipher(out, real);
        real.resetReaderIndex();
        System.out.println(ByteBufUtils.readUTF8String(real));

        in.release();
        out.release();
        real.release();
    }
}
