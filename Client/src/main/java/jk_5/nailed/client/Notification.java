package jk_5.nailed.client;

import lombok.AllArgsConstructor;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
@AllArgsConstructor
public class Notification {

    public String text;
    public ResourceLocation image;
    public long expire;
    public long created;
    public int color;
}
