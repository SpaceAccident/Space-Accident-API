package space.accident.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.implementations.hathes.Hatch_Output_Fluid;

import static space.accident.api.util.Utility.getFluidForFilledItem;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;

public class Container_Hatch_Output_Fluid extends Container_BasicTank {
	
	public Container_Hatch_Output_Fluid(InventoryPlayer aInventoryPlayer, ITile aTileEntity) {
		super(aInventoryPlayer, aTileEntity);
	}
	
	@Override
	public void addSlots(InventoryPlayer aInventoryPlayer) {
		addSlotToContainer(new Slot(mTileEntity, 0, 80, 17));
		addSlotToContainer(new Slot_Output(mTileEntity, 1, 80, 53));
		addSlotToContainer(new Slot_Render(mTileEntity, 2, 59, 42));
		addSlotToContainer(new Slot_Render(mTileEntity, 3, 150, 42));
	}
	
	@Override
	public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer player) {
		if (aSlotIndex == 3 && aMouseclick < 2) {
			Hatch_Output_Fluid tHatch = (Hatch_Output_Fluid) mTileEntity.getMetaTile();
			FluidStack tReadyLockFluid = getFluidForFilledItem(player.inventory.getItemStack(), true);
			int tMode = tHatch.getMode();
			// If player click the locker slot with empty or the same fluid cell, clear the lock fluid
			if (tReadyLockFluid == null || (tMode >= 8 && tReadyLockFluid.getFluid().getName().equals(tHatch.getLockedFluidName()))) {
				tHatch.setLockedFluidName(null);
				sendChat(player, trans("300", "Fluid Lock Cleared."));
				tHatch.mMode = 0;
			}
			else {
				tHatch.setLockedFluidName(tReadyLockFluid.getFluid().getName());
				sendChat(player, String.format(trans("151.4", "Sucessfully locked Fluid to %s"), tReadyLockFluid.getLocalizedName()));
				tHatch.mMode = 9;
			}
		}
		return super.slotClick(aSlotIndex, aMouseclick, aShifthold, player);
	}
}