package jk_5.nailed.coremod.transformers;

import java.io.*;

import cpw.mods.fml.common.asm.transformers.*;

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
