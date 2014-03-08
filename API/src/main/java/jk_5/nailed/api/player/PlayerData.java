package jk_5.nailed.api.player;

import jk_5.nailed.api.database.DataObject;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PlayerData extends DataObject {

    public int getTimesOnline();
    public void setTimesOnline(int timesOnline);
}
