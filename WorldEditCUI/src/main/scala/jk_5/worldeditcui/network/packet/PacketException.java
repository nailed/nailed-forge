package jk_5.worldeditcui.network.packet;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketException extends RuntimeException {

    public PacketException(String message){
        super(message);
    }

    public PacketException(String message, Throwable cause){
        super(message, cause);
    }
}
