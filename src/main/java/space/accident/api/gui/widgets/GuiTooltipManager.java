package space.accident.api.gui.widgets;

import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class GuiTooltipManager {
	public interface IToolTipRenderer {
		int getGuiLeft();
		int getGuiTop();
		int getXSize();
		FontRenderer getFontRenderer();
		void drawHoveringText(List<String> text, int mouseX, int mouseY, FontRenderer font);
	}
	
	private static final long DELAY = 5;
	private int mouseStopped;
	private int lastMouseX = -1;
	private int lastMouseY = -1;
	private final List<GuiTooltip> tips = new ArrayList<>();
	
	public void addToolTip(GuiTooltip tip) {
		if (tip != null && !tips.contains(tip)) tips.add(tip);
	}
	
	public boolean removeToolTip(GuiTooltip tip) {
		return tips.remove(tip);
	}
	
	public final void onTick(IToolTipRenderer render, int mouseX, int mouseY) {
		if ((Math.abs(mouseX-lastMouseX) < 2 ) && (Math.abs(mouseY-lastMouseY) < 2 )) {
			mouseStopped = Math.min(mouseStopped+1, 50);
		} else {
			mouseStopped = 0;
		}
		
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		
		mouseX -= render.getGuiLeft();
		mouseY -= render.getGuiTop();
		for (GuiTooltip tip : tips) {
			// Give the tooltip the opportunity to decide whether they should be enabled
			tip.onTick();
			if (tip.enabled && (!tip.isDelayed() || mouseStopped > DELAY) && tip.getBounds().contains(mouseX, mouseY)) {
				tip.updateText();
				drawTooltip(tip, mouseX, mouseY, render);
				break;
			}
		}
	}
	
	private void drawTooltip(GuiTooltip tip, int mouseX, int mouseY, IToolTipRenderer render) {
		List<String> text = tip.getToolTipText();
		if (text == null)
			return;
		
		if (mouseX > render.getGuiLeft() + render.getXSize()/2) {
			int maxWidth = 0;
			for (String s : text) {
				int w = render.getFontRenderer().getStringWidth(s);
				if (w > maxWidth) {
					maxWidth = w;
				}
			}
			mouseX -= (maxWidth + 18);
		}
		
		render.drawHoveringText(text, mouseX, mouseY, render.getFontRenderer());
	}
	
}
