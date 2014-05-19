package jk_5.nailed.api.effect.firework;

import com.google.common.collect.Lists;
import jk_5.nailed.api.map.Map;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class Firework {

    private List<FireworkEffect> effects = Lists.newArrayList();
    private int power = 0;

    public void addEffect(@Nonnull FireworkEffect effect){
        Validate.notNull(effect, "effect");
        this.effects.add(effect);
    }

    public void addEffects(@Nonnull FireworkEffect... effects){
        for (FireworkEffect effect : effects) {
            Validate.notNull(effect, "Color cannot be null");
            this.effects.add(effect);
        }
    }

    public void addEffects(@Nonnull Iterable<FireworkEffect> effects){
        for (FireworkEffect effect : effects) {
            Validate.notNull(effect, "Color cannot be null");
            this.effects.add(effect);
        }
    }

    public void setPower(int power){
        Validate.inclusiveBetween(0, 10, power);
        this.power = power;
    }

    public NBTTagCompound toNBT(){
        NBTTagCompound ret = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        ret.setTag("Fireworks", tag);

        NBTTagList explosions = new NBTTagList();
        for(FireworkEffect effect : this.effects){
            explosions.appendTag(effect.toNBT());
        }

        tag.setTag("Explosions", explosions);
        tag.setByte("Flight", (byte) this.power);

        return ret;
    }

    public ItemStack toItemStack(){
        ItemStack stack = new ItemStack(Items.fireworks);
        stack.setTagCompound(this.toNBT());
        return stack;
    }

    public void spawnInWorld(Map map, double x, double y, double z) {
        map.getWorld().spawnEntityInWorld(new EntityFireworkRocket(map.getWorld(), x, y, z, this.toItemStack()));
    }
}
