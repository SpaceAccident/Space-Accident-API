package buildcraft.api.transport;

import buildcraft.api.core.EnumColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IInjectable {
	boolean canInjectItems(ForgeDirection var1);
	int injectItem(ItemStack var1, boolean var2, ForgeDirection var3, EnumColor var4);
}