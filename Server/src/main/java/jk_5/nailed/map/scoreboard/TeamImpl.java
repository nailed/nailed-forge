package jk_5.nailed.map.scoreboard;

import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import lombok.Getter;
import net.minecraft.network.play.server.S3EPacketTeams;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamImpl implements ScoreboardTeam {

    private final Map map;
    @Getter private final String id;
    @Getter private String displayName;
    @Getter private String prefix = "";
    @Getter private String suffix = "";
    @Getter private boolean friendlyFire = true;
    @Getter private boolean friendlyInvisiblesVisible = false;
    @Getter private final Set<Player> players = Sets.newHashSet();

    public TeamImpl(String id, Map map){
        this.id = id;
        this.displayName = id;
        this.map = map;
    }

    @Override
    public void setDisplayName(String displayName){
        this.displayName = displayName;
        this.sendUpdates();
    }

    @Override
    public void setPrefix(String prefix){
        this.prefix = prefix;
        this.sendUpdates();
    }

    @Override
    public void setSuffix(String suffix){
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
    public boolean addPlayer(Player player){
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
    public boolean removePlayer(Player player){
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
    public Set<String> getPlayerNames(){
        Set<String> names = Sets.newHashSet();
        for(Player p : this.players){
            names.add(p.getUsername());
        }
        return names;
    }
}
