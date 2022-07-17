package space.accident.api.interfaces.te


/**
 * This File has just internal Information about the Redstone State of a TileEntity
 */
interface IRedstoneEmitter : IHasWorldObjectAndCoords {
    /**
     * gets the Redstone Level the TileEntity should emit to the given Output Side
     */
    fun getOutputRedstoneSignal(aSide: Byte): Byte

    /**
     * sets the Redstone Level the TileEntity should emit to the given Output Side
     *
     *
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedstoneSignal for Machine internal Output Redstone, so that it doesnt conflict with Cover Redstone.
     * This sets the true Redstone Output Signal. Only Cover Behaviors should use it, not MetaTileEntities.
     */
    fun setOutputRedstoneSignal(aSide: Byte, aStrength: Byte)

    /**
     * gets the Redstone Level the TileEntity should emit to the given Output Side
     */
    fun getStrongOutputRedstoneSignal(aSide: Byte): Byte

    /**
     * sets the Redstone Level the TileEntity should emit to the given Output Side
     *
     *
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedstoneSignal for Machine internal Output Redstone, so that it doesnt conflict with Cover Redstone.
     * This sets the true Redstone Output Signal. Only Cover Behaviors should use it, not MetaTileEntities.
     */
    fun setStrongOutputRedstoneSignal(aSide: Byte, aStrength: Byte)

    /**
     * Gets the Output for the comparator on the given Side
     */
    fun getComparatorValue(aSide: Byte): Byte

    /**
     * Get the redstone output signal strength for a given side
     */
    fun getGeneralRS(aSide: Byte): Byte {
        return 0
    }
}
