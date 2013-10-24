package jk_5.nailed.map;

/**
 * No description given
 *
 * @author jk-5
 */
public class DiscardedMappackInitializationException extends MappackInitializationException {

    public DiscardedMappackInitializationException(Mappack mappack, String message){
        super(mappack, message);
    }

    public DiscardedMappackInitializationException(Mappack mappack, String message, Throwable cause){
        super(mappack, message, cause);
    }
}
