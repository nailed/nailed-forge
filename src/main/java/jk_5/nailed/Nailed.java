package jk_5.nailed;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "Nailed", version = "2.0.0-SNAPSHOT")
@NetworkMod(clientSideRequired = true)
public class Nailed {

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        event.getModLog().info("HERE WE GO!");
    }

    @VersionCheckHandler
    public boolean acceptClientVersion(String version){
        return true;
    }
}
