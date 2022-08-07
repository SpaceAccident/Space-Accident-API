package space.accident.api.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import space.accident.api.util.SpaceLog;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * Main GUI-Container-Class which basically contains the Code needed to prevent crashes from improperly Coded Items.
 */
public class GUIContainer extends GuiContainer {
    
    public boolean mCrashed = false;
    
    public ResourceLocation mGUIbackground;
    
    public String mGUIbackgroundPath;
    
    public GUIContainer(Container aContainer, String aGUIbackground) {
        super(aContainer);
        mGUIbackground = new ResourceLocation(mGUIbackgroundPath = aGUIbackground);
    }
    
    public int getLeft() {
        return guiLeft;
    }
    
    public int getTop() {
        return guiTop;
    }
    
    public void setGuiLeft(int value) {
        guiLeft = value;
    }
    
    public void setGuiTop(int value) {
        guiTop = value;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float parTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(mGUIbackground);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float parTicks) {
        try {
            super.drawScreen(mouseX, mouseY, parTicks);
        } catch (Throwable e) {
            try {
                Tessellator.instance.draw();
            } catch (Throwable f) {
                //
            }
        }
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
    
    protected void onMouseWheel(int mx, int my, int delta) {
    }
    
    public boolean isMouseOverSlot(int slotIndex, int mx, int my) {
        int size = inventorySlots.inventorySlots.size();
        if (slotIndex < 0 || slotIndex >= size) {
            // slot does not exist somehow. log and carry on
            SpaceLog.FML_LOGGER.error("Slot {} required where only {} is present", slotIndex, size);
            return false;
        }
        Slot slot = inventorySlots.getSlot(slotIndex);
        return this.func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mx, my);
    }
}
