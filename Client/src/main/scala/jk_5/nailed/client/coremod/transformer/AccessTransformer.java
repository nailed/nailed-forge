package jk_5.nailed.client.coremod.transformer;

import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class AccessTransformer extends cpw.mods.fml.common.asm.transformers.AccessTransformer {

    public AccessTransformer() throws IOException{
        super("nailed_at.cfg");
    }
}
