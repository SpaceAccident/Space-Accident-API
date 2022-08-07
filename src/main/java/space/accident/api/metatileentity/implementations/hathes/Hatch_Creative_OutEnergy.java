package space.accident.api.metatileentity.implementations.hathes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.extensions.StringUtils;

import static space.accident.api.enums.Values.V;

public class Hatch_Creative_OutEnergy extends HatchBase {
	
	public Hatch_Creative_OutEnergy(int id, String aNameRegional) {
		super(id, "creative_out_energy_hatch", aNameRegional, 5, 0, StringUtils.array());
	}
	
	public Hatch_Creative_OutEnergy(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	public Hatch_Creative_OutEnergy(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 0, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI[mTier]};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI[mTier]};
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
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isEnetOutput() {
		return true;
	}
	
	@Override
	public boolean isOutputFacing(int side) {
		return side == getBaseMetaTileEntity().getFrontFace();
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return false;
	}
	
	@Override
	public long getMinimumStoredEU() {
		return 512;
	}
	
	@Override
	public long maxEUOutput() {
		return V[mTier];
	}
	
	@Override
	public long maxEUStore() {
		return 512L + V[mTier + 1] * 2L;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Creative_OutEnergy(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public void onPostTick(ITile te, long tick) {
		super.onPostTick(te, tick);
		if (tick % 20 == 0) {
			setEUVar(maxEUStore());
		}
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
}
