package jk_5.nailed.api.scripting;

import net.minecraft.nbt.NBTTagCompound;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMachineSynchronizer {
    public void unload();
    public void destroy();
    public void update();
    public void turnOn();
    public void writeToNBT(NBTTagCompound nbttagcompound);
    public void readFromNBT(NBTTagCompound nbttagcompound);
}
