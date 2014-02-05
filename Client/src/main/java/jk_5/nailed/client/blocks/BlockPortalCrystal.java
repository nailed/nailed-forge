package jk_5.nailed.client.blocks;

import net.minecraft.block.material.Material;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortalCrystal extends NailedBlock {

    public BlockPortalCrystal(){
        super("portalCrystal", Material.glass);
        this.setBlockUnbreakable();
        this.setBlockTextureName("nailed:crystal");
    }
}
