package space.accident.api.metatileentity.implementations.hathes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.API;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.RecipeMap;
import space.accident.extensions.StringUtils;

import static space.accident.api.enums.Textures.BlockIcons.FLUID_IN_SIGN;
import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_PIPE_IN;
import static space.accident.extensions.NumberUtils.format;

public class Hatch_Input_Fluid extends HatchBase {
	public RecipeMap mRecipeMap = null;
	
	public Hatch_Input_Fluid(int id, String aNameRegional, int aTier) {
		super(id, "hatch.input.tier." + aTier, aNameRegional, aTier, 3, StringUtils.array(
						"Fluid Input for Multiblocks",
						"Capacity: " + format(8000 * (1 << aTier)) + "L"
				)
		);
	}
	
	public Hatch_Input_Fluid(int id, int aSlot, String name, String aNameRegional, int aTier) {
		super(id, name, aNameRegional, aTier, aSlot, StringUtils.array(
						"Fluid Input for Multiblocks",
						"Capacity: " + format(8000 * (1 << aTier) / aSlot) + "L",
						"Can hold " + aSlot + " types of fluid."
				)
		);
	}
	
	public Hatch_Input_Fluid(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 3, aDescription, aTextures);
	}
	
	public Hatch_Input_Fluid(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 3, aDescription, aTextures);
	}
	
	public Hatch_Input_Fluid(String name, int aSlots, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aSlots, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN), TextureFactory.of(FLUID_IN_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN), TextureFactory.of(FLUID_IN_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN)};
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
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Input_Fluid(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (mRecipeMap != null)
			nbt.setString("recipeMap", mRecipeMap.mUniqueIdentifier);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		mRecipeMap = RecipeMap.sIndexedMappings.getOrDefault(nbt.getString("recipeMap"), null);
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		if (baseTile.isClientSide()) return true;
		baseTile.openGUI(player);
		return true;
	}
	
	@Override
	public boolean doesFillContainers() {
		//return true;
		return false;
	}
	
	@Override
	public boolean doesEmptyContainers() {
		return true;
	}
	
	@Override
	public boolean canTankBeFilled() {
		return true;
	}
	
	@Override
	public boolean canTankBeEmptied() {
		return true;
	}
	
	@Override
	public boolean displaysItemStack() {
		return true;
	}
	
	@Override
	public boolean displaysStackSize() {
		return false;
	}
	
	public void updateSlots() {
		if (mInventory[getInputSlot()] != null && mInventory[getInputSlot()].stackSize <= 0)
			mInventory[getInputSlot()] = null;
	}
	
	@Override
	public boolean isFluidInputAllowed(FluidStack aFluid) {
		return mRecipeMap == null || mRecipeMap.containsInput(aFluid);
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == baseTile.getFrontFace() && index == 1;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == baseTile.getFrontFace() && index == 0;
	}
	
	@Override
	public int getCapacity() {
		return 8000 * (1 << mTier);
	}
	
	@Override
	public int getTankPressure() {
		return -100;
	}
}

