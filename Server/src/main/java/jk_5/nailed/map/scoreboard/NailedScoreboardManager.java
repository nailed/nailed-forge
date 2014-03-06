package jk_5.nailed.map.scoreboard;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.ScoreboardManager;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;

import java.util.EnumMap;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedScoreboardManager implements ScoreboardManager {

    private final Map map;
    private final Set<Objective> objectives = Sets.newHashSet();
    private final Set<ScoreboardTeam> teams = Sets.newHashSet();

    private final EnumMap<DisplayType, Objective> displayLocations = Maps.newEnumMap(DisplayType.class);

    @Override
    public Objective getOrCreateObjective(String name){
        Objective obj = this.getObjective(name);
        if(obj != null){
            return obj;
        }
        obj = new ObjectiveImpl(this.map, name);
        this.objectives.add(obj);

        S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
        packet.field_149343_a = obj.getId();
        packet.field_149341_b = obj.getDisplayName();
        packet.field_149342_c = 0;
        this.map.broadcastPacket(packet);

        return obj;
    }

    @Override
    public Objective getObjective(String name){
        for(Objective objective : this.objectives){
            if(objective.getId().equals(name)){
                return objective;
            }
        }
        return null;
    }

    @Override
    public void onPlayerJoinedMap(Player player){
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
            if(team.isFriendlyFire()) flags |= 0x1;
            if(team.isFriendlyInvisiblesVisible()) flags |= 0x2;

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
    public void onPlayerLeftMap(Player player){
        if(player.isOnline()){
            for(Objective objective : this.objectives){
                S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
                packet.field_149343_a = objective.getId();
                packet.field_149341_b = objective.getDisplayName();
                packet.field_149342_c = 1;
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
    public void setDisplay(DisplayType type, Objective objective){
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
    public ScoreboardTeam getOrCreateTeam(String id){
        ScoreboardTeam team = this.getTeam(id);
        if(team != null){
            return team;
        }
        team = new TeamImpl(id, this.map);
        this.teams.add(team);

        int flags = 0;
        if(team.isFriendlyFire()) flags |= 0x1;
        if(team.isFriendlyInvisiblesVisible()) flags |= 0x2;

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
    public ScoreboardTeam getTeam(String id){
        for(ScoreboardTeam team : this.teams){
            if(team.getId().equals(id)){
                return team;
            }
        }
        return null;
    }
}
