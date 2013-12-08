package jk_5.nailed.map.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class DefaultStat implements Stat {

    @Getter private final String name;
    @Getter @Setter private IStatType type;
    @Getter private boolean enabled = false;

    @Override
    public void enable() {
        this.enabled = true;
        MinecraftForge.EVENT_BUS.post(new StatEvent.Enable(this));
    }

    @Override
    public void disable() {
        this.enabled = false;
        MinecraftForge.EVENT_BUS.post(new StatEvent.Disable(this));
    }

    @Override
    public Stat clone() {
        return new DefaultStat(this.name, this.type, this.enabled);
    }
}
