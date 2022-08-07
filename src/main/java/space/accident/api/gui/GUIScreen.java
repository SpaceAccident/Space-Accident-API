package space.accident.api.gui;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import space.accident.api.enums.Colors;
import space.accident.api.gui.widgets.GuiFakeItemButton;
import space.accident.api.gui.widgets.GuiIntegerTextBox;
import space.accident.api.gui.widgets.GuiTooltip;
import space.accident.api.gui.widgets.GuiTooltipManager;
import space.accident.api.interfaces.IGuiScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class GUIScreen extends GuiScreen implements GuiTooltipManager.IToolTipRenderer, IGuiScreen {
	
	public String header;
	public GuiFakeItemButton headerIcon;
	protected GuiTooltipManager ttManager = new GuiTooltipManager();
	protected int gui_width = 176;
	protected int gui_height = 107;
	protected int guiTop, guiLeft;
	protected boolean drawButtons = true;
	protected List<IGuiElement> elements = new ArrayList<>();
	protected List<GuiIntegerTextBox> textBoxes = new ArrayList<>();
	private GuiButton selectedButton;
	
	public GUIScreen(int width, int height, String header) {
		this.gui_width  = width;
		this.gui_height = height;
		this.header     = header;
		this.headerIcon = new GuiFakeItemButton(this, 5, 5, null);
	}
	
	@Override
	public void initGui() {
		guiLeft = (this.width - this.gui_width) / 2;
		guiTop  = (this.height - this.gui_height) / 2;
		
		for (IGuiElement element : elements) {
			if (element instanceof GuiButton) buttonList.add(element);
			if (element instanceof GuiIntegerTextBox) textBoxes.add((GuiIntegerTextBox) element);
		}
		
		onInitGui(guiLeft, guiTop, gui_width, gui_height);
		
		for (IGuiElement element : elements) {
			element.onInit();
		}
		super.initGui();
	}
	
	protected abstract void onInitGui(int guiLeft, int guiTop, int gui_width, int gui_height);
	
	public void onMouseWheel(int x, int y, int delta) {
	}
	
	@Override
	public void handleMouseInput() {
		int delta = Mouse.getEventDWheel();
		if (delta != 0) {
			int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			onMouseWheel(i, j, delta);
		}
		super.handleMouseInput();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float parTicks) {
		drawDefaultBackground();
		
		drawBackground(mouseX, mouseY, parTicks);
		
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		if (drawButtons) {
			RenderHelper.enableGUIStandardItemLighting();
			for (IGuiElement e : elements)
				e.draw(mouseX, mouseY, parTicks);
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		drawForegroundLayer(mouseX, mouseY, parTicks);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}
	
	public void drawForegroundLayer(int mouseX, int mouseY, float parTicks) {
		drawExtras(mouseX, mouseY, parTicks);
		ttManager.onTick(this, mouseX, mouseY);
	}
	
	public void drawBackground(int mouseX, int mouseY, float parTicks) {
		int[] color = Colors.MACHINE_METAL.getRGBA();
		GL11.glColor3ub((byte) color[0], (byte) color[1], (byte) color[2]);
		this.mc.renderEngine.bindTexture(new ResourceLocation("sa:textures/gui/GuiCover.png"));
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, gui_width, gui_height);
	}
	
	public void drawExtras(int mouseX, int mouseY, float parTicks) {
		this.fontRendererObj.drawString(header, 25, 9, 0xFF222222);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	public void closeScreen() {
		this.mc.displayGuiScreen(null);
		this.mc.setIngameFocus();
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		for (GuiTextField f : textBoxes) {
			f.updateCursorCounter();
		}
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) {
		for (GuiIntegerTextBox tBox : textBoxes) {
			boolean hadFocus = tBox.isFocused();
			if (tBox.isEnabled() || hadFocus) tBox.mouseClicked(x, y, button);
			
			if (tBox.isFocused() && button == 1 && tBox.isEnabled()) //rightclick -> lcear it
				tBox.setText("0");
			else if (hadFocus && !tBox.isFocused()) applyTextBox(tBox);
		}
		super.mouseClicked(x, y, button);
	}
	
	@Override
	public void keyTyped(char c, int key) {
		GuiIntegerTextBox focusedTextBox = null;
		for (GuiIntegerTextBox textBox : textBoxes) {
			if (textBox.isFocused()) focusedTextBox = textBox;
		}
		
		if (key == 1) { //esc
			if (focusedTextBox != null) {
				resetTextBox(focusedTextBox);
				setFocusedTextBox(null);
				return;
			} else {
				closeScreen();
				// don't fall through to parent
				return;
			}
		}
		
		if (c == '\t') { //tab
			for (int i = 0; i < textBoxes.size(); i++) {
				GuiIntegerTextBox box = textBoxes.get(i);
				if (box.isFocused()) {
					applyTextBox(box);
					setFocusedTextBox(((i + 1) < textBoxes.size()) ? textBoxes.get(i + 1) : null);
					return;
				}
			}
			if (!textBoxes.isEmpty()) setFocusedTextBox(textBoxes.get(0));
			return;
		}
		
		if (focusedTextBox != null && focusedTextBox.textboxKeyTyped(c, key)) {
			return;
		}
		
		if (key == 28 && focusedTextBox != null) { // enter
			applyTextBox(focusedTextBox);
			setFocusedTextBox(null);
			return;
		}
		
		if (key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			if (focusedTextBox != null) {
				applyTextBox(focusedTextBox);
				setFocusedTextBox(null);
				return;
			}
			closeScreen();
			return;
		}
		super.keyTyped(c, key);
	}
	
	/**
	 * Button
	 */
	
	@Override
	public void actionPerformed(GuiButton button) {
		selectedButton = button;
	}
	
	@Override
	public void clearSelectedButton() {
		selectedButton = null;
	}
	
	@Override
	public GuiButton getSelectedButton() {return selectedButton;}
	
	@Override
	public void buttonClicked(GuiButton button) {
	
	}
	
	/**
	 * TextBoxes
	 */
	private void setFocusedTextBox(GuiIntegerTextBox boxToFocus) {
		for (GuiIntegerTextBox textBox : textBoxes) {
			textBox.setFocused(textBox.equals(boxToFocus) && textBox.isEnabled());
		}
	}
	
	/**
	 * Given textbox's value might have changed.
	 */
	public void applyTextBox(GuiIntegerTextBox box) {
	
	}
	
	/**
	 * Reset the given textbox to the last valid value, <b>NOT</b> 0.
	 */
	public void resetTextBox(GuiIntegerTextBox box) {
	
	}
	
	/**
	 * IToolTipRenderer
	 */
	@Override
	public void drawHoveringText(List text, int mouseX, int mouseY, FontRenderer render) {
		super.drawHoveringText(text, mouseX, mouseY, render);
	}
	
	@Override
	public FontRenderer getFontRenderer() {
		return super.fontRendererObj;
	}
	
	@Override
	public void addToolTip(GuiTooltip toolTip) {
		ttManager.addToolTip(toolTip);
	}
	
	@Override
	public boolean removeToolTip(GuiTooltip toolTip) {
		return ttManager.removeToolTip(toolTip);
	}
	
	/**
	 * Junk
	 */
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
		return gui_width;
	}
	
	@Override
	public int getYSize() {
		return gui_height;
	}
	
	@Override
	public RenderItem getItemRenderer() {
		return itemRender;
	}
	
	@Override
	public void addElement(IGuiElement element) {
		if (elements.contains(element)) return;
		elements.add(element);
	}
	
	@Override
	public boolean removeElement(IGuiElement element) {
		return elements.remove(element);
	}
}
