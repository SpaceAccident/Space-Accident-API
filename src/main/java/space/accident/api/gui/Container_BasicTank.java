package space.accident.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.IFluidAccess;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.BasicTank;

import static space.accident.api.util.Utility.getFluidFromDisplayStack;

public class Container_BasicTank extends ContainerMetaTile_Machine {
	
	public int mContent = 0;
	private int oContent = 0;
	
	public Container_BasicTank(InventoryPlayer aInventoryPlayer, ITile aTileEntity) {
		super(aInventoryPlayer, aTileEntity);
	}
	
	/**
	 * Subclasses must ensure third slot (aSlotIndex==2) is drainable fluid display item slot.
	 * Otherwise, subclasses must intercept the appropriate the slotClick event and call super.slotClick(2, xxx) if necessary
	 */
	@Override
	public void addSlots(InventoryPlayer aInventoryPlayer) {
		addSlotToContainer(new Slot(mTileEntity, 0, 80, 17));
		addSlotToContainer(new Slot_Output(mTileEntity, 1, 80, 53));
		addSlotToContainer(new Slot_Render(mTileEntity, 2, 59, 42));
	}
	
	@Override
	public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer player) {
		if (aSlotIndex == 2 && aMouseclick < 2) {
			if (mTileEntity.isClientSide()) {

				BasicTank tTank = (BasicTank) mTileEntity.getMetaTile();
				tTank.setDrainableStack(getFluidFromDisplayStack(tTank.getStackInSlot(2)));
			}
			BasicTank tTank = (BasicTank) mTileEntity.getMetaTile();
			BasicTankFluidAccess tDrainableAccess = BasicTankFluidAccess.from(tTank, false);
			return handleFluidSlotClick(tDrainableAccess, player, aMouseclick == 0, true, !tTank.isDrainableStackSeparate());
		}
		return super.slotClick(aSlotIndex, aMouseclick, aShifthold, player);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (mTileEntity.isClientSide() || mTileEntity.getMetaTile() == null) return;
		if (((BasicTank) mTileEntity.getMetaTile()).mFluid != null)
			mContent = ((BasicTank) mTileEntity.getMetaTile()).mFluid.amount;
		else
			mContent = 0;
		for (Object crafter : this.crafters) {
			ICrafting player = (ICrafting) crafter;
			if (mTimer % 500 == 0 || oContent != mContent) {
				player.sendProgressBarUpdate(this, 100, mContent & 65535);
				player.sendProgressBarUpdate(this, 101, mContent >>> 16);
			}
		}
		
		oContent = mContent;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		switch (id) {
			case 100:
				mContent = mContent & 0xffff0000 | value & 0x0000ffff;
				break;
			case 101:
				mContent = mContent & 0xffff | value << 16;
				break;
		}
	}
	
	@Override
	public int getSlotCount() {
		return 2;
	}
	
	@Override
	public int getShiftClickSlotCount() {
		return 1;
	}
	
	static class BasicTankFluidAccess implements IFluidAccess {
		private final BasicTank mTank;
		private final boolean mIsFillableStack;
		
		public BasicTankFluidAccess(BasicTank aTank, boolean aIsFillableStack) {
			this.mTank = aTank;
			this.mIsFillableStack = aIsFillableStack;
		}
		
		@Override
		public void set(FluidStack stack) {
			if (mIsFillableStack)
				mTank.setFillableStack(stack);
			else
				mTank.setDrainableStack(stack);
		}
		
		@Override
		public FluidStack get() {
			return mIsFillableStack ? mTank.getFillableStack() : mTank.getDrainableStack();
		}
		
		@Override
		public int getCapacity() {
			return mTank.getCapacity();
		}
		
		static BasicTankFluidAccess from(BasicTank aTank, boolean aIsFillableStack) {
			return new BasicTankFluidAccess(aTank, aIsFillableStack);
		}
	}
}
