package jk_5.nailed.map.scoreboard;

import java.util.*;
import javax.annotation.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import net.minecraft.network.play.server.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedScoreboardManager implements ScoreboardManager {

    private final Map map;
    private final Set<Objective> objectives = Sets.newHashSet();
    private final Set<ScoreboardTeam> teams = Sets.newHashSet();

    private final EnumMap<DisplayType, Objective> displayLocations = Maps.newEnumMap(DisplayType.class);

    public NailedScoreboardManager(Map map) {
        this.map = map;
    }

    @Override
    @Nonnull
    public Objective getOrCreateObjective(@Nonnull String id) {
        Preconditions.checkNotNull(id, "id");
        Preconditions.checkArgument(id.length() <= 16, "id may not be longer than 16");

        Objective obj = this.getObjective(id);
        if(obj != null){
            return obj;
        }
        obj = new ObjectiveImpl(this.map, id);
        this.objectives.add(obj);

        S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
        packet.field_149343_a = obj.getId();
        packet.field_149341_b = obj.getDisplayName();
        packet.field_149342_c = 0;
        this.map.broadcastPacket(packet);

        return obj;
    }

    @Override
    @Nullable
    public Objective getObjective(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name");
        for(Objective objective : this.objectives){
            if(objective.getId().equals(name)){
                return objective;
            }
        }
        return null;
    }

    @Override
    public void onPlayerJoinedMap(@Nonnull Player player) {
        Preconditions.checkNotNull(player, "player");
        for(Objective objective : this.objectives){
            S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
            packet.field_149343_a = objective.getId();
            packet.field_149341_b = objective.getDisplayName();
            packet.field_149342_c = 0;
            player.sendPacket(packet);

            ((ObjectiveImpl) objective).sendData(player);
        }

        for(java.util.Map.Entry<DisplayType, Objective> e : this.displayLocations.entrySet()){
            S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard();
            packet.field_149374_a = e.getKey().getId();
            packet.field_149373_b = e.getValue().getId();
            player.sendPacket(packet);
        }

        for(ScoreboardTeam team : this.teams){
            int flags = 0;
            if(team.isFriendlyFire()){
                flags |= 0x1;
            }
            if(team.isFriendlyInvisiblesVisible()){
                flags |= 0x2;
            }

            S3EPacketTeams packet = new S3EPacketTeams();
            packet.field_149320_a = team.getId();
            packet.field_149318_b = team.getDisplayName();
            packet.field_149319_c = team.getPrefix();
            packet.field_149316_d = team.getSuffix();
            packet.field_149317_e = team.getPlayerNames();
            packet.field_149314_f = 0; //Create
            packet.field_149315_g = flags;
            player.sendPacket(packet);
        }
    }

    @Override
    public void onPlayerLeftMap(@Nonnull Player player) {
        Preconditions.checkNotNull(player, "player");
        if(player.isOnline()){
            for(Objective objective : this.objectives){
                S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
                packet.field_149343_a = objective.getId();
                packet.field_149341_b = objective.getDisplayName();
                packet.field_149342_c = 1; //Remove
                player.sendPacket(packet);
            }

            for(ScoreboardTeam team : this.teams){
                S3EPacketTeams packet = new S3EPacketTeams();
                packet.field_149320_a = team.getId();
                packet.field_149314_f = 1; //Remove
                player.sendPacket(packet);
            }
        }
    }

    @Override
    public void setDisplay(@Nonnull DisplayType type, @Nullable Objective objective) {
        Preconditions.checkNotNull(type, "type");
        //if(this.displayLocations.get(type) == objective){
        //    return;
        //}
        S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard();
        packet.field_149374_a = type.getId();
        if(objective == null){
            this.displayLocations.remove(type);
            packet.field_149373_b = "";
        }else{
            this.displayLocations.put(type, objective);
            packet.field_149373_b = objective.getId();
        }
        this.map.broadcastPacket(packet);
    }

    @Override
    @Nonnull
    public ScoreboardTeam getOrCreateTeam(@Nonnull String id) {
        Preconditions.checkNotNull(id, "id");
        ScoreboardTeam team = this.getTeam(id);
        if(team != null){
            return team;
        }
        team = new TeamImpl(id, this.map);
        this.teams.add(team);

        int flags = 0;
        if(team.isFriendlyFire()){
            flags |= 0x1;
        }
        if(team.isFriendlyInvisiblesVisible()){
            flags |= 0x2;
        }

        S3EPacketTeams packet = new S3EPacketTeams();
        packet.field_149320_a = team.getId();
        packet.field_149318_b = team.getDisplayName();
        packet.field_149319_c = team.getPrefix();
        packet.field_149316_d = team.getSuffix();
        packet.field_149317_e = ImmutableList.of();
        packet.field_149314_f = 0; //Create
        packet.field_149315_g = flags;
        this.map.broadcastPacket(packet);

        return team;
    }

    @Override
    @Nullable
    public ScoreboardTeam getTeam(@Nonnull String id) {
        Preconditions.checkNotNull(id, "id");
        for(ScoreboardTeam team : this.teams){
            if(team.getId().equals(id)){
                return team;
            }
        }
        return null;
    }
}
