package space.accident.main.render;


import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.enums.Colors;
import space.accident.api.enums.Values;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.ITextureBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class TextureBuilder implements ITextureBuilder {
	private final List<IIconContainer> iconContainerList;
	private final List<ITexture> textureLayers;
	private Block fromBlock;
	private int fromMeta;
	private ForgeDirection fromSide;
	private int[] rgba;
	private boolean allowAlpha;
	private boolean stdOrient;
	private boolean extFacing;
	private boolean glow;
	private Boolean worldCoord = null;
	
	public TextureBuilder() {
		textureLayers = new ArrayList<>();
		iconContainerList = new ArrayList<>();
		rgba = Colors._NULL.mRGBa;
		allowAlpha = true;
		stdOrient = false;
		glow = false;
	}
	
	private static Boolean apply(Block b, int m) {
		Class<?> clazz = b.getClass();
		while (clazz != Block.class) {
			String className = clazz.getName();
			if (Values.mCTMDisabledBlock.contains(className)) return false;
			if (Values.mCTMEnabledBlock.contains(className)) return true;
			clazz = clazz.getSuperclass();
		}
		return false;
	}
	
	@Override
	public ITextureBuilder setFromBlock(final Block block, final int meta) {
		this.fromBlock = block;
		this.fromMeta  = meta;
		this.fromSide  = ForgeDirection.UNKNOWN;
		return this;
	}
	
	@Override
	public ITextureBuilder setFromSide(final ForgeDirection side) {
		this.fromSide = side;
		return this;
	}
	
	@Override
	public ITextureBuilder addIcon(final IIconContainer... iconContainers) {
		this.iconContainerList.addAll(Arrays.asList(iconContainers));
		return this;
	}
	
	@Override
	public ITextureBuilder setRGBA(final int[] rgba) {
		this.rgba = rgba;
		return this;
	}
	
	@Override
	public ITextureBuilder addLayer(final ITexture... iTextures) {
		this.textureLayers.addAll(Arrays.asList(iTextures));
		return this;
	}
	
	@Override
	public ITextureBuilder setAllowAlpha(final boolean allowAlpha) {
		this.allowAlpha = allowAlpha;
		return this;
	}
	
	@Override
	public ITextureBuilder stdOrient() {
		this.stdOrient = true;
		return this;
	}
	
	@Override
	public ITextureBuilder useWorldCoord() {
		if (fromBlock == null) throw new IllegalStateException("no from block");
		this.worldCoord = true;
		return this;
	}
	
	@Override
	public ITextureBuilder noWorldCoord() {
		if (fromBlock == null) throw new IllegalStateException("no from block");
		this.worldCoord = false;
		return this;
	}
	
	@Override
	public ITextureBuilder extFacing() {
		this.extFacing = true;
		return this;
	}
	
	@Override
	public ITextureBuilder glow() {
		glow = true;
		return this;
	}
	
	@Override
	public ITexture build() {
		if (fromBlock != null) {
			if (worldCoord == Boolean.TRUE || worldCoord == null && isCTMBlock(fromBlock, fromMeta))
				return new CopiedCTMBlockTexture(fromBlock, fromSide.ordinal(), fromMeta, rgba, allowAlpha);
			else
				return new CopiedBlockTexture(fromBlock, fromSide.ordinal(), fromMeta, rgba, allowAlpha);
		}
		if (!textureLayers.isEmpty()) return new MultiTexture(textureLayers.toArray(new ITexture[0]));
		switch (iconContainerList.size()) {
			case 1:
				return new RenderedTexture(iconContainerList.get(0), rgba, allowAlpha, glow, stdOrient, extFacing);
			case 6:
				return new SidedTexture(
						iconContainerList.get(ForgeDirection.DOWN.ordinal()),
						iconContainerList.get(ForgeDirection.UP.ordinal()),
						iconContainerList.get(ForgeDirection.NORTH.ordinal()),
						iconContainerList.get(ForgeDirection.SOUTH.ordinal()),
						iconContainerList.get(ForgeDirection.WEST.ordinal()),
						iconContainerList.get(ForgeDirection.EAST.ordinal()),
						rgba, allowAlpha
				);
			default:
				throw new IllegalStateException("Invalid sideIconContainer count");
		}
	}
	
	private boolean isCTMBlock(Block fromBlock, int fromMeta) {
		return Values.mCTMBlockCache.computeIfAbsent(fromBlock, (byte) fromMeta, TextureBuilder::apply);
	}
}
