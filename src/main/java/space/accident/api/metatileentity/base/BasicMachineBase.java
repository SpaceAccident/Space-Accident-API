package space.accident.api.metatileentity.base;


import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import space.accident.api.API;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.objects.ItemStackData;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.*;
import space.accident.main.common.power.BasicMachineEUPower;
import space.accident.main.common.power.Power;
import space.accident.main.items.ItemList;

import java.util.Arrays;
import java.util.List;

import static space.accident.api.API.getCoverBehaviorNew;
import static space.accident.api.API.sSoundList;
import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;
import static space.accident.api.enums.Textures.MACHINE_CASINGS;
import static space.accident.api.enums.Values.V;
import static space.accident.api.util.Utility.*;
import static space.accident.extensions.NumberUtils.format;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;
import static space.accident.main.SpaceAccidentApi.proxy;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the main construct for my Basic Machines such as the Automatic Extractor
 * Extend this class to make a simple Machine
 */
public abstract class BasicMachineBase extends BasicTank /*implements IMachineCallback<Object>*/ {
	
	public static final int OTHER_SLOT_COUNT = 5;
	/**
	 * return values for checkRecipe()
	 */
	protected static final int DID_NOT_FIND_RECIPE = 0, FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1, FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2;
	public final ItemStack[] mOutputItems;
	public final int mInputSlotCount, mAmperage;
	protected final Power mPower;
	public boolean mAllowInputFromOutputSide = false, mFluidTransfer = false, mItemTransfer = false, mHasBeenUpdated = false, mStuttering = false, mCharge = false, mDecharge = false;
	public boolean mDisableFilter = true;
	public boolean mDisableMultiStack = true;
	public int mMainFacing = -1, mProgresstime = 0, mMaxProgresstime = 0, mEUt = 0, mOutputBlocked = 0;
	public FluidStack mOutputFluid;
	public String mGUIName, mNEIName;
	/**
	 * Contains the Recipe which has been previously used, or null if there was no previous Recipe, which could have been buffered
	 */
	protected Recipe mLastRecipe = null;
	private FluidStack mFluidOut;
	
	/**
	 * @param aOverlays 0 = SideFacingActive
	 *                  1 = SideFacingInactive
	 *                  2 = FrontFacingActive
	 *                  3 = FrontFacingInactive
	 *                  4 = TopFacingActive
	 *                  5 = TopFacingInactive
	 *                  6 = BottomFacingActive
	 *                  7 = BottomFacingInactive
	 *                  ----- Not all Array Elements have to be initialised, you can also just use 8 Parameters for the Default Pipe Texture Overlays -----
	 *                  8 = BottomFacingPipeActive
	 *                  9 = BottomFacingPipeInactive
	 *                  10 = TopFacingPipeActive
	 *                  11 = TopFacingPipeInactive
	 *                  12 = SideFacingPipeActive
	 *                  13 = SideFacingPipeInactive
	 */
	public BasicMachineBase(int id, String name, String aNameRegional, int aTier, int aAmperage, String aDescription, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName, ITexture... aOverlays) {
		super(id, name, aNameRegional, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aOverlays);
		mInputSlotCount = Math.max(0, aInputSlotCount);
		mOutputItems    = new ItemStack[Math.max(0, aOutputSlotCount)];
		mAmperage       = aAmperage;
		mGUIName        = aGUIName;
		mNEIName        = aNEIName;
		mPower          = buildPower();
	}
	
	public BasicMachineBase(int id, String name, String aNameRegional, int aTier, int aAmperage, String[] aDescription, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName, ITexture... aOverlays) {
		super(id, name, aNameRegional, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aOverlays);
		mInputSlotCount = Math.max(0, aInputSlotCount);
		mOutputItems    = new ItemStack[Math.max(0, aOutputSlotCount)];
		mAmperage       = aAmperage;
		mGUIName        = aGUIName;
		mNEIName        = aNEIName;
		mPower          = buildPower();
	}
	
	public BasicMachineBase(String name, int aTier, int aAmperage, String aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName) {
		super(name, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aTextures);
		mInputSlotCount = Math.max(0, aInputSlotCount);
		mOutputItems    = new ItemStack[Math.max(0, aOutputSlotCount)];
		mAmperage       = aAmperage;
		mGUIName        = aGUIName;
		mNEIName        = aNEIName;
		mPower          = buildPower();
	}
	
	public BasicMachineBase(String name, int aTier, int aAmperage, String[] aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName) {
		super(name, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aTextures);
		mInputSlotCount = Math.max(0, aInputSlotCount);
		mOutputItems    = new ItemStack[Math.max(0, aOutputSlotCount)];
		mAmperage       = aAmperage;
		mGUIName        = aGUIName;
		mNEIName        = aNEIName;
		mPower          = buildPower();
	}
	
	public static boolean isValidForLowGravity(Recipe tRecipe, int dimId) {
		return //TODO check or get a better solution
				DimensionManager.getProvider(dimId).getClass().getName().contains("Orbit") || DimensionManager.getProvider(dimId).getClass().getName().endsWith("Space") || DimensionManager.getProvider(dimId).getClass().getName().endsWith("Asteroids") || DimensionManager.getProvider(dimId).getClass().getName().endsWith("SS") || DimensionManager.getProvider(dimId).getClass().getName().contains("SpaceStation");
	}
	
	/**
	 * To be called by the constructor to initialize this instance's Power
	 */
	protected Power buildPower() {
		return new BasicMachineEUPower(mTier, mAmperage);
	}
	
	protected boolean isValidMainFacing(int side) {
		return side > 1;
	}
	
	public boolean setMainFacing(int side) {
		if (!isValidMainFacing(side)) return false;
		mMainFacing = side;
		if (getBaseMetaTileEntity().getFrontFace() == mMainFacing) {
			getBaseMetaTileEntity().setFrontFace(getOppositeSide(side));
		}
		onFacingChange();
		onMachineBlockUpdate();
		return true;
	}

//	@Override
//	public Cleanroom getCallbackBase() {
//		return this.mCleanroom;
//	}
//
//	@Override
//	public void setCallbackBase(Cleanroom callback) {
//		this.mCleanroom = callback;
//	}
//
//	@Override
//	public Class<Cleanroom> getType() {
//		return Cleanroom.class;
//	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (allowSelectCircuit() && index == getCircuitSlot() && stack != null && stack.stackSize != 0) stack = copyAmount(0, stack);
		super.setInventorySlotContents(index, stack);
	}
	
	@Override
	public ITexture[][][] getTextureSet(ITexture[] aTextures) {
		ITexture[][][] rTextures = new ITexture[14][17][];
		aTextures = Arrays.copyOf(aTextures, 14);
		
		for (int i = 0; i < aTextures.length; i++)
			if (aTextures[i] != null) {
				for (int c = -1; c < 16; c++) {
					if (rTextures[i][c + 1] == null) rTextures[i][c + 1] = new ITexture[]{MACHINE_CASINGS[mTier][c + 1], aTextures[i]};
				}
			}
		
		for (int c = -1; c < 16; c++) {
			if (rTextures[0][c + 1] == null) rTextures[0][c + 1] = getSideFacingActive(c);
			if (rTextures[1][c + 1] == null) rTextures[1][c + 1] = getSideFacingInactive(c);
			if (rTextures[2][c + 1] == null) rTextures[2][c + 1] = getFrontFacingActive(c);
			if (rTextures[3][c + 1] == null) rTextures[3][c + 1] = getFrontFacingInactive(c);
			if (rTextures[4][c + 1] == null) rTextures[4][c + 1] = getTopFacingActive(c);
			if (rTextures[5][c + 1] == null) rTextures[5][c + 1] = getTopFacingInactive(c);
			if (rTextures[6][c + 1] == null) rTextures[6][c + 1] = getBottomFacingActive(c);
			if (rTextures[7][c + 1] == null) rTextures[7][c + 1] = getBottomFacingInactive(c);
			if (rTextures[8][c + 1] == null) rTextures[8][c + 1] = getBottomFacingPipeActive(c);
			if (rTextures[9][c + 1] == null) rTextures[9][c + 1] = getBottomFacingPipeInactive(c);
			if (rTextures[10][c + 1] == null) rTextures[10][c + 1] = getTopFacingPipeActive(c);
			if (rTextures[11][c + 1] == null) rTextures[11][c + 1] = getTopFacingPipeInactive(c);
			if (rTextures[12][c + 1] == null) rTextures[12][c + 1] = getSideFacingPipeActive(c);
			if (rTextures[13][c + 1] == null) rTextures[13][c + 1] = getSideFacingPipeInactive(c);
		}
		return rTextures;
	}
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int face, int aColorIndex, boolean active, boolean aRedstone) {
		return mTextures[mMainFacing < 2 ? side == face ? active ? 2 : 3 : side == 0 ? active ? 6 : 7 : side == 1 ? active ? 4 : 5 : active ? 0 : 1 : side == mMainFacing ? active ? 2 : 3 : (showPipeFacing() && side == face) ? side == 0 ? active ? 8 : 9 : side == 1 ? active ? 10 : 11 : active ? 12 : 13 : side == 0 ? active ? 6 : 7 : side == 1 ? active ? 4 : 5 : active ? 0 : 1][aColorIndex + 1];
	}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
	}
	
	@Override
	public boolean isOverclockerUpgradable() {
		return false;
	}
	
	@Override
	public boolean isTransformerUpgradable() {
		return false;
	}
	
	@Override
	public boolean isElectric() {
		return true;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return index > 0 && super.isValidSlot(index) && index != getCircuitSlot() && index != OTHER_SLOT_COUNT + mInputSlotCount + mOutputItems.length;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return mMainFacing > 1 || face > 1;
	}
	
	@Override
	public boolean isEnetInput() {
		return true;
	}
	
	@Override
	public boolean isInputFacing(int side) {
		return side != mMainFacing;
	}
	
	@Override
	public boolean isOutputFacing(int side) {
		return false;
	}
	
	@Override
	public boolean isTeleporterCompatible() {
		return false;
	}
	
	@Override
	public boolean isLiquidInput(int side) {
		return side != mMainFacing && (mAllowInputFromOutputSide || side != getBaseMetaTileEntity().getFrontFace());
	}
	
	@Override
	public boolean isLiquidOutput(int side) {
		return side != mMainFacing;
	}
	
	@Override
	public long getMinimumStoredEU() {
		return V[mTier] * 16L;
	}
	
	@Override
	public long maxEUStore() {
		return V[mTier] * 64L;
	}
	
	@Override
	public long maxEUInput() {
		return V[mTier];
	}
	
	@Override
	public long maxSteamStore() {
		return maxEUStore();
	}
	
	@Override
	public long maxAmperesIn() {
		return ((long) mEUt * 2L) / V[mTier] + 1L;
	}
	
	@Override
	public int getInputSlot() {
		return OTHER_SLOT_COUNT;
	}
	
	@Override
	public int getOutputSlot() {
		return OTHER_SLOT_COUNT + mInputSlotCount;
	}
	
	@Override
	public int getStackDisplaySlot() {
		return 2;
	}
	
	@Override
	public int rechargerSlotStartIndex() {
		return 1;
	}
	
	@Override
	public int dechargerSlotStartIndex() {
		return 1;
	}
	
	@Override
	public int rechargerSlotCount() {
		return mCharge ? 1 : 0;
	}
	
	@Override
	public int dechargerSlotCount() {
		return mDecharge ? 1 : 0;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public int getProgresstime() {
		return mProgresstime;
	}
	
	@Override
	public int maxProgresstime() {
		return mMaxProgresstime;
	}
	
	@Override
	public int increaseProgress(int aProgress) {
		mProgresstime += aProgress;
		return mMaxProgresstime - mProgresstime;
	}
	
	@Override
	public boolean isFluidInputAllowed(FluidStack aFluid) {
		return getFillableStack() != null || (getRecipeList() != null && getRecipeList().containsInput(aFluid));
	}
	
	@Override
	public boolean isFluidChangingAllowed() {
		return true;
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
		return true;
	}
	
	@Override
	public FluidStack getDisplayedFluid() {
		return displaysOutputFluid() ? getDrainableStack() : null;
	}
	
	@Override
	public FluidStack getDrainableStack() {
		return mFluidOut;
	}
	
	@Override
	public FluidStack setDrainableStack(FluidStack aFluid) {
		markDirty();
		mFluidOut = aFluid;
		return mFluidOut;
	}
	
	@Override
	public boolean isDrainableStackSeparate() {
		return true;
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		if (baseTile.isClientSide()) return true;
		if (!API.mForceFreeFace) {
			baseTile.openGUI(player);
			return true;
		}
		for (int i = 0; i < 6; i++) {
			if (baseTile.getAirAtSide(i)) {
				baseTile.openGUI(player);
				return true;
			}
		}
		sendChat(player, "No free Side!");
		return true;
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
//		return new Container_BasicMachine(aPlayerInventory, baseTile);
		return null;
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
//		return new GUIContainer_BasicMachine(aPlayerInventory, baseTile, getLocalName(),
//				mGUIName, isStringValid(mNEIName) ? mNEIName : getRecipeList() != null ? getRecipeList().mUnlocalizedName : "");
		return null;
	}
	
	@Override
	public void initDefaultModes(NBTTagCompound nbt) {
		mMainFacing = -1;
		if (!getBaseMetaTileEntity().getWorld().isRemote) {
			SA_ClientPreference tPreference = proxy.getClientPreference(getBaseMetaTileEntity().getOwnerUuid());
			if (tPreference != null) {
				mDisableFilter     = !tPreference.isSingleBlockInitialFilterEnabled();
				mDisableMultiStack = !tPreference.isSingleBlockInitialMultiStackEnabled();
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("mFluidTransfer", mFluidTransfer);
		nbt.setBoolean("mItemTransfer", mItemTransfer);
		nbt.setBoolean("mHasBeenUpdated", mHasBeenUpdated);
		nbt.setBoolean("mAllowInputFromOutputSide", mAllowInputFromOutputSide);
		nbt.setBoolean("mDisableFilter", mDisableFilter);
		nbt.setBoolean("mDisableMultiStack", mDisableMultiStack);
		nbt.setInteger("mEUt", mEUt);
		nbt.setInteger("mMainFacing", mMainFacing);
		nbt.setInteger("mProgresstime", mProgresstime);
		nbt.setInteger("mMaxProgresstime", mMaxProgresstime);
		if (mOutputFluid != null) nbt.setTag("mOutputFluid", mOutputFluid.writeToNBT(new NBTTagCompound()));
		if (mFluidOut != null) nbt.setTag("mFluidOut", mFluidOut.writeToNBT(new NBTTagCompound()));
		
		for (int i = 0; i < mOutputItems.length; i++)
			if (mOutputItems[i] != null) nbt.setTag("mOutputItem" + i, mOutputItems[i].writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		mFluidTransfer            = nbt.getBoolean("mFluidTransfer");
		mItemTransfer             = nbt.getBoolean("mItemTransfer");
		mHasBeenUpdated           = nbt.getBoolean("mHasBeenUpdated");
		mAllowInputFromOutputSide = nbt.getBoolean("mAllowInputFromOutputSide");
		mDisableFilter            = nbt.getBoolean("mDisableFilter");
		mDisableMultiStack        = nbt.getBoolean("mDisableMultiStack");
		mEUt                      = nbt.getInteger("mEUt");
		mMainFacing               = nbt.getInteger("mMainFacing");
		mProgresstime             = nbt.getInteger("mProgresstime");
		mMaxProgresstime          = nbt.getInteger("mMaxProgresstime");
		mOutputFluid              = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mOutputFluid"));
		mFluidOut                 = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mFluidOut"));
		
		for (int i = 0; i < mOutputItems.length; i++) mOutputItems[i] = loadItem(nbt, "mOutputItem" + i);
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		
		if (baseTile.isServerSide()) {
			mCharge   = baseTile.getStoredEU() / 2 > baseTile.getEUCapacity() / 3;
			mDecharge = baseTile.getStoredEU() < baseTile.getEUCapacity() / 3;
			
			doDisplayThings();
			
			boolean tSucceeded = false;
			
			if (mMaxProgresstime > 0 && (mProgresstime >= 0 || baseTile.isAllowedToWork())) {
				markDirty();
				baseTile.setActive(true);
				if (mProgresstime < 0 || drainEnergyForProcess(mEUt)) {
					if (++mProgresstime >= mMaxProgresstime) {
						for (int i = 0; i < mOutputItems.length; i++)
							for (int j = 0; j < mOutputItems.length; j++)
								if (baseTile.addStackToSlot(getOutputSlot() + ((j + i) % mOutputItems.length), mOutputItems[i])) break;
						if (mOutputFluid != null) if (getDrainableStack() == null) setDrainableStack(mOutputFluid.copy());
						else if (mOutputFluid.isFluidEqual(getDrainableStack())) getDrainableStack().amount += mOutputFluid.amount;
						Arrays.fill(mOutputItems, null);
						mOutputFluid     = null;
						mEUt             = 0;
						mProgresstime    = 0;
						mMaxProgresstime = 0;
						mStuttering      = false;
						tSucceeded       = true;
						endProcess();
					}
					if (mProgresstime > 5) mStuttering = false;
					//XSTR aXSTR = new XSTR();
					//if(API.mAprilFool && aXSTR.nextInt(5000)==0)sendSoundToPlayers(baseTile.getWorld(), API.sSoundList.get(Sounds.EXPLODE), 10.0F, -1.0F, baseTile.getXCoord(), baseTile.getYCoord(),baseTile.getZCoord());
				} else {
					if (!mStuttering) {
						stutterProcess();
						if (canHaveInsufficientEnergy()) mProgresstime = -100;
						mStuttering = true;
					}
				}
			} else {
				baseTile.setActive(false);
			}
			
			boolean tRemovedOutputFluid = false;
			
			if (doesAutoOutputFluids() && getDrainableStack() != null && baseTile.getFrontFace() != mMainFacing && (tSucceeded || tick % 20 == 0)) {
				IFluidHandler tTank = baseTile.getITankContainerAtSide(baseTile.getFrontFace());
				if (tTank != null) {
					FluidStack tDrained = drain(1000, false);
					if (tDrained != null) {
						int tFilledAmount = tTank.fill(ForgeDirection.getOrientation(baseTile.getBackFace()), tDrained, false);
						if (tFilledAmount > 0) tTank.fill(ForgeDirection.getOrientation(baseTile.getBackFace()), drain(tFilledAmount, true), true);
					}
				}
				if (getDrainableStack() == null) tRemovedOutputFluid = true;
			}
			
			if (doesAutoOutput() && !isOutputEmpty() && baseTile.getFrontFace() != mMainFacing && (tSucceeded || mOutputBlocked % 300 == 1 || baseTile.hasInventoryBeenModified() || tick % 600 == 0)) {
				TileEntity tTileEntity2 = baseTile.getTileEntityAtSide(baseTile.getFrontFace());
				long tStoredEnergy = baseTile.getUniversalEnergyStored();
				int tMaxStacks = (int) (tStoredEnergy / 64L);
				if (tMaxStacks > mOutputItems.length) tMaxStacks = mOutputItems.length;
				
				moveMultipleItemStacks(baseTile, tTileEntity2, baseTile.getFrontFace(), baseTile.getBackFace(), null, false, 64, 1, 64, 1, tMaxStacks);
//                for (int i = 0, tCosts = 1; i < mOutputItems.length && tCosts > 0 && baseTile.isUniversalEnergyStored(128); i++) {
//                    tCosts = moveOneItemStack(baseTile, tTileEntity2, baseTile.getFrontFacing(), baseTile.getBackFacing(), null, false, 64, 1, 64, 1);
//                    if (tCosts > 0) baseTile.decreaseStoredEnergyUnits(tCosts, true);
//                }
			}
			
			if (mOutputBlocked != 0) if (isOutputEmpty()) mOutputBlocked = 0;
			else mOutputBlocked++;
			
			if (allowToCheckRecipe()) {
				if (mMaxProgresstime <= 0 && baseTile.isAllowedToWork() && (tRemovedOutputFluid || tSucceeded || baseTile.hasInventoryBeenModified() || tick % 600 == 0 || baseTile.hasWorkJustBeenEnabled()) && hasEnoughEnergyToCheckRecipe()) {
					if (checkRecipe() == FOUND_AND_SUCCESSFULLY_USED_RECIPE) {
						if (mInventory[3] != null && mInventory[3].stackSize <= 0) mInventory[3] = null;
						for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++)
							if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
						for (int i = 0; i < mOutputItems.length; i++) {
							mOutputItems[i] = copyOrNull(mOutputItems[i]);
							if (mOutputItems[i] != null && mOutputItems[i].stackSize > 64) mOutputItems[i].stackSize = 64;
							mOutputItems[i] = OreDictUnifier.get(true, mOutputItems[i]);
						}
						if (mFluid != null && mFluid.amount <= 0) mFluid = null;
						mMaxProgresstime = Math.max(1, mMaxProgresstime);
//						if (isDebugItem(mInventory[dechargerSlotStartIndex()])) {
//							mEUt = mMaxProgresstime = 1;
//						}
						startProcess();
					} else {
						mMaxProgresstime = 0;
						Arrays.fill(mOutputItems, null);
						mOutputFluid = null;
					}
				}
			} else {
				if (!mStuttering) {
					stutterProcess();
					mStuttering = true;
				}
			}
		}
		baseTile.setErrorDisplayID((baseTile.getErrorDisplayID() & ~127));// | (mStuttering ? 1 : 0));
	}
	
	protected void doDisplayThings() {
		if (mMainFacing < 2 && getBaseMetaTileEntity().getFrontFace() > 1) {
			mMainFacing = getBaseMetaTileEntity().getFrontFace();
		}
		if (mMainFacing >= 2 && !mHasBeenUpdated) {
			mHasBeenUpdated = true;
			getBaseMetaTileEntity().setFrontFace(getBaseMetaTileEntity().getBackFace());
		}
	}
	
	@Override
	public void updateFluidDisplayItem() {
		super.updateFluidDisplayItem();
		if (displaysInputFluid()) {
			int tDisplayStackSlot = OTHER_SLOT_COUNT + mInputSlotCount + mOutputItems.length;
			if (getFillableStack() == null) {
				if (ItemList.Display_Fluid.isStackEqual(mInventory[tDisplayStackSlot], true, true))
					mInventory[tDisplayStackSlot] = null;
			} else {
				mInventory[tDisplayStackSlot] = getFluidDisplayStack(getFillableStack(), true, !displaysStackSize());
			}
		}
	}
	
	protected boolean hasEnoughEnergyToCheckRecipe() {
		return getBaseMetaTileEntity().isUniversalEnergyStored(getMinimumStoredEU() / 2);
	}
	
	protected boolean drainEnergyForProcess(long aEUt) {
		return getBaseMetaTileEntity().decreaseStoredEnergyUnits(aEUt, false);
	}
	
	protected void calculateOverclockedNess(Recipe aRecipe) {
		calculateOverclockedNess(aRecipe.mEUt, aRecipe.mDuration);
	}
	
	/**
	 * Calcualtes overclocked ness using long integers
	 *
	 * @param aEUt      - recipe EUt
	 * @param aDuration - recipe Duration
	 */
	protected void calculateOverclockedNess(int aEUt, int aDuration) {
		mPower.computePowerUsageAndDuration(aEUt, aDuration);
		mEUt             = mPower.getEuPerTick();
		mMaxProgresstime = mPower.getDurationTicks();
	}
	
	protected ItemStack getSpecialSlot() {
		return mInventory[3];
	}
	
	protected ItemStack getOutputAt(int index) {
		return mInventory[getOutputSlot() + index];
	}
	
	protected ItemStack[] getAllOutputs() {
		ItemStack[] rOutputs = new ItemStack[mOutputItems.length];
		for (int i = 0; i < mOutputItems.length; i++) rOutputs[i] = getOutputAt(i);
		return rOutputs;
	}
	
	protected boolean canOutput(Recipe aRecipe) {
		return aRecipe != null && (aRecipe.mNeedsEmptyOutput ? isOutputEmpty() && getDrainableStack() == null : canOutput(aRecipe.getFluidOutput(0)) && canOutput(aRecipe.mOutputs));
	}
	
	protected boolean canOutput(ItemStack... aOutputs) {
		if (aOutputs == null) return true;
		ItemStack[] tOutputSlots = getAllOutputs();
		for (int i = 0; i < tOutputSlots.length && i < aOutputs.length; i++)
			if (tOutputSlots[i] != null && aOutputs[i] != null && (!areStacksEqual(tOutputSlots[i], aOutputs[i], false) || tOutputSlots[i].stackSize + aOutputs[i].stackSize > tOutputSlots[i].getMaxStackSize())) {
				mOutputBlocked++;
				return false;
			}
		return true;
	}
	
	protected boolean canOutput(FluidStack aOutput) {
		return getDrainableStack() == null || aOutput == null || (getDrainableStack().isFluidEqual(aOutput) && (getDrainableStack().amount <= 0 || getDrainableStack().amount + aOutput.amount <= getCapacity()));
	}
	
	protected ItemStack getInputAt(int index) {
		return mInventory[getInputSlot() + index];
	}
	
	protected ItemStack[] getAllInputs() {
		int tRealInputSlotCount = this.mInputSlotCount + (allowSelectCircuit() ? 1 : 0);
		ItemStack[] rInputs = new ItemStack[tRealInputSlotCount];
		for (int i = 0; i < mInputSlotCount; i++) rInputs[i] = getInputAt(i);
		if (allowSelectCircuit()) rInputs[mInputSlotCount] = getStackInSlot(getCircuitSlot());
		return rInputs;
	}
	
	protected boolean isOutputEmpty() {
		boolean rIsEmpty = true;
		for (ItemStack tOutputSlotContent : getAllOutputs())
			if (tOutputSlotContent != null) {
				rIsEmpty = false;
				break;
			}
		return rIsEmpty;
	}
	
	protected boolean displaysInputFluid() {
		return true;
	}
	
	protected boolean displaysOutputFluid() {
		return true;
	}
	
	@Override
	public void onValueUpdate(int value) {
		mMainFacing = value;
	}
	
	@Override
	public int getUpdateData() {
		return mMainFacing;
	}
	
	@Override
	public void doSound(int index, double x, double y, double z) {
		super.doSound(index, x, y, z);
		if (index == 8) doSoundAtClient(sSoundList.get(210), 100, 1.0F, x, y, z);
	}
	
	public boolean doesAutoOutput() {
		return mItemTransfer;
	}
	
	public boolean doesAutoOutputFluids() {
		return mFluidTransfer;
	}
	
	public boolean allowToCheckRecipe() {
		return true;
	}
	
	public boolean showPipeFacing() {
		return true;
	}
	
	/**
	 * Called whenever the Machine successfully started a Process, useful for Sound Effects
	 */
	public void startProcess() {
		//
	}
	
	/**
	 * Called whenever the Machine successfully finished a Process, useful for Sound Effects
	 */
	public void endProcess() {
		//
	}
	
	/**
	 * Called whenever the Machine aborted a Process, useful for Sound Effects
	 */
	public void abortProcess() {
		//
	}
	
	/**
	 * Called whenever the Machine aborted a Process but still works on it, useful for Sound Effects
	 */
	public void stutterProcess() {
		if (useStandardStutterSound()) sendSound(8);
	}
	
	/**
	 * If this Machine can have the Insufficient Energy Line Problem
	 */
	public boolean canHaveInsufficientEnergy() {
		return true;
	}
	
	public boolean useStandardStutterSound() {
		return true;
	}
	
	@Override
	public String[] getInfoData() {
		return new String[]{EnumChatFormatting.BLUE + mNEIName + EnumChatFormatting.RESET, "Progress:", EnumChatFormatting.GREEN + format((mProgresstime / 20)) + EnumChatFormatting.RESET + " s / " + EnumChatFormatting.YELLOW + format(mMaxProgresstime / 20) + EnumChatFormatting.RESET + " s", "Stored Energy:", EnumChatFormatting.GREEN + format(getBaseMetaTileEntity().getStoredEU()) + EnumChatFormatting.RESET + " EU / " + EnumChatFormatting.YELLOW + format(getBaseMetaTileEntity().getEUCapacity()) + EnumChatFormatting.RESET + " EU", "Probably uses: " + EnumChatFormatting.RED + format(mEUt) + EnumChatFormatting.RESET + " EU/t at " + EnumChatFormatting.RED + format(mEUt == 0 ? 0 : mAmperage) + EnumChatFormatting.RESET + " A"};
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
	@Override
	public void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z) {
		if (side == getBaseMetaTileEntity().getFrontFace() || side == mMainFacing) {
			if (player.isSneaking()) {
				mDisableFilter = !mDisableFilter;
				sendChat(player, StatCollector.translateToLocal("hatch.disableFilter." + mDisableFilter));
			} else {
				mAllowInputFromOutputSide = !mAllowInputFromOutputSide;
				sendChat(player, mAllowInputFromOutputSide ? trans("095", "Input from Output Side allowed") : trans("096", "Input from Output Side forbidden"));
			}
		}
	}
	
	@Override
	public boolean onSolderingToolRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (!player.isSneaking()) return false;
		boolean click = super.onSolderingToolRightClick(side, aWrenchingSide, player, x, y, z);
		if (click) return true;
		if (aWrenchingSide != mMainFacing) return false;
		mDisableMultiStack = !mDisableMultiStack;
		sendChat(player, StatCollector.translateToLocal("hatch.disableMultiStack." + mDisableMultiStack));
		return true;
	}
	
	@Override
	public boolean allowCoverOnSide(int side, ItemStackData coverId) {
		if (side != mMainFacing) return true;
		SA_CoverBehaviorBase<?> tBehavior = getCoverBehaviorNew(coverId.toStack());
		return tBehavior.isGUIClickable(side, stackToInt(coverId.toStack()), tBehavior.createDataObject(), getBaseMetaTileEntity());
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side != mMainFacing && index >= getOutputSlot() && index < getOutputSlot() + mOutputItems.length;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		if (side == mMainFacing || index < getInputSlot() || index >= getInputSlot() + mInputSlotCount || (!mAllowInputFromOutputSide && side == baseTile.getFrontFace())) return false;
		for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++)
			if (areStacksEqual(OreDictUnifier.get(stack), mInventory[i]) && mDisableMultiStack) return i == index;
		return mDisableFilter || allowPutStackValidated(baseTile, index, side, stack);
	}
	
	/**
	 * Test if given stack can be inserted into specified slot.
	 * If mDisableMultiStack is false, before execution of this method it is ensured there is no such kind of item inside any input slots already.
	 * Otherwise, you don't need to check for it anyway.
	 */
	protected boolean allowPutStackValidated(ITile baseTile, int index, int side, ItemStack stack) {
		return !mDisableMultiStack || mInventory[index] == null;
	}
	
	public boolean allowSelectCircuit() {
		return false;
	}
	
	protected final ItemStack[] appendSelectedCircuit(ItemStack... inputs) {
		if (allowSelectCircuit()) {
			ItemStack circuit = getStackInSlot(getCircuitSlot());
			if (circuit != null) {
				ItemStack[] result = Arrays.copyOf(inputs, inputs.length + 1);
				result[inputs.length] = circuit;
				return result;
			}
		}
		return inputs;
	}
	
	/**
	 * This might be non-final in the future, but for now, no, don't change this.
	 */
	public final int getCircuitSlot() {
		return 4;
	}
	
	/**
	 * @return the Recipe List which is used for this Machine, this is a useful Default Handler
	 */
	public RecipeMap getRecipeList() {
		return null;
	}
	
	/**
	 * Override this to check the Recipes yourself, super calls to this could be useful if you just want to add a special case
	 * <p/>
	 * I thought about Enum too, but Enum doesn't add support for people adding other return Systems.
	 * <p/>
	 * Funny how Eclipse marks the word Enum as not correctly spelled.
	 *
	 * @return see constants above
	 */
	public int checkRecipe() {
		return checkRecipe(false);
	}
	
	/**
	 * @param skipOC disables OverclockedNess calculation and check - if you do you must implement your own method...
	 * @return DID_NOT_FIND_RECIPE = 0,
	 * FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1,
	 * FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2;
	 */
	public int checkRecipe(boolean skipOC) {
		RecipeMap tMap = getRecipeList();
		if (tMap == null) return DID_NOT_FIND_RECIPE;
		Recipe tRecipe = tMap.findRecipe(getBaseMetaTileEntity(), mLastRecipe, false, V[mTier], new FluidStack[]{getFillableStack()}, getSpecialSlot(), getAllInputs());
		if (tRecipe == null) return DID_NOT_FIND_RECIPE;
		
		if ((tRecipe.mSpecialValue == -100 || tRecipe.mSpecialValue == -300) && !isValidForLowGravity(tRecipe, getBaseMetaTileEntity().getWorld().provider.dimensionId))
			return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
		if (tRecipe.mCanBeBuffered) mLastRecipe = tRecipe;
		if (!canOutput(tRecipe)) {
			mOutputBlocked++;
			return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
		}
//		if (tRecipe.mSpecialValue == -200 && (getCallbackBase() == null || getCallbackBase().mEfficiency == 0))
//			return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
		if (!tRecipe.isRecipeInputEqual(true, new FluidStack[]{getFillableStack()}, getAllInputs())) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
		for (int i = 0; i < mOutputItems.length; i++)
			if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(i)) mOutputItems[i] = tRecipe.getOutput(i);
//		if (tRecipe.mSpecialValue == -200 || tRecipe.mSpecialValue == -300)
//			for (int i = 0; i < mOutputItems.length; i++)
//				if (mOutputItems[i] != null && getBaseMetaTileEntity().getRandomNumber(10000) > getCallbackBase().mEfficiency) {
//					mOutputItems[i] = null;
//				}
		mOutputFluid = tRecipe.getFluidOutput(0);
		if (!skipOC) {
			calculateOverclockedNess(tRecipe);
			//In case recipe is too OP for that machine
			if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
		}
		return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
	}
	
	public ITexture[] getSideFacingActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getSideFacingInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getFrontFacingActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getFrontFacingInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getTopFacingActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getTopFacingInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getBottomFacingActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getBottomFacingInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1]};
	}
	
	public ITexture[] getBottomFacingPipeActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	public ITexture[] getBottomFacingPipeInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	public ITexture[] getTopFacingPipeActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	public ITexture[] getTopFacingPipeInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	public ITexture[] getSideFacingPipeActive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	public ITexture[] getSideFacingPipeInactive(int color) {
		return new ITexture[]{MACHINE_CASINGS[mTier][color + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		final NBTTagCompound tag = accessor.getNBTData();
		
		currentTip.add(String.format("Progress: %d s / %d s", tag.getInteger("progressSingleBlock"), tag.getInteger("maxProgressSingleBlock")));
		super.getWailaBody(itemStack, currentTip, accessor, config);
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		super.getWailaNBTData(player, tile, tag, world, x, y, z);
		
		tag.setInteger("progressSingleBlock", mProgresstime / 20);
		tag.setInteger("maxProgressSingleBlock", mMaxProgresstime / 20);
	}
	
	public Power getPower() {
		return mPower;
	}
}
