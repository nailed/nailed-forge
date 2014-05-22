package jk_5.nailed.api.map;

import java.io.*;
import java.util.*;
import javax.annotation.*;

import net.minecraft.world.*;

import jk_5.nailed.api.concurrent.*;

/**
 * This interface is responsable for loading new Maps in new dimensions and controlling game state in them
 *
 * @author jk-5
 */
public interface MapLoader {

    /**
     * Returns the folder where all active and loaded {@link Map}s are stored
     *
     * @return The map storage directory
     */
    @Nonnull
    File getMapsFolder();

    /**
     * Returns a random used for the random spawnpoint selector
     *
     * @return A random that can be used for spawnpoint selection
     */
    @Nonnull
    Random getRandomSpawnpointSelector();

    /**
     * This method should return the lobby map. Used for determining where players should land when they join
     * for the first time.
     *
     * @return A {@link Map} that represents the lobby
     */
    @Nonnull
    Map getLobby();

    /**
     * This method will give you a list populated with all currently loaded {@link Map}s known to the system
     *
     * @return A list of loaded {@link Map}s
     */
    @Nonnull
    List<Map> getMaps();

    /**
     * Register a {@link Map} instance to the system. It will be listed so when you call {@code Map.getMaps()}
     * it returns this map
     *
     * @param map The map instance to register
     * @throws NullPointerException when the given map is null
     */
    void registerMap(@Nonnull Map map);

    /**
     * Used for asynchronously prepare a mappack, load it, and create a new {@link Map} for it so it is playable
     *
     * @param mappack  The {@link Mappack} to prepare
     * @param callback The {@link Callback} that will be called when the map is loaded and playable (Note: This is called in a different thread)
     * @throws NullPointerException when the given mappack is null
     */
    void createMapServer(@Nonnull Mappack mappack, @Nullable Callback<Map> callback);

    /**
     * Retrieve a {@link Map} by its name in the format of {@literal map_mappackname_mapid} (Example: {@literal map_nail_2}
     *
     * @param name The name of the {@link Map} to search for
     * @return The {@link Map} that was found, or null when no map was found
     * @throws NullPointerException when the given name is null
     */
    @Nullable
    Map getMap(@Nonnull String name);

    /**
     * Retrieve a {@link Map} by its id
     *
     * @param id The id to search for
     * @return The {@link Map} that was found, or null when no map was found
     */
    @Nullable
    Map getMap(int id);

    /**
     * Retrieve a {@link Map} linked to this {@link World} instance
     *
     * @param world The {@link World} instance to search this {@link Map} for
     * @return The {@link Map} linked to this world
     * @throws NullPointerException when the given world is null
     */
    @Nonnull
    Map getMap(@Nonnull World world);

    /**
     * Unload and remove the {@link Map} from the system
     *
     * @param map The {@link Map} to unload and remove
     * @throws NullPointerException when the given map is null
     */
    void removeMap(@Nonnull Map map);
}
