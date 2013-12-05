package jk_5.nailed.players;

import jk_5.nailed.map.Map;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class Team {

    @Getter private final Map map;
    @Getter private final String teamId;

    @Getter @Setter private String name;
    @Getter @Setter private ChatColor color;
    @Getter @Setter private Player leader;
    @Getter private boolean ready = false;
    @Getter @Setter private boolean friendlyFireEnabled = false;
    @Getter @Setter private ScorePlayerTeam scoreboardTeam;
    @Getter @Setter private ChunkCoordinates spawnPoint;

    public void setReady(boolean ready){
        this.ready = ready;
        if(this.isReady()){
            this.map.broadcastChatMessage("Team " + this.getColoredName() + " is ready!");
        }else{
            this.map.broadcastChatMessage("Team " + this.getColoredName() + " is not ready!");
        }
        //TODO
        //this.map.getGameController().updateReadyStates();
    }

    public void broadcastChatMessage(String message){
        this.broadcastChatMessage(ChatMessageComponent.createFromText(message));
    }

    public void broadcastChatMessage(ChatMessageComponent message){
        for(Player player : PlayerRegistry.instance().getPlayers()){
            if(player.getTeam() == this){
                player.sendChat(message);
            }
        }
    }

    public boolean shouldOverrideDefaultSpawnpoint(){
        return this.spawnPoint != null;
    }

    public String getColoredName(){
        return this.color + this.name + ChatColor.RESET;
    }

    public class TeamScoreboardWrapper extends net.minecraft.scoreboard.Team {

        @Override
        public String func_96661_b() {
            return null;
        }

        @Override
        public String func_142053_d(String s) {
            return null;
        }

        @Override
        public boolean func_98297_h() {
            return false;
        }

        @Override
        public boolean getAllowFriendlyFire() {
            return false;
        }
    }
}
