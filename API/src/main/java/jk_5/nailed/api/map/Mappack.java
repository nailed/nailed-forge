package jk_5.nailed.api.map;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.stat.StatConfig;
import jk_5.nailed.api.scripting.IMount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * @param callback The callback to call when the map is set up.
     */
    void prepareWorld(@Nonnull File destinationDir, @Nonnull Callback<Void> callback);

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
    @Nonnull
    Map createMap(@Nonnull MapBuilder mapBuilder);

    /**
     * Save the world data of the given map as the new world content of this mappack
     *
     * If this operation is not supported, return {@code false}. Else, return {@code true}.
     *
     * @param map The map where you should copy the data from
     * @return True if this operation is supported, false otherwise
     */
    boolean saveAsMappack(@Nonnull Map map);

    /**
     * @return The data from the mappack icon (PNG) used for displaying in the webinterface and ingame
     */
    @Nullable ByteBuf getMappackIcon();

    @Nullable IMount createMount();
}
