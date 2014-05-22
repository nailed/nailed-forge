package jk_5.nailed.api.map;

import javax.annotation.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackReloadListener {

    void onReload(@Nonnull MappackLoader loader);
}
