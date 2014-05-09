package jk_5.nailed.api.map;

/**
 * Created by matthias on 9-5-14.
 */
public interface NailedZone {
    public boolean isSecure();
    public boolean isInZone(int x, int y, int z);
}
