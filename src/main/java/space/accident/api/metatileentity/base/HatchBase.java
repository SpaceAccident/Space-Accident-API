package space.accident.api.metatileentity.base;


import net.minecraft.nbt.NBTTagCompound;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;

import static space.accident.main.SpaceAccidentApi.proxy;

/**
 * Handles texture changes internally. No special calls are necessary other than updateTexture in add***ToMachineList.
 */
public abstract class HatchBase extends BasicTank {
	/**
	 * Uses new texture changing methods to avoid limitations of int as texture index...
	 */
	public int mMachineBlock = 0;
	private int mTexturePage = 0;
	private int actualTexture = 0;
	
	public HatchBase(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public HatchBase(int id, String name, String aNameRegional, int aTier, int aInvSlotCount, String[] aDescription, ITexture... aTextures) {
		super(id, name, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public HatchBase(String name, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public HatchBase(String name, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aInvSlotCount, aDescription, aTextures);
	}
	
	public static int getSlots(int aTier) {
		return aTier < 1 ? 1 : aTier == 1 ? 4 : aTier == 2 ? 9 : 16;
	}
	
	@Override
	public ITexture[][][] getTextureSet(ITexture[] aTextures) {
		return new ITexture[0][0][0];
	}
	
	public abstract ITexture[] getTexturesActive(ITexture aBaseTexture);
	
	public abstract ITexture[] getTexturesInactive(ITexture aBaseTexture);
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int face, int aColorIndex, boolean active, boolean aRedstone) {
		int textureIndex = actualTexture | (mTexturePage << 7); //Shift seven since one page is 128 textures!
		int texturePointer = (actualTexture & 0x7F); //just to be sure, from my testing the 8th bit cannot be set clientside
		try {
			if (side != face) {
				if (textureIndex > 0) {
					return new ITexture[]{Textures.casingTexturePages[mTexturePage][texturePointer]};
				} else {
					return new ITexture[]{Textures.MACHINE_CASINGS[mTier][aColorIndex + 1]};
				}
			} else {
				if (textureIndex > 0) {
					if (active) {
						return getTexturesActive(Textures.casingTexturePages[mTexturePage][texturePointer]);
					} else {
						return getTexturesInactive(Textures.casingTexturePages[mTexturePage][texturePointer]);
					}
				} else {
					if (active) {
						return getTexturesActive(Textures.MACHINE_CASINGS[mTier][aColorIndex + 1]);
					} else {
						return getTexturesInactive(Textures.MACHINE_CASINGS[mTier][aColorIndex + 1]);
					}
				}
			}
		} catch (NullPointerException npe) {
			return new ITexture[]{Textures.MACHINE_CASINGS[0][0]};
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("mMachineBlock", actualTexture);
		nbt.setInteger("mTexturePage", mTexturePage);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		actualTexture = nbt.getInteger("mMachineBlock");
		mTexturePage  = nbt.getInteger("mTexturePage");
		
		if (mTexturePage != 0 && proxy.isServerSide()) {
			actualTexture |= 0x80; //<- lets just hope no one needs the correct value for that on server
		}
		mMachineBlock = actualTexture;
	}
	
	/**
	 * Sets texture with page and index, called on add to machine list
	 *
	 * @param id (page<<7)+index of the texture
	 */
	public final void updateTexture(int id) {
		onValueUpdate(id);
		onTexturePageUpdate(id >> 7);
	}
	
	/**
	 * Sets texture with page and index, rather unusable, but kept FFS
	 *
	 * @param page  page of texure
	 * @param index index of texure
	 */
	@Deprecated
	public final void updateTexture(int page, int index) {
		onValueUpdate(index);
		onTexturePageUpdate(page);
	}
	
	@Override
	public final void onValueUpdate(int value) {
		actualTexture = (value & 0x7F);
		mMachineBlock = actualTexture;
		mTexturePage  = 0;
	}
	
	@Override
	public final int getUpdateData() {
		return (actualTexture & 0x7F);
	}
	
	public final void onTexturePageUpdate(int value) {
		mTexturePage = (value & 0x7F);
		if (mTexturePage != 0 && getBaseMetaTileEntity().isServerSide()) { //just to be sure
			mMachineBlock |= 0x80; //<- lets just hope no one needs the correct value for that on server
			actualTexture = mMachineBlock;
		}
		//set last bit to allow working of the page reset-er to 0 in rare case when texture id is the same but page changes to 0
	}
	
	public final int getTexturePage() {
		return (mTexturePage & 0x7F);
	}
	
	@Override
	public boolean doesFillContainers() {
		return false;
	}
	
	@Override
	public boolean doesEmptyContainers() {
		return false;
	}
	
	@Override
	public boolean canTankBeFilled() {
		return false;
	}
	
	@Override
	public boolean canTankBeEmptied() {
		return false;
	}
	
	@Override
	public boolean displaysItemStack() {
		return false;
	}
	
	@Override
	public boolean displaysStackSize() {
		return false;
	}
	
	@Override
	public void onPreTick(ITile baseTile, long aTick) {//in that method since it is usually not overriden, especially for hatches.
		if (actualTexture != mMachineBlock) { //revert to page 0 on edition of the field - old code way
			actualTexture = (mMachineBlock & 0x7F);
			mMachineBlock = actualTexture; //clear last bit in mMachineBlock since now we are at page 0 after the direct field change
			mTexturePage  = 0; //assuming old code only supports page 0
		}
		super.onPreTick(baseTile, aTick);
	}
	//To change to other page -> use the setter method -> updateTexture
}
