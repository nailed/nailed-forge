package jk_5.nailed.api.zone;

import java.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ZoneConfig {

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    ZoneConfig reMake();
    List<IZone> getZones();
}
