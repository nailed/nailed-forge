package jk_5.nailed.ipc.mappack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcMappackRegistry {

    private static final Map<String, IpcMappack> mappackIds = Maps.newHashMap();
    private static final List<IpcMappack> mappacks = Lists.newArrayList();
    private static final Logger logger = LogManager.getLogger();

    public static void addMappack(final IpcMappack mappack){
        if(mappackIds.containsKey(mappack.getMappackID())){
            mappacks.remove(mappackIds.remove(mappack.getMappackID()));
        }
        mappacks.add(mappack);
        mappackIds.put(mappack.getMappackID(), mappack);
        logger.info("Registered IpcMappack " + mappack.getMappackID());
        mappack.filestore.requestMissingFiles(null);
        mappack.luaFilestore.requestMissingFiles(null);
    }
}
