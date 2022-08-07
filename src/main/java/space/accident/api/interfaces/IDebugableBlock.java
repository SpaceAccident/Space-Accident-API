package space.accident.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

/**
 * You are allowed to include this File in your Download, as i will not change it.
 */
public interface IDebugableBlock {
	/**
	 * Returns a Debug Message, for a generic DebugItem
	 * Blocks have to implement this interface NOT TileEntities!
	 *
	 * @param player   the Player, who rightclicked with his Debug Item
	 * @param x        Block-Coordinate
	 * @param y        Block-Coordinate
	 * @param z        Block-Coordinate
	 * @param logLevel the Log Level of the Debug Item.
	 *                  0 = Obvious
	 *                  1 = Visible for the regular Scanner
	 *                  2 = Only visible to more advanced Scanners
	 *                  3 = Debug ONLY
	 * @return a String-Array containing the DebugInfo, every Index is a separate line (0 = first Line)
	 */
	ArrayList<String> getDebugInfo(EntityPlayer player, int x, int y, int z, int logLevel);
}
