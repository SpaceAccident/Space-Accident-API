package space.accident.main.render;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.API;
import space.accident.api.interfaces.IColorModulationContainer;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.util.LightingHelper;
import space.accident.structurelib.alignment.IAlignmentProvider;
import space.accident.structurelib.alignment.enumerable.ExtendedFacing;
import space.accident.structurelib.alignment.enumerable.Flip;
import space.accident.structurelib.alignment.enumerable.Rotation;

import static space.accident.api.util.LightingHelper.MAX_BRIGHTNESS;
import static space.accident.main.SpaceAccidentApi.proxy;

public class RenderedTexture extends TextureBase implements ITexture, IColorModulationContainer {
	protected final IIconContainer mIconContainer;
	private final int[] mRGBa;
	private final boolean glow;
	private final boolean stdOrient;
	private final boolean useExtFacing;
	
	protected RenderedTexture(IIconContainer aIcon, int[] aRGBa, boolean allowAlpha, boolean glow, boolean stdOrient, boolean extFacing) {
		if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ RenderedTexture");
		mIconContainer    = aIcon;
		mRGBa             = aRGBa;
		this.glow         = glow;
		this.stdOrient    = stdOrient;
		this.useExtFacing = extFacing;
	}
	
	@Override
	public boolean isOldTexture() {
		return false;
	}
	
	@Override
	public void renderXPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 1.0f, 0.0f, 0.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			if (enableAO) lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingXPos(block, x, y, z).setupColor(ForgeDirection.EAST.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceXPos(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			lighting.setupColor(ForgeDirection.EAST.ordinal(), 0xffffff);
			renderFaceXPos(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public void renderXNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, -1.0f, 0.0f, 0.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingXNeg(block, x, y, z).setupColor(ForgeDirection.WEST.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceXNeg(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			lighting.setupColor(ForgeDirection.WEST.ordinal(), 0xffffff);
			renderFaceXNeg(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public void renderYPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 1.0f, 0.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingYPos(block, x, y, z).setupColor(ForgeDirection.UP.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceYPos(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			lighting.setupColor(ForgeDirection.UP.ordinal(), 0xffffff);
			renderFaceYPos(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public void renderYNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, -1.0f, 0.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingYNeg(block, x, y, z).setupColor(ForgeDirection.DOWN.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceYNeg(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			Tessellator.instance.setColorRGBA(255, 255, 255, 255);
			renderFaceYNeg(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public void renderZPos(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 0.0f, 1.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingZPos(block, x, y, z).setupColor(ForgeDirection.SOUTH.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceZPos(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			lighting.setupColor(ForgeDirection.SOUTH.ordinal(), 0xffffff);
			renderFaceZPos(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public void renderZNeg(RenderBlocks aRenderer, Block block, int x, int y, int z) {
		startDrawingQuads(aRenderer, 0.0f, 0.0f, -1.0f);
		final boolean enableAO = aRenderer.enableAO;
		final LightingHelper lighting = new LightingHelper(aRenderer);
		if (glow) {
			if (!API.mRenderGlowTextures) {
				draw(aRenderer);
				return;
			}
			aRenderer.enableAO = false;
			lighting.setLightnessOverride(1.0F);
			lighting.setBrightnessOverride(MAX_BRIGHTNESS);
		}
		lighting.setupLightingZNeg(block, x, y, z).setupColor(ForgeDirection.NORTH.ordinal(), mRGBa);
		final ExtendedFacing rotation = getExtendedFacing(x, y, z);
		renderFaceZNeg(aRenderer, x, y, z, mIconContainer.getIcon(), rotation);
		if (mIconContainer.getOverlayIcon() != null) {
			lighting.setupColor(ForgeDirection.NORTH.ordinal(), 0xffffff);
			renderFaceZNeg(aRenderer, x, y, z, mIconContainer.getOverlayIcon(), rotation);
		}
		aRenderer.enableAO = enableAO;
		draw(aRenderer);
	}
	
	@Override
	public int[] getRGBA() {
		return mRGBa;
	}
	
	@Override
	public boolean isValidTexture() {
		return mIconContainer != null;
	}
	
	/**
	 * Renders the given texture to the bottom face of the block. Args: block, x, y, z, texture
	 */
	protected void renderFaceYNeg(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateBottom = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateBottom = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateBottom = 3;
				break;
			default:
				aRenderer.uvRotateBottom = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceYNeg(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped() ^ !stdOrient, aFlip.isVerticallyFliped()) : new IconFlipped(icon, !stdOrient, false));
		aRenderer.uvRotateBottom = 0;
	}
	
	/**
	 * Renders the given texture to the top face of the block. Args: block, x, y, z, texture
	 */
	protected void renderFaceYPos(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateTop = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateTop = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateTop = 3;
				break;
			default:
				aRenderer.uvRotateTop = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceYPos(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped(), aFlip.isVerticallyFliped()) : icon);
		aRenderer.uvRotateTop = 0;
	}
	
	/**
	 * Renders the given texture to the north (z-negative) face of the block.  Args: block, x, y, z, texture
	 */
	protected void renderFaceZNeg(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		aRenderer.field_152631_f = true;
		// **NOT A BUG**: aRenderer.uvRotateEast REALLY CONTROLS THE ROTATION OF THE NORTH SIDE
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateEast = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateEast = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateEast = 3;
				break;
			default:
				aRenderer.uvRotateEast = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceZNeg(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped(), aFlip.isVerticallyFliped()) : icon);
		aRenderer.uvRotateEast   = 0;
		aRenderer.field_152631_f = false;
	}
	
	/**
	 * Renders the given texture to the south (z-positive) face of the block.  Args: block, x, y, z, texture
	 */
	protected void renderFaceZPos(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		// **NOT A BUG**: aRenderer.uvRotateWest REALLY CONTROLS THE ROTATION OF THE SOUTH SIDE
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateWest = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateWest = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateWest = 3;
				break;
			default:
				aRenderer.uvRotateWest = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceZPos(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped(), aFlip.isVerticallyFliped()) : icon);
		aRenderer.uvRotateWest = 0;
	}
	
	/**
	 * Renders the given texture to the west (x-negative) face of the block.  Args: block, x, y, z, texture
	 */
	protected void renderFaceXNeg(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		// **NOT A BUG**: aRenderer.uvRotateNorth REALLY CONTROLS THE ROTATION OF THE WEST SIDE
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateNorth = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateNorth = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateNorth = 3;
				break;
			default:
				aRenderer.uvRotateNorth = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceXNeg(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped(), aFlip.isVerticallyFliped()) : icon);
		aRenderer.uvRotateNorth = 0;
	}
	
	/**
	 * Renders the given texture to the east (x-positive) face of the block.  Args: block, x, y, z, texture
	 */
	protected void renderFaceXPos(RenderBlocks aRenderer, double x, double y, double z, IIcon icon, ExtendedFacing extendedFacing) {
		aRenderer.field_152631_f = true;
		// **NOT A BUG**: aRenderer.uvRotateSouth REALLY CONTROLS THE ROTATION OF THE EAST SIDE
		switch (useExtFacing ? extendedFacing.getRotation() : Rotation.NORMAL) {
			case COUNTER_CLOCKWISE:
				aRenderer.uvRotateSouth = 2;
				break;
			case CLOCKWISE:
				aRenderer.uvRotateSouth = 1;
				break;
			case UPSIDE_DOWN:
				aRenderer.uvRotateSouth = 3;
				break;
			default:
				aRenderer.uvRotateSouth = 0;
				break;
		}
		
		final Flip aFlip = extendedFacing.getFlip();
		aRenderer.renderFaceXPos(Blocks.air, x, y, z, useExtFacing && API.mRenderFlippedMachinesFlipped ? new IconFlipped(icon, aFlip.isHorizontallyFlipped(), aFlip.isVerticallyFliped()) : icon);
		aRenderer.uvRotateSouth  = 0;
		aRenderer.field_152631_f = false;
	}
	
	private ExtendedFacing getExtendedFacing(int x, int y, int z) {
		if (stdOrient) return ExtendedFacing.DEFAULT;
		final EntityPlayer player = proxy.getPlayer();
		if (player == null) return ExtendedFacing.DEFAULT;
		final World w = player.getEntityWorld();
		if (w == null) return ExtendedFacing.DEFAULT;
		final TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof ITile) {
			final IMetaTile meta = ((ITile) te).getMetaTile();
			if (meta instanceof IAlignmentProvider) {
				return ((IAlignmentProvider) meta).getAlignment().getExtendedFacing();
			} else if (meta != null) {
				return ExtendedFacing.of(ForgeDirection.getOrientation(meta.getBaseMetaTileEntity().getFrontFace()));
			}
		} else if (te instanceof IAlignmentProvider) {
			return ((IAlignmentProvider) te).getAlignment().getExtendedFacing();
		}
		return ExtendedFacing.DEFAULT;
	}
}
