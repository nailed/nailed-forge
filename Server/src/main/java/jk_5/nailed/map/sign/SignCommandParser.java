package jk_5.nailed.map.sign;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.Collection;

/**
 * No description given
 *
 * @author jk-5
 */
public class SignCommandParser {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event){
        for(TileEntity tile : (Collection<TileEntity>) event.getChunk().field_150816_i.values()){
            if(tile instanceof TileEntitySign){
                TileEntitySign sign = (TileEntitySign) tile;
                System.out.println(sign.field_145915_a[0] + " - " + sign.field_145915_a[1] + " - " + sign.field_145915_a[2] + " - " + sign.field_145915_a[3]);
            }
        }
    }
}
