package space.accident.api.metatileentity.base;


import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import space.accident.api.gui.Container_BasicTank;
import space.accident.api.gui.GUIContainer_BasicTank;
import space.accident.api.interfaces.IHasFluidDisplayItem;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.main.items.ItemList;

import static space.accident.api.util.Utility.*;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p>
 * This is the main construct for my generic Tanks. Filling and emptying behavior have to be implemented manually
 */
public abstract class BasicTank extends TieredMachineBase implements IHasFluidDisplayItem {
	
	public FluidStack mFluid;
	protected int mOpenerCount;
	
	/**
	 * @param aInvSlotCount should be 3
	 */
	public BasicTank(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public BasicTank(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String[] aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public BasicTank(String name, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public BasicTank(String name, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return index != getStackDisplaySlot();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if (mFluid != null) nbt.setTag("mFluid", mFluid.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		mFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mFluid"));
	}
	
	public abstract boolean doesFillContainers();
	
	public abstract boolean doesEmptyContainers();
	
	public abstract boolean canTankBeFilled();
	
	public abstract boolean canTankBeEmptied();
	
	public abstract boolean displaysItemStack();
	
	public abstract boolean displaysStackSize();
	
	public int getInputSlot() {
		return 0;
	}
	
	public int getOutputSlot() {
		return 1;
	}
	
	public int getStackDisplaySlot() {
		return 2;
	}
	
	public boolean isFluidInputAllowed(FluidStack aFluid) {
		return true;
	}
	
	public boolean isFluidChangingAllowed() {
		return true;
	}
	
	public FluidStack getFillableStack() {
		return mFluid;
	}
	
	public FluidStack setFillableStack(FluidStack aFluid) {
		mFluid = aFluid;
		return mFluid;
	}
	
	/**
	 * If you override this and change the field returned, be sure to override {@link #isDrainableStackSeparate()} as well!
	 */
	public FluidStack getDrainableStack() {
		return mFluid;
	}
	
	public FluidStack setDrainableStack(FluidStack aFluid) {
		mFluid = aFluid;
		return mFluid;
	}
	
	public boolean isDrainableStackSeparate() {
		return false;
	}
	
	public FluidStack getDisplayedFluid() {
		return getDrainableStack();
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new Container_BasicTank(aPlayerInventory, baseTile);
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new GUIContainer_BasicTank(aPlayerInventory, baseTile, getLocalName());
	}
	
	@Override
	public void onOpenGUI() {
		super.onOpenGUI();
		mOpenerCount++;
		if (mOpenerCount == 1) updateFluidDisplayItem();
	}
	
	@Override
	public void onCloseGUI() {
		super.onCloseGUI();
		mOpenerCount--;
	}
	
	@Override
	public void onPreTick(ITile baseTile, long aTick) {
		if (baseTile.isServerSide()) {
			if (isFluidChangingAllowed() && getFillableStack() != null && getFillableStack().amount <= 0) setFillableStack(null);
			
			if (mOpenerCount > 0) updateFluidDisplayItem();
			
			if (doesEmptyContainers()) {
				FluidStack tFluid = getFluidForFilledItem(mInventory[getInputSlot()], true);
				if (tFluid != null && isFluidInputAllowed(tFluid)) {
					if (getFillableStack() == null) {
						if (isFluidInputAllowed(tFluid) && tFluid.amount <= getCapacity()) {
							if (baseTile.addStackToSlot(getOutputSlot(), getContainerForFilledItem(mInventory[getInputSlot()], true), 1)) {
								setFillableStack(tFluid.copy());
								this.onEmptyingContainerWhenEmpty();
								baseTile.decrStackSize(getInputSlot(), 1);
							}
						}
					} else {
						if (tFluid.isFluidEqual(getFillableStack()) && ((long) tFluid.amount + getFillableStack().amount) <= (long) getCapacity()) {
							if (baseTile.addStackToSlot(getOutputSlot(), getContainerForFilledItem(mInventory[getInputSlot()], true), 1)) {
								getFillableStack().amount += tFluid.amount;
								baseTile.decrStackSize(getInputSlot(), 1);
							}
						}
					}
				}
			}
			
			if (doesFillContainers()) {
				ItemStack tOutput = fillFluidContainer(getDrainableStack(), mInventory[getInputSlot()], false, true);
				if (tOutput != null && baseTile.addStackToSlot(getOutputSlot(), tOutput, 1)) {
					FluidStack tFluid = getFluidForFilledItem(tOutput, true);
					baseTile.decrStackSize(getInputSlot(), 1);
					if (tFluid != null) getDrainableStack().amount -= tFluid.amount;
					if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) setDrainableStack(null);
				}
			}
		}
	}
	
	@Override
	public void updateFluidDisplayItem() {
		if (displaysItemStack() && getStackDisplaySlot() >= 0 && getStackDisplaySlot() < mInventory.length) {
			if (getDisplayedFluid() == null) {
				if (ItemList.Display_Fluid.isStackEqual(mInventory[getStackDisplaySlot()], true, true))
					mInventory[getStackDisplaySlot()] = null;
			} else {
				mInventory[getStackDisplaySlot()] = getFluidDisplayStack(getDisplayedFluid(), true, !displaysStackSize());
			}
		}
	}
	
	@Override
	public FluidStack getFluid() {
		return getDrainableStack();
	}
	
	@Override
	public int getFluidAmount() {
		return getDrainableStack() != null ? getDrainableStack().amount : 0;
	}
	
	@Override
	public int fill(FluidStack aFluid, boolean doFill) {
		if (aFluid == null || aFluid.getFluid().getID() <= 0 || aFluid.amount <= 0 || !canTankBeFilled() || !isFluidInputAllowed(aFluid)) return 0;
		
		if (getFillableStack() == null || getFillableStack().getFluid().getID() <= 0) {
			if (aFluid.amount <= getCapacity()) {
				if (doFill) {
					setFillableStack(aFluid.copy());
					getBaseMetaTileEntity().markDirty();
				}
				return aFluid.amount;
			}
			if (doFill) {
				setFillableStack(aFluid.copy());
				getFillableStack().amount = getCapacity();
				getBaseMetaTileEntity().markDirty();
			}
			return getCapacity();
		}
		
		if (!getFillableStack().isFluidEqual(aFluid)) return 0;
		
		int space = getCapacity() - getFillableStack().amount;
		if (aFluid.amount <= space) {
			if (doFill) {
				getFillableStack().amount += aFluid.amount;
				getBaseMetaTileEntity().markDirty();
			}
			return aFluid.amount;
		}
		if (doFill) getFillableStack().amount = getCapacity();
		return space;
	}
	
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (getDrainableStack() == null || !canTankBeEmptied()) return null;
		if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) {
			setDrainableStack(null);
			getBaseMetaTileEntity().markDirty();
			return null;
		}
		
		int used = maxDrain;
		if (getDrainableStack().amount < used) used = getDrainableStack().amount;
		
		if (doDrain) {
			getDrainableStack().amount -= used;
			getBaseMetaTileEntity().markDirty();
		}
		
		FluidStack drained = getDrainableStack().copy();
		drained.amount = used;
		
		if (getDrainableStack().amount <= 0 && isFluidChangingAllowed()) {
			setDrainableStack(null);
			getBaseMetaTileEntity().markDirty();
		}
		
		return drained;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		if (getCapacity() <= 0) return new FluidTankInfo[]{};
		if (isDrainableStackSeparate()) {
			return new FluidTankInfo[]{new FluidTankInfo(getFillableStack(), getCapacity()), new FluidTankInfo(getDrainableStack(), getCapacity())};
		} else {
			return new FluidTankInfo[]{new FluidTankInfo(this)};
		}
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return index == getOutputSlot();
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return index == getInputSlot();
	}
	
	protected void onEmptyingContainerWhenEmpty() {
		//Do nothing
	}
}
