package space.accident.api.gui.widgets;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import space.accident.api.interfaces.IGuiIcon;

import java.awt.*;

/**
 * Draws clickable and configurable tabs on the left or right side of another GUI
 */
public class GuiTabLine {
	// The tabs are arranged according to their index in this array
	protected final GuiTab[] mTabs;
	// Whether to display on the right side of the ITabRenderer instead of left
	protected boolean flipHorizontally, visible;
	private final int tabLineLeft;
	private final int tabLineTop;
	private final int tabHeight;
	private final int tabWidth;
	private final int tabSpacing;
	// In which direction to draw the tab line
	private final DisplayStyle xDir;
	private final DisplayStyle yDir;
	private final GuiTabIconSet tabBackground;
	private final ITabRenderer gui;
	
	/**
	 * Draws clickable and configurable tabs on the left or right side of a ITabRenderer
	 *
	 * @param gui           ITabRenderer gui which this tab line attaches to
	 * @param numTabs       number of tab positions in this tab line
	 * @param tabLineLeft   left position of the tab line in relation to the gui
	 * @param tabLineTop    top position of the tab line in relation to the gui
	 * @param tabHeight     height of a tab
	 * @param tabWidth      width of a tab
	 * @param tabSpacing    pixels between each tab
	 * @param xDir          whether to extend the line horizontally to the right (NORMAL),
	 *                      the left (INVERSE) or not at all (NONE)
	 * @param yDir          whether to extend the line vertically down (NORMAL), up (INVERSE)
	 *                      or not at all (NONE)
	 * @param displayMode   whether to display on the left side of the ITabRenderer
	 *                      (NORMAL), on it's right side (INVERSE) or not at all (NONE)
	 * @param tabBackground the set of textures used to draw this tab line's tab backgrounds
	 */
	public GuiTabLine(ITabRenderer gui, int numTabs, int tabLineLeft, int tabLineTop, int tabHeight, int tabWidth, int tabSpacing, DisplayStyle xDir, DisplayStyle yDir, DisplayStyle displayMode, GuiTabIconSet tabBackground) {
		this.gui              = gui;
		this.mTabs            = new GuiTab[numTabs];
		this.tabLineLeft      = tabLineLeft;
		this.tabLineTop       = tabLineTop;
		this.tabHeight        = tabHeight;
		this.tabWidth         = tabWidth;
		this.tabSpacing       = tabSpacing;
		this.xDir             = xDir;
		this.yDir             = yDir;
		this.tabBackground    = tabBackground;
		this.flipHorizontally = displayMode == DisplayStyle.INVERSE;
		this.visible          = !(displayMode == DisplayStyle.NONE);
	}
	
	/**
	 * Creates a new tab at the specified position with the given parameters.
	 * This class handles the positioning.
	 *
	 * @param tabId
	 * @param item
	 * @param overlay
	 * @param text
	 */
	public void setTab(int tabId, ItemStack item, IGuiIcon overlay, String[] text) {
		mTabs[tabId] = new GuiTab(this.gui, tabId, getBoundsForTab(tabId), this.tabBackground, item, overlay, text, this.flipHorizontally);
	}
	
	/**
	 * Get the bounds a given tab should occupy
	 *
	 * @param tabId
	 * @return
	 */
	protected Rectangle getBoundsForTab(int tabId) {
		return new Rectangle(getTabX(tabId), getTabY(tabId), this.tabWidth, this.tabHeight);
	}
	
	/**
	 * Enable or disable a tab. Disabled tabs have a dark background.
	 *
	 * @param tabId
	 * @param value
	 */
	public void setTabEnabled(int tabId, boolean value) {
		if (mTabs[tabId] != null) {
			mTabs[tabId].enabled = value;
		}
	}
	
	/**
	 * Draw the tabs for this tab bar
	 * ITabRenderer must call this method on drawGuiContainerBackgroundLayer
	 * or on drawScreen.
	 *
	 * @param parTicks
	 * @param mouseX
	 * @param mouseY
	 */
	public void drawTabs(float parTicks, int mouseX, int mouseY) {
		if (this.visible) {
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawBackground(parTicks, mouseX, mouseY);
			drawOverlays(parTicks, mouseX, mouseY);
			GL11.glPopAttrib();
		}
	}
	
	/**
	 * Draw the tab's backgrounds first
	 *
	 * @param parTicks
	 * @param mouseX
	 * @param mouseY
	 */
	protected void drawOverlays(float parTicks, int mouseX, int mouseY) {
		for (int i = 0; i < mTabs.length; i++) {
			if (mTabs[i] != null) {
				mTabs[i].drawOverlays(mouseX, mouseY, parTicks);
			}
		}
	}
	
	/**
	 * Draw anything that overlays the tab's background texture
	 *
	 * @param parTicks
	 * @param mouseX
	 * @param mouseY
	 */
	protected void drawBackground(float parTicks, int mouseX, int mouseY) {
		for (int i = 0; i < mTabs.length; i++) {
			if (mTabs[i] != null) {
				mTabs[i].drawBackground(mouseX, mouseY, parTicks);
			}
		}
	}
	
	/**
	 * Call tabClick for every tab that was clicked.
	 * ITabRenderer must call this method on mouseClicked.
	 *
	 * @param mouseX
	 * @param mouseY
	 * @param mouseButton
	 */
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		for (int tabId = 0; tabId < mTabs.length; tabId++) {
			if (mTabs[tabId] != null && mTabs[tabId].getBounds().contains(mouseX, mouseY)) {
				tabClicked(tabId, mouseButton);
				return;
			}
		}
	}
	
	/**
	 * Act on a tab being clicked.
	 *
	 * @param tabId
	 * @param mouseButton
	 */
	protected void tabClicked(int tabId, int mouseButton) {}
	
	/**
	 * Reposition ourselves whenever the ITabRenderer does so.
	 * ITabRenderer must call this method on Init.
	 */
	public void onInit() {
		for (int i = 0; i < mTabs.length; i++) {
			if (mTabs[i] != null) {
				mTabs[i].setPosition(getTabX(i), getTabY(i));
			}
		}
	}
	
	/**
	 * Get the proper X position for a given tab
	 *
	 * @param tabId
	 * @return
	 */
	private int getTabX(int tabId) {
		return this.gui.getGuiLeft() + (flipHorizontally ? (gui.getXSize() - tabLineLeft - tabWidth) : tabLineLeft) + (tabId * (tabWidth + tabSpacing) * xDir.getValue());
	}
	
	/**
	 * Get the proper Y position for a given tab
	 *
	 * @param tabId
	 * @return
	 */
	private int getTabY(int tabId) {
		return this.gui.getGuiTop() + tabLineTop + (tabId * (tabHeight + tabSpacing) * yDir.getValue());
	}
	
	/**
	 * Controls the rendering style of the tab line
	 */
	public enum DisplayStyle {
		NONE(0), NORMAL(1), INVERSE(-1);
		
		private final int value;
		
		DisplayStyle(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	/**
	 * A GUI should  implement these methods as well as call the tab line's
	 * onMouseClicked, onInit and drawTabs for the tab line to attach to it properly.
	 */
	public interface ITabRenderer {
		int getGuiLeft();
		
		int getGuiTop();
		
		int getXSize();
		
		RenderItem getItemRenderer();
		
		FontRenderer getFontRenderer();
		
		void addToolTip(GuiTooltip tooltip);
		
		boolean removeToolTip(GuiTooltip tooltip);
	}
	
	/**
	 * Defines a set of textures a tab line can use to render it's tab backgrounds
	 */
	public static class GuiTabIconSet {
		public IGuiIcon disabled;
		public IGuiIcon normal;
		public IGuiIcon highlight;
		
		public GuiTabIconSet(IGuiIcon normalIcon, IGuiIcon highlightIcon, IGuiIcon disabledIcon) {
			this.normal    = normalIcon;
			this.highlight = highlightIcon;
			this.disabled  = disabledIcon;
		}
	}
}
