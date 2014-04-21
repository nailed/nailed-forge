package jk_5.nailed.players;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedTeam implements Team {

    @Getter private final Map map;
    @Getter private final String teamId;

    @Getter @Setter private String name;
    @Getter @Setter private ChatColor color = ChatColor.RESET;
    @Getter @Setter private Player leader;
    @Getter private boolean ready = false;
    @Getter @Setter private boolean friendlyFireEnabled = false;
    @Setter private boolean seeFriendlyInvisibles = false;
    @Getter @Setter private ScoreboardTeam scoreboardTeam;
    @Getter @Setter private Location spawnpoint;
    @Getter @Setter private int teamSpeakChannelID = -1;

    public void onWorldSet(){
        if(this.scoreboardTeam == null){
            this.scoreboardTeam = this.map.getScoreboardManager().getOrCreateTeam(this.teamId);
            this.scoreboardTeam.setDisplayName(this.name);
            this.scoreboardTeam.setFriendlyFire(this.friendlyFireEnabled);
            this.scoreboardTeam.setFriendlyInvisiblesVisible(this.friendlyFireEnabled);
            this.scoreboardTeam.setPrefix(this.color.toString());
            this.scoreboardTeam.setSuffix(ChatColor.RESET.toString());
        }
    }

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
        this.broadcastChatMessage(new ChatComponentText(message));
    }

    public void broadcastChatMessage(IChatComponent message){
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.getTeam() == this){
                player.sendChat(message);
            }
        }
    }

    public boolean shouldOverrideDefaultSpawnpoint(){
        return this.spawnpoint != null;
    }

    public String getColoredName(){
        return this.color + this.name + ChatColor.RESET;
    }

    public List<Player> getMembers(){
        List<Player> ret = Lists.newArrayList();
        for(Player player : this.map.getPlayers()){
            if(player.getTeam() == this){
                ret.add(player);
            }
        }
        return ret;
    }

    public void onAddPlayer(Player player){
        this.addPlayerToScoreboardTeam(player);
    }

    public void onRemovePlayer(Player player){
        this.removePlayerFromScoreboardTeam(player);
    }

    public void addPlayerToScoreboardTeam(Player player){
        if(this.scoreboardTeam == null) return;
        this.scoreboardTeam.addPlayer(player);
    }

    public void removePlayerFromScoreboardTeam(Player player){
        if(this.scoreboardTeam == null) return;
        this.scoreboardTeam.removePlayer(player);
    }

    @Override
    public String getWinnerName(){
        return this.name;
    }

    @Override
    public String getWinnerColoredName(){
        return this.color + this.name;
    }

    @Override
    public boolean canSeeFriendlyInvisibles(){
        return this.seeFriendlyInvisibles;
    }
}
