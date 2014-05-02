package cpw.mods.fml.common;

import cpw.mods.fml.common.eventhandler.EventBus;

/**
 * No description given
 *
 * @author jk-5
 */
public class FMLCommonHandler {
    public static final FMLCommonHandler inst = new FMLCommonHandler();
    public static FMLCommonHandler instance(){return inst;}
    public EventBus bus(){return new EventBus();}
}
