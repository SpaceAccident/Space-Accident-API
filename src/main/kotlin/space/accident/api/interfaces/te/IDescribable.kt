package space.accident.api.interfaces.te

/**
 * To get simple things like a ToolTip Description
 */
interface IDescribable {
    /**
     * The Tooltip Text
     */
    fun getDescription(): Array<String>
}