package space.accident.api.interfaces.tileentity;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public interface IDebugableTileEntity {
	/**
	 * Returns a Debug Message, for a generic DebugItem
	 *
	 * @param player   the Player, who right-clicked with his Debug Item
	 * @param logLevel the Log Level of the Debug Item.
	 *                  0 = Obvious
	 *                  1 = Visible for the regular Scanner
	 *                  2 = Only visible to more advanced Scanners
	 *                  3 = Debug ONLY
	 * @return a String-Array containing the DebugInfo, every Index is a separate line (0 = first Line)
	 */
	ArrayList<String> getDebugInfo(EntityPlayer player, int logLevel);
}
