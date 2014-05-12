package jk_5.nailed.map;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;

import java.util.List;
import java.util.Random;

/**
 * Map for the normal overworld, used for the lobby
 * This uses a fixed dimension id of 0. Only use this for dimension 0!
 *
 * @author jk-5
 */
public class LobbyMap extends NailedMap {
    private Random random = new Random();

    public LobbyMap(){
        super(NailedAPI.getMappackLoader().getMappack("lobby"), 0);
        if(this.getMappack() != null){
            this.getMappack().prepareWorld(this.getSaveFolder(), null);
        }
    }

    @Override
    public void onPlayerJoined(Player player){
        super.onPlayerJoined(player);
        if (this.getAmountOfPlayers() < 40) {
            player.setPlayersVisible(this.getPlayers());
        } else {
            List<Player> allPlayers = this.getPlayers();
            List<Player> visiblePlayers = Lists.newArrayList();
            for(int a = 0; a < 40; ++a){
                Player b = allPlayers.get(this.random.nextInt() % allPlayers.size());
                allPlayers.remove(b);
                visiblePlayers.add(b);
            }
            player.setPlayersVisible(visiblePlayers);
        }
    }

    @Override
    public void onPlayerLeft(Player player){
        super.onPlayerLeft(player);
        if( this.getAmountOfPlayers() < 40){
            List<Player> mapPlayers = this.getPlayers();
            for (Player otherPlayer: mapPlayers){
                otherPlayer.setPlayersVisible(mapPlayers);
            }
        } else {
            List<Player> mapPlayers = this.getPlayers();
            for (Player otherPlayer: mapPlayers){
                otherPlayer.replacePlayerVisible(player, mapPlayers, this.random);
            }
        }
    }
}
