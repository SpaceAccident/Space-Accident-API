package buildcraft.api.core;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTStoreable {
	void readFromNBT(NBTTagCompound var1);
	
	void writeToNBT(NBTTagCompound var1);
}
