package space.accident.api.interfaces;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import space.accident.api.gui.widgets.GuiTooltip;

public interface IGuiScreen {
	
	interface IGuiElement {
		void onInit();
		default void onRemoved() {}
		void draw(int mouseX, int mouseY, float parTicks);
	}
	
	void addToolTip(GuiTooltip toolTip);
	
	boolean removeToolTip(GuiTooltip toolTip);
	
	GuiButton getSelectedButton();
	void clearSelectedButton();
	void buttonClicked(GuiButton button);
	
	int getGuiLeft();
	int getGuiTop();
	
	int getXSize();
	int getYSize();
	
	void addElement(IGuiElement element);
	boolean removeElement(IGuiElement element);
	
	RenderItem getItemRenderer();
	FontRenderer getFontRenderer();
}
