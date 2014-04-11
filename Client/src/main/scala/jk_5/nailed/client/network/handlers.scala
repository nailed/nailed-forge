package jk_5.nailed.client.network

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.network.NailedPacket
import jk_5.nailed.network.NailedPacket.{TimeUpdate, CheckClientUpdates}
import jk_5.nailed.client.updater.UpdaterApi
import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import net.minecraft.client.Minecraft
import jk_5.nailed.client.map.NailedWorldProvider
import jk_5.nailed.client.map.edit.MapEditManager
import jk_5.nailed.client.render.{TimeUpdateRenderer, NotificationRenderer}
import jk_5.nailed.client.blocks.tileentity.{NailedTileEntity, IGuiTileEntity}
import jk_5.nailed.client.particle.ParticleHelper
import jk_5.nailed.client.achievement.NailedAchievements
import jk_5.nailed.client.skinsync.SkinSync
import jk_5.nailed.NailedLog
import java.io.File
import javax.imageio.ImageIO
import io.netty.buffer.ByteBufInputStream
import jk_5.nailed.client.gui.GuiTerminal
import jk_5.nailed.client.scripting.ClientMachine
import jk_5.nailed.client.NailedClient

object ClientUpdateHandler extends SimpleChannelInboundHandler[NailedPacket.CheckClientUpdates] {
  val updaterFactory = new ThreadFactoryBuilder().setDaemon(false).setNameFormat("Updater main thread %d").build()

  override def channelRead0(ctx: ChannelHandlerContext, msg: CheckClientUpdates){
    updaterFactory.newThread(new Runnable {
      override def run(){
        if(UpdaterApi.update()){
          val comp = new ChatComponentText("Installed a few updates. Restart the game to make them active")
          comp.getChatStyle.setColor(EnumChatFormatting.GREEN)
          mc.ingameGUI.getChatGUI.printChatMessage(comp)
        }
      }
    })
  }
}

object MapDataHandler extends SimpleChannelInboundHandler[NailedPacket.MapData] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.MapData){
    val world = Minecraft.getMinecraft.theWorld
    if(world.provider.dimensionId == msg.dimId && world.provider.isInstanceOf[NailedWorldProvider]) {
      world.provider.asInstanceOf[NailedWorldProvider].readData(msg.data)
    }
  }
}

object MapEditHandler extends SimpleChannelInboundHandler[NailedPacket.EditMode] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.EditMode){
    MapEditManager.instance.setEnabled(msg.enable)
    if(msg.enable){
      MapEditManager.instance.readData(msg.buffer)
    }
  }
}

object NotificationHandler extends SimpleChannelInboundHandler[NailedPacket.Notification] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.Notification){
    NotificationRenderer.addNotification(msg.message, msg.icon, msg.color)
  }
}

object OpenGuiHandler extends SimpleChannelInboundHandler[NailedPacket.GuiOpen] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.GuiOpen){
    val tile = Minecraft.getMinecraft.theWorld.getTileEntity(msg.x, msg.y, msg.z)
    if(tile != null && tile.isInstanceOf[IGuiTileEntity]) {
      var gui = tile.asInstanceOf[IGuiTileEntity].getGui
      if(gui == null) return
      gui = gui.readGuiData(msg.data)
      if(gui == null) return
      Minecraft.getMinecraft.displayGuiScreen(gui)
    }
  }
}

object ParticleHandler extends SimpleChannelInboundHandler[NailedPacket.Particle] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.Particle){
    val world = mc.theWorld
    0 until 50 foreach(_ => {
      val f = world.rand.nextFloat - world.rand.nextFloat
      val f1 = world.rand.nextFloat * 2.0F
      val f2 = world.rand.nextFloat - world.rand.nextFloat
      ParticleHelper.spawnParticle(msg.name, msg.x + f, msg.y + f1, msg.z + f2, 0, 0, 0)
    })
  }
}

object RegisterAchievementHandler extends SimpleChannelInboundHandler[NailedPacket.RegisterAchievement] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.RegisterAchievement){
    NailedAchievements.register(msg.enable)
  }
}

object SkinDataHandler extends SimpleChannelInboundHandler[NailedPacket.PlayerSkin] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.PlayerSkin){
    if(msg.isSkin) {
      SkinSync.getInstance.setPlayerSkinName(msg.username, msg.skin)
    }else{
      SkinSync.getInstance.setPlayerCloakName(msg.username, msg.skin)
    }
  }
}

object StoreSkinHandler extends SimpleChannelInboundHandler[NailedPacket.StoreSkin] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.StoreSkin){
    NailedLog.info("Incoming {} data for {}", if(msg.isCape) "cape" else "skin", msg.skinName)
    val dest = new File("skincache", (if(msg.isCape) "cape_" else "skin_") + msg.skinName + ".png")
    dest.getParentFile.mkdirs
    val image = ImageIO.read(new ByteBufInputStream(msg.data))
    ImageIO.write(image, "PNG", dest)
    msg.data.release
    if(msg.isCape){
      SkinSync.getInstance.cacheCapeData(msg.skinName, image)
    }else{
      SkinSync.getInstance.cacheSkinData(msg.skinName, image)
    }
    NailedLog.info("Stored {} data for {}", if(msg.isCape) "cape" else "skin", msg.skinName)
  }
}

object TerminalGuiHandler extends SimpleChannelInboundHandler[NailedPacket.OpenTerminalGui] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.OpenTerminalGui){
    val machine = this.getMachine(msg.instanceId)
    mc.displayGuiScreen(new GuiTerminal(machine, msg.width, msg.height))
    machine.turnOn()
  }

  private def getMachine(id: Int): ClientMachine ={
    var ret = NailedClient.machines.get(id)
    if(ret == null) {
      ret = new ClientMachine(id)
      NailedClient.machines.add(id, ret)
    }
    ret
  }
}

object TileEntityDataHandler extends SimpleChannelInboundHandler[NailedPacket.TileEntityData] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.TileEntityData){
    val tile = mc.theWorld.getTileEntity(msg.x, msg.y, msg.z)
    if(tile != null && tile.isInstanceOf[NailedTileEntity]){
      tile.asInstanceOf[NailedTileEntity].readData(msg.data)
    }
  }
}

object TimeUpdateHandler extends SimpleChannelInboundHandler[NailedPacket.TimeUpdate] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: TimeUpdate){
    TimeUpdateRenderer.format = msg.data
  }
}
