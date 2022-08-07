package space.accident.api.interfaces.tileentity.redstone;

/**
 * This File has just internal Information about the RedStone State of a TileEntity
 */
public interface IRedStoneTileEntity extends IRedStoneEmitter, IRedStoneReceiver {
	/**
	 * enables/disables RedStone Output in general.
	 */
	void setGenericRedstoneOutput(boolean aOnOff);
	
	/**
	 * Causes a general Block update.
	 * Sends nothing to Client, just causes a Block Update.
	 */
	void issueBlockUpdate();
}
