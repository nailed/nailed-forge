package jk_5.nailed.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.gui.container.ContainerStatEmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int i, EntityPlayer entityPlayer, World world, int i2, int i3, int i4) {
        TileEntity tile = world.getBlockTileEntity(i2, i3, i4);
        if(tile == null) return null;
        EnumGui gui = EnumGui.fromID(i);
        switch (gui){
            case STATEMITTER: return new ContainerStatEmitter((TileEntityStatEmitter) tile);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int i, EntityPlayer entityPlayer, World world, int i2, int i3, int i4) {
        TileEntity tile = world.getBlockTileEntity(i2, i3, i4);
        EnumGui gui = EnumGui.fromID(i);
        if(tile == null) return null;
        switch (gui){
            case STATEMITTER: return new GuiStatEmitter((TileEntityStatEmitter) tile);
        }
        return null;
    }
}
