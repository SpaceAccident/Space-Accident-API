package space.accident.extensions

import space.accident.api.util.LanguageManager
import java.util.*

object StringUtils {

    @JvmStatic
    fun String?.isStringValid(): Boolean {
        return this != null && this.isNotEmpty()
    }

    @JvmStatic
    fun String?.isStringInvalid(): Boolean {
        return this == null || this.isEmpty()
    }

    @JvmStatic
    fun String?.capitalizeString(): String {
        return if (this != null && this.isNotEmpty()) this.substring(0, 1).uppercase(Locale.getDefault()) + this.substring(1) else ""
    }
    @JvmStatic
    fun String.trans(english: String) : String {
        return LanguageManager.addStringLocalization(this, english)
    }

    @JvmStatic
    fun <T> array(vararg type: T): Array<out T> {
        return type
    }
}