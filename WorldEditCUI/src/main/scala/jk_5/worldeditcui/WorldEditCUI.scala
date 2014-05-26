package jk_5.worldeditcui

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.MinecraftForge
import jk_5.worldeditcui.render.WERenderer
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.{ClientDisconnectionFromServerEvent, ClientConnectedToServerEvent}
import jk_5.worldeditcui.render.region.CuboidRegion
import jk_5.worldeditcui.network.WENetworkHandler

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "WorldEditCUI", name = "WorldEditCUI", version = "0.1-SNAPSHOT", modLanguage = "scala")
object WorldEditCUI {

  final val TWOPI = Math.PI * 2

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    MinecraftForge.EVENT_BUS.register(WERenderer)
    FMLCommonHandler.instance.bus.register(WERenderer)
    MinecraftForge.EVENT_BUS.register(this)
    FMLCommonHandler.instance.bus.register(this)
  }

  @SubscribeEvent def onLogin(event: ClientConnectedToServerEvent){
    WENetworkHandler.sendMessage("v|2")
  }

  @SubscribeEvent def onLogout(event: ClientDisconnectionFromServerEvent){
    WERenderer.selection = Some(new CuboidRegion)
    WERenderer.selection.get.initialize()
  }
}
