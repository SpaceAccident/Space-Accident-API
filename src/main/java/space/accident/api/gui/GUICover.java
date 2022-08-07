package space.accident.api.gui;

import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.main.network.Packet_TileEntityGuiRequest;

import static space.accident.main.SpaceAccidentApi.NETWORK;

public abstract class GUICover extends GUIScreen {
	
	public final ICoverable tile;
	public int parentGuiId = -1;
	
	public GUICover(ICoverable tile, int width, int height, ItemStack cover) {
		super(width, height, cover == null ? "" : cover.getDisplayName());
		this.tile = tile;
		headerIcon.setItem(cover);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		if (!tile.isUseableByPlayer(mc.thePlayer)) {
			closeScreen();
		}
	}
	
	/**
	 * The parent GUI to exit to. -1 is ignored.
	 *
	 * @param parentGuiId
	 */
	public void setParentGuiId(int parentGuiId) {
		this.parentGuiId = parentGuiId;
	}
	
	
	@Override
	public void closeScreen() {
		// If this cover was given a guiId, tell the server to open it for us when this GUI closes.
		if (parentGuiId != -1 && tile.isUseableByPlayer(mc.thePlayer)) {
			NETWORK.sendToServer(new Packet_TileEntityGuiRequest(
					tile.getX(),
					tile.getY(),
					tile.getZ(),
					parentGuiId,
					tile.getWorld().provider.dimensionId,
					mc.thePlayer.getEntityId()
			));
		} else {
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}
	}
}
