package jk_5.nailed.client.blocks.tileentity;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityStatModifier extends NailedTileEntity {

    private String programmedName = "";
    private boolean needsUpdate = false;

    public void setStatName(String statName){
        this.programmedName = statName;
        if(this.field_145850_b == null) this.needsUpdate = true;
    }

    /*
        Because the stat is confirmed as being an {@link DefaultStat}, and it is modifiable, i can directly call .enable()
        and .disable() on it.
     */
}
