package jk_5.nailed.api.zone;

/**
 * Created by matthias on 9-5-14.
 */
public interface IZone {
    public boolean isInZone(double x, double y, double z);
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public IZone clone();
}
