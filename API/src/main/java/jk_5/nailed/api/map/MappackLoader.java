package jk_5.nailed.api.map;

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
    public void loadMappacks();
    public void registerMappack(@Nonnull Mappack mappack);
    public void registerReloadListener(@Nonnull MappackReloadListener listener);
}
