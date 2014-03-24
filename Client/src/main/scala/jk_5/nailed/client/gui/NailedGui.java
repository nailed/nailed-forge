package jk_5.nailed.client.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.client.blocks.tileentity.IGuiTileEntity;
import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public abstract class NailedGui extends GuiScreen {

    private final IGuiTileEntity tileEntity;

    public final void sendGuiData(){
        TileEntity tile = (TileEntity) tileEntity;
        ByteBuf data = Unpooled.buffer();
        this.writeGuiData(data);
        ClientNetworkHandler.sendPacketToServer(new NailedPacket.GuiReturnDataPacket(tile.xCoord, tile.yCoord, tile.zCoord, data));
    }

    @SuppressWarnings("unchecked")
    protected final void addButton(GuiButton button){
        this.buttonList.add(button);
    }

    @SuppressWarnings("unchecked")
    protected final void addLabel(GuiLabel label){
        this.buttonList.add(label);
    }

    public NailedGui readGuiData(ByteBuf buffer){
        return this;
    }

    protected void writeGuiData(ByteBuf buffer){

    }
}
