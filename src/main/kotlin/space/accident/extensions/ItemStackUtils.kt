package space.accident.extensions

import ic2.api.item.IElectricItem
import net.minecraft.item.ItemStack
import space.accident.api.enums.Values.W
import space.accident.api.objects.ItemStackData

object ItemStackUtils {

    @JvmStatic
    public fun ItemStack?.isStackValid(): Boolean {
        return this is ItemStack && this.item != null && this.stackSize >= 0
    }

    @JvmStatic
    public fun ItemStack?.isStackInvalid(): Boolean {
        return this == null || this.item == null || this.stackSize < 0
    }

    @JvmStatic
    @JvmOverloads
    public fun ItemStack?.isElectricItem(tier: Int = 1000): Boolean {
        return this != null && this.item is IElectricItem && (this.item as IElectricItem).getTier(this) <= tier
    }

    @JvmStatic
    public fun ItemStack?.isStackInList( aList: Collection<ItemStackData?>): Boolean {
        return if (this == null) false else ItemStackData(this).isStackInList(aList)
    }

    @JvmStatic
    public fun ItemStackData?.isStackInList(aList: Collection<ItemStackData?>): Boolean {
        return this != null && (aList.contains(this) || aList.contains(ItemStackData(this.mItem, this.mStackSize.toLong(), W.toLong())))
    }
}