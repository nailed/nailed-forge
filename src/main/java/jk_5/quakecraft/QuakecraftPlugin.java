package jk_5.quakecraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", dependencies = "required-after:Nailed")
@NetworkMod
public class QuakecraftPlugin {

    @Mod.Instance("QuakeCraft")
    public static QuakecraftPlugin instance;

    @SidedProxy(modId = "QuakeCraft", clientSide = "jk_5.quakecraft.ProxyClient", serverSide = "jk_5.quakecraft.ProxyCommon")
    public static ProxyCommon proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if(event.getSide().isServer()){
            MinecraftForge.EVENT_BUS.register(this);
        }
        EntityRegistry.registerModEntity(EntityBullet.class, "entityBullet", 3, this, 128, 5, true);
        proxy.register(this);
    }

    @ForgeSubscribe
    public void onInteract(PlayerInteractEvent event){
        Map map = MapLoader.instance().getMap(event.entity.worldObj);
        if(this.isQuakecraft(map)){
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR){
                ItemStack held = event.entityPlayer.getHeldItem();
                if(held != null && held.getItem() instanceof ItemHoe){
                    EntityBullet ns = new EntityBullet(event.entity.worldObj, event.entityPlayer);
                    event.entity.worldObj.spawnEntityInWorld(ns);
                }
            }
        }
    }

    public boolean isQuakecraft(Map map){
        return map.getMappack().getMappackMetadata().getGameType().equals("quakecraft");
    }
}
