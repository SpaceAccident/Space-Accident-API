package space.accident.api.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.API;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.objects.ItemStackData;
import space.accident.api.objects.ItemData;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

import static space.accident.api.enums.Values.W;

public class OreDictUnifier {
	private static final Map<String, ItemStack> sName2StackMap = new HashMap<>();
	private static final Map<ItemStackData, ItemData> sItemStack2DataMap = new HashMap<>();
	private static final Map<ItemStackData, List<ItemStack>> sUnificationTable = new HashMap<>();
	private static final HashSet<ItemStackData> sNoUnificationList = new HashSet<>();
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
	public static void addToBlacklist(ItemStack stack) {
		if (ItemStackUtils.isStackValid(stack) && !ItemStackUtils.isStackInList(stack, sNoUnificationList)) sNoUnificationList.add(new ItemStackData(stack));
	}
	
	public static boolean isBlacklisted(ItemStack stack) {
		return ItemStackUtils.isStackInList(stack, sNoUnificationList);
	}
	
	public static void add(OrePrefixes aPrefix, Materials aMaterial, ItemStack stack) {
		set(aPrefix, aMaterial, stack, false, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack stack) {
		set(aPrefix, aMaterial, stack, true, false);
	}
	
	public static void set(OrePrefixes aPrefix, Materials aMaterial, ItemStack stack, boolean aOverwrite, boolean aAlreadyRegistered) {
		if (aMaterial == null || aPrefix == null || ItemStackUtils.isStackValid(stack) || Items.feather.getDamage(stack) == W) return;
		isAddingOre++;
		stack = Utility.copyAmount(1, stack);
		if (!aAlreadyRegistered) registerOre(aPrefix.get(aMaterial), stack);
		addAssociation(aPrefix, aMaterial, stack, isBlacklisted(stack));
		if (aOverwrite || ItemStackUtils.isStackValid(sName2StackMap.get(aPrefix.get(aMaterial)))) sName2StackMap.put(aPrefix.get(aMaterial), stack);
		isAddingOre--;
	}
	
	public static ItemStack getFirstOre(String name, long amount) {
		if (StringUtils.isStringInvalid(name)) return null;
		ItemStack tStack = sName2StackMap.get(name);
		if (ItemStackUtils.isStackValid(tStack)) return Utility.copyAmount(amount, tStack);
		return Utility.copyAmount(amount, getOresImmutable(name).toArray(new ItemStack[0]));
	}
	
	public static ItemStack get(String name, long amount) {
		return get(name, null, amount, true, true);
	}
	
	public static ItemStack get(String name, ItemStack aReplacement, long amount) {
		return get(name, aReplacement, amount, true, true);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Materials aMaterial, long amount) {
		return get(aPrefix, aMaterial, null, amount);
	}
	
	public static ItemStack get(OrePrefixes aPrefix, Materials aMaterial, ItemStack aReplacement, long amount) {
		if (OrePrefixes.mPreventableComponents.contains(aPrefix) && aPrefix.mDisabledItems.contains(aMaterial)) {
			return aReplacement;
		}
		return get(aPrefix.get(aMaterial), aReplacement, amount, false, true);
	}
	
	public static ItemStack get(String name, ItemStack aReplacement, long amount, boolean aMentionPossibleTypos, boolean aNoInvalidAmounts) {
		if (aNoInvalidAmounts && amount < 1) return null;
		final ItemStack stackFromName = sName2StackMap.get(name);
		if (stackFromName != null) return Utility.copyAmount(amount, stackFromName);
		if (aMentionPossibleTypos) {
			SpaceLog.err.println("Unknown Key for Unification, Typo? " + name);
		}
		final ItemStack stackFirstOre = getFirstOre(name, amount);
		if (stackFirstOre != null) return Utility.copyAmount(amount, stackFirstOre);
		return Utility.copyAmount(amount, aReplacement);
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
	
	public static ItemStack setStack(ItemStack stack) {
		return setStack(true, stack);
	}
	
	public static ItemStack setStack(boolean aUseBlackList, ItemStack stack) {
		if (ItemStackUtils.isStackValid(stack)) return stack;
		ItemStack tStack = get(aUseBlackList, stack);
		if (Utility.areStacksEqual(stack, tStack)) return stack;
		if (tStack != null) {
			stack.func_150996_a(tStack.getItem());
			Items.feather.setDamage(stack, Items.feather.getDamage(tStack));
		}
		return stack;
	}
	
	public static ItemStack get(ItemStack stack) {
		return get(true, stack);
	}
	
	public static ItemStack get(boolean aUseBlackList, ItemStack stack) {
		if (ItemStackUtils.isStackInvalid(stack)) return null;
		ItemData tPrefixMaterial = getAssociation(stack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData() || (aUseBlackList && tPrefixMaterial.mBlackListed)) return Utility.copyOrNull(stack);
		if (aUseBlackList && !API.sUnificationEntriesRegistered && isBlacklisted(stack)) {
			tPrefixMaterial.mBlackListed = true;
			return Utility.copyOrNull(stack);
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return Utility.copyOrNull(stack);
		assert rStack != null;
		rStack.setTagCompound(stack.getTagCompound());
		return Utility.copyAmount(stack.stackSize, rStack);
	}
	
	/**
	 * Doesn't copy the returned stack or set quantity. Be careful and do not mutate it;
	 * intended only to optimize comparisons
	 */
	public static ItemStack get_nocopy(ItemStack stack) {
		return get_nocopy(true, stack);
	}
	
	/**
	 * Doesn't copy the returned stack or set quantity. Be careful and do not mutate it;
	 * intended only to optimize comparisons
	 */
	static ItemStack get_nocopy(boolean aUseBlackList, ItemStack stack) {
		if (ItemStackUtils.isStackValid(stack)) return null;
		ItemData tPrefixMaterial = getAssociation(stack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData() || (aUseBlackList && tPrefixMaterial.mBlackListed)) return stack;
		if (aUseBlackList && !API.sUnificationEntriesRegistered && isBlacklisted(stack)) {
			tPrefixMaterial.mBlackListed = true;
			return stack;
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return stack;
		assert rStack != null;
		rStack.setTagCompound(stack.getTagCompound());
		return rStack;
	}
	
	/**
	 * Compares the first argument against an already-unificated second argument as if
	 * aUseBlackList was both true and false.
	 */
	public static boolean isInputStackEqual(ItemStack stack, ItemStack unified_tStack) {
		boolean alreadyCompared = false;
		if (ItemStackUtils.isStackValid(stack)) return false;
		ItemData tPrefixMaterial = getAssociation(stack);
		ItemStack rStack = null;
		if (tPrefixMaterial == null || !tPrefixMaterial.hasValidPrefixMaterialData()) return Utility.areStacksEqual(stack, unified_tStack, true);
		else if (tPrefixMaterial.mBlackListed) {
			if (Utility.areStacksEqual(stack, unified_tStack, true)) return true;
			else alreadyCompared = true;
		}
		if (!alreadyCompared && !API.sUnificationEntriesRegistered && isBlacklisted(stack)) {
			tPrefixMaterial.mBlackListed = true;
			if (Utility.areStacksEqual(stack, unified_tStack, true)) return true;
			else alreadyCompared = true;
		}
		if (tPrefixMaterial.mUnificationTarget == null) tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		rStack = tPrefixMaterial.mUnificationTarget;
		if (ItemStackUtils.isStackValid(rStack)) return !alreadyCompared && Utility.areStacksEqual(stack, unified_tStack, true);
		rStack.setTagCompound(stack.getTagCompound());
		return Utility.areStacksEqual(rStack, unified_tStack, true);
	}
	
	public static List<ItemStack> getNonUnifiedStacks(Object obj) {
		if (sUnificationTable.isEmpty() && !sItemStack2DataMap.isEmpty()) {
			// use something akin to double check lock. this synchronization overhead is causing lag whenever my
			// 5900x tries to do NEI lookup
			synchronized (sUnificationTable) {
				if (sUnificationTable.isEmpty() && !sItemStack2DataMap.isEmpty()) {
					for (ItemStackData tGTStack0 : sItemStack2DataMap.keySet()) {
						ItemStack tStack0 = tGTStack0.toStack();
						ItemStack tStack1 = get_nocopy(false, tStack0);
						if (!Utility.areStacksEqual(tStack0, tStack1)) {
							ItemStackData tGTStack1 = new ItemStackData(tStack1);
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
		for (ItemStack stack : aStacks) {
			rList.add(stack);
			List<ItemStack> tList = sUnificationTable.get(new ItemStackData(stack));
			if (tList != null) {
				for (ItemStack tStack : tList) {
					ItemStack tStack1 = Utility.copyAmount(stack.stackSize, tStack);
					rList.add(tStack1);
				}
			}
		}
		return rList;
	}
	
	public static void addItemData(ItemStack stack, ItemData aData) {
		if (ItemStackUtils.isStackValid(stack) && getItemData(stack) == null && aData != null) setItemData(stack, aData);
	}
	
	public static void setItemData(ItemStack stack, ItemData aData) {
		if (ItemStackUtils.isStackValid(stack) || aData == null) return;
		ItemData tData = getItemData(stack);
		if (tData == null || !tData.hasValidPrefixMaterialData()) {
			if (tData != null) {
				for (Object tObject : tData.mExtraData) {
					if (!aData.mExtraData.contains(tObject)) {
						aData.mExtraData.add(tObject);
					}
				}
			}
			if (stack.stackSize > 1) {
				if (aData.mMaterial != null) {
					aData.mMaterial.mAmount /= stack.stackSize;
				}
//				for (MaterialStack tMaterial : aData.mByProducts) {
//					tMaterial.mAmount /= stack.stackSize;
//				}
				stack = Utility.copyAmount(1, stack);
			}
			sItemStack2DataMap.put(new ItemStackData(stack), aData);
			
			if (mRunThroughTheList) {
				if (API.sLoadStarted) {
					mRunThroughTheList = false;
				}
			}
		} else {
			for (Object tObject : aData.mExtraData) {
				if (!tData.mExtraData.contains(tObject)) tData.mExtraData.add(tObject);
			}
		}
	}
	
	public static void addAssociation(OrePrefixes aPrefix, Materials aMaterial, ItemStack stack, boolean aBlackListed) {
		if (aPrefix == null || aMaterial == null || ItemStackUtils.isStackValid(stack)) return;
		if (Items.feather.getDamage(stack) == W) {
			for (int i = 0; i < 16; i++) {
				setItemData(Utility.copyAmountAndMetaData(1, i, stack), new ItemData(aPrefix, aMaterial, aBlackListed));
			}
		}
		setItemData(stack, new ItemData(aPrefix, aMaterial, aBlackListed));
	}
	
	public static ItemData getItemData(ItemStack stack) {
		if (ItemStackUtils.isStackValid(stack)) return null;
		ItemData rData = sItemStack2DataMap.get(new ItemStackData(stack));
		if (rData == null) rData = sItemStack2DataMap.get(new ItemStackData(stack, true));
		return rData;
	}
	
	public static ItemData getAssociation(ItemStack stack) {
		ItemData rData = getItemData(stack);
		return rData != null && rData.hasValidPrefixMaterialData() ? rData : null;
	}
	
	public static boolean isItemStackInstanceOf(ItemStack stack, String name) {
		if (StringUtils.isStringInvalid(name) || ItemStackUtils.isStackValid(stack)) return false;
		for (ItemStack tOreStack : getOresImmutable(name))
			if (Utility.areStacksEqual(tOreStack, stack, true)) return true;
		return false;
	}
	
	public static boolean registerOre(OrePrefixes aPrefix, Materials aMaterial, ItemStack stack) {
		return registerOre(aPrefix.get(aMaterial), stack);
	}
	
	public static boolean registerOre(Object name, ItemStack stack) {
		if (name == null || ItemStackUtils.isStackInvalid(stack)) return false;
		
		String tName = name.toString();
		
		if (StringUtils.isStringInvalid(tName)) return false;
		
		for (ItemStack itemStack : getOresImmutable(tName))
			if (Utility.areStacksEqual(itemStack, stack, true)) return false;
		
		isRegisteringOre++;
		OreDictionary.registerOre(tName, Utility.copyAmount(1, stack));
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
		String name = aOreName == null ? "" : aOreName.toString();
		ArrayList<ItemStack> rList = new ArrayList<>();
		if (StringUtils.isStringValid(name)) rList.addAll(OreDictionary.getOres(name));
		return rList;
	}
	
	/**
	 * Fast version of {@link #getOres(Object)},
	 * which doesn't call {@link System#arraycopy(Object, int, Object, int, int)} in {@link ArrayList#addAll}
	 */
	public static List<ItemStack> getOresImmutable(@Nullable String aOreName) {
		String name = aOreName == null ? "" : aOreName;
		return StringUtils.isStringValid(name) ? Collections.unmodifiableList(OreDictionary.getOres(name)) : Collections.emptyList();
	}
}
