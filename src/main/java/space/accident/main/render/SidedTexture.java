package space.accident.main.render;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import space.accident.api.interfaces.IColorModulationContainer;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.render.TextureFactory;

public class SidedTexture extends TextureBase implements ITexture, IColorModulationContainer {
	protected final ITexture[] mTextures;
	/**
	 * DO NOT MANIPULATE THE VALUES INSIDE THIS ARRAY!!!
	 * <p/>
	 * Just set this variable to another different Array instead.
	 * Otherwise some colored things will get Problems.
	 */
	private final int[] mRGBa;
	
	protected SidedTexture(IIconContainer aIcon0, IIconContainer aIcon1, IIconContainer aIcon2, IIconContainer aIcon3, IIconContainer aIcon4, IIconContainer aIcon5, int[] aRGBa,
						   boolean aAllowAlpha) {
		if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ RenderedTexture");
		mTextures = new ITexture[]{
				TextureFactory.of(aIcon0, aRGBa, aAllowAlpha),
				TextureFactory.of(aIcon1, aRGBa, aAllowAlpha),
				TextureFactory.of(aIcon2, aRGBa, aAllowAlpha),
				TextureFactory.of(aIcon3, aRGBa, aAllowAlpha),
				TextureFactory.of(aIcon4, aRGBa, aAllowAlpha),
				TextureFactory.of(aIcon5, aRGBa, aAllowAlpha)
		};
		mRGBa = aRGBa;
	}
	
	@Override
	public boolean isOldTexture() {
		return false;
	}
	
	@Override
	public void renderXPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[5].renderXPos(aRenderer, block, x ,y, z);
	}
	
	@Override
	public void renderXNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[4].renderXNeg(aRenderer, block, x ,y, z);
	}
	
	@Override
	public void renderYPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[1].renderYPos(aRenderer, block, x ,y, z);
	}
	
	@Override
	public void renderYNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[0].renderYNeg(aRenderer, block, x ,y, z);
	}
	
	@Override
	public void renderZPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[3].renderZPos(aRenderer, block, x ,y, z);
	}
	
	@Override
	public void renderZNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		mTextures[2].renderZNeg(aRenderer, block, x ,y, z);
	}
	
	@Override
	public int[] getRGBA() {
		return mRGBa;
	}
	
	@Override
	public boolean isValidTexture() {
		for (ITexture renderedTexture : mTextures) {
			if (!renderedTexture.isValidTexture()) return false;
		}
		return true;
	}
}
