package jk_5.nailed.map.weather;

import java.util.*;

import net.minecraft.entity.effect.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class WeatherController implements jk_5.nailed.api.map.WeatherController {

    protected int rainDuration;
    protected int rainDurationBase;
    protected int rainCooldown;
    protected int rainCooldownBase;
    protected int thunderDuration;
    protected int thunderDurationBase;
    protected int thunderCooldown;
    protected int thunderCooldownBase;

    private Random random = new Random();
    private int updateLCG = this.random.nextInt();
    private double rainingStrength;
    private double thunderingStrength;

    private int thunderCounter;
    private boolean thundering;
    private int rainCounter;
    private boolean raining;

    private EnumSet<WeatherType> permittedWeatherTypes;

    public WeatherController(Map map) {
        Mappack mappack = map.getMappack();
        if(mappack == null){
            this.permittedWeatherTypes = EnumSet.allOf(WeatherType.class);
        }else{
            this.permittedWeatherTypes = mappack.getMappackMetadata().getPermittedWeatherTypes();
        }
    }

    @Override
    public void updateRaining() {
        if(this.thunderCounter <= 0){
            if(this.thundering){
                this.thunderCounter = this.thunderDurationBase;
                if(this.thunderDuration > 0){
                    this.thunderCounter += this.random.nextInt(this.thunderDuration);
                }
            }else{
                this.thunderCounter = this.thunderCooldownBase;
                if(this.thunderCooldown > 0){
                    this.thunderCounter += this.random.nextInt(this.thunderCooldown);
                }
            }
        }else{
            if(this.thunderCounter <= 0){
                this.thundering = !this.thundering;
            }
            this.thunderCounter--;
        }

        if(this.rainCounter <= 0){
            if(this.raining){
                this.rainCounter = this.rainDurationBase;
                if(this.rainDuration > 0){
                    this.rainCounter += this.random.nextInt(this.rainDuration);
                }
            }else{
                this.rainCounter = this.rainCooldownBase;
                if(this.rainCooldown > 0){
                    this.rainCounter += this.random.nextInt(this.rainCooldown);
                }
            }
        }else{
            if(this.rainCounter <= 0){
                this.raining = !this.raining;
            }
            this.rainCounter--;
        }

        if(this.raining){
            this.rainingStrength += 0.01D;
        }else{
            this.rainingStrength -= 0.01D;
        }

        if(this.thundering){
            this.thunderingStrength += 0.01D;
        }else{
            this.thunderingStrength -= 0.01D;
        }

        this.rainingStrength = clamp(this.rainingStrength);
        this.thunderingStrength = clamp(this.thunderingStrength);

        if(!this.permittedWeatherTypes.contains(WeatherType.RAIN)){
            this.raining = false;
            this.rainingStrength = 0;
        }
        if(!this.permittedWeatherTypes.contains(WeatherType.THUNDER)){
            this.thundering = false;
            this.thunderingStrength = 0;
        }
    }

    @Override
    public void tick(World worldObj, Chunk chunk) {
        if(worldObj.isRaining() && worldObj.isThundering() && worldObj.rand.nextInt(100000) == 0){
            int xBase = chunk.xPosition * 16;
            int zBase = chunk.zPosition * 16;
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int coords = this.updateLCG >> 2;
            int x = xBase + (coords & 0xF);
            int z = zBase + (coords >> 8 & 0xF);
            int y = worldObj.getPrecipitationHeight(x, z);

            if(worldObj.canLightningStrikeAt(x, y, z)){
                worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, x, y, z));
            }
        }
    }

    @Override
    public void clear() {
        this.rainCounter = 0;
        this.raining = false;
        this.thunderCounter = 0;
        this.thundering = false;
    }

    public float getTemperature(float current, int x, int z) {
        return current;
    }

    public boolean getEnableSnow(boolean current, int x, int y) {
        return current;
    }

    public boolean getEnableRain(boolean current, int x, int y) {
        return current;
    }

    private static double clamp(double value) {
        return Math.min(Math.max(value, 0d), 1d);
    }

    @Override
    public void toggleRain() {
        this.rainCounter = 1;
    }

    @Override
    public double getRainingStrength() {
        return this.rainingStrength;
    }

    @Override
    public double getThunderingStrength() {
        return this.thunderingStrength;
    }
}
