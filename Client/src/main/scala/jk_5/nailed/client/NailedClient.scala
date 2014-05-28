package jk_5.nailed.client

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.relauncher.FMLLaunchHandler
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import jk_5.nailed.NailedLog
import jk_5.nailed.client.network.ClientNetworkHandler
import net.minecraftforge.common.MinecraftForge
import jk_5.nailed.client.render._
import jk_5.nailed.client.map.edit.MapEditManager
import jk_5.nailed.client.blocks.NailedBlocks
import net.minecraft.network.play.server.S2BPacketChangeGameState
import jk_5.nailed.client.scripting.ClientMachine
import jk_5.nailed.map.script.MachineRegistry

/**
 * No description given
 *
 * @author jk-5
 */
object Constants {
  final val MCVERSION = "1.7.2"
  final val MODID = "Nailed"
}

@Mod(modid = Constants.MODID, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84", modLanguage = "scala")
object NailedClient {
  if(FMLLaunchHandler.side.isServer){
    throw new RuntimeException("Nailed-Client is client-only, don\'t use it on the server!")
  }
  var renderer: CustomsRenderer = CustomsRenderer.instance()
  var fixedWidthFontRenderer: FixedWidthFontRenderer = _
  val machines = new MachineRegistry[ClientMachine]()

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    val cfg = event.getSuggestedConfigurationFile
    if(cfg.exists()) cfg.delete()

    NailedLog.info("Registering network handlers")
    ClientNetworkHandler.registerChannel()

    NailedLog.info("Registering event handlers")
    MinecraftForge.EVENT_BUS.register(TimeUpdateRenderer)
    MinecraftForge.EVENT_BUS.register(MapEditManager.instance())
    MinecraftForge.EVENT_BUS.register(CustomsRenderer.instance())
    MinecraftForge.EVENT_BUS.register(StencilSkyRenderer)
    MinecraftForge.EVENT_BUS.register(TeamInformationRenderer)
    FMLCommonHandler.instance().bus().register(MapEditManager.instance())
    FMLCommonHandler.instance().bus().register(CustomsRenderer.instance())
    FMLCommonHandler.instance().bus().register(TickHandlerClient)
    FMLCommonHandler.instance().bus().register(this)

    NailedLog.info("Adding creativetab")
    CreativeTabNailed

    NailedLog.info("Registering blocks")
    NailedBlocks.init()

    S2BPacketChangeGameState.field_149142_a(3) = null //Prevent annoying "Your gamemode has been updated" message. If we want it we'll send it ourselves
  }

  @Mod.EventHandler def init(event: FMLInitializationEvent){
    fixedWidthFontRenderer = new FixedWidthFontRenderer
  }
}
