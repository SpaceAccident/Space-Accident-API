package space.accident.api.interfaces.tileentity;

import net.minecraft.block.Block;
import space.accident.api.interfaces.ITexture;

public interface ITexturedTileEntity {
	/**
	 * @return the Textures rendered by the Rendering
	 */
	ITexture[] getTexture(Block block, int side);
}
