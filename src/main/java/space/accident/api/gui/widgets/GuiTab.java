package space.accident.api.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import space.accident.api.interfaces.IGuiIcon;

import java.awt.*;

/**
 * A tab to be attached to a tab line
 */
public class GuiTab {
	private static final int SLOT_SIZE = 18;
	
	public boolean
			visible = true,
			mousedOver,
			enabled = true;
	
	private Rectangle bounds;
	private GuiTabLine.GuiTabIconSet tabBackground;
	private ItemStack item;
	private GuiTabLine.ITabRenderer gui;
	private GuiTooltip tooltip;
	private IGuiIcon overlay;
	private boolean flipHorizontally;
	
	/**
	 * A tab to be attached to a tab line
	 *
	 * @param gui              ITile the tab line this tab belongs to is attached to
	 * @param id               both the ID and position in the tab line of this tab
	 * @param bounds           bounds of this tab
	 * @param tabBackground    set of background textures
	 * @param item             item to draw atop the background texture, not colored
	 * @param overlay          texture to draw atop the background texture, not colored
	 * @param tooltipText      tooltip of this tab
	 * @param flipHorizontally whether to draw this tab on the right side of the ITile
	 */
	public GuiTab(GuiTabLine.ITabRenderer gui, int id, Rectangle bounds, GuiTabLine.GuiTabIconSet tabBackground, ItemStack item,
				  IGuiIcon overlay, String[] tooltipText, boolean flipHorizontally) {
		this.gui           = gui;
		this.bounds        = bounds;
		this.item          = item;
		this.tabBackground = tabBackground;
		this.overlay       = overlay;
		if (tooltipText != null) {
			setTooltipText(tooltipText);
		}
		this.flipHorizontally = flipHorizontally;
	}
	
	public GuiTab(GuiTabLine.ITabRenderer gui, int id, Rectangle bounds, GuiTabLine.GuiTabIconSet tabBackground) {
		this(gui, id, bounds, tabBackground, null, null, null, false);
	}
	
	/**
	 * Set this tab's tooltip text
	 *
	 * @param text
	 * @return This tab for chaining
	 */
	public GuiTab setTooltipText(String... text) {
		if (tooltip == null) {
			tooltip = new GuiTooltip(bounds, text);
			gui.addToolTip(tooltip);
		} else {
			tooltip.setToolTipText(text);
		}
		return this;
	}
	
	/**
	 * @return This tab's tooltip object
	 */
	public GuiTooltip getTooltip() {
		return tooltip;
	}
	
	/**
	 * Draw the background texture for this tab
	 *
	 * @param mouseX
	 * @param mouseY
	 * @param parTicks
	 */
	public void drawBackground(int mouseX, int mouseY, float parTicks) {
		if (this.visible) {
			GuiIcon.render(getBackgroundTexture(), bounds.x, bounds.y, bounds.width, bounds.height, 1, true,
					this.flipHorizontally
			);
		}
	}
	
	/**
	 * Draw overlay textures and items atop the background texture
	 *
	 * @param mouseX
	 * @param mouseY
	 * @param parTicks
	 */
	public void drawOverlays(int mouseX, int mouseY, float parTicks) {
		this.mousedOver = bounds.contains(mouseX, mouseY);
		
		if (this.tooltip != null) {
			this.tooltip.enabled = this.visible;
		}
		
		if (this.visible) {
			if (overlay != null) {
				GL11.glColor4f(1, 1, 1, 1);
				GuiIcon.render(overlay, bounds.x, bounds.y, bounds.width, bounds.height, 1, true);
			}
			if (item != null) {
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				
				if (item.getItem() instanceof ItemBlock) {
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				}
				int margin = (bounds.height - SLOT_SIZE);
				gui.getItemRenderer().renderItemAndEffectIntoGUI(gui.getFontRenderer(),
						Minecraft.getMinecraft().getTextureManager(), item,
						bounds.x + (this.flipHorizontally ? 0 : margin), bounds.y + margin
				);
				
				if (item.getItem() instanceof ItemBlock)
					GL11.glPopAttrib();
				
				GL11.glPopAttrib();
			}
		}
	}
	
	/**
	 * @return the texture this tab should currently use as it's background
	 */
	protected IGuiIcon getBackgroundTexture() {
		if (!enabled)
			return tabBackground.disabled;
		
		return mousedOver ? tabBackground.highlight : tabBackground.normal;
	}
	
	/**
	 * @return the screen space occupied by this tab
	 */
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	/**
	 * Reposition this tab on the screen
	 *
	 * @param xPos
	 * @param yPos
	 */
	public void setPosition(int xPos, int yPos) {
		this.bounds = new Rectangle(xPos, yPos, bounds.width, bounds.height);
	}
}
