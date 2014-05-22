package jk_5.nailed.item;

import cpw.mods.fml.common.registry.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class NailedItems {

    public static ItemPDA pda;

    private NailedItems(){

    }

    public static void init() {
        pda = new ItemPDA();

        GameRegistry.registerItem(pda, "pda");
    }
}
