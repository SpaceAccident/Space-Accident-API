package space.accident.main.render;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import space.accident.api.interfaces.ITexture;

/**
 * <p>Lets Multiple ITextures Render overlay over each other.<</p>
 * <p>I should have done this much earlier...</p>
 */
public class MultiTexture extends TextureBase implements ITexture {
	protected final ITexture[] mTextures;
	
	protected MultiTexture(ITexture... aTextures) {
		mTextures = aTextures;
	}
	
	@Override
	public void renderXPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderXPos(aRenderer, block, x, y, z);
	}
	
	@Override
	public void renderXNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderXNeg(aRenderer, block, x, y, z);
	}
	
	@Override
	public void renderYPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderYPos(aRenderer, block, x, y, z);
	}
	
	@Override
	public void renderYNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderYNeg(aRenderer, block, x, y, z);
	}
	
	@Override
	public void renderZPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderZPos(aRenderer, block, x, y, z);
	}
	
	@Override
	public void renderZNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		for (ITexture tTexture : mTextures)
			if (tTexture != null && tTexture.isValidTexture()) tTexture.renderZNeg(aRenderer, block, x, y, z);
	}
	
	@Override
	public boolean isValidTexture() {
		return true;
	}
}
