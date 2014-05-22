package jk_5.nailed.permissions.zone.types;

import javax.annotation.*;

import jk_5.nailed.api.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class AbstractZone implements IZone {

    protected final String name;
    protected final boolean inverted;

    protected AbstractZone(String name, boolean inverted) {
        this.name = name;
        this.inverted = inverted;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }
}
