package space.accident.api.interfaces.te

/**
 * This File has just internal Information about the Redstone State of a TileEntity
 */
interface IRedstoneReceiver : IHasWorldObjectAndCoords {
    /**
     * gets the Redstone Level of the TileEntity to the given Input Side
     *
     *
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalInputRedstoneSignal for Machine internal Input Redstone
     * This returns the true incoming Redstone Signal. Only Cover Behaviors should check it, not MetaTileEntities.
     */
    fun getInputRedstoneSignal(aSide: Byte): Byte

    /**
     * gets the strongest Redstone Level the TileEntity receives
     */
    fun getStrongestRedstone(): Byte

    /**
     * gets if the TileEntity receives Redstone
     */
    fun getRedstone(): Boolean

    /**
     * gets if the TileEntity receives Redstone at this Side
     */
    fun getRedstone(aSide: Byte): Boolean
}