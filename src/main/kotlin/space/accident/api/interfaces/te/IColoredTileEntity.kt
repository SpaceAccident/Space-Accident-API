package space.accident.api.interfaces.te

interface IColoredTileEntity {
    /**
     * @return 0 - 15 are Colors, while -1 means uncolored
     */
    fun getColorization(): Byte

    /**
     * Sets the Color Modulation of the Block
     *
     * @param aColor the Color you want to set it to. -1 for reset.
     */
    fun setColorization(aColor: Byte): Byte
}
