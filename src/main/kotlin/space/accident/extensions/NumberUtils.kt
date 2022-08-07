package space.accident.extensions

import net.minecraftforge.common.util.ForgeDirection
import space.accident.api.enums.Values.V
import java.text.NumberFormat

object NumberUtils {

    @JvmStatic
    public fun Number.format(): String {
        return NumberFormat.getInstance().format(this.toLong())
    }

    @JvmStatic
    public fun Number.getOppositeSide(): Int {
        return ForgeDirection.getOrientation(this.toInt()).opposite.ordinal
    }

    @JvmStatic
    public fun Number.getTier(): Int {
        var i: Int = -1
        while (++i < V.size) if (this.toInt() <= V[i]) return i
        return i
    }
}