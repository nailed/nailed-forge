package jk_5.quakecraft

import java.util
import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.MinecraftForge
import cpw.mods.fml.common.gameevent.TickEvent
import java.lang.String
import scala.collection.mutable
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util._
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.map.{PvpIgnoringDamageSource, MapLoader}
import net.minecraft.item.{ItemHoe, ItemStack}
import net.minecraft.init.Items
import jk_5.nailed.util.ChatColor
import net.minecraft.potion.{Potion, PotionEffect}
import net.minecraft.world.World
import jk_5.nailed.util.raytracing.RayTracer
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraftforge.event.entity.player.{PlayerDropsEvent, PlayerInteractEvent}
import jk_5.nailed.players.PlayerRegistry
import net.minecraft.entity.item.EntityFireworkRocket
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import jk_5.nailed.map.instruction.{GameController, IInstruction, TimedInstruction, RegisterInstructionEvent}
import net.minecraftforge.event.entity.living.{LivingFallEvent, LivingHurtEvent}
import net.minecraft.scoreboard.{IScoreObjectiveCriteria, ScoreObjective}

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", version = "0.1", dependencies = "required-after:Nailed", modLanguage = "scala")
object Quakecraft {
  val reloadCooldown = mutable.HashMap[String, Int]()

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    if(event.getSide.isServer){
      FMLCommonHandler.instance.bus.register(this)
      MinecraftForge.EVENT_BUS.register(this)
    }
  }

  @SubscribeEvent def onCooldownTick(event: TickEvent.PlayerTickEvent){
    if(event.phase == TickEvent.Phase.START) return
    val id = event.player.func_146103_bH.getId
    if(this.reloadCooldown.get(id).isDefined){
      val ticks = this.reloadCooldown.get(id).get + 1
      this.reloadCooldown.put(id, ticks)
    }
  }

  @SubscribeEvent def onInteract(event: PlayerInteractEvent){
    val world = event.entity.worldObj
    val player = PlayerRegistry.instance.getPlayer(event.entityPlayer)
    if(this.isQuakecraft(world)){
      val map = MapLoader.instance.getMap(world)
      if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR){
        if(this.reloadCooldown.get(player.getId).isEmpty){
          this.reloadCooldown.put(player.getId, 31)
        }
        if(this.reloadCooldown.get(player.getId).get <= 30) return
        val held = event.entityPlayer.getHeldItem
        if(held != null && held.getItem.isInstanceOf[ItemHoe]){
          val result = this.rayTrace(event.entityPlayer)
          if(result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
            val particlesPerBlock = 1d
            val dx = result.entityHit.posX - event.entity.posX
            val dy = result.entityHit.posY - event.entity.posY
            val dz = result.entityHit.posZ - event.entity.posZ
            val d = Math.sqrt(dx * dx + dy * dy + dz * dz) / (1 / particlesPerBlock)
            val total = (d * particlesPerBlock).toInt
            for(i <- 0 until total){
              val x = dx / d * i + event.entity.posX
              val y = dy / d * i + event.entity.posY + event.entityPlayer.eyeHeight
              val z = dz / d * i + event.entity.posZ
              val entity = new EntityFireworkRocket(world, x, y, z, null)
              world.spawnEntityInWorld(entity)
              world.removeEntity(entity)
            }

            val rocket = new EntityFireworkRocket(world, result.entityHit.posX, result.entityHit.posY + (result.entityHit.height / 2), result.entityHit.posZ, this.getEntityExplosionEffect(0xFF0000))
            world.spawnEntityInWorld(rocket)
            world.setEntityState(rocket, 17.toByte)
            world.removeEntity(rocket)

            event.entity.worldObj.playSoundAtEntity(result.entityHit, "mob.blaze.death", 2.0f, 4.0f)
            event.entity.worldObj.playSoundAtEntity(result.entityHit, "random.explode", 2.0f, 4.0f)

            result.entityHit match {
              case hit: EntityLivingBase => if(hit.isEntityAlive){
                hit.attackEntityFrom(new DamageSourceRailgun(event.entity), hit.getMaxHealth)
                val scoreboard = world.getScoreboard
                val objective = scoreboard.getObjective(map.getID + "-kills")
                val score = scoreboard.func_96529_a(player.getUsername, objective)
                score.func_96649_a(1)
                if(score.getScorePoints >= 25){
                  import scala.collection.JavaConversions._
                  for(instruction <- map.getGameController.getInstructions.filter(_.isInstanceOf[InstructionAwaitFinalKill]).map(_.asInstanceOf[InstructionAwaitFinalKill])){
                    instruction.finalKillMade = true
                  }
                  map.broadcastChatMessage(new ChatComponentTranslation("quakecraft.message.winner", player.getUsername))
                }
              }
              case e => {}
            }
          }else{
            event.entity.worldObj.playSoundAtEntity(event.entity, "mob.blaze.hit", 2.0f, 4.0f)
          }
          this.reloadCooldown.put(player.getId, 0)
        }
      }
    }
  }

  @SubscribeEvent def onPlayerDrop(event: PlayerDropsEvent){
    if(this.isQuakecraft(event.entity.worldObj)){
      event.setCanceled(true)
    }
  }

  @SubscribeEvent def onEntitySpawn(event: EntityJoinWorldEvent) = event.entity match {
    case player: EntityPlayer =>
      if(this.isQuakecraft(event.world)){
        val map = MapLoader.instance.getMap(event.world)
        if(map.getGameController.isRunning){
          val stack = new ItemStack(Items.wooden_hoe, 1)
          stack.func_151001_c(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun")
          player.inventory.setInventorySlotContents(0, stack)
          player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true))
        }
      }else if (this.isQuakecraft(event.entity.worldObj)){
        player.inventory.setInventorySlotContents(0, null)
        player.removePotionEffect(Potion.moveSpeed.id)
      }
    case e => {}
  }

  @SubscribeEvent def registerInstructions(event: RegisterInstructionEvent){
    event.register("startquakecraft", classOf[InstructionStartQuakecraft])
    event.register("awaitfinakquakekill", classOf[InstructionAwaitFinalKill])
  }

  @SubscribeEvent def onDamage(event: LivingHurtEvent){
    if(!this.isQuakecraft(event.entity.worldObj)) return
    if(!event.source.isInstanceOf[DamageSourceRailgun] && !(event.source == DamageSource.outOfWorld)){
      event.setCanceled(true)
    }
  }

  @SubscribeEvent def onFall(event: LivingFallEvent){
    if(this.isQuakecraft(event.entity.worldObj)){
      event.setCanceled(true)
    }
  }

  @SuppressWarnings(Array("unchecked")) def rayTrace(player: EntityPlayer): MovingObjectPosition = {
    var result: MovingObjectPosition = null
    val world = player.worldObj
    val pointedBlock = RayTracer.reTrace(world, player)
    result = pointedBlock
    val rangeLimit = 500d
    var d1 = rangeLimit
    val vec3 = world.getWorldVec3Pool.getVecFromPool(player.posX, player.posY + player.eyeHeight, player.posZ)
    val lookVec = player.getLook(1.0F)
    val vec32 = vec3.addVector(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit)
    if(pointedBlock != null){
      d1 = pointedBlock.hitVec.distanceTo(vec3)
    }
    var pointedEntity: Entity = null
    val f1 = 1.0F
    val list = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit).expand(f1.asInstanceOf[Double], f1.asInstanceOf[Double], f1.asInstanceOf[Double])).asInstanceOf[util.List[Entity]]
    var d2 = d1
    import scala.collection.JavaConversions._
    for(entity <- list.filter(_.canBeCollidedWith)){
      val f2 = entity.getCollisionBorderSize
      val axisalignedbb = entity.boundingBox.expand(f2.asInstanceOf[Double], f2.asInstanceOf[Double], f2.asInstanceOf[Double])
      val movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32)
      if(axisalignedbb.isVecInside(vec3)){
        if(0.0D < d2 || d2 == 0.0D){
          pointedEntity = entity
          d2 = 0.0D
        }
      }else if(movingobjectposition != null){
        val d3 = vec3.distanceTo(movingobjectposition.hitVec)
        if(d3 < d2 || d2 == 0.0D){
          if(entity == entity.ridingEntity && !entity.canRiderInteract){
            if(d2 == 0.0D){
              pointedEntity = entity
            }
          }else{
            pointedEntity = entity
            d2 = d3
          }
        }
      }
    }
    if(pointedEntity != null && (d2 < d1 || result == null)){
      result = new MovingObjectPosition(pointedEntity)
    }
    result
  }

  def getEntityExplosionEffect(color: Int): ItemStack = {
    val firework = new ItemStack(Items.fireworks, 1)
    val tag = new NBTTagCompound
    firework.setTagCompound(tag)
    val fireworks = new NBTTagCompound
    tag.setTag("Fireworks", fireworks)
    fireworks.setByte("Flight", 2.toByte)
    val explosions = new NBTTagList
    fireworks.setTag("Explosions", explosions)
    val explosion1 = new NBTTagCompound
    explosion1.setByte("Type", 0.toByte)
    explosion1.setIntArray("Colors", Array[Int](color, 0xFFFFFF))
    explosions.appendTag(explosion1)
    firework
  }

  def isQuakecraft(world: World): Boolean = {
    val mappack = MapLoader.instance.getMap(world).getMappack
    if(mappack == null) return false
    mappack.getMappackMetadata.getGameType == "quakecraft"
  }
}

/**
 * No description given
 *
 * @author jk-5
 */
class DamageSourceRailgun(entity: Entity) extends EntityDamageSource("railgun", entity) with PvpIgnoringDamageSource {
  def disableWhenPvpDisabled = false
}

/**
 * No description given
 *
 * @author jk-5
 */
class InstructionAwaitFinalKill extends TimedInstruction {
  var finalKillMade: Boolean = false
  def injectArguments(args: String){}
  def executeTimed(controller: GameController, ticks: Int) = this.finalKillMade
  def cloneInstruction: IInstruction = new InstructionAwaitFinalKill
}

/**
 * No description given
 *
 * @author jk-5
 */
class InstructionStartQuakecraft extends IInstruction {
  def injectArguments(args: String){}
  def cloneInstruction = new InstructionStartQuakecraft
  def execute(controller: GameController){
    import scala.collection.JavaConversions._
    for(player <- controller.getMap.getPlayers){
      val stack = new ItemStack(Items.wooden_hoe, 1)
      stack.func_151001_c(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun")
      player.getEntity.inventory.setInventorySlotContents(0, stack)
      player.getEntity.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true))
    }
    val objectiveName = controller.getMap.getID + "-kills"
    val scoreboard = controller.getMap.getWorld.getScoreboard
    if(scoreboard.getObjective(objectiveName) != null){
      val objective: ScoreObjective = scoreboard.getObjective(objectiveName)
      scoreboard.func_96519_k(objective)
    }
    val objective = controller.getMap.getWorld.getScoreboard.func_96535_a(objectiveName, IScoreObjectiveCriteria.field_96641_b)
    objective.setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Leaderboard")
    scoreboard.func_96530_a(1, objective)
    import scala.collection.JavaConversions._
    for(player <- controller.getMap.getPlayers){
      scoreboard.func_96529_a(player.getUsername, objective).func_96647_c(0)
    }
  }
}
