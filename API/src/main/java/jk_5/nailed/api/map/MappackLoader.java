package jk_5.nailed.api.map;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackLoader {

    public File getMappackFolder();
    public List<Mappack> getMappacks();
    public Mappack getMappack(String mappackID);
    public void loadMappacks();
    public void registerMappack(Mappack mappack);
}
