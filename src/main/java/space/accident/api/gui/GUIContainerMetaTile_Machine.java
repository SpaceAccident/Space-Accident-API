package space.accident.api.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import space.accident.api.API;
import space.accident.api.enums.Colors;
import space.accident.api.gui.widgets.*;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.util.TooltipDataCache;
import space.accident.main.Config;

import java.util.List;

public class GUIContainerMetaTile_Machine extends GUIContainer implements GuiTooltipManager.IToolTipRenderer, GuiTabLine.ITabRenderer {
	
	private static final int
			COVER_TAB_LEFT = -16,
			COVER_TAB_TOP = 1,
			COVER_TAB_HEIGHT = 20,
			COVER_TAB_WIDTH = 18,
			COVER_TAB_SPACING = 2;
	private static final GuiTabLine.DisplayStyle
			COVER_TAB_X_DIR = GuiTabLine.DisplayStyle.NONE,
			COVER_TAB_Y_DIR = GuiTabLine.DisplayStyle.NORMAL;
	private static final GuiTabLine.GuiTabIconSet TAB_ICONSET = new GuiTabLine.GuiTabIconSet(
			GuiIcon.TAB_NORMAL,
			GuiIcon.TAB_HIGHLIGHT,
			GuiIcon.TAB_DISABLED
	);
	public final ContainerMetaTile_Machine mContainer;
	// Cover Tabs support. Subclasses can override display position, style and visuals by overriding setupCoverTabs
	public GuiCoverTabLine coverTabs;
	protected GuiTooltipManager mTooltipManager = new GuiTooltipManager();
	protected TooltipDataCache mTooltipCache = new TooltipDataCache();
	
	public GUIContainerMetaTile_Machine(ContainerMetaTile_Machine aContainer, String aGUIbackground) {
		super(aContainer, aGUIbackground);
		mContainer = aContainer;
		
		GuiTabLine.DisplayStyle preferredDisplayStyle = Config.mCoverTabsVisible
				? (Config.mCoverTabsFlipped ? GuiTabLine.DisplayStyle.INVERSE : GuiTabLine.DisplayStyle.NORMAL)
				: GuiTabLine.DisplayStyle.NONE;
		setupCoverTabs(preferredDisplayStyle);
		
		// Only setup tooltips if they're currently enabled.
		if (Config.mTooltipVerbosity > 0 || Config.mTooltipShiftVerbosity > 0) {
			setupTooltips();
		}
	}
	
	public GUIContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, ITile aTileEntity,
										String aGUIbackground) {
		this(new ContainerMetaTile_Machine(aInventoryPlayer, aTileEntity), aGUIbackground);
	}
	
	/**
	 * Initialize the coverTabs object according to client preferences
	 */
	protected void setupCoverTabs(GuiTabLine.DisplayStyle preferredDisplayStyle) {
		coverTabs = new GuiCoverTabLine(
				this,
				COVER_TAB_LEFT,
				COVER_TAB_TOP,
				COVER_TAB_HEIGHT,
				COVER_TAB_WIDTH,
				COVER_TAB_SPACING,
				COVER_TAB_X_DIR,
				COVER_TAB_Y_DIR,
				preferredDisplayStyle,
				getTabBackground(),
				getMachine().getBaseMetaTileEntity(),
				getColorization()
		);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float parTicks) {
		super.drawScreen(mouseX, mouseY, parTicks);
		if (mc.thePlayer.inventory.getItemStack() == null) {
			GL11.glPushMatrix();
			GL11.glTranslatef(guiLeft, guiTop, 0.0F);
			mTooltipManager.onTick(this, mouseX, mouseY);
			GL11.glPopMatrix();
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
		// Drawing tabs
		coverTabs.drawTabs(parTicks, mouseX, mouseY);
		
		// Applying machine coloration, which subclasses rely on
		Colors color = getColorization();
		GL11.glColor3ub((byte) color.mRGBa[0], (byte) color.mRGBa[1], (byte) color.mRGBa[2]);
		
		// Binding machine's own texture, which subclasses rely on being set
		super.drawGuiContainerBackgroundLayer(parTicks, mouseX, mouseY);
	}
	
	/**
	 * @return The color used to render this machine's GUI
	 */
	private Colors getColorization() {
		if (API.sMachineMetalGUI) {
			return Colors.MACHINE_METAL;
		} else if (API.sColoredGUI && mContainer != null && mContainer.mTileEntity != null) {
			int colorization = mContainer.mTileEntity.getColorization();
			Colors color;
			if (colorization != -1)
				color = Colors.get(colorization);
			else
				color = Colors.MACHINE_METAL;
			return color;
		} else {
			return Colors.dyeWhite;
		}
	}
	
	/**
	 * @return This machine's MetaTileEntity
	 */
	private MetaTileEntity getMachine() {
		return (MetaTileEntity) mContainer.mTileEntity.getMetaTile();
	}
	
	// Tabs support
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Check for clicked tabs
		coverTabs.onMouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		// Perform layout of tabs
		coverTabs.onInit();
	}
	
	/**
	 * @return the background textures used by this machine GUI's tabs
	 */
	protected GuiTabLine.GuiTabIconSet getTabBackground() {
		return TAB_ICONSET;
	}
	
	// Tooltips support
	
	/**
	 * Load data for and create appropriate tooltips for this machine.
	 * Only called when one of regular or shift tooltips are enabled.
	 */
	protected void setupTooltips() {}
	
	// IToolTipRenderer and ITabRenderer implementations
	@Override
	public void drawHoveringText(List text, int mouseX, int mouseY, FontRenderer font) {
		super.drawHoveringText(text, mouseX, mouseY, font);
	}
	
	@Override
	public int getGuiTop() {
		return guiTop;
	}
	
	@Override
	public int getGuiLeft() {
		return guiLeft;
	}
	
	@Override
	public int getXSize() {
		return xSize;
	}
	
	@Override
	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}
	
	@Override
	public RenderItem getItemRenderer() {
		return itemRender;
	}
	
	@Override
	public void addToolTip(GuiTooltip toolTip) {
		mTooltipManager.addToolTip(toolTip);
	}
	
	@Override
	public boolean removeToolTip(GuiTooltip toolTip) {
		return mTooltipManager.removeToolTip(toolTip);
	}
}
