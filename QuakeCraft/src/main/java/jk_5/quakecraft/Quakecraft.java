package jk_5.quakecraft;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.RayTracer;
import jk_5.nailed.api.events.MapCreatedEvent;
import jk_5.nailed.api.events.RegisterLuaApiEvent;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.Score;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.util.ChatColor;
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
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", version = "0.1", dependencies = "required-after:Nailed")
public class Quakecraft {

    @Mod.Instance("QuakeCraft")
    public static Quakecraft instance;

    public java.util.Map<String, Integer> reloadCooldown = Maps.newHashMap();

    @NetworkCheckHandler
    public boolean accept(java.util.Map<String, String> versions, Side side){
        return true;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if(event.getSide().isServer()){
            FMLCommonHandler.instance().bus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void registerApi(RegisterLuaApiEvent event){
        if(isQuakecraft(event.getMap())){
            event.registerApi(new QuakecraftLuaApi(event.getMap()));
        }
    }

    @SubscribeEvent
    public void registerApi(MapCreatedEvent event){
        if(isQuakecraft(event.map)){
            Objective obj = event.map.getScoreboardManager().getOrCreateObjective("kills");
            obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Kills");
        }
    }

    @SubscribeEvent
    public void onCooldownTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        String id = event.player.getGameProfile().getId();
        if(Quakecraft.instance.reloadCooldown.containsKey(id)){
            int ticks = Quakecraft.instance.reloadCooldown.get(id) + 1;
            Quakecraft.instance.reloadCooldown.put(id, ticks);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event){
        World world = event.entity.worldObj;
        Player player = NailedAPI.getPlayerRegistry().getPlayer(event.entityPlayer);
        Map map = NailedAPI.getMapLoader().getMap(world);
        if(this.isQuakecraft(map)){
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
                if(!this.reloadCooldown.containsKey(player.getId())){
                    this.reloadCooldown.put(player.getId(), 21);
                }
                if(this.reloadCooldown.get(player.getId()) <= 30) return;
                ItemStack held = event.entityPlayer.getHeldItem();
                if(held != null && held.getItem() instanceof ItemHoe){
                    MovingObjectPosition result = RayTracer.rayTrace(event.entityPlayer);
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

                            Objective obj = map.getScoreboardManager().getOrCreateObjective("kills");
                            Score score = obj.getScore(player.getUsername());
                            score.addValue(1);

                            if(score.getValue() >= 25){
                                /*for(IInstruction instruction : map.getInstructionController().getInstructions()){
                                    if(instruction instanceof InstructionAwaitFinalKill){
                                        ((InstructionAwaitFinalKill) instruction).finalKillMade = true;
                                    }
                                }*/
                                QuakecraftLuaApi.doneMaps.add(map.getSaveFileName());
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
    public void onPlayerDrop(PlayerDropsEvent event){
        if(this.isQuakecraft(event.entity.worldObj)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void onEntitySpawn(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.entity;
            if(this.isQuakecraft(event.world)){
                Map map = NailedAPI.getMapLoader().getMap(event.world);
                if(map.getGameManager().isGameRunning()){
                    ItemStack stack = new ItemStack(Items.wooden_hoe, 1);
                    stack.setStackDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun");
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
    public void onDamage(LivingHurtEvent event){
        if(!this.isQuakecraft(event.entity.worldObj)) return;
        if(!(event.source instanceof DamageSourceRailgun) && !(event.source == DamageSource.outOfWorld)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event){
        if(this.isQuakecraft(event.entity.worldObj)){
            event.setCanceled(true);
        }
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
        Mappack mappack = NailedAPI.getMapLoader().getMap(world).getMappack();
        return mappack != null && mappack.getMappackMetadata().getGameType().equals("quakecraft");
    }

    public boolean isQuakecraft(Map map){
        Mappack mappack = map.getMappack();
        return mappack != null && mappack.getMappackMetadata().getGameType().equals("quakecraft");
    }
}
