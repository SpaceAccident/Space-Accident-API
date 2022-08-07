package space.accident.api.objects;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import space.accident.api.enums.Values;
import space.accident.api.util.Utility;

public class ItemStackData {
	public final Item mItem;
	public final int mStackSize;
	public final short mMetaData;
	
	public ItemStackData(Item aItem, long aStackSize, long meta) {
		mItem      = aItem;
		mStackSize = (int) aStackSize;
		mMetaData  = (short) meta;
	}
	
	public ItemStackData(ItemStack stack) {
		this(stack, false);
	}
	
	public ItemStackData(ItemStack stack, boolean wildcard) {
		this(stack == null ? null : stack.getItem(), stack == null ? 0 : stack.stackSize, stack == null ? 0 : wildcard ? Values.W : Items.feather.getDamage(stack));
	}
	
	public ItemStackData(int aHashCode) {
		this(Utility.intToStack(aHashCode));
	}
	
	public final ItemStack toStack() {
		if (mItem == null) return null;
		return new ItemStack(mItem, 1, mMetaData);
	}
	
	public final boolean isStackEqual(ItemStack stack) {
		return Utility.areStacksEqual(toStack(), stack);
	}
	
	public final boolean isStackEqual(ItemStackData stack) {
		return Utility.areStacksEqual(toStack(), stack.toStack());
	}
	
	@Override
	public boolean equals(Object stack) {
		if (stack == this) return true;
		if (stack instanceof ItemStackData) {
			return ((ItemStackData) stack).mItem == mItem && ((ItemStackData) stack).mMetaData == mMetaData;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Utility.stackToInt(toStack());
	}
	
	
}