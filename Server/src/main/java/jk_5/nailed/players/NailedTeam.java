package jk_5.nailed.players;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;
import jk_5.nailed.util.ChatColor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedTeam implements Team {

    private final Map map;
    private final String teamId;

    private String name;
    private ChatColor color = ChatColor.RESET;
    private Player leader;
    private boolean ready = false;
    private boolean friendlyFireEnabled = false;
    private boolean seeFriendlyInvisibles = false;
    private ScoreboardTeam scoreboardTeam;
    private Location spawnpoint;
    private int teamSpeakChannelID = -1;

    public NailedTeam(Map map, String teamId) {
        this.map = map;
        this.teamId = teamId;
    }

    @Override
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

    @Override
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

    @Override
    public boolean shouldOverrideDefaultSpawnpoint(){
        return this.spawnpoint != null;
    }

    @Override
    public String getColoredName(){
        return this.color + this.name + ChatColor.RESET;
    }

    @Override
    public List<Player> getMembers(){
        List<Player> ret = Lists.newArrayList();
        for(Player player : this.map.getPlayers()){
            if(player.getTeam() == this){
                ret.add(player);
            }
        }
        return ret;
    }

    @Override
    public void onAddPlayer(Player player){
        this.addPlayerToScoreboardTeam(player);
    }

    @Override
    public void onRemovePlayer(Player player){
        this.removePlayerFromScoreboardTeam(player);
    }

    @Override
    public void addPlayerToScoreboardTeam(Player player){
        if(this.scoreboardTeam == null) return;
        this.scoreboardTeam.addPlayer(player);
    }

    @Override
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

    @Override
    public Map getMap() {
        return this.map;
    }

    @Override
    public String getTeamId() {
        return this.teamId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ChatColor getColor() {
        return this.color;
    }

    @Override
    public Player getLeader() {
        return this.leader;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public boolean isFriendlyFireEnabled() {
        return this.friendlyFireEnabled;
    }

    @Override
    public ScoreboardTeam getScoreboardTeam() {
        return this.scoreboardTeam;
    }

    @Override
    public Location getSpawnpoint() {
        return this.spawnpoint;
    }

    public int getTeamSpeakChannelID() {
        return this.teamSpeakChannelID;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public void setLeader(Player leader) {
        this.leader = leader;
    }

    @Override
    public void setFriendlyFireEnabled(boolean friendlyFireEnabled) {
        this.friendlyFireEnabled = friendlyFireEnabled;
    }

    @Override
    public void setSeeFriendlyInvisibles(boolean seeFriendlyInvisibles) {
        this.seeFriendlyInvisibles = seeFriendlyInvisibles;
    }

    @Override
    public void setScoreboardTeam(ScoreboardTeam scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }

    @Override
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    public void setTeamSpeakChannelID(int teamSpeakChannelID) {
        this.teamSpeakChannelID = teamSpeakChannelID;
    }
}
