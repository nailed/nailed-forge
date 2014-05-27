package jk_5.nailed.effect;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class FireworkColorizer {

    private FireworkColorizer(){

    }

    public static ItemStack getItemStack(int color) {
        /*ItemStack firework = new ItemStack(Item.firework, 1);
        NBTTagCompound tag = new NBTTagCompound();
        firework.setTagCompound(tag);
        NBTTagCompound fireworks = new NBTTagCompound();
        tag.setCompoundTag("Fireworks", fireworks);
        fireworks.setByte("Flight", (byte) 2);
        NBTTagList explosions = new NBTTagList();
        fireworks.setTag("Explosions", explosions);

        NBTTagCompound explosion1 = new NBTTagCompound();
        explosion1.setByte("Flicker", (byte) 1);
        explosion1.setByte("Type", (byte) 1);
        explosion1.setIntArray("Colors", new int[]{ItemDye.dyeColors[1], ItemDye.dyeColors[14]});
        explosion1.setIntArray("FadeColors", new int[]{ItemDye.dyeColors[15], ItemDye.dyeColors[11]});
        explosions.appendTag(explosion1);

        NBTTagCompound explosion2 = new NBTTagCompound();
        explosion2.setByte("Type", (byte) 2);
        explosion2.setIntArray("Colors", new int[]{ItemDye.dyeColors[1], ItemDye.dyeColors[14]});
        explosions.appendTag(explosion2);

        NBTTagCompound explosion3 = new NBTTagCompound();
        explosion3.setByte("Flicker", (byte) 1);
        explosion3.setByte("Trail", (byte) 1);
        explosion3.setByte("Type", (byte) 0);
        explosion3.setIntArray("Colors", new int[]{ItemDye.dyeColors[15], ItemDye.dyeColors[1], ItemDye.dyeColors[14]});
        explosion3.setIntArray("FadeColors", new int[]{ItemDye.dyeColors[15], ItemDye.dyeColors[11], ItemDye.dyeColors[14]});
        explosions.appendTag(explosion3);
        return firework;*/

        int color1 = color & 0xAAAA00;
        int color2 = color | 0x00AA00;
        color = Math.min(Math.max(color, 0x000000), 0xFFFFFF);
        color1 = Math.min(Math.max(color1, 0x000000), 0xFFFFFF);
        color2 = Math.min(Math.max(color2, 0x000000), 0xFFFFFF);

        ItemStack firework = new ItemStack(Items.fireworks, 1);
        NBTTagCompound tag = new NBTTagCompound();
        firework.setTagCompound(tag);
        NBTTagCompound fireworks = new NBTTagCompound();
        tag.setTag("Fireworks", fireworks);
        fireworks.setByte("Flight", (byte) 2);
        NBTTagList explosions = new NBTTagList();
        fireworks.setTag("Explosions", explosions);

        NBTTagCompound explosion1 = new NBTTagCompound();
        explosion1.setByte("Flicker", (byte) 1);
        explosion1.setByte("Type", (byte) 1);
        explosion1.setIntArray("Colors", new int[]{color, color2});
        explosion1.setIntArray("FadeColors", new int[]{0xFFFFFF, color1});
        explosions.appendTag(explosion1);

        NBTTagCompound explosion2 = new NBTTagCompound();
        explosion2.setByte("Type", (byte) 2);
        explosion2.setIntArray("Colors", new int[]{color, color2});
        explosions.appendTag(explosion2);

        NBTTagCompound explosion3 = new NBTTagCompound();
        explosion3.setByte("Flicker", (byte) 1);
        explosion3.setByte("Trail", (byte) 1);
        explosion3.setByte("Type", (byte) 0);
        explosion3.setIntArray("Colors", new int[]{0xFFFFFF, color, color2});
        explosion3.setIntArray("FadeColors", new int[]{0xFFFFFF, color1, color2});
        explosions.appendTag(explosion3);
        return firework;
    }
}
