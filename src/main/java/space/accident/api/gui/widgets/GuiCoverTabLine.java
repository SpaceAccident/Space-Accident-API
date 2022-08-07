package space.accident.api.gui.widgets;

import codechicken.nei.api.API;
import codechicken.nei.api.INEIGuiAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import space.accident.api.enums.Colors;
import space.accident.api.gui.GUIContainerMetaTile_Machine;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.main.network.Packet_TileEntityGuiRequest;
import space.accident.main.proxy.GuiHandler;

import java.awt.*;
import java.util.List;

import static space.accident.main.SpaceAccidentApi.NETWORK;

public class GuiCoverTabLine extends GuiTabLine {
	// Names of the block a cover could be on
	private final static String[] SIDES = new String[]{
			"coverTabs.down",
			"coverTabs.up",
			"coverTabs.north",
			"coverTabs.south",
			"coverTabs.west",
			"coverTabs.east"};
	
	// Not sure there's a point in JIT translation but that's what this is
	private String[] translatedSides;
	private ITile tile;
	private Colors colorization;
	
	/**
	 * Let's you access an ITile's covers as tabs on the GUI's sides
	 *
	 * @param gui ITabRenderer gui which this tab line attaches to
	 * @param tabLineLeft left position of the tab line in relation to the gui
	 * @param tabLineTop top position of the tab line in relation to the gui
	 * @param tabHeight height of a tab
	 * @param tabWidth width of a tab
	 * @param tabSpacing pixels between each tab
	 * @param xDir whether to extend the line horizontally to the right (NORMAL),
	 * the left (INVERSE) or not at all (NONE)
	 * @param yDir whether to extend the line vertically down (NORMAL), up (INVERSE)
	 * or not at all (NONE)
	 * @param displayMode  whether to display on the left side of the ITabRenderer
	 * (NORMAL), on it's right side (INVERSE) or not at all (NONE)
	 * @param tabBackground the set of textures used to draw this tab line's tab backgrounds
	 * @param tile The ITile the covers of which we are accessing
	 * @param colorization The colorization of the GUI we are adding tabs to
	 */
	public GuiCoverTabLine(GUIContainerMetaTile_Machine gui, int tabLineLeft, int tabLineTop, int tabHeight,
						   int tabWidth, int tabSpacing, DisplayStyle xDir, DisplayStyle yDir, DisplayStyle displayMode,
						   GuiTabIconSet tabBackground, ITile tile, Colors colorization) {
		super(gui, 6, tabLineLeft, tabLineTop, tabHeight, tabWidth, tabSpacing, xDir, yDir, displayMode, tabBackground);
		this.tile = tile;
		this.colorization = colorization;
		this.translatedSides = new String[6];
		setupTabs();
	}
	
	/**
	 * Add a tab for each existing cover on this ITile at creation time
	 */
	private void setupTabs() {
		for (int tSide = 0; tSide < 6; tSide++) {
			ItemStack cover = tile.getCoverItemAtSide(tSide);
			if (cover != null) {
				addCoverToTabs(tSide, cover);
			}
		}
	}
	
	@Override
	protected void drawBackground(float parTicks, int mouseX, int mouseY) {
		// Apply this tile's coloration to draw the background
		GL11.glColor3ub((byte) colorization.mRGBa[0], (byte) colorization.mRGBa[1], (byte) colorization.mRGBa[2]);
		super.drawBackground(parTicks, mouseX, mouseY);
	}
	
	@Override
	protected void tabClicked(int tabId, int mouseButton) {
		if (mouseButton == 0 && mTabs[tabId].enabled) {
			NETWORK.sendToServer(new Packet_TileEntityGuiRequest(
					this.tile.getX(),
					this.tile.getY(),
					this.tile.getZ(),
					tabId + GuiHandler.GUI_ID_COVER_SIDE_BASE,
					this.tile.getWorld().provider.dimensionId,
					Minecraft.getMinecraft().thePlayer.getEntityId(),
					0));
		}
	}
	
	/**
	 * Add the cover on this side of the ITile to the tabs
	 * @param side
	 * @param cover
	 */
	private void addCoverToTabs(int side, ItemStack cover) {
		boolean enabled = this.tile.getCoverBehaviorAtSideNew(side).hasCoverGUI();
		this.setTab(side, cover, null, getTooltipForCoverTab(side, cover, enabled));
		this.setTabEnabled(side, enabled);
		
	}
	
	/**
	 * Decorate the cover's tooltips according to the side it's on and on whether the tab is enabled or not
	 * @param side
	 * @param cover
	 * @param enabled
	 * @return This cover tab's tooltip
	 */
	private String[] getTooltipForCoverTab(int side, ItemStack cover, boolean enabled) {
		List<String> tooltip = cover.getTooltip(Minecraft.getMinecraft().thePlayer, true);
		tooltip.set(0,
				(enabled ? EnumChatFormatting.UNDERLINE : EnumChatFormatting.DARK_GRAY)
						+ getSideDescription(side)
						+ (enabled ? EnumChatFormatting.RESET + ": " : ": " + EnumChatFormatting.RESET)
						+ tooltip.get(0));
		return tooltip.toArray(new String[0]);
	}
	
	/**
	 * Get the translated name for a side of the ITile
	 * @param side
	 * @return translated name for a side of the ITile
	 */
	private String getSideDescription(int side) {
		if (side < SIDES.length) {
			if (this.translatedSides[side] == null) {
				this.translatedSides[side] = StatCollector.translateToLocal(SIDES[side]);
			}
			return this.translatedSides[side] ;
		}
		return null;
	}
	
	/**
	 * Hide any NEI slots that would intersect with a cover tab
	 */
	static class CoverTabLineNEIHandler extends INEIGuiAdapter {
		@Override
		public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
			Rectangle neiSlotArea = new Rectangle(x, y, w, h);
			if (gui instanceof GUIContainerMetaTile_Machine) {
				GuiTabLine tabLine = ((GUIContainerMetaTile_Machine) gui).coverTabs;
				if (!tabLine.visible) {
					return false;
				}
				for (int i = 0; i < tabLine.mTabs.length; i++ ) {
					if (tabLine.mTabs[i] != null && tabLine.mTabs[i].getBounds().intersects(neiSlotArea)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	static {
		API.registerNEIGuiHandler(new CoverTabLineNEIHandler());
	}
}
