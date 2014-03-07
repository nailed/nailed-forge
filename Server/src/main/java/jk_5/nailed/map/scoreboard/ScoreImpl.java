package jk_5.nailed.map.scoreboard;

import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.Score;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.play.server.S3CPacketUpdateScore;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class ScoreImpl implements Score {

    private final Objective owner;
    @Getter private final String name;
    @Getter private int value = 0;

    @Override
    public void setValue(int value){
        this.value = value;
        this.update();
    }

    @Override
    public void addValue(int value){
        this.value += value;
        this.update();
    }

    @Override
    public void update(){
        S3CPacketUpdateScore p = new S3CPacketUpdateScore();
        p.field_149329_a = this.name;
        p.field_149327_b = this.owner.getId();
        p.field_149328_c = this.value;
        p.field_149326_d = 0;
        this.owner.getMap().broadcastPacket(p);
    }
}
