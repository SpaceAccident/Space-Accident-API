package space.accident.api.ext

import net.minecraftforge.common.util.ForgeDirection

public fun Number.getOppositeSide(): Byte {
    return ForgeDirection.getOrientation(this.toInt()).opposite.ordinal.toByte()
}

public fun Number.getSide(): Byte {
    return ForgeDirection.getOrientation(this.toInt()).ordinal.toByte()
}

public fun Number.getDirectionOpposite(): ForgeDirection {
    return ForgeDirection.getOrientation(this.toInt()).opposite
}

public fun Number.getDirection(): ForgeDirection {
    return ForgeDirection.getOrientation(this.toInt())
}