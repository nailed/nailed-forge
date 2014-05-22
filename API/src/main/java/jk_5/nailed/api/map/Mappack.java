package jk_5.nailed.api.map;

import java.io.*;
import javax.annotation.*;

import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.api.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Mappack {

    /**
     * @return An unique name for this mappack
     */
    @Nonnull
    String getMappackID();

    /**
     * @return The metadata for the mappack
     */
    @Nonnull
    MappackMetadata getMappackMetadata();

    /**
     * @return The config about which {@link jk_5.nailed.api.map.stat.IStatType}s should be available. May not be null!
     */
    @Nonnull
    StatConfig getStatConfig();

    /**
     * This method should prepare the game world at the given location asynchronously, and call the callback when it's done
     *
     * @param destinationDir The location where the game world should be prepared
     * @param callback       The callback to call when the map is set up.
     */
    void prepareWorld(@Nonnull File destinationDir, @Nullable Callback<Void> callback);

    /**
     * Set the Map up, so it can load after this and players can join it
     * <p/>
     * This is always called AFTER prepareWorld
     * <p/>
     * If you don't have to do some fancy loading, you can just return potentialMap.createMap()
     *
     * @param mapBuilder The mapBuilder that contains all the required data
     * @return A map instance
     */
    @Nonnull
    Map createMap(@Nonnull MapBuilder mapBuilder);

    /**
     * Save the world data of the given map as the new world content of this mappack
     * <p/>
     * If this operation is not supported, return {@code false}. Else, return {@code true}.
     *
     * @param map The map where you should copy the data from
     * @return True if this operation is supported, false otherwise
     */
    boolean saveAsMappack(@Nonnull Map map);

    @Nullable
    IMount createMount();

    @Nonnull
    ZoneConfig getZoneConfig();
}
