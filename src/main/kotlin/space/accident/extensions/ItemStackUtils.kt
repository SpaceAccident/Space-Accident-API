package space.accident.extensions

import ic2.api.item.IElectricItem
import net.minecraft.item.ItemStack
import space.accident.api.enums.Values.W
import space.accident.api.objects.GT_ItemStack

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
    public fun ItemStack?.isStackInList( aList: Collection<GT_ItemStack?>): Boolean {
        return if (this == null) false else GT_ItemStack(this).isStackInList(aList)
    }

    @JvmStatic
    public fun GT_ItemStack?.isStackInList(aList: Collection<GT_ItemStack?>): Boolean {
        return this != null && (aList.contains(this) || aList.contains(GT_ItemStack(this.mItem, this.mStackSize.toLong(), W.toLong())))
    }
}