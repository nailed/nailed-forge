package jk_5.quakecraft;

import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class ProxyClient extends ProxyCommon {

    @Override
    public void register(QuakecraftPlugin plugin){
        super.register(plugin);
        RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new RenderBullet());
    }
}
