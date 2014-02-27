package jk_5.nailed.map.game;

import jk_5.nailed.api.map.GameManager;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.map.NailedMap;
import jk_5.nailed.map.script.ServerMachine;
import jk_5.nailed.map.stat.StatTypeManager;
import jk_5.nailed.map.stat.types.StatTypeGameHasWinner;
import jk_5.nailed.map.stat.types.StatTypeGameloopRunning;
import jk_5.nailed.map.stat.types.StatTypeGameloopStopped;
import jk_5.nailed.map.stat.types.StatTypeIsWinner;
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
    @Getter private boolean gameRunning;
    @Getter private PossibleWinner winner = null;

    @Override
    public void setCountdownMessage(String message){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.TimeUpdate(true, message), this.map.getID());
    }

    @Override
    public void setWinner(PossibleWinner winner){
        if(!this.gameRunning || this.winner != null) return;
        this.winner = winner;
        this.gameRunning = false;
        this.stopGame();
        StatTypeManager.instance().getStatType(StatTypeGameHasWinner.class).onWin(this.map);
        StatTypeManager.instance().getStatType(StatTypeIsWinner.class).onWinnerSet(this.map, winner);
        this.map.broadcastNotification("Winner is " + winner.getWinnerColoredName());
    }

    public void startGame(){
        //TODO: start lua
        ServerMachine machine = ((NailedMap) this.map).getMachine();
        machine.getTerminal();
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onStart(this.map);
    }

    public void stopGame(){
        this.gameRunning = false;
        StatTypeManager.instance().getStatType(StatTypeGameloopStopped.class).onEnd(this.map);
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onEnd(this.map);
    }
}
