package jk_5.nailed.api.map;

import jk_5.nailed.api.concurrent.Callback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackLoader {

    @Nonnull public File getMappackFolder();
    @Nonnull public List<Mappack> getMappacks();
    @Nullable public Mappack getMappack(@Nonnull String mappackID);
    public void loadMappacks(@Nullable Callback<MappackLoader> callback);
    public void registerMappack(@Nonnull Mappack mappack);
    public void registerReloadListener(@Nonnull MappackReloadListener listener);
}
