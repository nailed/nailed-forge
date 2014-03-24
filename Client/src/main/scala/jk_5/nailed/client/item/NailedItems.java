package jk_5.nailed.client.item;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedItems {

    public static ItemPDA pda;

    public static void init(){
        pda = new ItemPDA();

        GameRegistry.registerItem(pda, "pda");
    }
}
