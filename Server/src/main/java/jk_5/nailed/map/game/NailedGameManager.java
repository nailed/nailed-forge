package jk_5.nailed.map.game;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.GameManager;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.NailedMap;
import jk_5.nailed.map.stat.StatTypeManager;
import jk_5.nailed.map.stat.types.StatTypeGameHasWinner;
import jk_5.nailed.map.stat.types.StatTypeGameloopRunning;
import jk_5.nailed.map.stat.types.StatTypeGameloopStopped;
import jk_5.nailed.map.stat.types.StatTypeIsWinner;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.ChatColor;

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
    public void setCountdownMessage(String message){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.TimeUpdate(message), this.map.getID());
    }

    @Override
    public void setWinner(PossibleWinner winner){
        if(!this.gameRunning || this.winner != null) return;
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

    public void startGame(){
        ((NailedMap) this.map).getMachine().queueEvent("game_start");
    }

    public void stopGame(){
        ((NailedMap) this.map).getMachine().queueEvent("game_stop");
    }

    @Override
    public void onStarted(){
        this.gameRunning = true;
        StatTypeManager.instance().getStatType(StatTypeGameloopRunning.class).onStart(this.map);
    }

    @Override
    public void onStopped(boolean finished){
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
