package jk_5.nailed.client

import net.minecraft.util.ResourceLocation

/**
 * No description given
 *
 * @author jk-5
 */
case class Notification(var text: String, var image: ResourceLocation, var expire: Long, var created: Long, var color: Int)
