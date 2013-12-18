package jk_5.nailed.client;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class SoundManager {

    @ForgeSubscribe
    public void onSoundLoad(SoundLoadEvent event){
        event.manager.addSound("nailed:teleport/link.ogg");
        event.manager.addSound("nailed:teleport/link-disarm.ogg");
        event.manager.addSound("nailed:teleport/link-fissure.ogg");
        event.manager.addSound("nailed:teleport/link-following.ogg");
        event.manager.addSound("nailed:teleport/link-intra.ogg");
        event.manager.addSound("nailed:teleport/link-portal.ogg");
        event.manager.addSound("nailed:teleport/pop.wav");
        event.manager.addSound("nailed:teleport.ogg");
    }
}
