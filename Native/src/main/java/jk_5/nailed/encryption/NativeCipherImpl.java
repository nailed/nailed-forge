package jk_5.nailed.encryption;

/**
 * No description given
 *
 * @author jk-5
 */
public class NativeCipherImpl {

    public native long init(byte[] key);
    public native void free(long key);
    public native void cipher(boolean encrypt, long key, byte[] iv, long in, long out, long length);

    public static void main(String[] args){
        new NativeCipherImpl().free(0);
    }

    static {
        System.loadLibrary("nativecipher");
    }
}
