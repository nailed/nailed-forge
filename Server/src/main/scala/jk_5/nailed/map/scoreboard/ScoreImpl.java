package jk_5.nailed.map.scoreboard;

import javax.annotation.*;

import com.google.common.base.*;

import net.minecraft.network.play.server.*;

import jk_5.nailed.api.map.scoreboard.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ScoreImpl implements Score {

    private final Objective owner;
    private final String name;
    private int value = 0;

    public ScoreImpl(@Nonnull Objective owner, @Nonnull String name) {
        Preconditions.checkNotNull(owner, "owner");
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkArgument(name.length() <= 16, "name may not be longer than 16");

        this.owner = owner;
        this.name = name;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
        this.update();
    }

    @Override
    public void addValue(int value) {
        this.value += value;
        this.update();
    }

    @Override
    public void update() {
        S3CPacketUpdateScore p = new S3CPacketUpdateScore();
        p.field_149329_a = this.name;
        p.field_149327_b = this.owner.getId();
        p.field_149328_c = this.value;
        p.field_149326_d = 0;
        this.owner.getMap().broadcastPacket(p);
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
