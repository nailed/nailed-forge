package jk_5.nailed.map.gen;

import net.minecraft.world.WorldProvider;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedWorldProvider extends WorldProvider {

    private int mapID;

    @Override
    public void setDimension(int dim){
        this.mapID = dim;
        super.setDimension(dim);
    }

    @Override
    public String getDimensionName(){
        return "Nailed";
    }

    @Override
    public String getSaveFolder(){
        return "maps/map" + this.mapID + ""; //TODO: mappack name
    }
}
