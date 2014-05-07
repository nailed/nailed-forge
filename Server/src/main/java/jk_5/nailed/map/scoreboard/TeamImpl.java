package jk_5.nailed.map.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import net.minecraft.network.play.server.S3EPacketTeams;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamImpl implements ScoreboardTeam {

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
}
