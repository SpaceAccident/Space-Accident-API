package space.accident.api.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.IDamagableItem;

import static space.accident.api.API.sSolderingMetalList;
import static space.accident.api.API.sSolderingToolList;
import static space.accident.api.enums.Values.B;
import static space.accident.api.enums.Values.V;
import static space.accident.extensions.ItemStackUtils.*;

/**
 * This is the Interface I use for interacting with other Mods.
 * Due to the many imports, this File can cause compile Problems if not all the APIs are installed
 */
public class ModHandler {
	
	/**
	 * Returns if that Liquid is Water or Distilled Water
	 */
	public static boolean isWater(FluidStack aFluid) {
		if (aFluid == null) return false;
		return aFluid.isFluidEqual(getWater(1)) || aFluid.isFluidEqual(getDistilledWater(1));
	}
	
	/**
	 * Returns a Liquid Stack with given amount of Water.
	 */
	public static FluidStack getWater(long amount) {
		return FluidRegistry.getFluidStack("water", (int) amount);
	}
	
	/**
	 * Returns a Liquid Stack with given amount of distilled Water.
	 */
	public static FluidStack getDistilledWater(long amount) {
		FluidStack tFluid = FluidRegistry.getFluidStack("ic2distilledwater", (int) amount);
		if (tFluid == null) tFluid = getWater(amount);
		return tFluid;
	}
	
	/**
	 * Returns if that Liquid is Lava
	 */
	public static boolean isLava(FluidStack aFluid) {
		if (aFluid == null) return false;
		return aFluid.isFluidEqual(getLava(1));
	}
	
	/**
	 * Returns a Liquid Stack with given amount of Lava.
	 */
	public static FluidStack getLava(long amount) {
		return FluidRegistry.getFluidStack("lava", (int) amount);
	}
	
	/**
	 * Returns if that Liquid is Steam
	 */
	public static boolean isSteam(FluidStack aFluid) {
		if (aFluid == null) return false;
		return aFluid.isFluidEqual(getSteam(1));
	}
	
	/**
	 * Returns a Liquid Stack with given amount of Steam.
	 */
	public static FluidStack getSteam(long amount) {
		return FluidRegistry.getFluidStack("steam", (int) amount);
	}
	
	public static boolean useSolderingIron(ItemStack stack, EntityLivingBase player) {
		return useSolderingIron(stack, player, null);
	}
	
	/**
	 * Uses a Soldering Iron from player or external inventory
	 */
	public static boolean useSolderingIron(ItemStack stack, EntityLivingBase player, IInventory aExternalInventory) {
		if (player == null || stack == null) return false;
		if (isStackInList(stack, sSolderingToolList)) {
			if (player instanceof EntityPlayer) {
				EntityPlayer tPlayer = (EntityPlayer) player;
				if (tPlayer.capabilities.isCreativeMode) return true;
				if (isElectricItem(stack) && ic2.api.item.ElectricItem.manager.getCharge(stack) > 1000.0d) {
					if (consumeSolderingMaterial(tPlayer) || (aExternalInventory != null && consumeSolderingMaterial(aExternalInventory))) {
						if (canUseElectricItem(stack, 10000)) {
							return ModHandler.useElectricItem(stack, 10000, (EntityPlayer) player);
						}
						ModHandler.useElectricItem(stack, (int) ic2.api.item.ElectricItem.manager.getCharge(stack), (EntityPlayer) player);
						return false;
					}
				}
			} else {
				damageOrDechargeItem(stack, 1, 1000, player);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Consumes soldering material from given inventory
	 */
	public static boolean consumeSolderingMaterial(IInventory aInventory) {
		for (int i = 0; i < aInventory.getSizeInventory(); i++) {
			ItemStack tStack = aInventory.getStackInSlot(i);
			if (isStackInList(tStack, sSolderingMetalList)) {
				if (tStack.stackSize < 1) return false;
				if (tStack.stackSize == 1) {
					tStack = null;
				} else {
					tStack.stackSize--;
				}
				aInventory.setInventorySlotContents(i, tStack);
				aInventory.markDirty();
				return true;
			}
		}
		return false;
	}
	
	public static boolean consumeSolderingMaterial(EntityPlayer player) {
		if (player.capabilities.isCreativeMode) return true;
		if (consumeSolderingMaterial(player.inventory)) {
			if (player.inventoryContainer != null) {
				player.inventoryContainer.detectAndSendChanges();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Charges an Electric Item. Only if it's a valid Electric Item of course.
	 * This forces the Usage of proper Voltages (so not the transfer limits defined by the Items) unless you ignore the Transfer Limit.
	 * If aTier is Integer.MAX_VALUE it will ignore Tier based Limitations.
	 *
	 * @return the actually used Energy.
	 */
	public static int chargeElectricItem(ItemStack stack, int aCharge, int aTier, boolean aIgnoreLimit, boolean aSimulate) {
		try {
			if (isElectricItem(stack)) {
				int tTier = ((ic2.api.item.IElectricItem) stack.getItem()).getTier(stack);
				if (tTier < 0 || tTier == aTier || aTier == Integer.MAX_VALUE) {
					if (!aIgnoreLimit && tTier >= 0) aCharge = (int) Math.min(aCharge, V[Math.max(0, Math.min(V.length - 1, tTier))]);
					if (aCharge > 0) {
						int rCharge = (int) Math.max(0.0, ic2.api.item.ElectricItem.manager.charge(stack, aCharge, tTier, true, aSimulate));
						return rCharge + (rCharge * 4 > aTier ? aTier : 0);
					}
				}
			}
		} catch (Throwable e) {/*Do nothing*/}
		return 0;
	}
	
	/**
	 * Discharges an Electric Item. Only if it's a valid Electric Item for that of course.
	 * This forces the Usage of proper Voltages (so not the transfer limits defined by the Items) unless you ignore the Transfer Limit.
	 * If aTier is Integer.MAX_VALUE it will ignore Tier based Limitations.
	 *
	 * @return the Energy got from the Item.
	 */
	public static int dischargeElectricItem(ItemStack stack, int aCharge, int aTier, boolean aIgnoreLimit, boolean aSimulate, boolean aIgnoreDischargability) {
		try {
//			if (isElectricItem(stack) &&  (aIgnoreDischargability || ((ic2.api.item.IElectricItem)stack.getItem()).canProvideEnergy(stack))) {
			if (isElectricItem(stack)) {
				int tTier = ((ic2.api.item.IElectricItem) stack.getItem()).getTier(stack);
				if (tTier < 0 || tTier == aTier || aTier == Integer.MAX_VALUE) {
					if (!aIgnoreLimit && tTier >= 0) aCharge = (int) Math.min(aCharge, V[Math.max(0, Math.min(V.length - 1, tTier))] + B[Math.max(0, Math.min(V.length - 1, tTier))]);
					if (aCharge > 0) {
//						int rCharge = Math.max(0, ic2.api.item.ElectricItem.manager.discharge(stack, aCharge + (aCharge * 4 > aTier ? aTier : 0), tTier, T, aSimulate));
						int rCharge = (int) Math.max(0, ic2.api.item.ElectricItem.manager.discharge(stack, aCharge + (aCharge * 4 > aTier ? aTier : 0), tTier, true, !aIgnoreDischargability, aSimulate));
						return rCharge - (rCharge * 4 > aTier ? aTier : 0);
					}
				}
			}
		} catch (Throwable e) {/*Do nothing*/}
		return 0;
	}
	
	/**
	 * Uses an Electric Item. Only if it's a valid Electric Item for that of course.
	 *
	 * @return if the action was successful
	 */
	public static boolean canUseElectricItem(ItemStack stack, int aCharge) {
		try {
			if (isElectricItem(stack)) {
				return ic2.api.item.ElectricItem.manager.canUse(stack, aCharge);
			}
		} catch (Throwable e) {/*Do nothing*/}
		return false;
	}
	
	/**
	 * Uses an Electric Item. Only if it's a valid Electric Item for that of course.
	 *
	 * @return if the action was successful
	 */
	public static boolean useElectricItem(ItemStack stack, int aCharge, EntityPlayer player) {
		try {
			if (isElectricItem(stack)) {
				ic2.api.item.ElectricItem.manager.use(stack, 0, player);
				if (ic2.api.item.ElectricItem.manager.canUse(stack, aCharge)) {
					return ic2.api.item.ElectricItem.manager.use(stack, aCharge, player);
				}
			}
		} catch (Throwable e) {/*Do nothing*/}
		return false;
	}
	
	public static boolean damageOrDechargeItem(ItemStack stack, int aDamage, int aDecharge, EntityLivingBase player) {
		if (isStackInvalid(stack) || (stack.getMaxStackSize() <= 1 && stack.stackSize > 1)) return false;
		if (player != null && player instanceof EntityPlayer && ((EntityPlayer) player).capabilities.isCreativeMode) return true;
		if (stack.getItem() instanceof IDamagableItem) {
			return ((IDamagableItem) stack.getItem()).doDamageToItem(stack, aDamage);
		} else if (isElectricItem(stack)) {
			if (canUseElectricItem(stack, aDecharge)) {
				if (player != null && player instanceof EntityPlayer) {
					return ModHandler.useElectricItem(stack, aDecharge, (EntityPlayer) player);
				}
				return ModHandler.dischargeElectricItem(stack, aDecharge, Integer.MAX_VALUE, true, false, true) >= aDecharge;
			}
		} else if (stack.getItem().isDamageable()) {
			if (player == null) {
				stack.setItemDamage(stack.getItemDamage() + aDamage);
			} else {
				stack.damageItem(aDamage, player);
			}
			if (stack.getItemDamage() >= stack.getMaxDamage()) {
				stack.setItemDamage(stack.getMaxDamage() + 1);
				ItemStack tStack = Utility.getContainerItem(stack, true);
				if (tStack != null) {
					stack.func_150996_a(tStack.getItem());
					stack.setItemDamage(tStack.getItemDamage());
					stack.stackSize = tStack.stackSize;
					stack.setTagCompound(tStack.getTagCompound());
				}
			}
			return true;
		}
		return false;
	}
	
}
