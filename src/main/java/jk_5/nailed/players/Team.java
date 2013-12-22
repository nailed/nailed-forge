package jk_5.nailed.players;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

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
    @Getter @Setter private ChatColor color = ChatColor.RESET;
    @Getter @Setter private Player leader;
    @Getter private boolean ready = false;
    @Getter @Setter private boolean friendlyFireEnabled = false;
    @Getter @Setter private boolean seeFriendlyInvisibles = false;
    @Getter @Setter private ScorePlayerTeam scoreboardTeam;
    @Getter @Setter private ChunkCoordinates spawnPoint;

    public void onWorldSet(){
        if(this instanceof TeamUndefined) return;
        String name = "map" + this.map.getID() + this.teamId;
        Scoreboard scoreboard = this.map.getWorld().getScoreboard();
        ScorePlayerTeam scoreplayerteam = scoreboard.func_96508_e(name);
        if(scoreplayerteam == null){
            this.scoreboardTeam = scoreboard.createTeam(name);
            this.scoreboardTeam.setTeamName(this.name);
            this.scoreboardTeam.setAllowFriendlyFire(this.friendlyFireEnabled);
            this.scoreboardTeam.setSeeFriendlyInvisiblesEnabled(this.seeFriendlyInvisibles);
            this.scoreboardTeam.setNamePrefix(this.color.toString());
            this.scoreboardTeam.setNameSuffix(ChatColor.RESET.toString());
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
        if(this instanceof TeamUndefined) return;
        //this.map.getWorld().getScoreboard().addPlayerToTeam(player.getUsername(), this.scoreboardTeam);    //TODO: something crashes the client when this triggers
    }

    public void onRemovePlayer(Player player){
        if(this instanceof TeamUndefined) return;
        //this.map.getWorld().getScoreboard().removePlayerFromTeams(player.getUsername());
    }
}
