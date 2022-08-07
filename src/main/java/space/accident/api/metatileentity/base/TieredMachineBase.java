package space.accident.api.metatileentity.base;

import space.accident.api.interfaces.ITexture;

import static space.accident.main.SpaceAccidentApi.proxy;

public abstract class TieredMachineBase extends MetaTileEntity {
	/**
	 * Value between [0 - 9] to describe the Tier of this Machine.
	 * PLZ [0-15] works - READ! Values class.
	 */
	public final int mTier;
	
	@Deprecated
	public final String mDescription;
	
	/**
	 * A simple Description.
	 */
	public final String[] mDescriptionArray;
	
	/**
	 * Contains all Textures used by this Block.
	 */
	public final ITexture[][][] mTextures;
	
	public TieredMachineBase(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aInvSlotCount);
		mTier             = Math.max(0, Math.min(aTier, 15));
		mDescriptionArray = aDescription == null ? new String[0] : new String[]{aDescription};
		mDescription      = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
		// must always be the last call!
		if (proxy.isClientSide()) mTextures = getTextureSet(aTextures);
		else mTextures = null;
	}
	
	public TieredMachineBase(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String[] aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aInvSlotCount);
		mTier             = Math.max(0, Math.min(aTier, 15));
		mDescriptionArray = aDescription == null ? new String[0] : aDescription;
		mDescription      = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
		
		// must always be the last call!
		if (proxy.isClientSide()) mTextures = getTextureSet(aTextures);
		else mTextures = null;
	}
	
	public TieredMachineBase(String name, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
		super(name, aInvSlotCount);
		mTier             = aTier;
		mDescriptionArray = aDescription == null ? new String[0] : new String[]{aDescription};
		mDescription      = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
		mTextures         = aTextures;
	}
	
	public TieredMachineBase(String name, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aInvSlotCount);
		mTier             = aTier;
		mDescriptionArray = aDescription == null ? new String[0] : aDescription;
		mDescription      = mDescriptionArray.length > 0 ? mDescriptionArray[0] : "";
		mTextures         = aTextures;
	}
	
	@Override
	public int getTileEntityBaseType() {
		return (Math.min(3, mTier <= 0 ? 0 : 1 + ((mTier - 1) / 4)));
	}
	
	@Override
	public long getInputTier() {
		return mTier;
	}
	
	@Override
	public long getOutputTier() {
		return mTier;
	}
	
	@Override
	public String[] getDescription() {
		return mDescriptionArray;
	}
	
	/**
	 * Used Client Side to get a Texture Set for this Block.
	 * Called after setting the Tier and the Description so that those two are accessible.
	 *
	 * @param aTextures is the optional Array you can give to the Constructor.
	 */
	public abstract ITexture[][][] getTextureSet(ITexture[] aTextures);
}