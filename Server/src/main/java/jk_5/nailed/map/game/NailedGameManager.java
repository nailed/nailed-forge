package jk_5.nailed.map.game;

import jk_5.nailed.api.map.GameManager;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedGameManager implements GameManager {

    private final Map map;
    @Getter @Setter private boolean watchUnready = false;
    @Getter @Setter private boolean winnerInterrupt = false;

    @Override
    public void setCountdownMessage(String message){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.TimeUpdate(true, message), this.map.getID());
    }
}
