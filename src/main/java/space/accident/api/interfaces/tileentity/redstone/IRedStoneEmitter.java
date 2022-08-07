package space.accident.api.interfaces.tileentity.redstone;

import space.accident.api.interfaces.tileentity.IWorldInteraction;

/**
 * This File has just internal Information about the RedStone State of a TileEntity
 */
public interface IRedStoneEmitter extends IWorldInteraction {
	/**
	 * gets the RedStone Level the TileEntity should emit to the given Output Side
	 */
	int getOutputRedStoneSignal(int side);
	
	/**
	 * sets the RedStone Level the TileEntity should emit to the given Output Side
	 * <p/>
	 * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedstoneSignal for Machine internal Output RedStone, so that it doesnt conflict with Cover RedStone.
	 * This sets the true RedStone Output Signal. Only Cover Behaviors should use it, not MetaTileEntities.
	 */
	void setOutputRedStoneSignal(int side, int aStrength);
	
	/**
	 * gets the RedStone Level the TileEntity should emit to the given Output Side
	 */
	int getStrongOutputRedStoneSignal(int side);
	
	/**
	 * sets the RedStone Level the TileEntity should emit to the given Output Side
	 * <p/>
	 * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedStoneSignal for Machine internal Output RedStone, so that it doesnt conflict with Cover RedStone.
	 * This sets the true RedStone Output Signal. Only Cover Behaviors should use it, not MetaTileEntities.
	 */
	void setStrongOutputRedStoneSignal(int side, int aStrength);
	
	/**
	 * Gets the Output for the comparator on the given Side
	 */
	int getComparatorValue(int side);
	
	/**
	 * Get the RedStone output signal strength for a given side
	 */
	default int getSideRedStone(int side) {
		return 0;
	}
}
