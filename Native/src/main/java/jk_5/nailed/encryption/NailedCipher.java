package jk_5.nailed.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

/**
 * No description given
 *
 * @author jk-5
 */
public interface NailedCipher {

    public void init(boolean encrypt, SecretKey key) throws GeneralSecurityException;
    public void free();
    public void cipher(ByteBuf in, ByteBuf out) throws GeneralSecurityException;
    public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws GeneralSecurityException;
}
