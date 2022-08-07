package space.accident.api.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import space.accident.api.interfaces.IItemBehaviour;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.SpaceLog;
import space.accident.api.util.Utility;
import space.accident.extensions.FluidUtils;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.NumberUtils;
import space.accident.extensions.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.util.EnumChatFormatting.*;
import static space.accident.api.enums.Values.V;

public abstract class MetaBaseItem extends GenericItem implements ISpecialElectricItem, IElectricItemManager, IFluidContainerItem {
	
	private final ConcurrentHashMap<Short, ArrayList<IItemBehaviour<MetaBaseItem>>> mItemBehaviors = new ConcurrentHashMap<>();
	
	public MetaBaseItem(String unLocalName) {
		super(unLocalName, "Generated Item", null, false);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	public final MetaBaseItem addItemBehavior(int aMetaValue, IItemBehaviour<MetaBaseItem> aBehavior) {
		if (aMetaValue < 0 || aMetaValue >= 32766 || aBehavior == null) return this;
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.computeIfAbsent((short) aMetaValue, k -> new ArrayList<>(1));
		tList.add(aBehavior);
		return this;
	}
	
	public abstract Long[] getElectricStats(ItemStack stack);
	
	public abstract Long[] getFluidContainerStats(ItemStack stack);
	
	@Override
	public boolean isItemStackUsable(ItemStack stack) {
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		if (tList != null) {
			for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
				if (!tBehavior.isItemStackUsable(this, stack)) {
					return false;
				}
			}
		}
		return super.isItemStackUsable(stack);
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		use(stack, 0, player);
		isItemStackUsable(stack);
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		try {
			if (tList != null) {
				for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
					if (tBehavior.onLeftClickEntity(this, stack, player, entity)) {
						if (stack.stackSize <= 0) {
							player.destroyCurrentEquippedItem();
						}
						return true;
					}
				}
			}
			if (stack.stackSize <= 0) {
				player.destroyCurrentEquippedItem();
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
		return false;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		use(stack, 0, player);
		isItemStackUsable(stack);
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		try {
			if (tList != null) {
				for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
					if (tBehavior.onItemUse(this, stack, player, world, x, y, z, side, hitX, hitY, hitZ)) {
						if (stack.stackSize <= 0) {
							player.destroyCurrentEquippedItem();
						}
						return true;
					}
				}
			}
			if (stack.stackSize <= 0) {
				player.destroyCurrentEquippedItem();
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
		return false;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		use(stack, 0, player);
		isItemStackUsable(stack);
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		try {
			if (tList != null) {
				for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
					if (tBehavior.onItemUseFirst(this, stack, player, world, x, y, z, side, hitX, hitY, hitZ)) {
						if (stack.stackSize <= 0) player.destroyCurrentEquippedItem();
						return true;
					}
				}
			}
			if (stack.stackSize <= 0) {
				player.destroyCurrentEquippedItem();
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		use(stack, 0, player);
		isItemStackUsable(stack);
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		try {
			if (tList != null) {
				for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
					stack = tBehavior.onItemRightClick(this, stack, world, player);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
		return stack;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final void addInformation(ItemStack stack, EntityPlayer player, List aList, boolean aF3_H) {
		String tKey = getUnlocalizedName(stack) + ".tooltip";
		String[] tStrings = LanguageManager.getTranslation(tKey).split("/n ");
		for (String tString : tStrings)
			if (StringUtils.isStringValid(tString) && !tKey.equals(tString)) {
				aList.add(tString);
			}
		
		Long[] tStats = getElectricStats(stack);
		if (tStats != null) {
			if (tStats[3] > 0) {
				aList.add(AQUA + String.format(transItem("009", "Contains %s EU   Tier: %s"), NumberUtils.format(tStats[3]), "" + (tStats[2] >= 0 ? tStats[2] : 0)) + GRAY);
			} else {
				long tCharge = getRealCharge(stack);
				if (tStats[3] == -2 && tCharge <= 0) {
					aList.add(AQUA + transItem("010", "Empty. You should recycle it properly.") + GRAY);
				} else {
					aList.add(AQUA + String.format(
									transItem("011", "%s / %s EU - Voltage: %s"),
									NumberUtils.format(tCharge),
									NumberUtils.format(Math.abs(tStats[0])),
									"" + V[(int) (tStats[2] >= 0 ? tStats[2] < V.length ? tStats[2] : V.length - 1 : 1)]
							) + GRAY
					);
				}
			}
		}
		
		tStats = getFluidContainerStats(stack);
		if (tStats != null && tStats[0] > 0) {
			FluidStack tFluid = getFluidContent(stack);
			aList.add(BLUE + ((tFluid == null ? transItem("012", "No Fluids Contained") : FluidUtils.getFluidName(tFluid, true))) + GRAY);
			aList.add(BLUE + String.format(transItem("013", "%sL / %sL"), "" + (tFluid == null ? 0 : tFluid.amount), "" + tStats[0]) + GRAY);
		}
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		if (tList != null) {
			for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
				aList = tBehavior.getAdditionalToolTips(this, aList, stack);
			}
		}
		addToolTip(aList, stack, player);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int aTimer, boolean aIsInHand) {
		ArrayList<IItemBehaviour<MetaBaseItem>> tList = mItemBehaviors.get((short) getDamage(stack));
		if (tList != null) {
			for (IItemBehaviour<MetaBaseItem> tBehavior : tList) {
				tBehavior.onUpdate(this, stack, world, player, aTimer, aIsInHand);
			}
		}
	}
	
	@Override
	public final boolean canProvideEnergy(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null) return false;
		return tStats[3] > 0 || (stack.stackSize == 1 && (tStats[3] == -2 || tStats[3] == -3));
	}
	
	@Override
	public final double getMaxCharge(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null) return 0;
		return Math.abs(tStats[0]);
	}
	
	@Override
	public final double getTransferLimit(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null) return 0;
		return Math.max(tStats[1], tStats[3]);
	}
	
	@Override
	public final double charge(ItemStack stack, double aCharge, int aTier, boolean aIgnoreTransferLimit, boolean aSimulate) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null || tStats[2] > aTier || !(tStats[3] == -1 || tStats[3] == -3 || (tStats[3] < 0 && aCharge == Integer.MAX_VALUE)) || stack.stackSize != 1)
			return 0;
		long tTransfer = aIgnoreTransferLimit ? (long) aCharge : Math.min(tStats[1], (long) aCharge);
		long tChargeBefore = getRealCharge(stack);
		long tNewCharge = Math.min(Math.abs(tStats[0]), Long.MAX_VALUE - tTransfer >= tChargeBefore ? tChargeBefore + tTransfer : Long.MAX_VALUE);
		if (!aSimulate) setCharge(stack, tNewCharge);
		return tNewCharge - tChargeBefore;
	}
	
	@Override
	public final double discharge(ItemStack stack, double aCharge, int aTier, boolean aIgnoreTransferLimit, boolean aBatteryAlike, boolean aSimulate) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null || tStats[2] > aTier) return 0;
		if (aBatteryAlike && !canProvideEnergy(stack)) return 0;
		if (tStats[3] > 0) {
			if (aCharge < tStats[3] || stack.stackSize < 1) return 0;
			if (!aSimulate) stack.stackSize--;
			return tStats[3];
		}
		long tChargeBefore = getRealCharge(stack), tNewCharge = Math.max(0, tChargeBefore - (aIgnoreTransferLimit ? (long) aCharge : Math.min(tStats[1], (long) aCharge)));
		if (!aSimulate) setCharge(stack, tNewCharge);
		return tChargeBefore - tNewCharge;
	}
	
	@Override
	public final double getCharge(ItemStack stack) {
		return getRealCharge(stack);
	}
	
	@Override
	public final boolean canUse(ItemStack stack, double amount) {
		return getRealCharge(stack) >= amount;
	}
	
	@Override
	public final boolean use(ItemStack stack, double amount, EntityLivingBase player) {
		chargeFromArmor(stack, player);
		if (player instanceof EntityPlayer && ((EntityPlayer) player).capabilities.isCreativeMode) return true;
		double tTransfer = discharge(stack, amount, Integer.MAX_VALUE, true, false, true);
		if (Math.abs(tTransfer - amount) < .0000001) {
			discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
			chargeFromArmor(stack, player);
			return true;
		}
		discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
		chargeFromArmor(stack, player);
		return false;
	}
	
	@Override
	public final void chargeFromArmor(ItemStack stack, EntityLivingBase player) {
		if (player == null || player.worldObj.isRemote) return;
		for (int i = 1; i < 5; i++) {
			ItemStack tArmor = player.getEquipmentInSlot(i);
			if (ItemStackUtils.isElectricItem(tArmor)) {
				IElectricItem tArmorItem = (IElectricItem) tArmor.getItem();
				if (tArmorItem.canProvideEnergy(tArmor) && tArmorItem.getTier(tArmor) >= getTier(stack)) {
					double tCharge = ElectricItem.manager.discharge(
							tArmor,
							charge(stack, Integer.MAX_VALUE - 1, Integer.MAX_VALUE, true, true),
							Integer.MAX_VALUE, true, true, false
					);
					if (tCharge > 0) {
						charge(stack, tCharge, Integer.MAX_VALUE, true, false);
						if (player instanceof EntityPlayer) {
							Container tContainer = ((EntityPlayer) player).openContainer;
							if (tContainer != null) tContainer.detectAndSendChanges();
						}
					}
				}
			}
		}
	}
	
	public final long getRealCharge(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null) return 0;
		if (tStats[3] > 0) return (int) (long) tStats[3];
		NBTTagCompound tNBT = stack.getTagCompound();
		return tNBT == null ? 0 : tNBT.getLong("item_charge");
	}
	
	public final boolean setCharge(ItemStack stack, long aCharge) {
		Long[] tStats = getElectricStats(stack);
		if (tStats == null || tStats[3] > 0) return false;
		NBTTagCompound tNBT = stack.getTagCompound();
		if (tNBT == null) tNBT = new NBTTagCompound();
		tNBT.removeTag("item_charge");
		aCharge = Math.min(tStats[0] < 0 ? Math.abs(tStats[0] / 2) : aCharge, Math.abs(tStats[0]));
		if (aCharge > 0) {
			stack.setItemDamage(getChargedMetaData(stack));
			tNBT.setLong("item_charge", aCharge);
		} else {
			stack.setItemDamage(getEmptyMetaData(stack));
		}
		if (tNBT.hasNoTags()) stack.setTagCompound(null);
		else stack.setTagCompound(tNBT);
		isItemStackUsable(stack);
		return true;
	}
	
	public short getChargedMetaData(ItemStack stack) {
		return (short) stack.getItemDamage();
	}
	
	public short getEmptyMetaData(ItemStack stack) {
		return (short) stack.getItemDamage();
	}
	
	@Override
	public FluidStack getFluid(ItemStack stack) {
		return getFluidContent(stack);
	}
	
	@Override
	public int getCapacity(ItemStack stack) {
		Long[] tStats = getFluidContainerStats(stack);
		return tStats == null ? 0 : (int) Math.max(0, tStats[0]);
	}
	
	@Override
	public int fill(ItemStack stack, FluidStack aFluid, boolean doFill) {
		if (stack == null || stack.stackSize != 1) return 0;
		
		ItemStack tStack = Utility.fillFluidContainer(aFluid, stack, false, false);
		
		if (tStack != null) {
			stack.setItemDamage(tStack.getItemDamage());
			stack.func_150996_a(tStack.getItem());
			FluidStack fl = Utility.getFluidForFilledItem(tStack, false);
			return fl == null ? 0 : fl.amount;
		}
		
		Long[] tStats = getFluidContainerStats(stack);
		if (tStats == null || tStats[0] <= 0 || aFluid == null || aFluid.getFluid().getID() <= 0 || aFluid.amount <= 0) {
			return 0;
		}
		
		FluidStack tFluid = getFluidContent(stack);
		
		if (tFluid == null || tFluid.getFluid().getID() <= 0) {
			if (aFluid.amount <= tStats[0]) {
				if (doFill) {
					setFluidContent(stack, aFluid);
				}
				return aFluid.amount;
			}
			if (doFill) {
				tFluid = aFluid.copy();
				tFluid.amount = (int) (long) tStats[0];
				setFluidContent(stack, tFluid);
			}
			return (int) (long) tStats[0];
		}
		
		if (!tFluid.isFluidEqual(aFluid)) return 0;
		
		int space = (int) (long) tStats[0] - tFluid.amount;
		if (aFluid.amount <= space) {
			if (doFill) {
				tFluid.amount += aFluid.amount;
				setFluidContent(stack, tFluid);
			}
			return aFluid.amount;
		}
		if (doFill) {
			tFluid.amount = (int) (long) tStats[0];
			setFluidContent(stack, tFluid);
		}
		return space;
	}
	
	@Override
	public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
		if (stack == null || stack.stackSize != 1) return null;
		
		FluidStack tFluid = Utility.getFluidForFilledItem(stack, false);
		if (tFluid != null && maxDrain >= tFluid.amount) {
			ItemStack tStack = Utility.getContainerItem(stack, false);
			if (tStack == null) {
				if (doDrain) stack.stackSize = 0;
				return tFluid;
			}
			if (doDrain) {
				stack.setItemDamage(tStack.getItemDamage());
				stack.func_150996_a(tStack.getItem());
			}
			return tFluid;
		}
		
		Long[] tStats = getFluidContainerStats(stack);
		if (tStats == null || tStats[0] <= 0) return null;
		
		tFluid = getFluidContent(stack);
		if (tFluid == null) return null;
		
		int used = maxDrain;
		if (tFluid.amount < used) used = tFluid.amount;
		if (doDrain) {
			tFluid.amount -= used;
			setFluidContent(stack, tFluid);
		}
		
		FluidStack drained = tFluid.copy();
		drained.amount = used;
		return drained;
	}
	
	public FluidStack getFluidContent(ItemStack stack) {
		Long[] tStats = getFluidContainerStats(stack);
		if (tStats == null || tStats[0] <= 0) return Utility.getFluidForFilledItem(stack, false);
		NBTTagCompound tNBT = stack.getTagCompound();
		return tNBT == null ? null : FluidStack.loadFluidStackFromNBT(tNBT.getCompoundTag("FluidContent"));
	}
	
	public void setFluidContent(ItemStack stack, FluidStack aFluid) {
		NBTTagCompound tNBT = stack.getTagCompound();
		if (tNBT == null) tNBT = new NBTTagCompound();
		else tNBT.removeTag("FluidContent");
		if (aFluid != null && aFluid.amount > 0)
			tNBT.setTag("FluidContent", aFluid.writeToNBT(new NBTTagCompound()));
		if (tNBT.hasNoTags()) stack.setTagCompound(null);
		else stack.setTagCompound(tNBT);
		isItemStackUsable(stack);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		if (tStats != null && (tStats[3] == -1 || tStats[3] == -2 || tStats[3] == -3) && getRealCharge(stack) > 0) return 1;
		tStats = getFluidContainerStats(stack);
		if (tStats != null) return (int) (long) tStats[1];
		if (getDamage(stack) == 32763) return 1;
		return super.getItemStackLimit(stack);
	}
	
	@Override
	public final Item getChargedItem(ItemStack itemStack) {
		return this;
	}
	
	@Override
	public final Item getEmptyItem(ItemStack itemStack) {
		return this;
	}
	
	@Override
	public final int getTier(ItemStack stack) {
		Long[] tStats = getElectricStats(stack);
		return (int) (tStats == null ? Integer.MAX_VALUE : tStats[2]);
	}
	
	@Override
	public final String getToolTip(ItemStack stack) {
		return null;
	} // This has its own ToolTip Handler, no need to let the IC2 Handler screw us up at this Point
	
	@Override
	public final IElectricItemManager getManager(ItemStack stack) {
		return this;
	} // We are our own Manager
	
	@Override
	public final boolean getShareTag() {
		return true;
	} // just to be sure.
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack aBook) {
		return false;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack aMaterial) {
		return false;
	}
}
