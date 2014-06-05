package jk_5.nailed.ipc.mappack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jk_5.nailed.NailedLog;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Mappack;
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
    private static final CountDownLatch lobbyLoadLatch = new CountDownLatch(2);

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

        Callback<Void> cb = new Callback<Void>() {
            @Override
            public void callback(Void obj) {
                if(mappack.getMappackID().equals("lobby")) lobbyLoadLatch.countDown();
            }
        };
        mappack.filestore.requestMissingFiles(cb);
        mappack.luaFilestore.requestMissingFiles(cb);
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

    public static Mappack getLobbyMappack() {
        NailedLog.info("Lobby mappack is not loaded yet. Waiting for it to load");
        try{
            lobbyLoadLatch.await();
        }catch(InterruptedException ignored){}
        NailedLog.info("Finished waiting for lobby mappack");
        return mappackIds.get("lobby");
    }
}
