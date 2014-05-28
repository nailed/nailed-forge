package jk_5.nailed.map.game;

import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.scheduler.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.script.*;
import jk_5.nailed.map.stat.*;
import jk_5.nailed.map.stat.types.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedGameManager implements GameManager {

    private final Map map;
    private boolean watchUnready = false;
    private boolean winnerInterrupt = false;
    private boolean gameRunning;
    private PossibleWinner winner = null;

    @java.beans.ConstructorProperties({"map"})
    public NailedGameManager(Map map) {
        this.map = map;
    }

    @Override
    public void setCountdownMessage(String message) {
        for(Player player : map.getPlayers()){
            player.sendTimeUpdate(message);
        }
    }

    @Override
    public void setWinner(PossibleWinner winner) {
        if(!this.gameRunning || this.winner != null){
            return;
        }
        this.winner = winner;
        if(this.winnerInterrupt){
            this.gameRunning = false;
            this.stopGame();
        }
        StatTypeManager.instance().getStatType(StatTypeGameHasWinner.class).onWin(this.map);
        StatTypeManager.instance().getStatType(StatTypeIsWinner.class).onWinnerSet(this.map, winner);
        this.map.broadcastChatMessage(ChatColor.GRAY + "------------------------------------------");
        this.map.broadcastChatMessage(ChatColor.GOLD + "Winner is " + winner.getWinnerColoredName());
        this.map.broadcastChatMessage(ChatColor.GRAY + "------------------------------------------");
    }

    public void startGame() {
        NailedMap map = (NailedMap) this.map;
        final ServerMachine machine = map.getMachine();
        if(!machine.getVM().isOn()){
            machine.turnOn();
            machine.terminalChanged = true; //Force an update
            map.mounted = map.getMappack() == null; //Also force a remount of the mappack data, if we have a mappack
            map.mappackMount = null;
        }
        NailedAPI.getScheduler().runTaskLater(new NailedRunnable() {
            @Override
            public void run() {
                machine.queueEvent("game_start");
            }
        }, 2);
    }

    public void stopGame() {
        ((NailedMap) this.map).getMachine().queueEvent("game_stop");
    }

    @Override
    public void onStarted() {
        this.gameRunning = true;
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onStart(this.map);
    }

    @Override
    public void onStopped(boolean finished) {
        this.gameRunning = false;
        StatTypeManager.instance().getStatType(StatTypeGameloopStopped.class).onEnd(this.map);
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onEnd(this.map);

        //Reset some stuff
        this.setCountdownMessage("");
        this.map.getScoreboardManager().setDisplay(DisplayType.BELOW_NAME, null);
        this.map.getScoreboardManager().setDisplay(DisplayType.SIDEBAR, null);
        this.map.getScoreboardManager().setDisplay(DisplayType.BELOW_NAME, null);

        Mappack mappack = this.map.getMappack();
        if(mappack != null){
            switch(mappack.getMappackMetadata().getPostGameAction()){
                case TO_LOBBY:
                    for(Player player : this.map.getPlayers()){
                        player.teleportToLobby();
                    }
                    break;
                case TO_SPAWN:
                    TeleportOptions options = this.map.getSpawnTeleport();
                    for(Player player : this.map.getPlayers()){
                        NailedAPI.getTeleporter().teleportEntity(player.getEntity(), options);
                    }
                    break;
                case NOTHING:
                    break;
            }
        }
    }

    public boolean isWatchUnready() {
        return this.watchUnready;
    }

    public boolean isWinnerInterrupt() {
        return this.winnerInterrupt;
    }

    public boolean isGameRunning() {
        return this.gameRunning;
    }

    public PossibleWinner getWinner() {
        return this.winner;
    }

    public void setWatchUnready(boolean watchUnready) {
        this.watchUnready = watchUnready;
    }

    public void setWinnerInterrupt(boolean winnerInterrupt) {
        this.winnerInterrupt = winnerInterrupt;
    }
}