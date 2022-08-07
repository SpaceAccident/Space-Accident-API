package space.accident.api.interfaces.tileentity.redstone;

import space.accident.api.interfaces.tileentity.IWorldInteraction;

/**
 * This File has just internal Information about the RedStone State of a TileEntity
 */
public interface IRedStoneReceiver extends IWorldInteraction {
	/**
	 * gets the RedStone Level of the TileEntity to the given Input Side
	 * <p/>
	 * Do not use this if ICoverable is implemented. ICoverable has @getInternalInputRedstoneSignal for Machine internal Input RedStone
	 * This returns the true incoming RedStone Signal. Only Cover Behaviors should check it, not MetaTileEntities.
	 */
	int getInputRedStoneSignal(int side);
	
	/**
	 * gets the strongest RedStone Level the TileEntity receives
	 */
	int getStrongestRedStone();
	
	/**
	 * gets if the TileEntity receives RedStone
	 */
	boolean getRedStone();
	
	/**
	 * gets if the TileEntity receives RedStone at this Side
	 */
	boolean getRedStone(int side);
}
