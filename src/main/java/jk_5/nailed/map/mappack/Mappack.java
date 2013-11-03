package jk_5.nailed.map.mappack;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.PotentialMap;
import jk_5.nailed.map.instruction.InstructionList;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Mappack {

    /**
     * @return A unique, name for this mappack
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
     * @param potentialMap The potential map that contains all the required data
     * @return A map instance
     */
    Map createMap(PotentialMap potentialMap);
}
