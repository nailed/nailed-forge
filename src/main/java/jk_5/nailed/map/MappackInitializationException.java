package jk_5.nailed.map;

/**
 * No description given
 *
 * @author jk-5
 */
public class MappackInitializationException extends Exception {

    private final Mappack mappack;

    public MappackInitializationException(Mappack mappack, String message){
        super(message);
        this.mappack = mappack;
    }

    public MappackInitializationException(Mappack mappack, String message, Throwable cause){
        super(message, cause);
        this.mappack = mappack;
    }
}
