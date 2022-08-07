package space.accident.api.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class Slot_Render extends Slot_Holo {
    public Slot_Render(IInventory inventory, int slotIndex, int xPos, int yPos) {
        super(inventory, slotIndex, xPos, yPos, false, false, 0);
    }

    /**
     * NEI has a nice and "useful" Delete-All Function, which would delete the Content of this Slot. This is here to prevent that.
     */
    @Override
    public void putStack(ItemStack stack) {
        if (inventory instanceof TileEntity && ((TileEntity) inventory).getWorldObj().isRemote) {
            inventory.setInventorySlotContents(getSlotIndex(), stack);
        }
        onSlotChanged();
    }
}
