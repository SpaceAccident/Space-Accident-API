package space.accident.api.interfaces.tileentity;

import javax.annotation.Nonnull;

/**
 * You are allowed to include this File in your Download, as i will not change it.
 */
public interface ITileDeviceInfo {
	/**
	 * Is this even a TileEntity which allows Sensor Kits?
	 * I need things like this Function for MetaTileEntities, you MUST check this!!!
	 * Do not assume that it's a Information returning Device, when it just implements this Interface.
	 */
	boolean isGivingInformation();
	
	/**
	 * Up to 8 Strings can be returned.
	 * Note: If you insert "\\\\" in the String it tries to translate seperate Parts of the String instead of the String as a whole.
	 *
	 * @return an Array of Information Strings. Don't return null!
	 */
	@Nonnull
	String[] getInfoData();
}