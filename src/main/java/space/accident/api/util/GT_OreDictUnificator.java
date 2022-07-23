package space.accident.api.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.API;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.objects.GT_ItemStack;
import space.accident.api.objects.ItemData;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

import static space.accident.api.enums.Values.W;

public class GT_OreDictUnificator {
	private static final Map<String, ItemStack> sName2StackMap = new HashMap<>();
	private static final Map<GT_ItemStack, ItemData> sItemStack2DataMap = new HashMap<>();
	private static final Map<GT_ItemStack, List<ItemStack>> sUnificationTable = new HashMap<>();
	private static final HashSet<GT_ItemStack> sNoUnificationList = new HashSet<>();
	public static volatile int VERSION = 509;
	private static int isRegisteringOre = 0, isAddingOre = 0;
	private static boolean mRunThroughTheList = true;
	
	static {
		API.sItemStackMappings.add(sItemStack2DataMap);
		API.sItemStackMappings.add(sUnificationTable);
	}
	
	/**
	 * The Blacklist just prevents the Item from being unificated into something else.
	 * Useful if you have things like the Industrial Diamond, which is better than regular Diamond, but also usable in absolutely all Diamond Recipes.
	 */
	public static void addToBlacklist(ItemStack aStack) {
		if (ItemStackUtils.isStackValid(aStack) && !ItemStackUtils.isStackInList(aStack, sNoUnificationList)) sNoUnificationList.add(new GT_ItemStack(aStack));
	}
	
	public static boolean isBlacklisted(ItemStack aStack) {
		return ItemStackUtils.isStackInList(aStack, sNoUnificationList);
	}
	
	public static void add(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, false, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, true, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack, boolean aOverwrite, boolean aAlreadyRegistered) {
		if (aMaterial == null || aPrefix == null || ItemStackUtils.isStackValid(aStack) || Items.feather.getDamage(aStack) == W) return;
		isAddingOre++;
		aStack = Utility.copyAmount(1, aStack);
		if (!aAlreadyRegistered) registerOre(aPrefix.get(aMaterial), aStack);
		addAssociation(aPrefix, aMaterial, aStack, isBlacklisted(aStack));
		if (aOverwrite || ItemStackUtils.isStackValid(sName2StackMap.get(aPrefix.get(aMaterial)))) sName2StackMap.put(aPrefix.get(aMaterial), aStack);
		isAddingOre--;
	}
	
	public static ItemStack getFirstOre(String aName, long aAmount) {
		if (StringUtils.isStringInvalid(aName)) return null;
		ItemStack tStack = sName2StackMap.get(aName);
		if (ItemStackUtils.isStackValid(tStack)) return Utility.copyAmount(aAmount, tStack);
		return Utility.copyAmount(aAmount, getOresImmutable(aName).toArray(new ItemStack[0]));
	}
	
	public static ItemStack get(String aName, long aAmount) {
		return get(aName, null, aAmount, true, true);
	}
	
	public static ItemStack get(String aName, ItemStack aReplacement, long aAmount) {
		return get(aName, aReplacement, aAmount, true, true);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Materials aMaterial, long aAmount) {
		return get(aPrefix, aMaterial, null, aAmount);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Materials aMaterial, ItemStack aReplacement, long aAmount) {
		if (OrePrefixes.mPreventableComponents.contains(aPrefix) && aPrefix.mDisabledItems.contains(aMaterial)) {
			return aReplacement;
		}
		return get(aPrefix.get(aMaterial), aReplacement, aAmount, false, true);
	}
	
	public static ItemStack get(String aName, ItemStack aReplacement, long aAmount, boolean aMentionPossibleTypos, boolean aNoInvalidAmounts) {
		if (aNoInvalidAmounts && aAmount < 1) return null;
		final ItemStack stackFromName = sName2StackMap.get(aName);
		if (stackFromName != null) return Utility.copyAmount(aAmount, stackFromName);
		if (aMentionPossibleTypos) {
			SpaceLog.err.println("Unknown Key for Unification, Typo? " + aName);
		}
		final ItemStack stackFirstOre = getFirstOre(aName, aAmount);
		if (stackFirstOre != null) return Utility.copyAmount(aAmount, stackFirstOre);
		return Utility.copyAmount(aAmount, aReplacement);
	}
	
	public static ItemStack[] setStackArray(boolean aUseBlackList, ItemStack... aStacks) {
		for (int i = 0; i < aStacks.length; i++) {
			aStacks[i] = get(aUseBlackList, Utility.copyOrNull(aStacks[i]));
		}
		return aStacks;
	}
	
	public static ItemStack[] getStackArray(boolean aUseBlackList, ItemStack... aStacks) {
		ItemStack[] rStacks = new ItemStack[aStacks.length];
		for (int i = 0; i < aStacks.length; i++) {
			rStacks[i] = get(aUseBlackList, Utility.copy(aStacks[i]));
		}
		return rStacks;
	}
	
	public static ItemStack setStack(ItemStack aStack) {
		return setStack(true, aStack);
	}
	
	public static ItemStack setStack(boolean aUseBlackList, ItemStack aStack) {
		if (ItemStackUtils.isStackValid(aStack)) return aStack;
		ItemStack tStack = get(aUseBlackList, aStack);
		if (Utility.areStacksEqual(aStack, tStack)) return aStack;
		aStack.func_150996_a(tStack.getItem());
		Items.feather.setDamage(aStack, Items.feather.getDamage(tStack));
		return aStack;
	}
	
	public static ItemStack get(ItemStack aStack) {
		return get(true, aStack);
	}
	
	public static ItemStack get(boolean aUseBlackList, ItemStack aStack) {
		if (ItemStackUtils.isStackInvalid(aStack)) return null;
		ItemData tPrefixMaterial = getAssociation(aStack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData() || (aUseBlackList && tPrefixMaterial.mBlackListed)) return Utility.copyOrNull(aStack);
		if (aUseBlackList && !API.sUnificationEntriesRegistered && isBlacklisted(aStack)) {
			tPrefixMaterial.mBlackListed = true;
			return Utility.copyOrNull(aStack);
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return Utility.copyOrNull(aStack);
		assert rStack != null;
		rStack.setTagCompound(aStack.getTagCompound());
		return Utility.copyAmount(aStack.stackSize, rStack);
	}
	
	/**
	 * Doesn't copy the returned stack or set quantity. Be careful and do not mutate it;
	 * intended only to optimize comparisons
	 */
	public static ItemStack get_nocopy(ItemStack aStack) {
		return get_nocopy(true, aStack);
	}
	
	/**
	 * Doesn't copy the returned stack or set quantity. Be careful and do not mutate it;
	 * intended only to optimize comparisons
	 */
	static ItemStack get_nocopy(boolean aUseBlackList, ItemStack aStack) {
		if (ItemStackUtils.isStackValid(aStack)) return null;
		ItemData tPrefixMaterial = getAssociation(aStack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData() || (aUseBlackList && tPrefixMaterial.mBlackListed)) return aStack;
		if (aUseBlackList && !API.sUnificationEntriesRegistered && isBlacklisted(aStack)) {
			tPrefixMaterial.mBlackListed = true;
			return aStack;
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return aStack;
		assert rStack != null;
		rStack.setTagCompound(aStack.getTagCompound());
		return rStack;
	}
	
	/**
	 * Compares the first argument against an already-unificated second argument as if
	 * aUseBlackList was both true and false.
	 */
	public static boolean isInputStackEqual(ItemStack aStack, ItemStack unified_tStack) {
		boolean alreadyCompared = false;
		if (ItemStackUtils.isStackValid(aStack)) return false;
		ItemData tPrefixMaterial = getAssociation(aStack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData()) return Utility.areStacksEqual(aStack, unified_tStack, true);
		else if (tPrefixMaterial.mBlackListed) {
			if (Utility.areStacksEqual(aStack, unified_tStack, true)) return true;
			else alreadyCompared = true;
		}
		if (!alreadyCompared && !API.sUnificationEntriesRegistered && isBlacklisted(aStack)) {
			tPrefixMaterial.mBlackListed = true;
			if (Utility.areStacksEqual(aStack, unified_tStack, true)) return true;
			else alreadyCompared = true;
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return !alreadyCompared && Utility.areStacksEqual(aStack, unified_tStack, true);
		rStack.setTagCompound(aStack.getTagCompound());
		return Utility.areStacksEqual(rStack, unified_tStack, true);
	}
	
	public static List<ItemStack> getNonUnifiedStacks(Object obj) {
		if (sUnificationTable.isEmpty() && !sItemStack2DataMap.isEmpty()) {
			// use something akin to double check lock. this synchronization overhead is causing lag whenever my
			// 5900x tries to do NEI lookup
			synchronized (sUnificationTable) {
				if (sUnificationTable.isEmpty() && !sItemStack2DataMap.isEmpty()) {
					for (GT_ItemStack tGTStack0 : sItemStack2DataMap.keySet()) {
						ItemStack tStack0 = tGTStack0.toStack();
						ItemStack tStack1 = get_nocopy(false, tStack0);
						if (!Utility.areStacksEqual(tStack0, tStack1)) {
							GT_ItemStack tGTStack1 = new GT_ItemStack(tStack1);
							List<ItemStack> list = sUnificationTable.computeIfAbsent(tGTStack1, k -> new ArrayList<>());
							// greg's original code tries to dedupe the list using List#contains, which won't work
							// on vanilla ItemStack. I removed it since it never worked and can be slow.
							list.add(tStack0);
						}
					}
				}
			}
		}
		ItemStack[] aStacks = {};
		if (obj instanceof ItemStack) {
			aStacks = new ItemStack[]{(ItemStack) obj};
		} else if (obj instanceof ItemStack[]) {
			aStacks = (ItemStack[]) obj;
		} else if (obj instanceof List) {
			aStacks = (ItemStack[]) ((List) obj).toArray(new ItemStack[0]);
		}
		List<ItemStack> rList = new ArrayList<>();
		for (ItemStack aStack : aStacks) {
			rList.add(aStack);
			List<ItemStack> tList = sUnificationTable.get(new GT_ItemStack(aStack));
			if (tList != null) {
				for (ItemStack tStack : tList) {
					ItemStack tStack1 = Utility.copyAmount(aStack.stackSize, tStack);
					rList.add(tStack1);
				}
			}
		}
		return rList;
	}
	
	public static void addItemData(ItemStack aStack, ItemData aData) {
		if (ItemStackUtils.isStackValid(aStack) && getItemData(aStack) == null && aData != null) setItemData(aStack, aData);
	}
	
	public static void setItemData(ItemStack aStack, ItemData aData) {
		if (ItemStackUtils.isStackValid(aStack) || aData == null) return;
		ItemData tData = getItemData(aStack);
		if (tData == null || !tData.hasValidPrefixMaterialData()) {
			if (tData != null) {
				for (Object tObject : tData.mExtraData) {
					if (!aData.mExtraData.contains(tObject)) {
						aData.mExtraData.add(tObject);
					}
				}
			}
			if (aStack.stackSize > 1) {
				if (aData.mMaterial != null) {
					aData.mMaterial.mAmount /= aStack.stackSize;
				}
//				for (MaterialStack tMaterial : aData.mByProducts) {
//					tMaterial.mAmount /= aStack.stackSize;
//				}
				aStack = Utility.copyAmount(1, aStack);
			}
			sItemStack2DataMap.put(new GT_ItemStack(aStack), aData);
			
			if (mRunThroughTheList) {
				if (API.sLoadStarted) {
					mRunThroughTheList = false;
					//TODO Maybe add recycling items. Check GregTech this for example
				}
			}
		} else {
			for (Object tObject : aData.mExtraData) {
				if (!tData.mExtraData.contains(tObject)) tData.mExtraData.add(tObject);
			}
		}
	}
	
	public static void addAssociation(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack, boolean aBlackListed) {
		if (aPrefix == null || aMaterial == null || ItemStackUtils.isStackValid(aStack)) return;
		if (Items.feather.getDamage(aStack) == W) {
			for (byte i = 0; i < 16; i++) {
				setItemData(Utility.copyAmountAndMetaData(1, i, aStack), new ItemData(aPrefix, aMaterial, aBlackListed));
			}
		}
		setItemData(aStack, new ItemData(aPrefix, aMaterial, aBlackListed));
	}
	
	public static ItemData getItemData(ItemStack aStack) {
		if (ItemStackUtils.isStackValid(aStack)) return null;
		ItemData rData = sItemStack2DataMap.get(new GT_ItemStack(aStack));
		if (rData == null) rData = sItemStack2DataMap.get(new GT_ItemStack(aStack, true));
		return rData;
	}
	
	public static ItemData getAssociation(ItemStack aStack) {
		ItemData rData = getItemData(aStack);
		return rData != null && rData.hasValidPrefixMaterialData() ? rData : null;
	}
	
	public static boolean isItemStackInstanceOf(ItemStack aStack, String aName) {
		if (StringUtils.isStringInvalid(aName) || ItemStackUtils.isStackValid(aStack)) return false;
		for (ItemStack tOreStack : getOresImmutable(aName))
			if (Utility.areStacksEqual(tOreStack, aStack, true)) return true;
		return false;
	}
	
	public static boolean registerOre(OrePrefixes aPrefix, Materials aMaterial, ItemStack aStack) {
		return registerOre(aPrefix.get(aMaterial), aStack);
	}
	
	public static boolean registerOre(Object aName, ItemStack aStack) {
		if (aName == null || ItemStackUtils.isStackValid(aStack)) return false;
		
		String tName = aName.toString();
		
		if (StringUtils.isStringInvalid(tName)) return false;
		
		for (ItemStack itemStack : getOresImmutable(tName))
			if (Utility.areStacksEqual(itemStack, aStack, true)) return false;
		
		isRegisteringOre++;
		OreDictionary.registerOre(tName, Utility.copyAmount(1, aStack));
		isRegisteringOre--;
		return true;
	}
	
	public static boolean isRegisteringOres() {
		return isRegisteringOre > 0;
	}
	
	public static boolean isAddingOres() {
		return isAddingOre > 0;
	}
	
	public static void resetUnificationEntries() {
		for (ItemData tPrefixMaterial : sItemStack2DataMap.values())
			tPrefixMaterial.mUnificationTarget = null;
	}
	
	/**
	 * @return a Copy of the OreDictionary.getOres() List
	 */
	public static ArrayList<ItemStack> getOres(OrePrefixes aPrefix, Materials aMaterial) {
		return getOres(aPrefix.get(aMaterial));
	}
	
	/**
	 * @return a Copy of the OreDictionary.getOres() List
	 */
	public static ArrayList<ItemStack> getOres(Object aOreName) {
		String aName = aOreName == null ? "" : aOreName.toString();
		ArrayList<ItemStack> rList = new ArrayList<>();
		if (StringUtils.isStringValid(aName)) rList.addAll(OreDictionary.getOres(aName));
		return rList;
	}
	
	/**
	 * Fast version of {@link #getOres(Object)},
	 * which doesn't call {@link System#arraycopy(Object, int, Object, int, int)} in {@link ArrayList#addAll}
	 */
	public static List<ItemStack> getOresImmutable(@Nullable String aOreName) {
		String aName = aOreName == null ? "" : aOreName;
		return StringUtils.isStringValid(aName) ? Collections.unmodifiableList(OreDictionary.getOres(aName)) : Collections.emptyList();
	}
}
