package jk_5.nailed.coremod.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * No description given
 *
 * @author jk-5
 */
public class EntityPlayerMPTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        return bytes;
    }
}
