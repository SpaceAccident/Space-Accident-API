package space.accident.api.interfaces.te

import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack

interface IHasInventory : ISidedInventory, IHasWorldObjectAndCoords {

    fun markInventoryBeenModified() {}

    /**
     * if the Inventory of this TileEntity got modified this tick
     */
    fun hasInventoryBeenModified(): Boolean

    /**
     * if this is just a Holoslot
     */
    fun isValidSlot(aIndex: Int): Boolean

    /**
     * Tries to add a Stack to the Slot.
     * It doesn't matter if the Slot is valid or invalid as described at the Function above.
     *
     * @return true if aStack == null, then false if aIndex is out of bounds, then false if aStack cannot be added, and then true if aStack has been added
     */
    fun addStackToSlot(aIndex: Int, aStack: ItemStack?): Boolean

    /**
     * Tries to add X Items of a Stack to the Slot.
     * It doesn't matter if the Slot is valid or invalid as described at the Function above.
     *
     * @return true if aStack == null, then false if aIndex is out of bounds, then false if aStack cannot be added, and then true if aStack has been added
     */
    fun addStackToSlot(aIndex: Int, aStack: ItemStack?, aAmount: Int): Boolean
}
