package space.accident.api.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import space.accident.api.enums.MaterialList;
import space.accident.api.enums.SubTag;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.IItemBehaviour;
import space.accident.api.interfaces.IItemContainer;
import space.accident.api.objects.ItemData;
import space.accident.api.util.GT_LanguageManager;
import space.accident.api.util.GT_OreDictUnificator;
import space.accident.extensions.StringUtils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static space.accident.api.enums.Values.RES_PATH_ITEM;

public abstract class MetaGeneratedItem extends MetaBaseItem {
	
	public static int MAX_COUNT_AUTOGENERATED_ITEMS = 32000;
	
	public static final ConcurrentHashMap<String, MetaGeneratedItem> sInstances = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Short, Long[]> mElectricStats = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Short, Long[]> mFluidContainerStats = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Short, Short> mBurnValues = new ConcurrentHashMap<>();
	
	public final short mOffset, mItemAmount;
	public final BitSet mEnabledItems;
	public final BitSet mVisibleItems;
	public final IIcon[][] mIconList;
	
	public MetaGeneratedItem(String unlocalName, short offset, short itemCount) {
		super(unlocalName);
		setHasSubtypes(true);
		setMaxDamage(0);
		mEnabledItems = new BitSet(itemCount);
		mVisibleItems = new BitSet(itemCount);
		mOffset       = (short) Math.min(32766, offset);
		mItemAmount   = (short) Math.min(itemCount, 32766 - mOffset);
		mIconList     = new IIcon[itemCount][1];
		sInstances.put(getUnlocalizedName(), this);
	}
	
	/**
	 * This adds a Custom Item to the ending Range.
	 *
	 * @param aID         The Id of the assigned Item [0 - mItemAmount] (The MetaData gets auto-shifted by +mOffset)
	 * @param aEnglish    The Default Localized Name of the created Item
	 * @param aToolTip    The Default ToolTip of the created Item, you can also insert null for having no ToolTip
	 * @param aRandomData The OreDict Names you want to give the Item. Also used for TC Aspects and some other things.
	 * @return An ItemStack containing the newly created Item.
	 */
	public final ItemStack addItem(int aID, String aEnglish, String aToolTip, Object... aRandomData) {
		if (aToolTip == null) aToolTip = "";
		if (aID >= 0 && aID < mItemAmount) {
			ItemStack rStack = new ItemStack(this, 1, mOffset + aID);
			mEnabledItems.set(aID);
			mVisibleItems.set(aID);
			GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".name", aEnglish);
			GT_LanguageManager.addStringLocalization(getUnlocalizedName(rStack) + ".tooltip", aToolTip);
			// Important Stuff to do first
			for (Object tRandomData : aRandomData)
				if (tRandomData instanceof SubTag) {
					if (tRandomData == SubTag.INVISIBLE) {
						mVisibleItems.set(aID, false);
						continue;
					}
					if (tRandomData == SubTag.NO_UNIFICATION) {
						GT_OreDictUnificator.addToBlacklist(rStack);
						continue;
					}
				}
			// now check for the rest
			for (Object tRandomData : aRandomData) {
				if (tRandomData != null) {
					boolean tUseOreDict = true;
					if (tRandomData instanceof IItemBehaviour) {
						addItemBehavior(mOffset + aID, (IItemBehaviour<MetaBaseItem>) tRandomData);
						tUseOreDict = false;
					}
					if (tRandomData instanceof IItemContainer) {
						((IItemContainer) tRandomData).set(rStack);
						tUseOreDict = false;
					}
					if (tRandomData instanceof SubTag) {
						continue;
					}
					if (tRandomData instanceof ItemData) {
						if (StringUtils.isStringValid(tRandomData.toString()))
							GT_OreDictUnificator.registerOre(tRandomData, rStack);
						else GT_OreDictUnificator.addItemData(rStack, (ItemData) tRandomData);
						continue;
					}
					if (tUseOreDict) {
						GT_OreDictUnificator.registerOre(tRandomData, rStack);
						continue;
					}
				}
			}
			return rStack;
		}
		return null;
	}
	
	
	/**
	 * Sets the Furnace Burn Value for the Item.
	 *
	 * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aValue     200 = 1 Burn Process = 500 EU, max = 32767 (that is 81917.5 EU)
	 * @return the Item itself for convenience in constructing.
	 */
	public final MetaGeneratedItem setBurnValue(int aMetaValue, int aValue) {
		if (aMetaValue < 0 || aMetaValue >= mOffset + mEnabledItems.length() || aValue < 0) return this;
		if (aValue == 0) mBurnValues.remove((short) aMetaValue);
		else mBurnValues.put((short) aMetaValue, aValue > Short.MAX_VALUE ? Short.MAX_VALUE : (short) aValue);
		return this;
	}
	
	/**
	 * @param aMetaValue     the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aMaxCharge     Maximum Charge. (if this is == 0 it will remove the Electric Behavior)
	 * @param aTransferLimit Transfer Limit.
	 * @param aTier          The electric Tier.
	 * @param aSpecialData   If this Item has a Fixed Charge, like a SingleUse Battery (if > 0).
	 *                       Use -1 if you want to make this Battery chargeable (the use and canUse Functions will still discharge if you just use this)
	 *                       Use -2 if you want to make this Battery dischargeable.
	 *                       Use -3 if you want to make this Battery charge/discharge-able.
	 * @return the Item itself for convenience in constructing.
	 */
	public final MetaGeneratedItem setElectricStats(int aMetaValue, long aMaxCharge, long aTransferLimit, long aTier, long aSpecialData, boolean aUseAnimations) {
		if (aMetaValue < 0 || aMetaValue >= mOffset + mEnabledItems.length()) return this;
		if (aMaxCharge == 0) mElectricStats.remove((short) aMetaValue);
		else {
			mElectricStats.put((short) aMetaValue, new Long[]{aMaxCharge, Math.max(0, aTransferLimit), Math.max(-1, aTier), aSpecialData});
			if (aMetaValue >= mOffset && aUseAnimations)
				mIconList[aMetaValue - mOffset] = Arrays.copyOf(mIconList[aMetaValue - mOffset], Math.max(9, mIconList[aMetaValue - mOffset].length));
		}
		return this;
	}
	
	/**
	 * @param aMetaValue the Meta Value of the Item you want to set it to. [0 - 32765]
	 * @param aCapacity  fluid capacity in L or mb
	 * @param aStacksize item stack size
	 * @return the Item itself for convenience in constructing.
	 */
	public final MetaGeneratedItem setFluidContainerStats(int aMetaValue, long aCapacity, long aStacksize) {
		if (aMetaValue < 0 || aMetaValue >= mOffset + mEnabledItems.length()) return this;
		if (aCapacity < 0) mElectricStats.remove((short) aMetaValue);
		else mFluidContainerStats.put((short) aMetaValue, new Long[]{aCapacity, Math.max(1, aStacksize)});
		return this;
	}
	
	/**
	 * @return the Color Modulation the Material is going to be rendered with.
	 */
	public int[] getRGBa(ItemStack aStack) {
		return MaterialList._NULL.getRGBA();
	}
	
	/**
	 * @return the Icon the Material is going to be rendered with.
	 */
	public IIconContainer getIconContainer(int aMetaData) {
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void getSubItems(Item aItem, CreativeTabs aCreativeTab, List aList) {
		int j = mEnabledItems.length();
		for (int i = 0; i < j; i++) {
			if (mVisibleItems.get(i) || (mEnabledItems.get(i))) {
				Long[] tStats = mElectricStats.get((short) (mOffset + i));
				if (tStats != null && tStats[3] < 0) {
					ItemStack tStack = new ItemStack(this, 1, mOffset + i);
					setCharge(tStack, Math.abs(tStats[0]));
					isItemStackUsable(tStack);
					aList.add(tStack);
				}
				if (tStats == null || tStats[3] != -2) {
					ItemStack tStack = new ItemStack(this, 1, mOffset + i);
					isItemStackUsable(tStack);
					aList.add(tStack);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister aIconRegister) {
		short j = (short) mEnabledItems.length();
		for (short i = 0; i < j; i++) {
			if (mEnabledItems.get(i)) {
				for (byte k = 1; k < mIconList[i].length; k++) {
					mIconList[i][k] = aIconRegister.registerIcon(RES_PATH_ITEM + (getUnlocalizedName() + "/" + i + "/" + k));
				}
				mIconList[i][0] = aIconRegister.registerIcon(RES_PATH_ITEM + (getUnlocalizedName() + "/" + i));
			}
		}
	}
	
	@Override
	public final Long[] getElectricStats(ItemStack aStack) {
		return mElectricStats.get((short) aStack.getItemDamage());
	}
	
	@Override
	public final Long[] getFluidContainerStats(ItemStack aStack) {
		return mFluidContainerStats.get((short) aStack.getItemDamage());
	}
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack aStack, ItemStack aBook) {
		return false;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack aStack, ItemStack aMaterial) {
		return false;
	}
}