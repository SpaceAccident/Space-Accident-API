package space.accident.api.util;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import space.accident.api.API;
import space.accident.api.enums.Materials;
import space.accident.api.objects.GT_ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static space.accident.api.API.MAX_MATERIALS;
import static space.accident.api.enums.Values.W;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.ItemStackUtils.isStackValid;

public class Utility {
	
	private static final Map<Fluid, List<ItemStack>> sFluidToContainers = new HashMap<>();
	private static final Map<GT_ItemStack, Map<Fluid, FluidContainerRegistry.FluidContainerData>> sEmptyContainerToFluidToData = new HashMap<>();
	private static final Map<GT_ItemStack, FluidContainerRegistry.FluidContainerData> sFilledContainerToData = new HashMap<>();
	
	public static ItemStack fillFluidContainer(FluidStack aFluid, ItemStack aStack, boolean aRemoveFluidDirectly, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(aStack) || aFluid == null) return null;
//		if (GT_ModHandler.isWater(aFluid) && ItemList.Bottle_Empty.isStackEqual(aStack)) {
//			if (aFluid.amount >= 250) {
//				if (aRemoveFluidDirectly) aFluid.amount -= 250;
//				return new ItemStack(Items.potionitem, 1, 0);
//			}
//			return null;
//		}
		if (aCheckIFluidContainerItems && aStack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) aStack.getItem()).getFluid(aStack) == null && ((IFluidContainerItem) aStack.getItem()).getCapacity(aStack) <= aFluid.amount) {
			if (aRemoveFluidDirectly)
				aFluid.amount -= ((IFluidContainerItem) aStack.getItem()).fill(aStack = copyAmount(1, aStack), aFluid, true);
			else
				((IFluidContainerItem) aStack.getItem()).fill(aStack = copyAmount(1, aStack), aFluid, true);
			return aStack;
		}
		Map<Fluid, FluidContainerRegistry.FluidContainerData> tFluidToContainer = sEmptyContainerToFluidToData.get(new GT_ItemStack(aStack));
		if (tFluidToContainer == null) return null;
		FluidContainerRegistry.FluidContainerData tData = tFluidToContainer.get(aFluid.getFluid());
		if (tData == null || tData.fluid.amount > aFluid.amount) return null;
		if (aRemoveFluidDirectly) aFluid.amount -= tData.fluid.amount;
		return copyAmount(1, tData.filledContainer);
	}
	
	/**
	 * Get general container item, not only fluid container but also non-consumable item.
	 * getContainerForFilledItem works better for fluid container.
	 */
	public static ItemStack getContainerItem(ItemStack aStack, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(aStack)) return null;
		if (aStack.getItem().hasContainerItem(aStack)) {
			return aStack.getItem().getContainerItem(aStack);
		}
		
		// These are all special Cases, in which it is intended to have only GT Blocks outputting those Container Items
//		if (ItemList.Cell_Empty.isStackEqual(aStack, false, true)) return null;
//		if (ItemList.IC2_Fuel_Can_Filled.isStackEqual(aStack, false, true)) return ItemList.IC2_Fuel_Can_Empty.get(1);
//		if (aStack.getItem() == Items.potionitem || aStack.getItem() == Items.experience_bottle || ItemList.TF_Vial_FieryBlood.isStackEqual(aStack) || ItemList.TF_Vial_FieryTears.isStackEqual(aStack))
//			return ItemList.Bottle_Empty.get(1);
		
		if (aCheckIFluidContainerItems && aStack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) aStack.getItem()).getCapacity(aStack) > 0) {
			ItemStack tStack = copyAmount(1, aStack);
			((IFluidContainerItem) aStack.getItem()).drain(tStack, Integer.MAX_VALUE, true);
			if (!areStacksEqual(aStack, tStack)) return tStack;
			return null;
		}
		
//		int tCapsuleCount = GT_ModHandler.getCapsuleCellContainerCount(aStack);
//		if (tCapsuleCount > 0) return ItemList.Cell_Empty.get(tCapsuleCount);
//
//		if (ItemList.IC2_ForgeHammer.isStackEqual(aStack) || ItemList.IC2_WireCutter.isStackEqual(aStack))
//			return copyMetaData(Items.feather.getDamage(aStack) + 1, aStack);
		return null;
	}
	
	public static List<ItemStack> getContainersFromFluid(FluidStack tFluidStack) {
		if (tFluidStack != null) {
			List<ItemStack> tContainers = sFluidToContainers.get(tFluidStack.getFluid());
			if (tContainers == null) return new ArrayList<>();
			return tContainers;
		}
		return new ArrayList<>();
	}
	
	public static FluidStack getFluidForFilledItem(ItemStack aStack, boolean aCheckIFluidContainerItems) {
		if (isStackInvalid(aStack)) return null;
		if (aCheckIFluidContainerItems && aStack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) aStack.getItem()).getCapacity(aStack) > 0)
			return ((IFluidContainerItem) aStack.getItem()).drain(copyAmount(1, aStack), Integer.MAX_VALUE, true);
		FluidContainerRegistry.FluidContainerData tData = sFilledContainerToData.get(new GT_ItemStack(aStack));
		return tData == null ? null : tData.fluid.copy();
	}
	
	public static ItemStack copy(ItemStack... aStacks) {
		for (ItemStack tStack : aStacks) if (isStackValid(tStack)) return tStack.copy();
		return null;
	}
	
	@Nullable
	public static ItemStack copyOrNull(@Nullable ItemStack stack) {
		if (isStackValid(stack)) return stack.copy();
		return null;
	}
	
	public static ItemStack copyAmount(long aAmount, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		if (aAmount > 64) aAmount = 64;
		else if (aAmount == -1) aAmount = 111;
		else if (aAmount < 0) aAmount = 0;
		rStack.stackSize = (byte) aAmount;
		return rStack;
	}
	
	public static ItemStack copyAmountUnsafe(long aAmount, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		if (aAmount > Integer.MAX_VALUE) aAmount = Integer.MAX_VALUE;
		else if (aAmount < 0) aAmount = 0;
		rStack.stackSize = (int) aAmount;
		return rStack;
	}
	
	public static ItemStack copyMetaData(long aMetaData, ItemStack... aStacks) {
		ItemStack rStack = copy(aStacks);
		if (isStackInvalid(rStack)) return null;
		Items.feather.setDamage(rStack, (short) aMetaData);
		return rStack;
	}
	
	public static ItemStack copyAmountAndMetaData(long aAmount, long aMetaData, ItemStack... aStacks) {
		ItemStack rStack = copyAmount(aAmount, aStacks);
		if (isStackInvalid(rStack)) return null;
		Items.feather.setDamage(rStack, (short) aMetaData);
		return rStack;
	}
	
	public static int stackToInt(ItemStack aStack) {
		if (isStackInvalid(aStack)) return 0;
		return itemToInt(aStack.getItem(), Items.feather.getDamage(aStack));
	}
	
	public static int itemToInt(Item aItem, int aMeta) {
		return Item.getIdFromItem(aItem) | (aMeta << 16);
	}
	
	public static ItemStack intToStack(int aStack) {
		int tID = aStack & (~0 >>> 16), tMeta = aStack >>> 16;
		Item tItem = Item.getItemById(tID);
		if (tItem != null) return new ItemStack(tItem, 1, tMeta);
		return null;
	}
	
	public static boolean areFluidsEqual(FluidStack aFluid1, FluidStack aFluid2) {
		return areFluidsEqual(aFluid1, aFluid2, false);
	}
	
	public static boolean areFluidsEqual(FluidStack aFluid1, FluidStack aFluid2, boolean aIgnoreNBT) {
		return aFluid1 != null && aFluid2 != null && aFluid1.getFluid() == aFluid2.getFluid() && (aIgnoreNBT || ((aFluid1.tag == null) == (aFluid2.tag == null)) && (aFluid1.tag == null || aFluid1.tag.equals(aFluid2.tag)));
	}
	
	public static boolean areStacksEqual(ItemStack aStack1, ItemStack aStack2) {
		return areStacksEqual(aStack1, aStack2, false);
	}
	
	public static boolean areStacksEqual(ItemStack aStack1, ItemStack aStack2, boolean aIgnoreNBT) {
		return aStack1 != null && aStack2 != null && aStack1.getItem() == aStack2.getItem()
				&& (aIgnoreNBT || (((aStack1.getTagCompound() == null) == (aStack2.getTagCompound() == null)) && (aStack1.getTagCompound() == null || aStack1.getTagCompound().equals(aStack2.getTagCompound()))))
				&& (Items.feather.getDamage(aStack1) == Items.feather.getDamage(aStack2) || Items.feather.getDamage(aStack1) == W || Items.feather.getDamage(aStack2) == W);
	}
	
	public static String toSubscript(long no) {
		char[] chars = Long.toString(no).toCharArray();
		for (int i = 0; i < chars.length; i++) {
			chars[i] += 8272;
		}
		return new String(chars);
	}
	
	public static String getLocalizedNameForItem(String aFormat, int aMaterialID) {
		if (aMaterialID >= 0 && aMaterialID < MAX_MATERIALS) {
			Materials aMaterial = API.sGeneratedMaterials[aMaterialID];
			if (aMaterial != null)
				return aMaterial.getLocalizedNameForItem(aFormat);
		}
		return aFormat;
	}
}
