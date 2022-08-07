package space.accident.main.render;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.interfaces.IBlockContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.util.LightingHelper;

public class CopiedBlockTexture extends TextureBase implements ITexture, IBlockContainer {
	private final Block mBlock;
	private final int mSide, mMeta;
	
	protected CopiedBlockTexture(Block block, int side, int aMeta, int[] aRGBa, boolean allowAlpha) {
		if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ CopiedBlockTexture");
		mBlock = block;
		mSide  = side;
		mMeta  = aMeta;
	}
	
	@Override
	public boolean isOldTexture() {
		return false;
	}
	
	private IIcon getIcon(int side) {
		if (mSide == 6) return mBlock.getIcon(side, mMeta);
		return mBlock.getIcon(mSide, mMeta);
	}
	
	@Override
	public void renderXPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		final IIcon aIcon = getIcon(ForgeDirection.EAST.ordinal());
		aRenderer.field_152631_f = true;
		startDrawingQuads(aRenderer, 1.0f, 0.0f, 0.0f);
		new LightingHelper(aRenderer).setupLightingXPos(block, x, y, z).setupColor(ForgeDirection.EAST.ordinal(), 0xffffff);
		aRenderer.renderFaceXPos(block, x, y, z, aIcon);
		draw(aRenderer);
		aRenderer.field_152631_f = false;
	}
	
	@Override
	public void renderXNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, -1.0f, 0.0f, 0.0f);
		final IIcon aIcon = getIcon(ForgeDirection.WEST.ordinal());
		new LightingHelper(aRenderer).setupLightingXNeg(block, x, y, z).setupColor(ForgeDirection.WEST.ordinal(), 0xffffff);
		aRenderer.renderFaceXNeg(block, x, y, z, aIcon);
		draw(aRenderer);
	}
	
	@Override
	public void renderYPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 1.0f, 0.0f);
		final IIcon aIcon = getIcon(ForgeDirection.UP.ordinal());
		new LightingHelper(aRenderer).setupLightingYPos(block, x, y, z).setupColor(ForgeDirection.UP.ordinal(), 0xffffff);
		aRenderer.renderFaceYPos(block, x, y, z, aIcon);
		draw(aRenderer);
	}
	
	@Override
	public void renderYNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, -1.0f, 0.0f);
		final IIcon aIcon = getIcon(ForgeDirection.DOWN.ordinal());
		new LightingHelper(aRenderer).setupLightingYNeg(block, x, y, z).setupColor(ForgeDirection.DOWN.ordinal(), 0xffffff);
		aRenderer.renderFaceYNeg(block, x, y, z, aIcon);
		draw(aRenderer);
	}
	
	@Override
	public void renderZPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 0.0f, 1.0f);
		final IIcon aIcon = getIcon(ForgeDirection.SOUTH.ordinal());
		new LightingHelper(aRenderer).setupLightingZPos(block, x, y, z).setupColor(ForgeDirection.SOUTH.ordinal(), 0xffffff);
		aRenderer.renderFaceZPos(block, x, y, z, aIcon);
		draw(aRenderer);
	}
	
	@Override
	public void renderZNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 0.0f, -1.0f);
		final IIcon aIcon = getIcon(ForgeDirection.NORTH.ordinal());
		aRenderer.field_152631_f = true;
		new LightingHelper(aRenderer).setupLightingZNeg(block, x, y, z).setupColor(ForgeDirection.NORTH.ordinal(), 0xffffff);
		aRenderer.renderFaceZNeg(block, x, y, z, aIcon);
		draw(aRenderer);
		aRenderer.field_152631_f = false;
	}
	
	@Override
	public boolean isValidTexture() {
		return mBlock != null;
	}
	
	@Override
	public Block getBlock() {
		return mBlock;
	}
	
	@Override
	public int getMeta() {
		return mMeta;
	}
}
