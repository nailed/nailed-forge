package jk_5.nailed.ipc.mappack;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.ipc.packet.PacketListMappacks;

/**
 * No description given
 *
 * @author jk-5
 */
public final class IpcMappackRegistry {

    private static final Map<String, IpcMappack> mappackIds = Maps.newHashMap();
    private static final List<IpcMappack> mappacks = Lists.newArrayList();
    private static final Logger logger = LogManager.getLogger();

    private static Set<String> remoteMappacks = ImmutableSet.of();

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

    public static void setRemoteMappacks(Set<String> remoteMappacks){
        IpcMappackRegistry.remoteMappacks = ImmutableSet.copyOf(remoteMappacks);
    }

    public static Set<String> getRemoteMappacks(){
        return remoteMappacks;
    }

    public static void requestRemoteMappacks(){
        IpcManager.instance().sendPacket(new PacketListMappacks());
    }
}
