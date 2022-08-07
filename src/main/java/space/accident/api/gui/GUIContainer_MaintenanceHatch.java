package space.accident.api.gui;

import net.minecraft.entity.player.InventoryPlayer;
import space.accident.api.interfaces.tileentity.ITile;

import static space.accident.api.enums.Values.RES_PATH_GUI;

public class GUIContainer_MaintenanceHatch extends GUIContainerMetaTile_Machine {
	
	public GUIContainer_MaintenanceHatch(InventoryPlayer aInventoryPlayer, ITile aTileEntity) {
		super(new Container_MaintenanceHatch(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "Maintenance.png");
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString("Maintenance Hatch", 8, 4, 4210752);
		fontRendererObj.drawString("Click with Tool to repair.", 8, 12, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(parTicks, mouseX, mouseY);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}