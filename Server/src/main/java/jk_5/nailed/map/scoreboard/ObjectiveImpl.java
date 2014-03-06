package jk_5.nailed.map.scoreboard;

import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.Score;
import jk_5.nailed.api.player.Player;
import lombok.Getter;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class ObjectiveImpl implements Objective {

    @Getter private final Map map;
    @Getter private final String id;
    @Getter private String displayName;
    private final Set<Score> scores = Sets.newHashSet();

    public ObjectiveImpl(Map map, String id){
        this.map = map;
        this.id = id;
        this.displayName = id;
    }

    @Override
    public void setDisplayName(String displayName){
        this.displayName = displayName;
        S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
        packet.field_149343_a = this.getId();
        packet.field_149341_b = this.displayName;
        packet.field_149342_c = 2;
        this.map.broadcastPacket(packet);
    }

    @Override
    public Score getScore(String name){
        for(Score score : this.scores){
            if(score.getName().equals(name)){
                return score;
            }
        }
        Score score = new ScoreImpl(this, name);
        this.scores.add(score);
        return score;
    }

    @Override
    public void removeScore(Score score){
        if(this.scores.remove(score)){
            S3CPacketUpdateScore p = new S3CPacketUpdateScore();
            p.field_149329_a = score.getName();
            p.field_149326_d = 1;
            this.map.broadcastPacket(p);
        }
    }

    public void sendData(Player player){
        for(Score score : this.scores){
            S3CPacketUpdateScore p = new S3CPacketUpdateScore();
            p.field_149329_a = score.getName();
            p.field_149327_b = this.getId();
            p.field_149328_c = score.getValue();
            p.field_149326_d = 0;
            player.sendPacket(p);
        }
    }
}
