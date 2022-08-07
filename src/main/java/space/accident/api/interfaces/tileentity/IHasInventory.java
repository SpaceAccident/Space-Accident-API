package space.accident.api.interfaces.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public interface IHasInventory extends ISidedInventory, IWorldInteraction {
	
	default void markInventoryBeenModified() {}
	
	/**
	 * if the Inventory of this TileEntity got modified this tick
	 */
	boolean hasInventoryBeenModified();
	
	/**
	 * if this is just a Holo slot
	 */
	boolean isValidSlot(int index);
	
	/**
	 * Tries to add a Stack to the Slot.
	 * It doesn't matter if the Slot is valid or invalid as described at the Function above.
	 *
	 * @return true if stack == null, then false if index is out of bounds, then false if stack cannot be added, and then true if stack has been added
	 */
	boolean addStackToSlot(int index, ItemStack stack);
	
	/**
	 * Tries to add X Items of a Stack to the Slot.
	 * It doesn't matter if the Slot is valid or invalid as described at the Function above.
	 *
	 * @return true if stack == null, then false if index is out of bounds, then false if stack cannot be added, and then true if stack has been added
	 */
	boolean addStackToSlot(int index, ItemStack stack, int amount);
}
