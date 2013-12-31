package jk_5.quakecraft;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.instruction.RegisterInstructionEvent;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.raytracing.RayTracer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", dependencies = "required-after:Nailed")
public class QuakecraftPlugin {

    @Mod.Instance("QuakeCraft")
    public static QuakecraftPlugin instance;

    public java.util.Map<String, Integer> reloadCooldown = Maps.newHashMap();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if(event.getSide().isServer()){
            FMLCommonHandler.instance().bus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onCooldownTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        String id = event.player.func_146103_bH().getId();
        if(QuakecraftPlugin.instance.reloadCooldown.containsKey(id)){
            int ticks = QuakecraftPlugin.instance.reloadCooldown.get(id) + 1;
            QuakecraftPlugin.instance.reloadCooldown.put(id, ticks);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onInteract(PlayerInteractEvent event){
        World world = event.entity.worldObj;
        Player player = PlayerRegistry.instance().getPlayer(event.entityPlayer);
        if(this.isQuakecraft(world)){
            jk_5.nailed.map.Map map = MapLoader.instance().getMap(world);
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
                if(!this.reloadCooldown.containsKey(player.getId())){
                    this.reloadCooldown.put(player.getId(), 21);
                }
                if(this.reloadCooldown.get(player.getId()) <= 30) return;
                ItemStack held = event.entityPlayer.getHeldItem();
                if(held != null && held.getItem() instanceof ItemHoe){
                    MovingObjectPosition result = this.rayTrace(event.entityPlayer);
                    if(result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
                        double particlesPerBlock = 1d;
                        double dx = result.entityHit.posX - event.entity.posX;
                        double dy = result.entityHit.posY - event.entity.posY;
                        double dz = result.entityHit.posZ - event.entity.posZ;
                        double d = Math.sqrt(dx * dx + dy * dy + dz * dz) / (1 / particlesPerBlock);
                        int total = (int)(d * particlesPerBlock);
                        for(int i = 0; i < total; i++){
                            double x = dx / d * i + event.entity.posX;
                            double y = dy / d * i + event.entity.posY + event.entityPlayer.eyeHeight;
                            double z = dz / d * i + event.entity.posZ;
                            Entity entity = new EntityFireworkRocket(world, x, y, z, null);
                            world.spawnEntityInWorld(entity);
                            world.removeEntity(entity);
                        }

                        Entity entity = new EntityFireworkRocket(world, result.entityHit.posX, result.entityHit.posY + (result.entityHit.height / 2), result.entityHit.posZ, this.getEntityExplosionEffect(0xFF0000));
                        world.spawnEntityInWorld(entity);
                        world.setEntityState(entity, (byte) 17);
                        world.removeEntity(entity);

                        event.entity.worldObj.playSoundAtEntity(result.entityHit, "mob.blaze.death", 2.0f, 4.0f);
                        event.entity.worldObj.playSoundAtEntity(result.entityHit, "random.explode", 2.0f, 4.0f);

                        if(result.entityHit instanceof EntityLivingBase && result.entityHit.isEntityAlive()){
                            EntityLivingBase hit = (EntityLivingBase) result.entityHit;
                            hit.attackEntityFrom(new DamageSourceRailgun(event.entity), hit.getMaxHealth());

                            Scoreboard scoreboard = world.getScoreboard();
                            ScoreObjective objective = scoreboard.getObjective(map.getID() + "-kills");
                            Score score = scoreboard.func_96529_a(player.getUsername(), objective);
                            score.func_96649_a(1);

                            if(score.getScorePoints() >= 25){
                                for(IInstruction instruction : map.getGameController().getInstructions()){
                                    if(instruction instanceof InstructionAwaitFinalKill){
                                        ((InstructionAwaitFinalKill) instruction).finalKillMade = true;
                                    }
                                }
                                map.broadcastChatMessage(new ChatComponentTranslation("quakecraft.message.winner", player.getUsername()));
                            }
                        }
                    }else{
                        event.entity.worldObj.playSoundAtEntity(event.entity, "mob.blaze.hit", 2.0f, 4.0f);
                    }
                    this.reloadCooldown.put(player.getId(), 0);
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDrop(PlayerDropsEvent event){
        if(this.isQuakecraft(event.entity.worldObj)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntitySpawn(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.entity;
            if(this.isQuakecraft(event.world)){
                jk_5.nailed.map.Map map = MapLoader.instance().getMap(event.world);
                if(map.getGameController().isRunning()){
                    ItemStack stack = new ItemStack(Items.wooden_hoe, 1);
                    stack.func_151001_c(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun");
                    player.inventory.setInventorySlotContents(0, stack);
                    player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true));
                }
            }else if(this.isQuakecraft(event.entity.worldObj)){
                player.inventory.setInventorySlotContents(0, null);
                player.removePotionEffect(Potion.moveSpeed.id);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void registerInstructions(RegisterInstructionEvent event){
        event.register("startquakecraft", InstructionStartQuakecraft.class);
        event.register("awaitfinakquakekill", InstructionAwaitFinalKill.class);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onDamage(LivingHurtEvent event){
        if(!this.isQuakecraft(event.entity.worldObj)) return;
        if(!(event.source instanceof DamageSourceRailgun) && !(event.source == DamageSource.outOfWorld)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onFall(LivingFallEvent event){
        if(this.isQuakecraft(event.entity.worldObj)){
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unchecked")
    public MovingObjectPosition rayTrace(EntityPlayer player){
        MovingObjectPosition result;

        World world = player.worldObj;
        MovingObjectPosition pointedBlock = RayTracer.reTrace(world, player);
        result = pointedBlock;
        double rangeLimit = 500;
        double d1 = rangeLimit;
        Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + player.eyeHeight, player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit);
        if (pointedBlock != null){
            d1 = pointedBlock.hitVec.distanceTo(vec3);
        }
        Entity pointedEntity = null;
        float f1 = 1.0F;
        List<Entity> list = (List<Entity>) world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit).expand((double)f1, (double)f1, (double)f1));
        double d2 = d1;

        for (Entity entity : list){
            if (entity.canBeCollidedWith()){
                float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)){
                    if (0.0D < d2 || d2 == 0.0D){
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
                }else if (movingobjectposition != null){
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D){
                        if (entity == entity.ridingEntity && !entity.canRiderInteract()){
                            if (d2 == 0.0D){
                                pointedEntity = entity;
                            }
                        }else{
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        if (pointedEntity != null && (d2 < d1 || result == null)){
            result = new MovingObjectPosition(pointedEntity);
        }
        return result;
    }

    public ItemStack getEntityExplosionEffect(int color){
        ItemStack firework = new ItemStack(Items.fireworks, 1);
        NBTTagCompound tag = new NBTTagCompound();
        firework.setTagCompound(tag);
        NBTTagCompound fireworks = new NBTTagCompound();
        tag.setTag("Fireworks", fireworks);
        fireworks.setByte("Flight", (byte) 2);
        NBTTagList explosions = new NBTTagList();
        fireworks.setTag("Explosions", explosions);

        NBTTagCompound explosion1 = new NBTTagCompound();
        explosion1.setByte("Type", (byte) 0);
        explosion1.setIntArray("Colors", new int[]{color, 0xFFFFFF});
        explosions.appendTag(explosion1);
        return firework;
    }

    public boolean isQuakecraft(World world){
        return MapLoader.instance().getMap(world).getMappack().getMappackMetadata().getGameType().equals("quakecraft");
    }
}
