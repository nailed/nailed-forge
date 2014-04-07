package jk_5.nailed.map.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.Score;
import jk_5.nailed.api.player.Player;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * This is the Nailed-implementation of the NailedAPI class {@link Objective}
 *
 * {@inheritDoc}
 */
public class ObjectiveImpl implements Objective {

    private final Map map;
    private final String id; //TODO: Max length of 16
    private String displayName;
    private final Set<Score> scores = Sets.newHashSet();

    /**
     * Creates a new {@link ObjectiveImpl} object for the given map with the given id
     * Note that the id may not be bigger than 16
     *
     * @param map The map that this {@link ObjectiveImpl} is created for
     * @param id The id of the objective (<= 16)
     * @throws IllegalArgumentException When id is bigger than 16
     */
    public ObjectiveImpl(@Nonnull Map map, @Nonnull String id){
        Preconditions.checkNotNull(map, "map");
        Preconditions.checkNotNull(id, "id");
        Preconditions.checkArgument(id.length() <= 16, "id may not be longer than 16");

        this.map = map;
        this.id = id;
        this.displayName = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayName(@Nonnull String displayName){
        Preconditions.checkNotNull(displayName, "displayName");
        Preconditions.checkArgument(displayName.length() <= 32, "displayName may not be longer than 32");

        this.displayName = displayName;
        S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective();
        packet.field_149343_a = this.getId();
        packet.field_149341_b = this.displayName;
        packet.field_149342_c = 2;
        this.map.broadcastPacket(packet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Score getScore(@Nonnull String name){
        Preconditions.checkNotNull(name, "name");
        for(Score score : this.scores){
            if(score.getName().equals(name)){
                return score;
            }
        }
        Score score = new ScoreImpl(this, name);
        this.scores.add(score);
        return score;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeScore(@Nonnull Score score){
        Preconditions.checkNotNull(score, "score");
        if(this.scores.remove(score)){
            S3CPacketUpdateScore p = new S3CPacketUpdateScore();
            p.field_149329_a = score.getName();
            p.field_149326_d = 1;
            this.map.broadcastPacket(p);
        }
    }

    /**
     * This method sends the objective data to the specified player
     *
     * @param player The player to send the Objective data to
     */
    public void sendData(@Nonnull Player player){
        Preconditions.checkNotNull(player, "player");
        for(Score score : this.scores){
            S3CPacketUpdateScore p = new S3CPacketUpdateScore();
            p.field_149329_a = score.getName();
            p.field_149327_b = this.getId();
            p.field_149328_c = score.getValue();
            p.field_149326_d = 0;
            player.sendPacket(p);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getId(){
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map getMap(){
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDisplayName(){
        return displayName;
    }
}
