package jk_5.nailed.client.network

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.network.NailedPacket
import jk_5.nailed.network.NailedPacket._
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import net.minecraft.client.Minecraft
import jk_5.nailed.client.map.edit.MapEditManager
import jk_5.nailed.client.render.TimeUpdateRenderer
import jk_5.nailed.client.blocks.tileentity.{NailedTileEntity, IGuiTileEntity}
import jk_5.nailed.client.particle.ParticleHelper
import jk_5.nailed.client.gui.{GuiCreateAccount, GuiLogin, GuiTerminal}
import jk_5.nailed.client.scripting.ClientMachine
import jk_5.nailed.client.NailedClient
import jk_5.nailed.map.script.ScriptPacket

object MapEditHandler extends SimpleChannelInboundHandler[NailedPacket.EditMode] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.EditMode){
    MapEditManager.instance.setEnabled(msg.enable)
    if(msg.enable){
      MapEditManager.instance.readData(msg.buffer)
    }
  }
}

object OpenGuiHandler extends SimpleChannelInboundHandler[NailedPacket.GuiOpen] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.GuiOpen){
    val tile = Minecraft.getMinecraft.theWorld.getTileEntity(msg.x, msg.y, msg.z)
    tile match {
      case g: IGuiTileEntity =>
        var gui = Option(g.getGui)
        if(gui.isEmpty) return
        gui = Option(gui.get.readGuiData(msg.data))
        if(gui.isEmpty) return
        mc.displayGuiScreen(gui.get)
      case _ =>
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

object RenderListhandler extends SimpleChannelInboundHandler[NailedPacket.RenderList]{
  override def channelRead0(ctx: ChannelHandlerContext, msg: NailedPacket.RenderList){
    msg.func match {
      case 0 => NailedClient.renderer.setRenderList(msg.points)
      case 1 => NailedClient.renderer.addRenderList(msg.points)
      case 2 => NailedClient.renderer.clearRenderList()
    }
  }
}

object TileEntityDataHandler extends SimpleChannelInboundHandler[TileEntityData] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: TileEntityData){
    val tile = mc.theWorld.getTileEntity(msg.x, msg.y, msg.z)
    if(tile != null && tile.isInstanceOf[NailedTileEntity]){
      tile.asInstanceOf[NailedTileEntity].readData(msg.data)
    }
  }
}

object TimeUpdateHandler extends SimpleChannelInboundHandler[TimeUpdate] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: TimeUpdate){
    TimeUpdateRenderer.format = msg.data
  }
}

object DisplayLoginHandler extends SimpleChannelInboundHandler[DisplayLogin] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: DisplayLogin){
    mc.displayGuiScreen(new GuiLogin)
  }
}

object LoginResponseHandler extends SimpleChannelInboundHandler[LoginResponse] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: LoginResponse){
    val gui = mc.currentScreen
    gui match {
      case l: GuiLogin => l.onResponse(msg)
      case l: GuiCreateAccount => l.onResponse(msg)
      case _ =>
    }
  }
}

object FieldStatusHandler extends SimpleChannelInboundHandler[FieldStatus] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FieldStatus){
    val gui = mc.currentScreen
    gui match {
      case l: GuiCreateAccount => l.onFieldStatus(msg)
      case _ =>
    }
  }
}

object MachineUpdateHandler extends SimpleChannelInboundHandler[ScriptPacket.UpdateMachine] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: ScriptPacket.UpdateMachine){
    val machine = Option(NailedClient.machines.get(msg.instanceId))
    machine.foreach(_.readData(msg.data))
  }
}
