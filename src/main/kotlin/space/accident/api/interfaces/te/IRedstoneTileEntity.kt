package space.accident.api.interfaces.te

/**
 * This File has just internal Information about the Redstone State of a TileEntity
 */
interface IRedstoneTileEntity : IRedstoneEmitter, IRedstoneReceiver {
    /**
     * enables/disables Redstone Output in general.
     */
    fun setGenericRedstoneOutput(aOnOff: Boolean)

    /**
     * Causes a general Block update.
     * Sends nothing to Client, just causes a Block Update.
     */
    fun issueBlockUpdate()
}