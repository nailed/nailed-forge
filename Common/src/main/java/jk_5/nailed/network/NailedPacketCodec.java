package jk_5.nailed.network;

import io.netty.buffer.*;
import io.netty.channel.*;

import cpw.mods.fml.common.network.*;

import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class NailedPacketCodec extends FMLIndexedMessageToMessageCodec<NailedPacket> {

    public NailedPacketCodec() {
        //0
        this.addDiscriminator(1, NailedPacket.MovementEvent.class);
        this.addDiscriminator(2, NailedPacket.GuiReturnDataPacket.class);
        this.addDiscriminator(3, NailedPacket.GuiOpen.class);
        this.addDiscriminator(4, NailedPacket.TileEntityData.class);
        this.addDiscriminator(5, NailedPacket.TimeUpdate.class);
        //6
        this.addDiscriminator(7, NailedPacket.RenderList.class);
        //8
        this.addDiscriminator(9, NailedPacket.Particle.class);
        this.addDiscriminator(10, NailedPacket.FPSSummary.class);
        this.addDiscriminator(11, NailedPacket.OpenTerminalGui.class);
        this.addDiscriminator(12, NailedPacket.EditMode.class);
        //13
        //14
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
    public void encodeInto(ChannelHandlerContext ctx, NailedPacket msg, ByteBuf target) throws Exception {
        msg.encode(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, NailedPacket msg) {
        msg.decode(source);
    }
}
