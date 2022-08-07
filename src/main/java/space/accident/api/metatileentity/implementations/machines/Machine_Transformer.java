package space.accident.api.metatileentity.implementations.machines;


import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.TieredMachineBase;

import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;
import static space.accident.api.enums.Values.V;
import static space.accident.extensions.StringUtils.trans;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the main construct for my Basic Machines such as the Automatic Extractor
 * Extend this class to make a simple Machine
 */
public class Machine_Transformer extends TieredMachineBase {
	
	public Machine_Transformer(int id, String aNameRegional, int aTier, String aDescription) {
		super(id, "transformer.tier." + aTier, aNameRegional, aTier, 0, aDescription);
	}
	
	public Machine_Transformer(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	public Machine_Transformer(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	@Override
	public ITexture[][][] getTextureSet(ITexture[] aTextures) {
		ITexture[][][] rTextures = new ITexture[12][17][];
		for (int i = -1; i < 16; i++) {
			rTextures[0][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
			rTextures[1][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
			rTextures[2][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
			rTextures[3][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[4][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[5][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[6][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[7][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[8][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
			rTextures[9][i + 1]  = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
			rTextures[10][i + 1] = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
			rTextures[11][i + 1] = new ITexture[]{Textures.MACHINE_CASINGS[mTier][i + 1], Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
		}
		return rTextures;
	}
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int face, int aColorIndex, boolean active, boolean aRedstone) {
		return mTextures[Math.min(2, side) + (side == face ? 3 : 0) + (baseTile.isAllowedToWork() ? 0 : 6)][aColorIndex + 1];
	}
	
	@Override
	public IMetaTile newMetaEntity(ITile aTileEntity) {
		return new Machine_Transformer(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isSimpleMachine() {
		return true;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return true;
	}
	
	@Override
	public boolean isEnetInput() {
		return true;
	}
	
	@Override
	public boolean isEnetOutput() {
		return true;
	}
	
	@Override
	public boolean isInputFacing(int side) {
		return getBaseMetaTileEntity().isAllowedToWork() == (side == getBaseMetaTileEntity().getFrontFace());
	}
	
	@Override
	public boolean isOutputFacing(int side) {
		return !isInputFacing(side);
	}
	
	@Override
	public boolean isTeleporterCompatible() {
		return false;
	}
	
	@Override
	public long getMinimumStoredEU() {
		return V[mTier + 1];
	}
	
	@Override
	public long maxEUStore() {
		return Math.max(512L, 1L << (mTier + 2)) + V[mTier + 1] * 4L;
	}
	
	@Override
	public long maxEUInput() {
		return V[getBaseMetaTileEntity().isAllowedToWork() ? mTier + 1 : mTier];
	}
	
	@Override
	public long maxEUOutput() {
		return V[getBaseMetaTileEntity().isAllowedToWork() ? mTier : mTier + 1];
	}
	
	@Override
	public long maxAmperesOut() {
		return getBaseMetaTileEntity().isAllowedToWork() ? 4 : 1;
	}
	
	@Override
	public long maxAmperesIn() {
		return getBaseMetaTileEntity().isAllowedToWork() ? 1 : 4;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean hasAlternativeModeText() {
		return true;
	}
	
	@Override
	public String getAlternativeModeText() {
		return
				(getBaseMetaTileEntity().isAllowedToWork() ? trans("145", "Step Down, In: ") : trans("146", "Step Up, In: ")) +
						maxEUInput() +
						trans("148", "V ") +
						maxAmperesIn() +
						trans("147", "A, Out: ") +
						maxEUOutput() +
						trans("148", "V ") +
						maxAmperesOut() +
						trans("149", "A");
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		final int facing = getBaseMetaTileEntity().getFrontFace();
		final NBTTagCompound tag = accessor.getNBTData();
		final int side = accessor.getSide().ordinal();
		final boolean allowedToWork = tag.getBoolean("isAllowedToWork");
		
		currentTip.add(
				String.format(
						"%s %d(%dA) -> %d(%dA)",
						(allowedToWork ? (GREEN + "Step Down") : (RED + "Step Up")) + RESET,
						tag.getLong("maxEUInput"),
						tag.getLong("maxAmperesIn"),
						tag.getLong("maxEUOutput"),
						tag.getLong("maxAmperesOut")
				)
		);
		
		if ((side == facing && allowedToWork) || (side != facing && !allowedToWork)) {
			currentTip.add(String.format(GOLD + "Input:" + RESET + " %d(%dA)", tag.getLong("maxEUInput"), tag.getLong("maxAmperesIn")));
		} else {
			currentTip.add(String.format(BLUE + "Output:" + RESET + " %d(%dA)", tag.getLong("maxEUOutput"), tag.getLong("maxAmperesOut")));
		}
		
		super.getWailaBody(itemStack, currentTip, accessor, config);
		
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		super.getWailaNBTData(player, tile, tag, world, x, y, z);
		tag.setBoolean("isAllowedToWork", getBaseMetaTileEntity().isAllowedToWork());
		tag.setLong("maxEUInput", maxEUInput());
		tag.setLong("maxAmperesIn", maxAmperesIn());
		tag.setLong("maxEUOutput", maxEUOutput());
		tag.setLong("maxAmperesOut", maxAmperesOut());
	}
	
	
}
