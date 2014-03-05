package jk_5.nailed.map.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.ScoreboardManager;
import jk_5.nailed.api.player.Player;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.server.MinecraftServer;

import java.util.EnumMap;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedScoreboardManager implements ScoreboardManager {

    private final Map map;
    private final List<Objective> objectives = Lists.newArrayList();

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
        packet.field_149343_a = obj.getID();
        packet.field_149341_b = obj.getDisplayName();
        packet.field_149342_c = 0;
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.map.getID());

        return obj;
    }

    @Override
    public Objective getObjective(String name){
        for(Objective objective : this.objectives){
            if(objective.getDisplayName().equals(name)){
                return objective;
            }
        }
        return null;
    }

    @Override
    public void onPlayerJoinedMap(Player player){
        for(Objective objective : this.objectives){
            S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
            packet.field_149343_a = objective.getID();
            packet.field_149341_b = objective.getDisplayName();
            packet.field_149342_c = 0;
            player.sendPacket(packet);
        }

        for(java.util.Map.Entry<DisplayType, Objective> e : this.displayLocations.entrySet()){
            S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard();
            packet.field_149374_a = e.getKey().getId();
            packet.field_149373_b = e.getValue().getID();
            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.map.getID());
        }

        Objective obj = this.getOrCreateObjective("MapInfo");
        this.setDisplay(DisplayType.SIDEBAR, obj);
        S3CPacketUpdateScore p = new S3CPacketUpdateScore();
        p.field_149329_a = "MapID";
        p.field_149327_b = obj.getID();
        p.field_149328_c = this.map.getID();
        p.field_149326_d = 0;
        player.sendPacket(p);
    }

    @Override
    public void onPlayerLeftMap(Player player){
        if(player.isOnline()){
            for(Objective objective : this.objectives){
                S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
                packet.field_149343_a = objective.getID();
                packet.field_149341_b = objective.getDisplayName();
                packet.field_149342_c = 1;
                player.sendPacket(packet);
            }
            for(DisplayType e : this.displayLocations.keySet()){
                S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard();
                packet.field_149374_a = e.getId();
                packet.field_149373_b = "";
                MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.map.getID());
            }
        }
    }

    @Override
    public void setDisplay(DisplayType type, Objective objective){
        S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard();
        packet.field_149374_a = type.getId();
        if(objective == null){
            this.displayLocations.remove(type);
            packet.field_149373_b = "";
        }else{
            this.displayLocations.remove(type);
            packet.field_149373_b = objective.getID();
        }
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.map.getID());
    }
}
