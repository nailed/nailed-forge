package jk_5.nailed.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;

/**
 * Created by matthias on 5/8/14.
 */
public class NailedFoodStats extends FoodStats {
    private int minFoodLevel = 0;
    private int maxFoodLevel = 20;

    @Override
    public void onUpdate(EntityPlayer player){
        super.onUpdate(player);
        if (this.maxFoodLevel < this.minFoodLevel && this.maxFoodLevel > 0) return;
        if (this.getFoodLevel() < this.minFoodLevel) this.setFoodLevel(this.minFoodLevel);
        if (this.getFoodLevel() > this.maxFoodLevel && this.maxFoodLevel > 0) this.setFoodLevel(this.maxFoodLevel);
    }

    public void setMinFoodLevel(int value){
        this.minFoodLevel = value;
    }

    public int getMinFoodLevel(){
        return this.minFoodLevel;
    }

    public void setMaxFoodLevel(int value){
        this.maxFoodLevel = value;
    }

    public int getMaxFoodLevel(){
        return this.maxFoodLevel;
    }
}