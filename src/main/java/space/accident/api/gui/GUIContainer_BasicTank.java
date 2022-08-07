package space.accident.api.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import space.accident.api.interfaces.tileentity.ITile;

import static space.accident.api.enums.Values.RES_PATH_GUI;
import static space.accident.api.util.Utility.parseNumberToString;

public class GUIContainer_BasicTank extends GUIContainerMetaTile_Machine {
	
	private final String mName;
	
	public GUIContainer_BasicTank(InventoryPlayer aInventoryPlayer, ITile aTileEntity, String name) {
		super(new Container_BasicTank(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "BasicTank.png");
		mName = name;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(mName, 8, 6, 4210752);
		if (mContainer != null) {
			fontRendererObj.drawString("Liquid Amount", 10, 20, 16448255);
			fontRendererObj.drawString(parseNumberToString(((Container_BasicTank) mContainer).mContent), 10, 30, 16448255);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(parTicks, mouseX, mouseY);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}