package jk_5.nailed

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import com.google.gson.{JsonParser, JsonObject}
import jk_5.nailed.irc.IrcBot
import jk_5.nailed.permissions.{PermissionEventHandler, NailedPermissionFactory}
import jk_5.nailed.api.plugin.DefaultPluginManager
import cpw.mods.fml.relauncher.{Side, FMLLaunchHandler}
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.map.NailedMapLoader
import jk_5.nailed.map.mappack.NailedMappackLoader
import jk_5.nailed.players.NailedPlayerRegistry
import jk_5.nailed.scheduler.{SchedulerCrashCallable, NailedScheduler}
import jk_5.nailed.map.teleport.{TeleportEventListenerMotion, TeleportEventListenerEffect, TeleportEventListenerForge, NailedTeleporter}
import jk_5.nailed.permissions.zone.NailedZoneRegistry
import jk_5.nailed.camera.MovementHandler
import net.minecraft.server.MinecraftServer
import jk_5.nailed.server.command.{LoggingCommandListener, NailedCommandManager}
import java.io._
import org.apache.commons.io.IOUtils
import java.text.SimpleDateFormat
import java.util.Date
import jk_5.nailed.chat.joinmessage.JoinMessageSender
import jk_5.nailed.network.NailedNetworkHandler
import jk_5.nailed.ipc.{IpcManager, IpcEventListener}
import net.minecraftforge.common.MinecraftForge
import jk_5.nailed.map.stat.{StatTypeManager, RegisterStatTypeEvent, StatEventHandler}
import jk_5.nailed.util.invsee.InvSeeTicker
import jk_5.nailed.blocks.NailedBlocks
import net.minecraftforge.permissions.api.{RegisteredPermValue, PermissionsManager}
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.util.NailedFoodStats
import jk_5.nailed.permissions.zone.types.{SphereZoneType, CircleZoneType, CubeZoneType, SquareZoneType}
import jk_5.nailed.api.plugin.java.JavaPluginLoader
import jk_5.nailed.api.plugin.internal.InternalPluginLoader
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import jk_5.nailed.api.events.RegisterZoneEvent
import cpw.mods.fml.common.network.NetworkCheckHandler

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "Nailed", version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84", modLanguage = "scala")
object NailedServer {

  final val COMMANDBLOCK_PERMISSION = "minecraft.commandBlock.edit"

  if(FMLLaunchHandler.side.isClient){
    throw new RuntimeException("Nailed-Server is server-only, don\'t use it on the client!")
  }

  var config: JsonObject = null
  var ircBot: IrcBot = null
  var permissionFactory: NailedPermissionFactory = null
  val pluginManager = new DefaultPluginManager

  NailedAPI.setMapLoader(new NailedMapLoader)
  NailedAPI.setMappackLoader(new NailedMappackLoader)
  NailedAPI.setPlayerRegistry(new NailedPlayerRegistry)
  NailedAPI.setScheduler(new NailedScheduler)
  NailedAPI.setTeleporter(new NailedTeleporter)
  NailedAPI.setZoneRegistry(new NailedZoneRegistry)
  NailedAPI.setMovementHandler(new MovementHandler)
  NailedAPI.setCommandRegistry(MinecraftServer.getServer.getCommandManager.asInstanceOf[NailedCommandManager])
  FMLCommonHandler.instance.bus.register(NailedAPI.getScheduler)
  this.loadPlugins()

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    val configDir = new File(event.getModConfigurationDirectory, "nailed")
    configDir.mkdirs()
    val configFile = new File(configDir, "config.json")
    if(!configFile.exists){
      NailedLog.info("Loading default config file")
      var is: InputStream = null
      var pw: PrintWriter = null
      try{
        is = this.getClass.getResourceAsStream("/assets/nailed/config.json")
        pw = new PrintWriter(configFile)
        IOUtils.copy(is, pw)
      }catch{
        case e: Exception => NailedLog.fatal("Error while creating default config file", e)
      }finally{
        IOUtils.closeQuietly(is)
        IOUtils.closeQuietly(pw)
      }
    }

    NailedLog.info("Creating config file")
    var fr: FileReader = null
    try{
      fr = new FileReader(configFile)
      config = new JsonParser().parse(fr).asInstanceOf[JsonObject]
    }catch{
      case e: FileNotFoundException => e.printStackTrace()
    }finally{
      IOUtils.closeQuietly(fr)
    }
    if(NailedAPI.getMapLoader.getMapsFolder.exists){
      NailedLog.info("Clearing away old maps folder")
      new File(".", "mapbackups").mkdirs
      val dest: File = new File(new File(".", "mapbackups"), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date))
      NailedAPI.getMapLoader.getMapsFolder.renameTo(dest)
    }

    NailedLog.info("Loading join message")
    JoinMessageSender.readConfig(configDir)

    NailedLog.info("Initializing network pipeline")
    NailedNetworkHandler.registerChannel()

    NailedLog.info("Registering event handlers")
    val ipcEventListener = new IpcEventListener
    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(NailedAPI.getPlayerRegistry)
    MinecraftForge.EVENT_BUS.register(NailedAPI.getMapLoader)
    MinecraftForge.EVENT_BUS.register(NailedAPI.getMappackLoader)
    MinecraftForge.EVENT_BUS.register(new StatEventHandler)
    MinecraftForge.EVENT_BUS.register(new TeleportEventListenerForge)
    MinecraftForge.EVENT_BUS.register(new TeleportEventListenerEffect)
    MinecraftForge.EVENT_BUS.register(new TeleportEventListenerMotion)
    MinecraftForge.EVENT_BUS.register(new PermissionEventHandler)
    MinecraftForge.EVENT_BUS.register(ipcEventListener)
    MinecraftForge.EVENT_BUS.register(new LoggingCommandListener)
    FMLCommonHandler.instance.bus.register(NailedAPI.getPlayerRegistry)
    FMLCommonHandler.instance.bus.register(NailedAPI.getMapLoader)
    FMLCommonHandler.instance.bus.register(new InvSeeTicker)
    FMLCommonHandler.instance.bus.register(ipcEventListener)
    FMLCommonHandler.instance.registerCrashCallable(new SchedulerCrashCallable)

    NailedLog.info("Registering blocks")
    NailedBlocks.init()

    NailedLog.info("Registering permissionmanager")
    permissionFactory = new NailedPermissionFactory

    PermissionsManager.setPermFactory(permissionFactory, "Nailed")

    ircBot = new IrcBot
  }

  @EventHandler def init(event: FMLInitializationEvent){
    MinecraftForge.EVENT_BUS.post(new RegisterStatTypeEvent(StatTypeManager.instance.getStatTypes))

    NailedLog.info("Registering permissions")
    JoinMessageSender.registerPermissions()
    PermissionsManager.registerPermission(COMMANDBLOCK_PERMISSION, RegisteredPermValue.OP)

    NailedLog.info("Registering zone types")
    NailedAPI.getZoneRegistry.registerZones()

    IpcManager.instance.start()
  }

  @EventHandler def postInit(event: FMLPostInitializationEvent){
    NailedLog.info("Loading the mappacks")
    NailedAPI.getMappackLoader.loadMappacks(null)

    ircBot.connect()
  }

  @EventHandler def serverAboutToStart(event: FMLServerAboutToStartEvent){
    NailedAPI.getZoneRegistry.lockZones()
  }

  @EventHandler def serverStarted(event: FMLServerStartedEvent){
    PermissionsManager.addPermissionsToFactory()

    NailedLog.info("Reading permission config")
    permissionFactory.readConfig()

    NailedAPI.getMappackLoader.asInstanceOf[NailedMappackLoader].loadASync = true
  }

  @SubscribeEvent def onPlayerJoin(event: EntityJoinWorldEvent): Unit = event.entity match {
    case p: EntityPlayer => p.foodStats = new NailedFoodStats
    case _ =>
  }

  @SubscribeEvent def registerZones(event: RegisterZoneEvent){
    event.registerZoneType("Square", new SquareZoneType)
    event.registerZoneType("Cube", new CubeZoneType)
    event.registerZoneType("Circle", new CircleZoneType)
    event.registerZoneType("Sphere", new SphereZoneType)
  }

  def loadPlugins(){
    pluginManager.registerLoader(classOf[JavaPluginLoader])
    pluginManager.registerLoader(classOf[InternalPluginLoader])

    val pluginFolder = new File("plugins")
    if(pluginFolder.exists){
      val plugins = pluginManager.loadPlugins(pluginFolder)
      for(plugin <- plugins){
        try{
          plugin.getLogger.info("Loading plugin " + plugin.getName)
          plugin.onLoad()
        }catch{
          case ex: Throwable => NailedLog.warn(ex.getMessage + " initializing " + plugin.getName, ex)
        }
      }
    }else{
      pluginFolder.mkdir
    }
  }

  @NetworkCheckHandler def accepts(mods: Map[String, String], side: Side) = true
}
