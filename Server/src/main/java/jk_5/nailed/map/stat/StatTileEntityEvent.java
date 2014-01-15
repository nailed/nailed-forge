package jk_5.nailed.map.stat;

import cpw.mods.fml.common.eventhandler.Event;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class StatTileEntityEvent extends Event {

    public final IStatTileEntity tileEntity;

    public static class Load extends StatTileEntityEvent {
        public Load(IStatTileEntity tileEntity){
            super(tileEntity);
        }
    }

    public static class Unload extends StatTileEntityEvent {
        public Unload(IStatTileEntity tileEntity){
            super(tileEntity);
        }
    }
}
