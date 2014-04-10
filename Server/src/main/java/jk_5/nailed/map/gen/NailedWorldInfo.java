package jk_5.nailed.map.gen;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

/**
 * Our own WorldInfo instance, because DerivedWorldInfo noop's a lot of stuff.
 * It looks like this is not used, but we hack this class in using ASM.
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class NailedWorldInfo extends WorldInfo {

    private final Map map;
    private final Mappack mappack;
    private final boolean hasMappack;

    private long worldTime = 0;
    private long totalWorldTime = 0;

    public NailedWorldInfo(WorldServer worldServer){
        this.map = NailedAPI.getMapLoader().getMap(worldServer);
        this.mappack = this.map.getMappack();
        this.hasMappack = this.mappack != null;
    }

    @Override
    public long getSeed(){
        return 0; //TODO: maybe add seeds to worlds?
    }

    @Override
    public long getWorldTotalTime(){
        return this.totalWorldTime;
    }

    @Override
    public long getWorldTime(){
        return this.worldTime;
    }

    @Override
    public int getVanillaDimension(){
        return this.map.getID();
    }

    @Override
    public void incrementTotalWorldTime(long newVal){
        this.totalWorldTime = newVal;
    }

    @Override
    public void setWorldTime(long time){
        this.worldTime = time;
    }

    @Override
    public void setSpawnPosition(int x, int y, int z){

    }

    @Override
    public String getWorldName(){
        return this.map.getSaveFileName();
    }

    @Override
    public void setWorldName(String par1Str){

    }

    @Override
    public WorldSettings.GameType getGameType(){
        return this.hasMappack ? this.mappack.getMappackMetadata().getGamemode() : WorldSettings.GameType.CREATIVE;
    }

    @Override
    public boolean isMapFeaturesEnabled(){
        return false;
    }

    @Override
    public void setGameType(WorldSettings.GameType par1EnumGameType){

    }

    @Override
    public boolean isHardcoreModeEnabled(){
        return false;
    }

    @Override
    public WorldType getTerrainType(){
        return WorldType.DEFAULT;
    }

    @Override
    public void setTerrainType(WorldType par1WorldType){

    }

    @Override
    public String getGeneratorOptions(){
        return "";
    }

    @Override
    public boolean areCommandsAllowed(){
        return true;
    }

    @Override
    public void addToCrashReport(CrashReportCategory category){
        category.addCrashSection("Map", this.map);
        category.addCrashSection("Mappack", this.mappack);
    }
}
