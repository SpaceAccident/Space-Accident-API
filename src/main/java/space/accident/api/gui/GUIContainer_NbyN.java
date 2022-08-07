package space.accident.api.gui;

import net.minecraft.entity.player.InventoryPlayer;
import space.accident.api.interfaces.tileentity.ITile;

import static space.accident.api.enums.Values.RES_PATH_GUI;

/**
 * Max n = 8, min n = 1
 */
public class GUIContainer_NbyN extends GUIContainerMetaTile_Machine {
	
	public GUIContainer_NbyN(InventoryPlayer aInventoryPlayer, ITile aTileEntity, int n) {
		super(new Container_NbyN(aInventoryPlayer, aTileEntity, n), RES_PATH_GUI + "slotsX" + n + ".png");
		ySize = n == 64 ? 238 : n == 49 ? 220 : n == 36 ? 202 : n == 25 ? 184 : 166;
		xSize = 194;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.drawGuiContainerBackgroundLayer(par1, par2, par3);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}