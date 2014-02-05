package jk_5.nailed.api.map;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.map.stat.StatConfig;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Mappack {

    /**
     * @return An unique name for this mappack
     */
    String getMappackID();

    /**
     * @return The metadata for the mappack
     */
    MappackMetadata getMappackMetadata();

    /**
     * @return An instructionlist with instructions for the game (may be empty)
     */
    InstructionList getInstructionList();

    /**
     * @return The config about which {@link jk_5.nailed.api.map.stat.IStatType}s should be available. May not be null!
     */
    StatConfig getStatConfig();

    /**
     * This method should prepare the game world at the given location
     *
     * @param destinationDir The location where the game world should be prepared
     * @return The location where the world was prepared, or null if it failed
     */
    File prepareWorld(File destinationDir);

    /**
     * Set the Map up, so it can load after this and players can join it
     *
     * This is always called AFTER prepareWorld
     *
     * If you don't have to do some fancy loading, you can just return potentialMap.createMap()
     *
     * @param mapBuilder The mapBuilder that contains all the required data
     * @return A map instance
     */
    Map createMap(MapBuilder mapBuilder);

    /**
     * Save the world data of the given map as the new world content of this mappack
     *
     * If this operation is not supported, return {@code false}. Else, return {@code true}.
     *
     * @param map The map where you should copy the data from
     * @return True if this operation is supported, false otherwise
     */
    boolean saveAsMappack(Map map);

    /**
     * @return The data from the mappack icon (PNG) used for displaying in the webinterface and ingame
     */
    ByteBuf getMappackIcon();
}
