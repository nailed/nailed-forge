package jk_5.nailed.ipc.mappack;

import java.util.*;

import com.google.common.collect.*;

import org.apache.logging.log4j.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class IpcMappackRegistry {

    private static final Map<String, IpcMappack> mappackIds = Maps.newHashMap();
    private static final List<IpcMappack> mappacks = Lists.newArrayList();
    private static final Logger logger = LogManager.getLogger();

    private IpcMappackRegistry(){

    }

    public static void addMappack(final IpcMappack mappack) {
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
