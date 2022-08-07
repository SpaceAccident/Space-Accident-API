package space.accident.main.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.IItemContainer;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.ModHandler;
import space.accident.api.util.OreDictUnifier;

import java.util.Locale;

import static net.minecraft.block.Block.getBlockFromItem;
import static space.accident.api.enums.Values.W;
import static space.accident.api.util.Utility.*;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;

public enum ItemList implements IItemContainer {
	
	Transformer_LV_ULV,
	Transformer_LV_LV, Display_Fluid, Hatch_Input_Bus_ULV, Hatch_Input_Bus_LV, Hatch_Input_Bus_MV, Hatch_Input_Bus_HV, Hatch_Input_Bus_EV, Hatch_Input_Bus_IV, Hatch_Input_Bus_LuV, Hatch_Input_Bus_ZPM, Hatch_Input_Bus_UV, Hatch_Input_Bus_MAX, Hatch_Output_Bus_ULV, Hatch_Output_Bus_LV, Hatch_Output_Bus_MV, Hatch_Output_Bus_HV, Hatch_Output_Bus_EV, Hatch_Output_Bus_IV, Hatch_Output_Bus_LuV, Hatch_Output_Bus_ZPM, Hatch_Output_Bus_UV, Hatch_Output_Bus_MAX, Hatch_Maintenance, Hatch_AutoMaintenance, Hatch_Input_ULV, Hatch_Input_LV, Hatch_Input_MV, Hatch_Input_HV, Hatch_Input_EV, Hatch_Input_IV, Hatch_Input_LuV, Hatch_Input_ZPM, Hatch_Input_UV, Hatch_Input_MAX, Hatch_Input_Multi_2x2, Hatch_Output_ULV, Hatch_Output_LV, Hatch_Output_MV, Hatch_Output_HV, Hatch_Output_EV, Hatch_Output_IV, Hatch_Output_LuV, Hatch_Output_ZPM, Hatch_Output_UV, Hatch_Output_MAX, DEBUG_HATCH_ENERGY_OUT, Hatch_Energy_ULV, Hatch_Energy_LV, Hatch_Energy_MV, Hatch_Energy_HV, Hatch_Energy_EV, Hatch_Energy_IV, Hatch_Energy_LuV, Hatch_Energy_ZPM, Hatch_Energy_UV, Hatch_Energy_MAX;
	
	private ItemStack mStack;
	private boolean mHasNotBeenSet = true;
	
	@Override
	public IItemContainer set(Item aItem) {
		mHasNotBeenSet = false;
		if (aItem == null)
			return this;
		ItemStack stack = new ItemStack(aItem, 1, 0);
		mStack = copyAmount(1, stack);
		return this;
	}
	
	@Override
	public IItemContainer set(ItemStack stack) {
		mHasNotBeenSet = false;
		mStack         = copyAmount(1, stack);
		return this;
	}
	
	@Override
	public Item getItem() {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return null;
		return mStack.getItem();
	}
	
	@Override
	public Block getBlock() {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		return getBlockFromItem(getItem());
	}
	
	@Override
	public final boolean hasBeenSet() {
		return !mHasNotBeenSet;
	}
	
	@Override
	public boolean isStackEqual(ItemStack stack) {
		return isStackEqual(stack, false, false);
	}
	
	@Override
	public boolean isStackEqual(ItemStack stack, boolean aWildcard, boolean aIgnoreNBT) {
		if (isStackInvalid(stack))
			return false;
		return areUnificationsEqual((ItemStack) stack, aWildcard ? getWildcard(1) : get(1), aIgnoreNBT);
	}
	
	@Override
	public ItemStack get(long amount, ItemStack... aReplacements) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return copyAmount(amount, aReplacements);
		return copyAmount(amount, OreDictUnifier.get(mStack));
	}
	
	@Override
	public ItemStack getWildcard(long amount, ItemStack... aReplacements) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return copyAmount(amount, aReplacements);
		return copyAmountAndMetaData(amount, W, OreDictUnifier.get(mStack));
	}
	
	@Override
	public ItemStack getUndamaged(long amount, ItemStack... aReplacements) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return copyAmount(amount, aReplacements);
		return copyAmountAndMetaData(amount, 0, OreDictUnifier.get(mStack));
	}
	
	@Override
	public ItemStack getAlmostBroken(long amount, ItemStack... aReplacements) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return copyAmount(amount, aReplacements);
		return copyAmountAndMetaData(amount, mStack.getMaxDamage() - 1, OreDictUnifier.get(mStack));
	}
	
	@Override
	public ItemStack getWithName(long amount, String aDisplayName, ItemStack... aReplacements) {
		ItemStack rStack = get(1, aReplacements);
		if (isStackInvalid(rStack))
			return null;
		
		// CamelCase alphanumeric words from aDisplayName
		StringBuilder tCamelCasedDisplayNameBuilder = new StringBuilder();
		final String[] tDisplayNameWords = aDisplayName.split("\\W");
		for (String tWord : tDisplayNameWords) {
			if (tWord.length() > 0) tCamelCasedDisplayNameBuilder.append(tWord.substring(0, 1).toUpperCase(Locale.US));
			if (tWord.length() > 1) tCamelCasedDisplayNameBuilder.append(tWord.substring(1).toLowerCase(Locale.US));
		}
		if (tCamelCasedDisplayNameBuilder.length() == 0) {
			// CamelCased DisplayName is empty, so use hash of aDisplayName
			tCamelCasedDisplayNameBuilder.append(((Long) (long) aDisplayName.hashCode()));
		}
		
		// Construct a translation key from UnlocalizedName and CamelCased DisplayName
		final String tKey = rStack.getUnlocalizedName() + ".with." + tCamelCasedDisplayNameBuilder + ".name";
		
		rStack.setStackDisplayName(LanguageManager.addStringLocalization(tKey, aDisplayName));
		return copyAmount(amount, rStack);
	}
	
	@Override
	public ItemStack getWithCharge(long amount, int aEnergy, ItemStack... aReplacements) {
		ItemStack rStack = get(1, aReplacements);
		if (isStackInvalid(rStack))
			return null;
		ModHandler.chargeElectricItem(rStack, aEnergy, Integer.MAX_VALUE, true, false);
		return copyAmount(amount, rStack);
	}
	
	@Override
	public ItemStack getWithDamage(long amount, long aMetaValue, ItemStack... aReplacements) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		if (isStackInvalid(mStack))
			return copyAmount(amount, aReplacements);
		return copyAmountAndMetaData(amount, aMetaValue, OreDictUnifier.get(mStack));
	}
	
	@Override
	public IItemContainer registerOre(String... aOreNames) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		for (Object tOreName : aOreNames)
			OreDictUnifier.registerOre(tOreName, get(1));
		return this;
	}
	
	@Override
	public IItemContainer registerWildcardAsOre(String... aOreNames) {
		if (mHasNotBeenSet)
			throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
		for (Object tOreName : aOreNames)
			OreDictUnifier.registerOre(tOreName, getWildcard(1));
		return this;
	}
	
	/**
	 * Returns the internal stack.
	 * This method is unsafe. It's here only for quick operations.
	 * DON'T CHANGE THE RETURNED VALUE!
	 */
	public ItemStack getInternalStack_unsafe() {
		return mStack;
	}
}