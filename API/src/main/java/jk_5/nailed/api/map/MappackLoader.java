package jk_5.nailed.api.map;

import java.io.*;
import java.util.*;
import javax.annotation.*;

import jk_5.nailed.api.concurrent.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackLoader {

    @Nonnull
    File getMappackFolder();
    @Nonnull
    List<Mappack> getMappacks();
    @Nullable
    Mappack getMappack(@Nonnull String mappackID);
    void loadMappacks(@Nullable Callback<MappackLoader> callback);
    void registerMappack(@Nonnull Mappack mappack);
    void registerReloadListener(@Nonnull MappackReloadListener listener);
}
