package space.accident.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.implementations.hathes.Hatch_Maintenance;

public class Container_MaintenanceHatch extends ContainerMetaTile_Machine {
	
	public Container_MaintenanceHatch(InventoryPlayer aInventoryPlayer, ITile aTileEntity) {
		super(aInventoryPlayer, aTileEntity);
	}
	
	@Override
	public void addSlots(InventoryPlayer aInventoryPlayer) {
		addSlotToContainer(new Slot_Holo(mTileEntity, 0, 80, 35, false, false, 1));
	}
	
	@Override
	public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer player) {
		if (aSlotIndex != 0)
			return super.slotClick(aSlotIndex, aMouseclick, aShifthold, player);
		ItemStack tStack = player.inventory.getItemStack();
		if (tStack != null) {
			((Hatch_Maintenance) mTileEntity.getMetaTile()).onToolClick(tStack, player);
			if (tStack.stackSize <= 0)
				player.inventory.setItemStack(null);
		}
		return null;
	}
}