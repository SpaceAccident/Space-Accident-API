package space.accident.api.interfaces.tileentity;

public interface IColoredTileEntity {
	/**
	 * @return 0 - 15 are Colors, while -1 means uncolored
	 */
	int getColorization();
	
	/**
	 * Sets the Color Modulation of the Block
	 *
	 * @param color the Color you want to set it to. -1 for reset.
	 */
	int setColorization(int color);
}
