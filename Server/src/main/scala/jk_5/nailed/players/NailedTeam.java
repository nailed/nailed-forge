package jk_5.nailed.players;

import java.util.*;

import com.google.common.collect.*;

import org.luaj.vm2.*;

import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedTeam implements Team, ILuaObject {

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
    public void onWorldSet() {
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
    public void setReady(boolean ready) {
        this.ready = ready;
        if(this.isReady()){
            this.map.broadcastChatMessage("Team " + this.getColoredName() + " is ready!");
        }else{
            this.map.broadcastChatMessage("Team " + this.getColoredName() + " is not ready!");
        }
        //TODO
        //this.map.getGameController().updateReadyStates();
    }

    public void broadcastChatMessage(String message) {
        this.broadcastChatMessage(new ChatComponentText(message));
    }

    public void broadcastChatMessage(IChatComponent message) {
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.getTeam() == this){
                player.sendChat(message);
            }
        }
    }

    @Override
    public boolean shouldOverrideDefaultSpawnpoint() {
        return this.spawnpoint != null;
    }

    @Override
    public String getColoredName() {
        return this.color + this.name + ChatColor.RESET;
    }

    @Override
    public List<Player> getMembers() {
        List<Player> ret = Lists.newArrayList();
        for(Player player : this.map.getPlayers()){
            if(player.getTeam() == this){
                ret.add(player);
            }
        }
        return ret;
    }

    @Override
    public List<String> getMemberNames(){
        List<String> names = Lists.newArrayList();
        for(Player player: this.getMembers()){
            names.add(player.getUsername());
        }
        return names;
    }

    @Override
    public List<String> getPrefixNames(){
        List<String> names = Lists.newArrayList();
        for(Player player: this.getMembers()){
            names.add(player.getChatPrefix());
        }
        return names;
    }

    @Override
    public void onAddPlayer(Player player) {
        this.addPlayerToScoreboardTeam(player);
    }

    @Override
    public void onRemovePlayer(Player player) {
        this.removePlayerFromScoreboardTeam(player);
    }

    @Override
    public void addPlayerToScoreboardTeam(Player player) {
        if(this.scoreboardTeam == null){
            return;
        }
        this.scoreboardTeam.addPlayer(player);
    }

    @Override
    public void removePlayerFromScoreboardTeam(Player player) {
        if(this.scoreboardTeam == null){
            return;
        }
        this.scoreboardTeam.removePlayer(player);
    }

    @Override
    public String getWinnerName() {
        return this.name;
    }

    @Override
    public String getWinnerColoredName() {
        return this.color + this.name;
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
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

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "getName",
                "getPlayers",
                "forEachPlayer",
                "setSpawn",
                "getType",
                "getID",
                "setMinFood",
                "setMinHealth",
                "setMaxFood",
                "setMaxHealth"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method){
            case 0: //getName
                return new Object[]{this.getName()};
            case 1: //getPlayers
                List<Player> players = this.getMembers();
                java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                for(int i = 0; i < players.size(); i++){
                    table.put(i + 1, players.get(i));
                }
                return new Object[]{table};
            case 2: //forEachPlayer
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    List<Player> players1 = this.getMembers();
                    for(int i = 0; i < players1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(context.toValues(new Object[]{players1.get(i)}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
            case 3: //setSpawn
                if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                    Location spawn = new Location((Double) arguments[0], (Double) arguments[1], (Double) arguments[2]);
                    this.setSpawnpoint(spawn);
                }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                    Location spawn = new Location((Double) arguments[0], (Double) arguments[1], (Double) arguments[2], ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                    this.setSpawnpoint(spawn);
                }else{
                    throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                }
                break;
            case 4: //getType
                return new Object[]{"team"};
            case 5: //getID
                return new Object[]{this.getTeamId()};
            case 6: // setMinFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.getMembers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMinFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 7: // setMinHealth
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.getMembers()){
                        player.setMinHealth(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 8: // setMaxFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.getMembers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 9: // setMaxHealth
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.getMembers()){
                        player.setMaxHealth(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
        }
        return null;
    }
}
