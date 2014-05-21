package jk_5.nailed.map.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.api.scripting.ILuaObject;
import net.minecraft.network.play.server.S3EPacketTeams;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamImpl implements ScoreboardTeam, ILuaObject {

    private final Map map;
    private final String id;
    private String displayName;
    private String prefix = "";
    private String suffix = "";
    private boolean friendlyFire = true;
    private boolean friendlyInvisiblesVisible = false;
    private final Set<Player> players = Sets.newHashSet();

    public TeamImpl(String id, Map map){
        this.id = id;
        this.displayName = id;
        this.map = map;
    }

    @Override
    public void setDisplayName(@Nonnull String displayName){
        Preconditions.checkNotNull(displayName, "displayName");
        this.displayName = displayName;
        this.sendUpdates();
    }

    @Override
    public void setPrefix(@Nonnull String prefix){
        Preconditions.checkNotNull(prefix, "prefix");
        this.prefix = prefix;
        this.sendUpdates();
    }

    @Override
    public void setSuffix(@Nonnull String suffix){
        Preconditions.checkNotNull(suffix, "suffix");
        this.suffix = suffix;
        this.sendUpdates();
    }

    @Override
    public void setFriendlyFire(boolean friendlyFire){
        this.friendlyFire = friendlyFire;
        this.sendUpdates();
    }

    @Override
    public void setFriendlyInvisiblesVisible(boolean friendlyInvisiblesVisible){
        this.friendlyInvisiblesVisible = friendlyInvisiblesVisible;
        this.sendUpdates();
    }

    public void sendUpdates(){
        int flags = 0;
        if(this.isFriendlyFire()) flags |= 0x1;
        if(this.isFriendlyInvisiblesVisible()) flags |= 0x2;

        S3EPacketTeams packet = new S3EPacketTeams();
        packet.field_149320_a = this.getId();
        packet.field_149318_b = this.getDisplayName();
        packet.field_149319_c = this.getPrefix();
        packet.field_149316_d = this.getSuffix();
        packet.field_149317_e = this.getPlayerNames();
        packet.field_149314_f = 2; //Update
        packet.field_149315_g = flags;
        this.map.broadcastPacket(packet);
    }

    @Override
    public boolean addPlayer(@Nonnull Player player){
        Preconditions.checkNotNull(player, "player");
        if(this.players.add(player)){
            S3EPacketTeams packet = new S3EPacketTeams();
            packet.field_149320_a = this.getId();
            packet.field_149317_e = this.getPlayerNames();
            packet.field_149314_f = 3; //Add Player
            this.map.broadcastPacket(packet);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayer(@Nonnull Player player){
        Preconditions.checkNotNull(player, "player");
        if(this.players.remove(player)){
            S3EPacketTeams packet = new S3EPacketTeams();
            packet.field_149320_a = this.getId();
            packet.field_149317_e = this.getPlayerNames();
            packet.field_149314_f = 4; //Remove Player
            this.map.broadcastPacket(packet);
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public Set<String> getPlayerNames(){
        Set<String> names = Sets.newHashSet();
        for(Player p : this.players){
            names.add(p.getUsername());
        }
        return names;
    }

    @Override
    @Nonnull
    public String getId(){
        return id;
    }

    @Override
    @Nonnull
    public String getDisplayName(){
        return displayName;
    }

    @Override
    @Nonnull
    public String getPrefix(){
        return prefix;
    }

    @Override
    @Nonnull
    public String getSuffix(){
        return suffix;
    }

    @Override
    @Nonnull
    public Set<Player> getPlayers(){
        return players;
    }

    @Override
    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    @Override
    public boolean isFriendlyInvisiblesVisible() {
        return this.friendlyInvisiblesVisible;
    }

    @Override
    public String[] getMethodNames(){
        return new String[]{
                "getId",
                "getDisplayName",
                "setDisplayName",
                "getType",
                "getPrefix",
                "setPrefix",
                "getSuffix",
                "setSuffix",
                "isFriendlyFire",
                "setFriendlyFire",
                "isFriendlyInvisiblesVisible",
                "setFriendlyInvisiblesVisible",
                "addPlayer",
                "removePlayer",
                "getPlayers",
                "forEachPlayer"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0: //getId
                return new Object[]{this.getId()};
            case 1: //getDisplayName
                return new Object[]{this.getDisplayName()};
            case 2: //setDisplayName
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.setDisplayName((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 3: //getType
                return new Object[]{"scoreboardTeam"};
            case 4: //getPrefix
                return new Object[]{this.getPrefix()};
            case 5: //setPrefix
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.setPrefix((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 6: //getSuffix
                return new Object[]{this.getSuffix()};
            case 7: //setSuffix
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.setSuffix((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 8: //isFriendlyFire
                return new Object[]{this.isFriendlyFire()};
            case 9: //setFriendlyFire
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.setFriendlyFire((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 10: //isFriendlyInvisiblesVisible
                return new Object[]{this.isFriendlyInvisiblesVisible()};
            case 11: //setFriendlyInvisiblesVisible
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.setFriendlyInvisiblesVisible((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 12: //addPlayer
                if(arguments.length == 1 && arguments[0] instanceof HashMap){
                    try{
                        //noinspection unchecked
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if(type.equals("player")){
                            String username = obj.get("getUsername").call().checkjstring();
                            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                            if(player == null){
                                throw new Exception("Player " + username + " does not exist");
                            }
                            if(player.getCurrentMap() != map){
                                throw new Exception("Player " + username + " is not in this world");
                            }
                            return new Object[]{this.addPlayer(player)};
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("The object passed is not a player");
                    }
                }else{
                    throw new Exception("Expected 1 player argument");
                }
                break;
            case 13: //removePlayer
                if(arguments.length == 1 && arguments[0] instanceof HashMap){
                    try{
                        //noinspection unchecked
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if(type.equals("player")){
                            String username = obj.get("getUsername").call().checkjstring();
                            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                            if(player == null){
                                throw new Exception("Player " + username + " does not exist");
                            }
                            if(player.getCurrentMap() != map){
                                throw new Exception("Player " + username + " is not in this world");
                            }
                            return new Object[]{this.removePlayer(player)};
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("The object passed is not a player");
                    }
                }else{
                    throw new Exception("Expected 1 player argument");
                }
                break;
            case 14: //getPlayers
                Set<Player> players = this.getPlayers();
                java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                int i = 1;
                for(Player player : players){
                    table.put(i, player);
                    i++;
                }
                return new Object[]{table};
            case 15: //forEachPlayer
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    Set<Player> players1 = this.getPlayers();
                    for(Player player : players1){
                        closure.invoke(LuaValue.varargsOf(context.toValues(new Object[]{player}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
        }
        return new Object[0];
    }
}
