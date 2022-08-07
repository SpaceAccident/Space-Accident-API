package space.accident.main.render;


import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import space.accident.api.API;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.IPipeRenderedTileEntity;
import space.accident.api.interfaces.tileentity.ITexturedTileEntity;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.main.common.blocks.Block_Machines;
import space.accident.main.common.blocks.Meta_Block_Abstract;
import space.accident.main.common.blocks.TileEntity_MetaBlocks;
import space.accident.structurelib.util.XSTR;

import static net.minecraftforge.common.util.ForgeDirection.*;
import static space.accident.api.interfaces.metatileentity.IConnectable.*;

public class Renderer_Block implements ISimpleBlockRenderingHandler {
	
	public static final float blockMin = 0.0F;
	public static final float blockMax = 1.0F;
	private static final float coverThickness = blockMax / 8.0F;
	private static final float coverInnerMin = blockMin + coverThickness;
	private static final float coverInnerMax = blockMax - coverThickness;
	public static Renderer_Block INSTANCE;
	public final int mRenderID;
	
	public Renderer_Block() {
		this.mRenderID = RenderingRegistry.getNextAvailableRenderId();
		INSTANCE       = this;
		RenderingRegistry.registerBlockHandler(this);
	}
	
	public static void register() {
		new Renderer_Block();
	}
	
	public static boolean renderStandardBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks aRenderer) {
		TileEntity tTileEntity = world.getTileEntity(x, y, z);
		if ((tTileEntity instanceof IPipeRenderedTileEntity)) {
			IPipeRenderedTileEntity te = (IPipeRenderedTileEntity) tTileEntity;
			return renderStandardBlock(world, x, y, z, block, aRenderer, new ITexture[][]{te.getTextureCovered(DOWN.ordinal()), te.getTextureCovered(UP.ordinal()), te.getTextureCovered(NORTH.ordinal()), te.getTextureCovered(SOUTH.ordinal()), te.getTextureCovered(WEST.ordinal()), te.getTextureCovered(EAST.ordinal())});
		}
		if ((tTileEntity instanceof ITexturedTileEntity)) {
			ITexturedTileEntity te = (ITexturedTileEntity) tTileEntity;
			return renderStandardBlock(world, x, y, z, block, aRenderer, new ITexture[][]{te.getTexture(block, DOWN.ordinal()), te.getTexture(block, UP.ordinal()), te.getTexture(block, NORTH.ordinal()), te.getTexture(block, SOUTH.ordinal()), te.getTexture(block, WEST.ordinal()), te.getTexture(block, EAST.ordinal())});
		}
		return false;
	}
	
	public static boolean renderStandardBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks aRenderer, ITexture[][] aTextures) {
		block.setBlockBounds(blockMin, blockMin, blockMin, blockMax, blockMax, blockMax);
		aRenderer.setRenderBoundsFromBlock(block);
		
		renderNegativeYFacing(world, aRenderer, block, x, y, z, aTextures[DOWN.ordinal()], true);
		renderPositiveYFacing(world, aRenderer, block, x, y, z, aTextures[UP.ordinal()], true);
		renderNegativeZFacing(world, aRenderer, block, x, y, z, aTextures[NORTH.ordinal()], true);
		renderPositiveZFacing(world, aRenderer, block, x, y, z, aTextures[SOUTH.ordinal()], true);
		renderNegativeXFacing(world, aRenderer, block, x, y, z, aTextures[WEST.ordinal()], true);
		renderPositiveXFacing(world, aRenderer, block, x, y, z, aTextures[EAST.ordinal()], true);
		return true;
	}
	
	public static boolean renderPipeBlock(IBlockAccess world, int x, int y, int z, Block block, IPipeRenderedTileEntity aTileEntity, RenderBlocks aRenderer) {
		final int aConnections = aTileEntity.getConnections();
		if ((aConnections & (HAS_FRESHFOAM | HAS_HARDENEDFOAM)) != 0) {
			return renderStandardBlock(world, x, y, z, block, aRenderer);
		}
		final float thickness = aTileEntity.getThickNess();
		if (thickness >= 0.99F) {
			return renderStandardBlock(world, x, y, z, block, aRenderer);
		}
		// Range of block occupied by pipe
		final float pipeMin = (blockMax - thickness) / 2.0F;
		final float pipeMax = blockMax - pipeMin;
		final boolean[] tIsCovered = new boolean[VALID_DIRECTIONS.length];
		for (int i = 0; i < VALID_DIRECTIONS.length; i++) {
			tIsCovered[i] = (aTileEntity.getCoverIDAtSide(i) != 0);
		}
		
		final ITexture[][] tIcons = new ITexture[VALID_DIRECTIONS.length][];
		final ITexture[][] tCovers = new ITexture[VALID_DIRECTIONS.length][];
		for (int i = 0; i < VALID_DIRECTIONS.length; i++) {
			tCovers[i] = aTileEntity.getTexture(block, i);
			tIcons[i]  = aTileEntity.getTextureUncovered(i);
		}
		
		switch (aConnections) {
			case NO_CONNECTION:
				block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
				aRenderer.setRenderBoundsFromBlock(block);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				break;
			case CONNECTED_EAST | CONNECTED_WEST:
				// EAST - WEST Pipe Sides
				block.setBlockBounds(blockMin, pipeMin, pipeMin, blockMax, pipeMax, pipeMax);
				aRenderer.setRenderBoundsFromBlock(block);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				
				// EAST - WEST Pipe Ends
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				break;
			case CONNECTED_DOWN | CONNECTED_UP:
				// UP - DOWN Pipe Sides
				block.setBlockBounds(pipeMin, blockMin, pipeMin, pipeMax, blockMax, pipeMax);
				aRenderer.setRenderBoundsFromBlock(block);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				
				// UP - DOWN Pipe Ends
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
				break;
			case CONNECTED_NORTH | CONNECTED_SOUTH:
				// NORTH - SOUTH Pipe Sides
				block.setBlockBounds(pipeMin, pipeMin, blockMin, pipeMax, pipeMax, blockMax);
				aRenderer.setRenderBoundsFromBlock(block);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				
				// NORTH - SOUTH Pipe Ends
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				break;
			default:
				if ((aConnections & CONNECTED_WEST) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(blockMin, pipeMin, pipeMin, pipeMin, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
					renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
					renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
					renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				}
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
				
				if ((aConnections & CONNECTED_EAST) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(pipeMax, pipeMin, pipeMin, blockMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
					renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
					renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
					renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				}
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				
				if ((aConnections & CONNECTED_DOWN) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(pipeMin, blockMin, pipeMin, pipeMax, pipeMin, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
					renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
					renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
					renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				}
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
				
				if ((aConnections & CONNECTED_UP) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(pipeMin, pipeMax, pipeMin, pipeMax, blockMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
					renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
					renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
					renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				}
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
				
				if ((aConnections & CONNECTED_NORTH) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(pipeMin, pipeMin, blockMin, pipeMax, pipeMax, pipeMin);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
					renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
					renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
					renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				}
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tIcons[NORTH.ordinal()], false);
				
				if ((aConnections & CONNECTED_SOUTH) == 0) {
					block.setBlockBounds(pipeMin, pipeMin, pipeMin, pipeMax, pipeMax, pipeMax);
					aRenderer.setRenderBoundsFromBlock(block);
				} else {
					block.setBlockBounds(pipeMin, pipeMin, pipeMax, pipeMax, pipeMax, blockMax);
					aRenderer.setRenderBoundsFromBlock(block);
					renderNegativeYFacing(world, aRenderer, block, x, y, z, tIcons[DOWN.ordinal()], false);
					renderPositiveYFacing(world, aRenderer, block, x, y, z, tIcons[UP.ordinal()], false);
					renderNegativeXFacing(world, aRenderer, block, x, y, z, tIcons[WEST.ordinal()], false);
					renderPositiveXFacing(world, aRenderer, block, x, y, z, tIcons[EAST.ordinal()], false);
				}
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tIcons[SOUTH.ordinal()], false);
				break;
		}
		
		// Render covers on pipes
		if (tIsCovered[DOWN.ordinal()]) {
			block.setBlockBounds(blockMin, blockMin, blockMin, blockMax, coverInnerMin, blockMax);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[NORTH.ordinal()]) {
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
			}
			if (!tIsCovered[SOUTH.ordinal()]) {
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
			}
			if (!tIsCovered[WEST.ordinal()]) {
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
			}
			if (!tIsCovered[EAST.ordinal()]) {
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
			}
			renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
			if ((aConnections & CONNECTED_DOWN) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMin, blockMin, blockMin, blockMax, blockMin, pipeMin);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMin, blockMin, pipeMax, blockMax, blockMin, blockMax);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMin, blockMin, pipeMin, pipeMin, blockMin, pipeMax);
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(pipeMax, blockMin, pipeMin, blockMax, blockMin, pipeMax);
			}
			renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[DOWN.ordinal()], false);
		}
		
		if (tIsCovered[UP.ordinal()]) {
			block.setBlockBounds(blockMin, coverInnerMax, blockMin, blockMax, blockMax, blockMax);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[NORTH.ordinal()]) {
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
			}
			if (!tIsCovered[SOUTH.ordinal()]) {
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
			}
			if (!tIsCovered[WEST.ordinal()]) {
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
			}
			if (!tIsCovered[EAST.ordinal()]) {
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
			}
			renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
			if ((aConnections & CONNECTED_UP) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMin, blockMax, blockMin, blockMax, blockMax, pipeMin);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMin, blockMax, pipeMax, blockMax, blockMax, blockMax);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMin, blockMax, pipeMin, pipeMin, blockMax, pipeMax);
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(pipeMax, blockMax, pipeMin, blockMax, blockMax, pipeMax);
			}
			renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[UP.ordinal()], false);
		}
		
		if (tIsCovered[NORTH.ordinal()]) {
			block.setBlockBounds(blockMin, blockMin, blockMin, blockMax, blockMax, coverInnerMin);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[DOWN.ordinal()]) {
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
			}
			if (!tIsCovered[UP.ordinal()]) {
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
			}
			if (!tIsCovered[WEST.ordinal()]) {
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
			}
			if (!tIsCovered[EAST.ordinal()]) {
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
			}
			renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
			if ((aConnections & CONNECTED_NORTH) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMin, blockMin, blockMin, blockMax, pipeMin, blockMin);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMin, pipeMax, blockMin, blockMax, blockMax, blockMin);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMin, pipeMin, blockMin, pipeMin, pipeMax, blockMin);
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(pipeMax, pipeMin, blockMin, blockMax, pipeMax, blockMin);
			}
			renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[NORTH.ordinal()], false);
		}
		
		if (tIsCovered[SOUTH.ordinal()]) {
			block.setBlockBounds(blockMin, blockMin, coverInnerMax, blockMax, blockMax, blockMax);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[DOWN.ordinal()]) {
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
			}
			if (!tIsCovered[UP.ordinal()]) {
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
			}
			if (!tIsCovered[WEST.ordinal()]) {
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
			}
			if (!tIsCovered[EAST.ordinal()]) {
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
			}
			renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
			if ((aConnections & CONNECTED_SOUTH) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMin, blockMin, blockMax, blockMax, pipeMin, blockMax);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMin, pipeMax, blockMax, blockMax, blockMax, blockMax);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMin, pipeMin, blockMax, pipeMin, pipeMax, blockMax);
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(pipeMax, pipeMin, blockMax, blockMax, pipeMax, blockMax);
			}
			renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[SOUTH.ordinal()], false);
		}
		
		if (tIsCovered[WEST.ordinal()]) {
			block.setBlockBounds(blockMin, blockMin, blockMin, coverInnerMin, blockMax, blockMax);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[DOWN.ordinal()]) {
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
			}
			if (!tIsCovered[UP.ordinal()]) {
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
			}
			if (!tIsCovered[NORTH.ordinal()]) {
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
			}
			if (!tIsCovered[SOUTH.ordinal()]) {
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
			}
			renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
			if ((aConnections & CONNECTED_WEST) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMin, blockMin, blockMin, blockMin, pipeMin, blockMax);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMin, pipeMax, blockMin, blockMin, blockMax, blockMax);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMin, pipeMin, blockMin, blockMin, pipeMax, pipeMin);
				renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(blockMin, pipeMin, pipeMax, blockMin, pipeMax, blockMax);
			}
			renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[WEST.ordinal()], false);
		}
		
		if (tIsCovered[EAST.ordinal()]) {
			block.setBlockBounds(coverInnerMax, blockMin, blockMin, blockMax, blockMax, blockMax);
			aRenderer.setRenderBoundsFromBlock(block);
			if (!tIsCovered[DOWN.ordinal()]) {
				renderNegativeYFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
			}
			if (!tIsCovered[UP.ordinal()]) {
				renderPositiveYFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
			}
			if (!tIsCovered[NORTH.ordinal()]) {
				renderNegativeZFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
			}
			if (!tIsCovered[SOUTH.ordinal()]) {
				renderPositiveZFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
			}
			renderNegativeXFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
			
			if ((aConnections & CONNECTED_EAST) != 0) {
				// Split outer face to leave hole for pipe
				// Lower panel
				aRenderer.setRenderBounds(blockMax, blockMin, blockMin, blockMax, pipeMin, blockMax);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
				// Upper panel
				aRenderer.setRenderBounds(blockMax, pipeMax, blockMin, blockMax, blockMax, blockMax);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
				// Middle left panel
				aRenderer.setRenderBounds(blockMax, pipeMin, blockMin, blockMax, pipeMax, pipeMin);
				renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
				// Middle right panel
				aRenderer.setRenderBounds(blockMax, pipeMin, pipeMax, blockMax, pipeMax, blockMax);
			}
			renderPositiveXFacing(world, aRenderer, block, x, y, z, tCovers[EAST.ordinal()], false);
		}
		block.setBlockBounds(blockMin, blockMin, blockMin, blockMax, blockMax, blockMax);
		aRenderer.setRenderBoundsFromBlock(block);
		
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public static void addHitEffects(EffectRenderer effectRenderer, Block block, World world, int x, int y, int z, int side) {
		double rX = x + XSTR.XSTR_INSTANCE.nextDouble() * 0.8 + 0.1;
		double rY = y + XSTR.XSTR_INSTANCE.nextDouble() * 0.8 + 0.1;
		double rZ = z + XSTR.XSTR_INSTANCE.nextDouble() * 0.8 + 0.1;
		if (side == 0) {
			rY = y - 0.1;
		} else if (side == 1) {
			rY = y + 1.1;
		} else if (side == 2) {
			rZ = z - 0.1;
		} else if (side == 3) {
			rZ = z + 1.1;
		} else if (side == 4) {
			rX = x - 0.1;
		} else if (side == 5) {
			rX = x + 1.1;
		}
		effectRenderer.addEffect((new EntityDiggingFX(world, rX, rY, rZ, 0.0, 0.0, 0.0, block, block.getDamageValue(world, x, y, z), side)).applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
	}
	
	@SideOnly(Side.CLIENT)
	public static void addDestroyEffects(EffectRenderer effectRenderer, Block block, World world, int x, int y, int z) {
		for (int iX = 0; iX < 4; ++iX) {
			for (int iY = 0; iY < 4; ++iY) {
				for (int iZ = 0; iZ < 4; ++iZ) {
					double bX = x + (iX + 0.5) / 4.0;
					double bY = y + (iY + 0.5) / 4.0;
					double bZ = z + (iZ + 0.5) / 4.0;
					effectRenderer.addEffect((new EntityDiggingFX(world, bX, bY, bZ, bX - x - 0.5, bY - y - 0.5, bZ - z - 0.5, block, block.getDamageValue(world, x, y, z))).applyColourMultiplier(x, y, z));
				}
			}
		}
	}
	
	private static void renderNormalInventoryMetaTileEntity(Block block, int aMeta, RenderBlocks aRenderer) {
		if ((aMeta <= 0) || (aMeta >= API.METATILEENTITIES.length)) {
			return;
		}
		IMetaTile tMetaTileEntity = API.METATILEENTITIES[aMeta];
		if (tMetaTileEntity == null) {
			return;
		}
		block.setBlockBoundsForItemRender();
		aRenderer.setRenderBoundsFromBlock(block);
		
		final ITile iTile = tMetaTileEntity.getBaseMetaTileEntity();
		
		if ((iTile instanceof IPipeRenderedTileEntity)) {
			final float tThickness = ((IPipeRenderedTileEntity) iTile).getThickNess();
			final float pipeMin = (blockMax - tThickness) / 2.0F;
			final float pipeMax = blockMax - pipeMin;
			
			block.setBlockBounds(blockMin, pipeMin, pipeMin, blockMax, pipeMax, pipeMax);
			aRenderer.setRenderBoundsFromBlock(block);
			renderNegativeYFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, DOWN.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, false, false), true);
			renderPositiveYFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, UP.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, false, false), true);
			renderNegativeZFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, NORTH.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, false, false), true);
			renderPositiveZFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, SOUTH.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, false, false), true);
			renderNegativeXFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, WEST.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, true, false), true);
			renderPositiveXFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, EAST.ordinal(), CONNECTED_WEST | CONNECTED_EAST, -1, true, false), true);
		} else {
			renderNegativeYFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, DOWN.ordinal(), WEST.ordinal(), -1, true, false), true);
			renderPositiveYFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, UP.ordinal(), WEST.ordinal(), -1, true, false), true);
			renderNegativeZFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, NORTH.ordinal(), WEST.ordinal(), -1, true, false), true);
			renderPositiveZFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, SOUTH.ordinal(), WEST.ordinal(), -1, true, false), true);
			renderNegativeXFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, WEST.ordinal(), WEST.ordinal(), -1, true, false), true);
			renderPositiveXFacing(null, aRenderer, block, 0, 0, 0, tMetaTileEntity.getTexture(iTile, EAST.ordinal(), WEST.ordinal(), -1, true, false), true);
		}
	}
	
	public static void renderNegativeYFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x, y - 1, z, 0))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, aFullBlock ? y - 1 : y, z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderYNeg(aRenderer, block, x, y, z);
			}
		}
	}
	
	public static void renderPositiveYFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x, y + 1, z, 1))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, aFullBlock ? y + 1 : y, z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderYPos(aRenderer, block, x, y, z);
			}
		}
	}
	
	public static void renderNegativeZFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x, y, z - 1, 2))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, aFullBlock ? z - 1 : z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderZNeg(aRenderer, block, x, y, z);
			}
		}
	}
	
	public static void renderPositiveZFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x, y, z + 1, 3))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, aFullBlock ? z + 1 : z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderZPos(aRenderer, block, x, y, z);
			}
		}
	}
	
	public static void renderNegativeXFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x - 1, y, z, 4))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, aFullBlock ? x - 1 : x, y, z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderXNeg(aRenderer, block, x, y, z);
			}
		}
	}
	
	public static void renderPositiveXFacing(IBlockAccess world, RenderBlocks aRenderer, Block block, int x, int y, int z, ITexture[] aIcon, boolean aFullBlock) {
		if (world != null) {
			if ((aFullBlock) && (!block.shouldSideBeRendered(world, x + 1, y, z, 5))) return;
			Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, aFullBlock ? x + 1 : x, y, z));
		}
		if (aIcon == null) return;
		for (ITexture iTexture : aIcon) {
			if (iTexture != null) {
				iTexture.renderXPos(aRenderer, block, x, y, z);
			}
		}
	}
	
	@Override
	public void renderInventoryBlock(Block block, int aMeta, int aModelID, RenderBlocks aRenderer) {
		aRenderer.enableAO         = false;
		aRenderer.useInventoryTint = true;
		
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		if (block instanceof Meta_Block_Abstract) {
			TileEntity_MetaBlocks tTileEntity = new TileEntity_MetaBlocks();
			tTileEntity.mMetaData = ((short) aMeta);
			
			block.setBlockBoundsForItemRender();
			aRenderer.setRenderBoundsFromBlock(block);
			renderNegativeYFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, DOWN.ordinal()), true);
			renderPositiveYFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, UP.ordinal()), true);
			renderNegativeZFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, NORTH.ordinal()), true);
			renderPositiveZFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, SOUTH.ordinal()), true);
			renderNegativeXFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, WEST.ordinal()), true);
			renderPositiveXFacing(null, aRenderer, block, 0, 0, 0, tTileEntity.getTexture(block, EAST.ordinal()), true);
			
		} else if (aMeta > 0 && (aMeta < API.METATILEENTITIES.length) && block instanceof Block_Machines && (API.METATILEENTITIES[aMeta] != null) && (!API.METATILEENTITIES[aMeta].renderInInventory(block, aMeta, aRenderer))) {
			renderNormalInventoryMetaTileEntity(block, aMeta, aRenderer);
		}
		block.setBlockBounds(blockMin, blockMin, blockMin, blockMax, blockMax, blockMax);
		aRenderer.setRenderBoundsFromBlock(block);
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		aRenderer.useInventoryTint = false;
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int aModelID, RenderBlocks aRenderer) {
		aRenderer.enableAO         = Minecraft.isAmbientOcclusionEnabled() && API.mRenderTileAmbientOcclusion;
		aRenderer.useInventoryTint = false;
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) return false;
		if (tileEntity instanceof ITile) {
			IMetaTile metaTileEntity;
			if ((metaTileEntity = ((ITile) tileEntity).getMetaTile()) != null && metaTileEntity.renderInWorld(world, x, y, z, block, aRenderer)) {
				aRenderer.enableAO = false;
				return true;
			}
		}
		if (tileEntity instanceof IPipeRenderedTileEntity && renderPipeBlock(world, x, y, z, block, (IPipeRenderedTileEntity) tileEntity, aRenderer)) {
			aRenderer.enableAO = false;
			return true;
		}
		if (renderStandardBlock(world, x, y, z, block, aRenderer)) {
			aRenderer.enableAO = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int aModel) {
		return true;
	}
	
	@Override
	public int getRenderId() {
		return this.mRenderID;
	}
}
