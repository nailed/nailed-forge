package jk_5.nailed.client.blocks;

import net.minecraft.block.material.Material;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortalCrystal extends NailedBlock {

    public BlockPortalCrystal(){
        super("portalCrystal", Material.field_151592_s); //Material.glass
        this.func_149722_s();
        this.func_149658_d("nailed:crystal");
    }
}
