package jk_5.nailed.network;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import jk_5.nailed.map.script.ScriptPacket;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class NailedPacketCodec extends FMLIndexedMessageToMessageCodec<NailedPacket> {

    public NailedPacketCodec(){
        this.addDiscriminator(0, NailedPacket.Notification.class);
        this.addDiscriminator(1, NailedPacket.MovementEvent.class);
        this.addDiscriminator(2, NailedPacket.GuiReturnDataPacket.class);
        this.addDiscriminator(3, NailedPacket.GuiOpen.class);
        this.addDiscriminator(4, NailedPacket.TileEntityData.class);
        this.addDiscriminator(5, NailedPacket.TimeUpdate.class);
        //6
        //7
        this.addDiscriminator(8, NailedPacket.MapData.class);
        this.addDiscriminator(9, NailedPacket.Particle.class);
        this.addDiscriminator(10, NailedPacket.FPSSummary.class);
        this.addDiscriminator(11, NailedPacket.OpenTerminalGui.class);
        this.addDiscriminator(12, NailedPacket.EditMode.class);
        this.addDiscriminator(13, NailedPacket.RegisterAchievement.class);
        this.addDiscriminator(14, NailedPacket.CheckClientUpdates.class);
        this.addDiscriminator(15, NailedPacket.DisplayLogin.class);
        this.addDiscriminator(16, NailedPacket.Login.class);
        this.addDiscriminator(17, NailedPacket.LoginResponse.class);
        this.addDiscriminator(18, NailedPacket.FieldStatus.class);
        this.addDiscriminator(19, NailedPacket.CreateAccount.class);

        this.addDiscriminator(20, ScriptPacket.QueueEvent.class);
        this.addDiscriminator(21, ScriptPacket.StateEvent.class);
        this.addDiscriminator(22, ScriptPacket.UpdateMachine.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, NailedPacket msg, ByteBuf target) throws Exception{
        msg.encode(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, NailedPacket msg){
        msg.decode(source);
    }
}
