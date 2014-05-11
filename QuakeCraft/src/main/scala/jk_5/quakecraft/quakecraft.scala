package jk_5.quakecraft

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import scala.collection.mutable
import cpw.mods.fml.common.network.NetworkCheckHandler
import java.util
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.MinecraftForge
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import jk_5.nailed.api.events.{MapCreatedEvent, RegisterLuaApiEvent}
import jk_5.nailed.util.ChatColor
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraftforge.event.entity.player.{PlayerDropsEvent, PlayerInteractEvent}
import jk_5.nailed.api.{RayTracer, NailedAPI}
import net.minecraft.item.{ItemStack, ItemHoe}
import net.minecraft.util.MovingObjectPosition.MovingObjectType
import net.minecraft.entity.item.EntityFireworkRocket
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.{EntityDamageSource, DamageSource, ChatComponentTranslation}
import jk_5.nailed.api.map.{PvpIgnoringDamageSource, Map}
import net.minecraft.world.World
import jk_5.nailed.api.player.Player
import jk_5.nailed.api.effect.firework.{Firework, Color, FireworkEffect}
import net.minecraftforge.event.entity.living.{LivingHurtEvent, LivingFallEvent}
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.potion.{Potion, PotionEffect}
import jk_5.nailed.api.scripting.{ILuaContext, ILuaAPI}
import jk_5.nailed.api.map.scoreboard.DisplayType
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", version = "0.1", dependencies = "required-after:Nailed", modLanguage = "scala")
object Quakecraft {

  val reloadCooldown = mutable.HashMap[String, Integer]()
  val doneMaps = mutable.ArrayBuffer[String]()

  @NetworkCheckHandler def accept(versions: util.Map[String, String], side: Side) = true

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    if(event.getSide.isClient) return
    FMLCommonHandler.instance().bus().register(this)
    MinecraftForge.EVENT_BUS.register(this)
  }

  @SubscribeEvent def registerApi(event: RegisterLuaApiEvent){
    if(this.isQuakecraft(event.getMap)){
      event.registerApi(new QuakecraftLuaApi(event.getMap))
    }
  }

  @SubscribeEvent def onMapRegistered(event: MapCreatedEvent){
    if(this.isQuakecraft(event.map)){
      val obj = event.map.getScoreboardManager.getOrCreateObjective("kills")
      obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Kills")
    }
  }

  @SubscribeEvent def onCooldownTick(event: TickEvent.PlayerTickEvent){
    if(event.phase == TickEvent.Phase.START) return
    val id = event.player.getGameProfile.getId
    if(reloadCooldown.contains(id)){
      reloadCooldown.put(id, reloadCooldown.get(id).get + 1)
    }
  }

  @SubscribeEvent def onInteract(event: PlayerInteractEvent){
    val world = event.entity.worldObj
    val player = NailedAPI.getPlayerRegistry.getPlayer(event.entityPlayer)
    val map = NailedAPI.getMapLoader.getMap(world)
    if(!this.isQuakecraft(map)) return
    if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return
    if(!this.reloadCooldown.contains(player.getId)) this.reloadCooldown.put(player.getId, 21)
    if(this.reloadCooldown.get(player.getId).get <= 30) return
    val held = event.entityPlayer.getHeldItem
    if(held != null && held.getItem.isInstanceOf[ItemHoe]){
      val result = RayTracer.rayTrace(event.entityPlayer)
      if(result != null){
        if(result.typeOfHit == MovingObjectType.ENTITY){
          this.particleBeam((event.entity.posX, event.entity.posY, event.entity.posZ), (result.entityHit.posX, result.entityHit.posY, result.entityHit.posZ), world)
          val entity = new EntityFireworkRocket(world, result.entityHit.posX, result.entityHit.posY + (result.entityHit.height / 2), result.entityHit.posZ, this.getExplosionEffect(player).toItemStack)
          world.spawnEntityInWorld(entity)
          world.setEntityState(entity, 17.toByte)
          world.removeEntity(entity)
          world.playSoundAtEntity(result.entityHit, "mob.blaze.death", 2f, 4f)
          world.playSoundAtEntity(result.entityHit, "random.explode", 2f, 4f)
          result.entityHit match {
            case hit: EntityLivingBase =>
              hit.attackEntityFrom(new DamageSourceRailgun(event.entity), hit.getMaxHealth)
              val obj = map.getScoreboardManager.getOrCreateObjective("kills")
              val score = obj.getScore(player.getUsername)
              score.addValue(1)
              if(score.getValue >= 25){
                this.doneMaps += map.getSaveFileName
                map.broadcastChatMessage(new ChatComponentTranslation("quakecraft.message.winner", player.getUsername))
              }
            case _ =>
          }
        }else if(result.typeOfHit == MovingObjectType.BLOCK){
          this.particleBeam((event.entity.posX, event.entity.posY, event.entity.posZ), (result.blockX + 0.5d, result.blockY + 0.5d, result.blockZ + 0.5d), world)
          world.playSoundAtEntity(event.entity, "mob.blaze.hit", 2f, 4f)
        }
        this.reloadCooldown.put(player.getId, 0)
      }
    }
  }

  def particleBeam(start: (Double, Double, Double), end: (Double, Double, Double), world: World){
    val particlesPerBlock = 1d
    val dx = end._1 - start._1
    val dy = end._2 - start._2
    val dz = end._3 - start._3
    val d = Math.sqrt(dx * dx + dy * dy + dz * dz) / (1 / particlesPerBlock)
    val total = (d * particlesPerBlock).toInt
    for(i <- 0 until total){
      val x = dx / d * i + start._1
      val y = dy / d * i + start._2
      val z = dz / d * i + start._3
      val entity = new EntityFireworkRocket(world, x, y, z, null)
      world.spawnEntityInWorld(entity) //TODO: spawnParticle
      world.removeEntity(entity)
    }
  }

  @SubscribeEvent def onPlayerDrop(event: PlayerDropsEvent){
    if(this.isQuakecraft(event.entity.worldObj)){
      event.setCanceled(true)
    }
  }

  @SubscribeEvent def onEntitySpawn(event: EntityJoinWorldEvent) = event.entity match {
    case p: EntityPlayer =>
      val map = NailedAPI.getMapLoader.getMap(event.world)
      if(this.isQuakecraft(map) && map.getGameManager.isGameRunning){
        p.inventory.setInventorySlotContents(0, this.getRailgun)
        p.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true))
      }else if(this.isQuakecraft(event.entity.worldObj)){
        p.inventory.setInventorySlotContents(0, null)
        p.removePotionEffect(Potion.moveSpeed.id)
      }
    case _ =>
  }

  def getRailgun: ItemStack = {
    val stack = new ItemStack(Items.wooden_hoe)
    stack.setStackDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun")
    stack
  }

  @SubscribeEvent def onDamage(event: LivingHurtEvent){
    if(!this.isQuakecraft(event.entity.worldObj)) return
    if(!event.source.isInstanceOf[DamageSourceRailgun] && event.source != DamageSource.outOfWorld){
      event.setCanceled(true)
    }
  }

  @SubscribeEvent def onFall(event: LivingFallEvent){
    if(this.isQuakecraft(event.entity.worldObj)){
      event.setCanceled(true)
    }
  }

  def getExplosionEffect(shooter: Player): Firework = {
    val expl1 = FireworkEffect.builder().`with`(FireworkEffect.Type.BALL).withColor(Color.WHITE, Color.RED, Color.YELLOW, Color.ORANGE).build()
    val rocket = new Firework
    rocket.addEffect(expl1)
    rocket.setPower(2)
    rocket
  }

  def isQuakecraft(world: World): Boolean = this.isQuakecraft(NailedAPI.getMapLoader.getMap(world))
  def isQuakecraft(map: Map): Boolean = map.getMappack != null && map.getMappack.getMappackMetadata.getGameType == "quakecraft"
}

class DamageSourceRailgun(entity: Entity) extends EntityDamageSource("railgun", entity) with PvpIgnoringDamageSource {
  this.setDamageBypassesArmor()
  override def disableWhenPvpDisabled() = true
}

class QuakecraftLuaApi(val map: Map) extends ILuaAPI {
  override def getNames = Array("quakecraft")
  override def advance(paramDouble: Double) = {}
  override def shutdown() = {}
  override def startup() = {}
  override def callMethod(context: ILuaContext, method: Int, arguments: Array[AnyRef]): Array[AnyRef] = method match {
    case 0 => Array(Quakecraft.doneMaps.contains(map.getSaveFileName).asInstanceOf[AnyRef])
    case 1 =>
      val obj = map.getScoreboardManager.getOrCreateObjective("kills")
      map.getScoreboardManager.setDisplay(DisplayType.SIDEBAR, obj)
      map.getPlayers.foreach(p => {
        val gun = Quakecraft.getRailgun
        p.getEntity.inventory.setInventorySlotContents(0, gun)
        p.getEntity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true))
        obj.getScore(p.getUsername).setValue(0)
      })
      Array.empty
  }
  override def getMethodNames = Array("isDone", "giveItems")
}
