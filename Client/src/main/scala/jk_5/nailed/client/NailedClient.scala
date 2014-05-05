package jk_5.nailed.client

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.relauncher.FMLLaunchHandler
import jk_5.nailed.util.config.ConfigFile
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import jk_5.nailed.NailedLog
import jk_5.nailed.client.achievement.NailedAchievements
import jk_5.nailed.client.network.ClientNetworkHandler
import net.minecraftforge.common.{DimensionManager, MinecraftForge}
import jk_5.nailed.client.render._
import jk_5.nailed.client.map.edit.MapEditManager
import jk_5.nailed.client.blocks.NailedBlocks
import jk_5.nailed.client.item.NailedItems
import jk_5.nailed.client.map.NailedWorldProvider
import net.minecraft.network.play.server.S2BPacketChangeGameState
import jk_5.nailed.client.scripting.ClientMachine
import jk_5.nailed.map.script.MachineRegistry
import jk_5.nailed.client.updater.UpdaterApi
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent
import net.minecraft.client.Minecraft.{getMinecraft => mc}

/**
 * No description given
 *
 * @author jk-5
 */
object Constants {
  final val MCVERSION = "1.7.2"
  final val MODID = "Nailed"
}

@Mod(modid = Constants.MODID, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84", guiFactory = "jk_5.nailed.client.config.NailedConfigGuiFactory", modLanguage = "scala")
object NailedClient {
  if(FMLLaunchHandler.side.isServer){
    throw new RuntimeException("Nailed-Client is client-only, don\'t use it on the server!")
  }

  var config: ConfigFile = _
  var providerId = -1
  var fixedWidthFontRenderer: FixedWidthFontRenderer = _
  val machines = new MachineRegistry[ClientMachine]()

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    NailedLog.info("Creating config file")
    config = new ConfigFile(event.getSuggestedConfigurationFile).setComment("Nailed main config file")

    NailedLog.info("Loading achievements")
    NailedAchievements.addAchievements()

    NailedLog.info("Registering network handlers")
    ClientNetworkHandler.registerChannel()

    NailedLog.info("Registering event handlers")
    //val handler = new ServerListHandler
    MinecraftForge.EVENT_BUS.register(TimeUpdateRenderer)
    MinecraftForge.EVENT_BUS.register(new NotificationRenderer())
    MinecraftForge.EVENT_BUS.register(MapEditManager.instance())
    MinecraftForge.EVENT_BUS.register(StencilSkyRenderer)
    //MinecraftForge.EVENT_BUS.register(handler)
    MinecraftForge.EVENT_BUS.register(TeamInformationRenderer)
    //FMLCommonHandler.instance().bus().register(handler)
    FMLCommonHandler.instance().bus().register(MapEditManager.instance())
    FMLCommonHandler.instance().bus().register(TickHandlerClient)
    FMLCommonHandler.instance().bus().register(this)

    NailedLog.info("Adding creativetab")
    CreativeTabNailed

    NailedLog.info("Registering blocks")
    NailedBlocks.init()
    NailedItems.init()

    NailedLog.info("Registering Nailed WorldProvider")
    NailedClient.providerId = NailedClient.config.getTag("providerId").setComment("The id for the nailed world provider").getIntValue(10)
    DimensionManager.registerProviderType(NailedClient.providerId, classOf[NailedWorldProvider], false)

    NailedLog.info("Overriding Default WorldProviders")
    //DimensionManager.unregisterProviderType(-1)
    DimensionManager.unregisterProviderType(0)
    //DimensionManager.unregisterProviderType(1)
    //DimensionManager.registerProviderType(-1, classOf[NailedWorldProvider], false)
    DimensionManager.registerProviderType(0, classOf[NailedWorldProvider], true)
    //DimensionManager.registerProviderType(1, classOf[NailedWorldProvider], false)

    S2BPacketChangeGameState.field_149142_a(3) = null //Prevent annoying "Your gamemode has been updated" message. If we want it we'll send it ourselves

    NailedLog.info("Updater installed: {}", UpdaterApi.updaterInstalled: java.lang.Boolean)
  }

  @Mod.EventHandler def init(event: FMLInitializationEvent){
    NailedLog.info("Registering achievements")
    NailedAchievements.init()
    fixedWidthFontRenderer = new FixedWidthFontRenderer
  }

  @SubscribeEvent def login(event: ClientConnectedToServerEvent){
    /*val uuid = mc.getSession.getPlayerID
    val packet = new NailedPacket.Login(uuid, "§uuidauth")
    ClientNetworkHandler.sendPacketToServer(packet)*/
  }
}
