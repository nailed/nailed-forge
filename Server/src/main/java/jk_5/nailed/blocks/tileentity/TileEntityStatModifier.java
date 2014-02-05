package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.types.StatTypeModifiable;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityStatModifier extends NailedTileEntity {

    private String programmedName = "";
    private DefaultStat stat;
    private boolean needsUpdate = false;

    public void setStatName(String statName){
        this.programmedName = statName;
        if(this.worldObj == null) this.needsUpdate = true;
        else{
            Stat stat = NailedAPI.getMapLoader().getMap(this.worldObj).getStatManager().getStat(this.programmedName);
            if(stat instanceof DefaultStat && ((DefaultStat) stat).getType() instanceof StatTypeModifiable){
                this.stat = (DefaultStat) stat;
            }
        }
    }

    /*
        Because the stat is confirmed as being an {@link DefaultStat}, and it is modifiable, i can directly call .enable()
        and .disable() on it.
     */
}
