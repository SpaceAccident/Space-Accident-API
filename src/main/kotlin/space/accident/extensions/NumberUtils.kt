package space.accident.extensions

import java.text.NumberFormat

object NumberUtils {

    @JvmStatic
    public fun Number.format(): String {
        return NumberFormat.getInstance().format(this as Long)
    }
}