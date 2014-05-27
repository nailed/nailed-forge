package jk_5.nailed.map.scoreboard;

import java.util.*;
import javax.annotation.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import net.minecraft.network.play.server.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.scripting.*;

/**
 * This is the Nailed-implementation of the NailedAPI class {@link Objective}
 * <p/>
 * {@inheritDoc}
 */
public class ObjectiveImpl implements Objective, ILuaObject {

    private final Map map;
    private final String id; //TODO: Max length of 16
    private String displayName;
    private final Set<Score> scores = Sets.newHashSet();

    /**
     * Creates a new {@link ObjectiveImpl} object for the given map with the given id
     * Note that the id may not be bigger than 16
     *
     * @param map The map that this {@link ObjectiveImpl} is created for
     * @param id  The id of the objective (<= 16)
     * @throws IllegalArgumentException When id is bigger than 16
     */
    public ObjectiveImpl(@Nonnull Map map, @Nonnull String id) {
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
    public void setDisplayName(@Nonnull String displayName) {
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
    public Score getScore(@Nonnull String name) {
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
    public void removeScore(@Nonnull Score score) {
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
    public void sendData(@Nonnull Player player) {
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
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map getMap() {
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "getId",
                "getDisplayName",
                "setDisplayName",
                "getScore",
                "setScore",
                "addScore",
                "getType"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
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
            case 3: //getScore
                if(arguments.length == 1 && arguments[0] instanceof String){
                    return new Object[]{this.getScore((String) arguments[0]).getValue()};
                }else{
                    throw new Exception("Expected 1 string argument");
                }
            case 4: //setScore
                if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
                    int score = ((Double) arguments[1]).intValue();
                    this.getScore((String) arguments[0]).setValue(score);
                }else{
                    throw new Exception("Expected 1 string and 1 int argument");
                }
                break;
            case 5: //addScore
                if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
                    int score = ((Double) arguments[1]).intValue();
                    this.getScore((String) arguments[0]).addValue(score);
                }else{
                    throw new Exception("Expected 1 string and 1 int argument");
                }
                break;
            case 6: //getType
                return new Object[]{"objective"};
        }
        return new Object[0];
    }
}
