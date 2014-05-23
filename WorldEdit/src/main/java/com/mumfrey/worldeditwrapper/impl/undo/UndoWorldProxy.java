package com.mumfrey.worldeditwrapper.impl.undo;

import java.util.*;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.command.*;
import net.minecraft.crash.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.pathfinding.*;
import net.minecraft.scoreboard.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.storage.*;

@SuppressWarnings("rawtypes")
public class UndoWorldProxy extends World {

    private static WorldInfo pendingWorldInfo;

    private final EditSession editSession;

    private final World proxy;

    public UndoWorldProxy(EditSession editSession, World proxy) {
        super(null, "Undo", new UndoWorldProxyProvider(), getWorldSettings(proxy), proxy.theProfiler);
        this.editSession = editSession;
        this.proxy = proxy;
        UndoWorldProxy.pendingWorldInfo = null;
    }

    public static WorldSettings getWorldSettings(World proxy) {
        UndoWorldProxy.pendingWorldInfo = proxy.getWorldInfo();
        return new WorldSettings(UndoWorldProxy.pendingWorldInfo);
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public Entity getEntityByID(int var1) {
        return null;
    }

    @Override
    public void calculateInitialSkylight() {
    }

    public int getTypeId(int x, int y, int z) {
        return this.editSession.getBlockType(new Vector(x, y, z));
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.editSession.getBlockType(new Vector(x, y, z)) == BlockID.AIR;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int blockId = this.editSession.getBlockType(new Vector(x, y, z));
        return VanillaWorld.getBlockById(blockId);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block) {
        try{
            return this.editSession.setBlock(new Vector(x, y, z), VanillaWorld.getBaseBlock(block));
        }catch(MaxChangedBlocksException ex){
            return false;
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block, int metaData, int flags) {
        try{
            return this.editSession.setBlock(new Vector(x, y, z), VanillaWorld.getBaseBlock(block, metaData));
        }catch(MaxChangedBlocksException ex){
            return false;
        }
    }

    @Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int metaData, int flags) {
        //		return this.proxy.setBlockMetadataWithNotify(x, y, z, metaData, flags);
        return false;
    }

    @Override
    public boolean setBlockToAir(int x, int y, int z) {
        try{
            return this.editSession.setBlock(new Vector(x, y, z), new BaseBlock(0));
        }catch(MaxChangedBlocksException ex){
            return false;
        }
    }

    @Override
    public WorldInfo getWorldInfo() {
        return this.proxy == null ? UndoWorldProxy.pendingWorldInfo : this.proxy.getWorldInfo();
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int par1, int par2) {
        return this.proxy.getBiomeGenForCoords(par1, par2);
    }

    @Override
    public WorldChunkManager getWorldChunkManager() {
        return this.proxy.getWorldChunkManager();
    }

    @Override
    protected void initialize(WorldSettings par1WorldSettings) {
    }

    @Override
    public void setSpawnLocation() {
        this.proxy.setSpawnLocation();
    }

    @Override
    public Block getTopBlock(int x, int z) {
        return this.proxy.getTopBlock(x, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return this.proxy.isAirBlock(x, y, z);
    }

    @Override
    public boolean blockExists(int par1, int par2, int par3) {
        return this.proxy.blockExists(par1, par2, par3);
    }

    @Override
    public boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4) {
        return this.proxy.doChunksNearChunkExist(par1, par2, par3, par4);
    }

    @Override
    public boolean checkChunksExist(int par1, int par2, int par3, int par4, int par5, int par6) {
        return this.proxy.checkChunksExist(par1, par2, par3, par4, par5, par6);
    }

    @Override
    protected boolean chunkExists(int par1, int par2) {
        return true;
    }

    @Override
    public Chunk getChunkFromBlockCoords(int par1, int par2) {
        return this.proxy.getChunkFromBlockCoords(par1, par2);
    }

    @Override
    public Chunk getChunkFromChunkCoords(int par1, int par2) {
        return this.proxy.getChunkFromChunkCoords(par1, par2);
    }

    @Override
    public int getBlockMetadata(int par1, int par2, int par3) {
        return this.proxy.getBlockMetadata(par1, par2, par3);
    }

    @Override //breakBlock
    public boolean func_147480_a(int x, int y, int z, boolean b) {
        return this.proxy.func_147480_a(x, y, z, b);
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
        this.proxy.markBlockForUpdate(x, y, z);
    }

    @Override
    public void notifyBlockChange(int x, int y, int z, Block block) {
        this.proxy.notifyBlockChange(x, y, z, block);
    }

    @Override
    public void markBlocksDirtyVertical(int par1, int par2, int par3, int par4) {
        this.proxy.markBlocksDirtyVertical(par1, par2, par3, par4);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.proxy.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void notifyBlocksOfNeighborChange(int x, int y, int z, Block block) {
        this.proxy.notifyBlocksOfNeighborChange(x, y, z, block);
    }

    @Override
    public void notifyBlocksOfNeighborChange(int x, int y, int z, Block block, int meta) {
        this.proxy.notifyBlocksOfNeighborChange(x, y, z, block, meta);
    }

    @Override
    public void notifyBlockOfNeighborChange(int x, int y, int z, Block block) {
        this.proxy.notifyBlockOfNeighborChange(x, y, z, block);
    }

    @Override
    public boolean isBlockTickScheduledThisTick(int x, int y, int z, Block block) {
        return this.proxy.isBlockTickScheduledThisTick(x, y, z, block);
    }

    @Override
    public boolean canBlockSeeTheSky(int par1, int par2, int par3) {
        return this.proxy.canBlockSeeTheSky(par1, par2, par3);
    }

    @Override
    public int getFullBlockLightValue(int par1, int par2, int par3) {
        return this.proxy.getFullBlockLightValue(par1, par2, par3);
    }

    @Override
    public int getBlockLightValue(int par1, int par2, int par3) {
        return this.proxy.getBlockLightValue(par1, par2, par3);
    }

    @Override
    public int getBlockLightValue_do(int par1, int par2, int par3, boolean par4) {
        return this.proxy.getBlockLightValue_do(par1, par2, par3, par4);
    }

    @Override
    public int getHeightValue(int par1, int par2) {
        return this.proxy.getHeightValue(par1, par2);
    }

    @Override
    public int getChunkHeightMapMinimum(int par1, int par2) {
        return this.proxy.getChunkHeightMapMinimum(par1, par2);
    }

    @Override
    public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
        return this.proxy.getSkyBlockTypeBrightness(par1EnumSkyBlock, par2, par3, par4);
    }

    @Override
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
        return this.proxy.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4);
    }

    @Override
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5) {
        this.proxy.setLightValue(par1EnumSkyBlock, par2, par3, par4, par5);
    }

    @Override
    public void func_147479_m(int x, int y, int z) {
        this.proxy.func_147479_m(x, y, z);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4) {
        return this.proxy.getLightBrightnessForSkyBlocks(par1, par2, par3, par4);
    }

    @Override
    public float getLightBrightness(int par1, int par2, int par3) {
        return this.proxy.getLightBrightness(par1, par2, par3);
    }

    @Override
    public boolean isDaytime() {
        return this.proxy.isDaytime();
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(Vec3 par1Vec3, Vec3 par2Vec3) {
        return this.proxy.rayTraceBlocks(par1Vec3, par2Vec3);
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(Vec3 par1Vec3, Vec3 par2Vec3, boolean par3) {
        return this.proxy.rayTraceBlocks(par1Vec3, par2Vec3, par3);
    }

    @Override
    public MovingObjectPosition func_147447_a(Vec3 start, Vec3 end, boolean b1, boolean b2, boolean b3) {
        return this.proxy.func_147447_a(start, end, b1, b2, b3);
    }

    @Override
    public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4) {
        this.proxy.playSoundAtEntity(par1Entity, par2Str, par3, par4);
    }

    @Override
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, float par3, float par4) {
        this.proxy.playSoundToNearExcept(par1EntityPlayer, par2Str, par3, par4);
    }

    @Override
    public void playSoundEffect(double par1, double par3, double par5, String par7Str, float par8, float par9) {
        this.proxy.playSoundEffect(par1, par3, par5, par7Str, par8, par9);
    }

    @Override
    public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10) {
        this.proxy.playSound(par1, par3, par5, par7Str, par8, par9, par10);
    }

    @Override
    public void playRecord(String par1Str, int par2, int par3, int par4) {
        this.proxy.playRecord(par1Str, par2, par3, par4);
    }

    @Override
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12) {
        this.proxy.spawnParticle(par1Str, par2, par4, par6, par8, par10, par12);
    }

    @Override
    public boolean addWeatherEffect(Entity par1Entity) {
        return this.proxy.addWeatherEffect(par1Entity);
    }

    @Override
    public boolean spawnEntityInWorld(Entity par1Entity) {
        return this.proxy.spawnEntityInWorld(par1Entity);
    }

    @Override
    public void onEntityAdded(Entity par1Entity) {
    }

    @Override
    public void onEntityRemoved(Entity par1Entity) {
    }

    @Override
    public void removeEntity(Entity par1Entity) {
        this.proxy.removeEntity(par1Entity);
    }

    @Override
    public void removePlayerEntityDangerously(Entity par1Entity) {
        this.proxy.removePlayerEntityDangerously(par1Entity);
    }

    @Override
    public void addWorldAccess(IWorldAccess par1iWorldAccess) {
        this.proxy.addWorldAccess(par1iWorldAccess);
    }

    @Override
    public void removeWorldAccess(IWorldAccess par1iWorldAccess) {
        this.proxy.removeWorldAccess(par1iWorldAccess);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
        return this.proxy.getCollidingBoundingBoxes(par1Entity, par2AxisAlignedBB);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AxisAlignedBB> func_147461_a(AxisAlignedBB aabb) {
        return this.proxy.func_147461_a(aabb);
    }

    @Override
    public int calculateSkylightSubtracted(float par1) {
        return this.proxy.calculateSkylightSubtracted(par1);
    }

    @Override
    public float getSunBrightness(float par1) {
        return this.proxy.getSunBrightness(par1);
    }

    @Override
    public Vec3 getSkyColor(Entity par1Entity, float par2) {
        return this.proxy.getSkyColor(par1Entity, par2);
    }

    @Override
    public float getCelestialAngle(float par1) {
        return this.proxy.getCelestialAngle(par1);
    }

    @Override
    public int getMoonPhase() {
        return this.proxy.getMoonPhase();
    }

    @Override
    public float getCurrentMoonPhaseFactor() {
        return this.proxy.getCurrentMoonPhaseFactor();
    }

    @Override
    public float getCelestialAngleRadians(float par1) {
        return this.proxy.getCelestialAngleRadians(par1);
    }

    @Override
    public Vec3 getCloudColour(float par1) {
        return this.proxy.getCloudColour(par1);
    }

    @Override
    public Vec3 getFogColor(float par1) {
        return this.proxy.getFogColor(par1);
    }

    @Override
    public int getPrecipitationHeight(int par1, int par2) {
        return this.proxy.getPrecipitationHeight(par1, par2);
    }

    @Override
    public int getTopSolidOrLiquidBlock(int par1, int par2) {
        return this.proxy.getTopSolidOrLiquidBlock(par1, par2);
    }

    @Override
    public float getStarBrightness(float par1) {
        return this.proxy.getStarBrightness(par1);
    }

    @Override
    public void scheduleBlockUpdate(int x, int y, int z, Block block, int meta) {
        this.proxy.scheduleBlockUpdate(x, y, z, block, meta);
    }

    @Override
    public void scheduleBlockUpdateWithPriority(int x, int y, int z, Block block, int meta, int prio) {
        this.proxy.scheduleBlockUpdateWithPriority(x, y, z, block, meta, prio);
    }

    @Override
    public void func_147446_b(int i1, int i2, int i3, Block block, int i4, int i5) {
        this.proxy.func_147446_b(i1, i2, i3, block, i4, i5);
    }

    @Override
    public void updateEntities() {
        this.proxy.updateEntities();
    }

    @Override
    public void func_147448_a(Collection list) {
        this.proxy.func_147448_a(list);
    }

    @Override
    public void updateEntity(Entity par1Entity) {
        this.proxy.updateEntity(par1Entity);
    }

    @Override
    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2) {
        this.proxy.updateEntityWithOptionalForce(par1Entity, par2);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB) {
        return this.proxy.checkNoEntityCollision(par1AxisAlignedBB);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB, Entity par2Entity) {
        return this.proxy.checkNoEntityCollision(par1AxisAlignedBB, par2Entity);
    }

    @Override
    public boolean checkBlockCollision(AxisAlignedBB par1AxisAlignedBB) {
        return this.proxy.checkBlockCollision(par1AxisAlignedBB);
    }

    @Override
    public boolean isAnyLiquid(AxisAlignedBB par1AxisAlignedBB) {
        return this.proxy.isAnyLiquid(par1AxisAlignedBB);
    }

    @Override
    public boolean func_147470_e(AxisAlignedBB aabb) {
        return this.proxy.func_147470_e(aabb);
    }

    @Override
    public boolean handleMaterialAcceleration(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity) {
        return this.proxy.handleMaterialAcceleration(par1AxisAlignedBB, par2Material, par3Entity);
    }

    @Override
    public boolean isMaterialInBB(AxisAlignedBB par1AxisAlignedBB, Material par2Material) {
        return this.proxy.isMaterialInBB(par1AxisAlignedBB, par2Material);
    }

    @Override
    public boolean isAABBInMaterial(AxisAlignedBB par1AxisAlignedBB, Material par2Material) {
        return this.proxy.isAABBInMaterial(par1AxisAlignedBB, par2Material);
    }

    @Override
    public Explosion createExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9) {
        return this.proxy.createExplosion(par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
        return this.proxy.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10);
    }

    @Override
    public float getBlockDensity(Vec3 par1Vec3, AxisAlignedBB par2AxisAlignedBB) {
        return this.proxy.getBlockDensity(par1Vec3, par2AxisAlignedBB);
    }

    @Override
    public boolean extinguishFire(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5) {
        return this.proxy.extinguishFire(par1EntityPlayer, par2, par3, par4, par5);
    }

    @Override
    public String getDebugLoadedEntities() {
        return this.proxy.getDebugLoadedEntities();
    }

    @Override
    public String getProviderName() {
        return this.proxy.getProviderName();
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return this.proxy.getTileEntity(x, y, z);
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tile) {
        this.proxy.setTileEntity(x, y, z, tile);
    }

    @Override
    public void removeTileEntity(int x, int y, int z) {
        this.proxy.removeTileEntity(x, y, z);
    }

    @Override
    public void func_147457_a(TileEntity tile) {
        this.proxy.func_147457_a(tile);
    }

    @Override
    public boolean func_147469_q(int x, int y, int z) {
        return this.proxy.func_147469_q(x, y, z);
    }

    @Override
    public boolean isBlockNormalCubeDefault(int x, int y, int z, boolean b) {
        return this.proxy.isBlockNormalCubeDefault(x, y, z, b);
    }

    @Override
    public void setAllowedSpawnTypes(boolean par1, boolean par2) {
        this.proxy.setAllowedSpawnTypes(par1, par2);
    }

    @Override
    public void tick() {
        this.proxy.tick();
    }

    @Override
    protected void updateWeather() {
    }

    @Override
    protected void setActivePlayerChunksAndCheckLight() {
    }

    @Override
    protected void func_147467_a(int x, int z, Chunk chunk) {
    }

    @Override
    protected void func_147456_g() {
    }

    @Override
    public boolean isBlockFreezable(int par1, int par2, int par3) {
        return this.proxy.isBlockFreezable(par1, par2, par3);
    }

    @Override
    public boolean isBlockFreezableNaturally(int par1, int par2, int par3) {
        return this.proxy.isBlockFreezableNaturally(par1, par2, par3);
    }

    @Override
    public boolean canBlockFreeze(int par1, int par2, int par3, boolean par4) {
        return this.proxy.canBlockFreeze(par1, par2, par3, par4);
    }

    @Override
    public boolean func_147478_e(int x, int y, int z, boolean b) {
        return this.proxy.func_147478_e(x, y, z, b);
    }

    @Override
    public boolean func_147451_t(int x, int y, int z) {
        return this.proxy.func_147451_t(x, y, z);
    }

    @Override
    public boolean updateLightByType(EnumSkyBlock type, int x, int y, int z) {
        return this.proxy.updateLightByType(type, x, y, z);
    }

    @Override
    public boolean tickUpdates(boolean par1) {
        return this.proxy.tickUpdates(par1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<List<NextTickListEntry>> getPendingBlockUpdates(Chunk par1Chunk, boolean par2) {
        return this.proxy.getPendingBlockUpdates(par1Chunk, par2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
        return this.proxy.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3iEntitySelector) {
        return this.proxy.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB, par3iEntitySelector);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB) {
        return this.proxy.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> selectEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3iEntitySelector) {
        return this.proxy.selectEntitiesWithinAABB(par1Class, par2AxisAlignedBB, par3iEntitySelector);
    }

    @Override
    public Entity findNearestEntityWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity) {
        return this.proxy.findNearestEntityWithinAABB(par1Class, par2AxisAlignedBB, par3Entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getLoadedEntityList() {
        return this.proxy.getLoadedEntityList();
    }

    @Override
    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tile) {
        this.proxy.markTileEntityChunkModified(x, y, z, tile);
    }

    @Override
    public int countEntities(Class par1Class) {
        return this.proxy.countEntities(par1Class);
    }

    @Override
    public void addLoadedEntities(List par1List) {
        this.proxy.addLoadedEntities(par1List);
    }

    @Override
    public void unloadEntities(List par1List) {
        this.proxy.unloadEntities(par1List);
    }

    @Override
    public boolean canPlaceEntityOnSide(Block block, int x, int y, int z, boolean b, int i, Entity entity, ItemStack stack) {
        return this.proxy.canPlaceEntityOnSide(block, x, y, z, b, i, entity, stack);
    }

    @Override
    public PathEntity getPathEntityToEntity(Entity par1Entity, Entity par2Entity, float par3, boolean par4, boolean par5, boolean par6, boolean par7) {
        return this.proxy.getPathEntityToEntity(par1Entity, par2Entity, par3, par4, par5, par6, par7);
    }

    @Override
    public PathEntity getEntityPathToXYZ(Entity par1Entity, int par2, int par3, int par4, float par5, boolean par6, boolean par7, boolean par8, boolean par9) {
        return this.proxy.getEntityPathToXYZ(par1Entity, par2, par3, par4, par5, par6, par7, par8, par9);
    }

    @Override
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4) {
        return this.proxy.isBlockProvidingPowerTo(par1, par2, par3, par4);
    }

    @Override
    public int getBlockPowerInput(int par1, int par2, int par3) {
        return this.proxy.getBlockPowerInput(par1, par2, par3);
    }

    @Override
    public boolean getIndirectPowerOutput(int par1, int par2, int par3, int par4) {
        return this.proxy.getIndirectPowerOutput(par1, par2, par3, par4);
    }

    @Override
    public int getIndirectPowerLevelTo(int par1, int par2, int par3, int par4) {
        return this.proxy.getIndirectPowerLevelTo(par1, par2, par3, par4);
    }

    @Override
    public boolean isBlockIndirectlyGettingPowered(int par1, int par2, int par3) {
        return this.proxy.isBlockIndirectlyGettingPowered(par1, par2, par3);
    }

    @Override
    public int getStrongestIndirectPower(int par1, int par2, int par3) {
        return this.proxy.getStrongestIndirectPower(par1, par2, par3);
    }

    @Override
    public EntityPlayer getClosestPlayerToEntity(Entity par1Entity, double par2) {
        return this.proxy.getClosestPlayerToEntity(par1Entity, par2);
    }

    @Override
    public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7) {
        return this.proxy.getClosestPlayer(par1, par3, par5, par7);
    }

    @Override
    public EntityPlayer getClosestVulnerablePlayerToEntity(Entity par1Entity, double par2) {
        return this.proxy.getClosestVulnerablePlayerToEntity(par1Entity, par2);
    }

    @Override
    public EntityPlayer getClosestVulnerablePlayer(double par1, double par3, double par5, double par7) {
        return this.proxy.getClosestVulnerablePlayer(par1, par3, par5, par7);
    }

    @Override
    public EntityPlayer getPlayerEntityByName(String par1Str) {
        return this.proxy.getPlayerEntityByName(par1Str);
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        this.proxy.sendQuittingDisconnectingPacket();
    }

    @Override
    public void checkSessionLock() throws MinecraftException {
        this.proxy.checkSessionLock();
    }

    @Override
    public void func_82738_a(long par1) {
        this.proxy.func_82738_a(par1);
    }

    @Override
    public long getSeed() {
        return this.proxy.getSeed();
    }

    @Override
    public long getTotalWorldTime() {
        return this.proxy.getTotalWorldTime();
    }

    @Override
    public long getWorldTime() {
        return this.proxy.getWorldTime();
    }

    @Override
    public void setWorldTime(long par1) {
        this.proxy.setWorldTime(par1);
    }

    @Override
    public ChunkCoordinates getSpawnPoint() {
        return this.proxy.getSpawnPoint();
    }

    @Override
    public void setSpawnLocation(int par1, int par2, int par3) {
        this.proxy.setSpawnLocation(par1, par2, par3);
    }

    @Override
    public void joinEntityInSurroundings(Entity par1Entity) {
        this.proxy.joinEntityInSurroundings(par1Entity);
    }

    @Override
    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4) {
        return this.proxy.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    @Override
    public void setEntityState(Entity par1Entity, byte par2) {
        this.proxy.setEntityState(par1Entity, par2);
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return this.proxy.getChunkProvider();
    }

    @Override
    public void addBlockEvent(int x, int y, int z, Block block, int event, int data) {
        this.proxy.addBlockEvent(x, y, z, block, event, data);
    }

    @Override
    public ISaveHandler getSaveHandler() {
        return this.proxy.getSaveHandler();
    }

    @Override
    public GameRules getGameRules() {
        return this.proxy.getGameRules();
    }

    @Override
    public void updateAllPlayersSleepingFlag() {
        this.proxy.updateAllPlayersSleepingFlag();
    }

    @Override
    public float getWeightedThunderStrength(float par1) {
        return this.proxy.getWeightedThunderStrength(par1);
    }

    @Override
    public void setThunderStrength(float thunderStrength) {
        this.proxy.setThunderStrength(thunderStrength);
    }

    @Override
    public float getRainStrength(float par1) {
        return this.proxy.getRainStrength(par1);
    }

    @Override
    public void setRainStrength(float par1) {
        this.proxy.setRainStrength(par1);
    }

    @Override
    public boolean isThundering() {
        return this.proxy.isThundering();
    }

    @Override
    public boolean isRaining() {
        return this.proxy.isRaining();
    }

    @Override
    public boolean canLightningStrikeAt(int par1, int par2, int par3) {
        return this.proxy.canLightningStrikeAt(par1, par2, par3);
    }

    @Override
    public boolean isBlockHighHumidity(int par1, int par2, int par3) {
        return this.proxy.isBlockHighHumidity(par1, par2, par3);
    }

    @Override
    public void setItemData(String par1Str, WorldSavedData par2WorldSavedData) {
        this.proxy.setItemData(par1Str, par2WorldSavedData);
    }

    @Override
    public WorldSavedData loadItemData(Class par1Class, String par2Str) {
        return this.proxy.loadItemData(par1Class, par2Str);
    }

    @Override
    public int getUniqueDataId(String par1Str) {
        return this.proxy.getUniqueDataId(par1Str);
    }

    @Override
    public void playBroadcastSound(int par1, int par2, int par3, int par4, int par5) {
        this.proxy.playBroadcastSound(par1, par2, par3, par4, par5);
    }

    @Override
    public void playAuxSFX(int par1, int par2, int par3, int par4, int par5) {
        this.proxy.playAuxSFX(par1, par2, par3, par4, par5);
    }

    @Override
    public void playAuxSFXAtEntity(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6) {
        this.proxy.playAuxSFXAtEntity(par1EntityPlayer, par2, par3, par4, par5, par6);
    }

    @Override
    public int getHeight() {
        return this.proxy.getHeight();
    }

    @Override
    public int getActualHeight() {
        return this.proxy.getActualHeight();
    }

    @Override
    public Random setRandomSeed(int par1, int par2, int par3) {
        return this.proxy.setRandomSeed(par1, par2, par3);
    }

    @Override
    public ChunkPosition findClosestStructure(String name, int x, int y, int z) {
        return this.proxy.findClosestStructure(name, x, y, z);
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.proxy.extendedLevelsInChunkCache();
    }

    @Override
    public double getHorizon() {
        return this.proxy.getHorizon();
    }

    @Override
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport par1CrashReport) {
        return this.proxy.addWorldInfoToCrashReport(par1CrashReport);
    }

    @Override
    public void destroyBlockInWorldPartially(int x, int y, int z, int i, int i1) {
        this.proxy.destroyBlockInWorldPartially(x, y, z, i, i1);
    }

    @Override
    public Vec3Pool getWorldVec3Pool() {
        return this.proxy.getWorldVec3Pool();
    }

    @Override
    public Calendar getCurrentDate() {
        return this.proxy.getCurrentDate();
    }

    @Override
    public void makeFireworks(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13nbtTagCompound) {
        this.proxy.makeFireworks(par1, par3, par5, par7, par9, par11, par13nbtTagCompound);
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.proxy.getScoreboard();
    }

    @Override
    public void func_147453_f(int x, int y, int z, Block block) {
        this.proxy.func_147453_f(x, y, z, block);
    }

    @Override
    public float func_147462_b(double x, double y, double z) {
        return this.proxy.func_147462_b(x, y, z);
    }

    @Override
    public float func_147473_B(int x, int y, int z) {
        return this.proxy.func_147473_B(x, y, z);
    }

    @Override
    public void func_147450_X() {
        this.proxy.func_147450_X();
    }
}
