package jk_5.nailed.api.zone;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ZoneConfig {

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public ZoneConfig reMake();
    public List<IZone> getZones();
}
