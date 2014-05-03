package jk_5.nailed.ipc.mappack;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcMappackRegistry {

    private static final List<IpcMappack> mappacks = Lists.newArrayList();
    private static final Logger logger = LogManager.getLogger();

    public static void addMappack(IpcMappack mappack){
        mappacks.add(mappack);
        logger.info("Loaded IpcMappack " + mappack.getMappackID());
    }
}
