package jk_5.nailed.coremod.transformers;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedAccessTransformer extends AccessTransformer {

    public NailedAccessTransformer() throws IOException {
        super("nailed_at.cfg");
    }
}
