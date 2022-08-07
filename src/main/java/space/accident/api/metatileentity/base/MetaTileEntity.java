package space.accident.api.metatileentity.base;


import appeng.api.util.AECableType;
import appeng.me.helpers.AENetworkProxy;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import space.accident.api.API;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.implementations.logistic.Cable_Electricity;
import space.accident.api.objects.ItemStackData;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.ModHandler;
import space.accident.api.util.SpaceLog;
import space.accident.api.util.Utility;
import space.accident.extensions.NumberUtils;
import space.accident.main.events.ClientEvents;
import space.accident.main.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static space.accident.api.util.Utility.getStrength;

@SuppressWarnings("unused")
public abstract class MetaTileEntity implements IMetaTile {
	/**
	 * Only assigned for the MetaTileEntity in the List! Also only used to get the localized Name for the ItemStack and for getInvName.
	 */
	public final String mName;
	/**
	 * The Inventory of the MetaTileEntity. Amount of Slots can be larger than 256. HAYO!
	 */
	public final ItemStack[] mInventory;
	public boolean doTickProfilingInThisTick = true;
	public long mSoundRequests = 0;
	/**
	 * accessibility to this Field is no longer given, see below
	 */
	private ITile mBaseMetaTileEntity;
	
	public MetaTileEntity(int id, String aBasicName, String aRegionalName, int aInvSlotCount) {
		if (API.sPostloadStarted || !API.sPreloadStarted) throw new IllegalAccessError("This Constructor has to be called in the load Phase");
		if (API.METATILEENTITIES[id] == null) {
			API.METATILEENTITIES[id] = this;
		} else {
			throw new IllegalArgumentException("MetaMachine-Slot Nr. " + id + " is already occupied!");
		}
		mName = aBasicName.replace(" ", "_").toLowerCase(Locale.ENGLISH);
		setBaseMetaTileEntity(API.constructBaseMetaTileEntity());
		getBaseMetaTileEntity().setMetaTileID((short) id);
		LanguageManager.addStringLocalization("sa.blockmachines." + mName + ".name", aRegionalName);
		mInventory = new ItemStack[aInvSlotCount];
	}
	
	/**
	 * This is the normal Constructor.
	 */
	public MetaTileEntity(String name, int aInvSlotCount) {
		mInventory = new ItemStack[aInvSlotCount];
		mName      = name;
	}
	
	/**
	 * This method will only be called on client side
	 *
	 * @return whether the secondary description should be display. default is false
	 */
	public boolean isDisplaySecondaryDescription() {
		return false;
	}
	
	@Override
	public ITile getBaseMetaTileEntity() {
		return mBaseMetaTileEntity;
	}
	
	@Override
	public void setBaseMetaTileEntity(ITile baseTile) {
		if (mBaseMetaTileEntity != null && baseTile == null) {
			mBaseMetaTileEntity.getMetaTile().inValidate();
			mBaseMetaTileEntity.setMetaTile(null);
		}
		mBaseMetaTileEntity = baseTile;
		if (mBaseMetaTileEntity != null) {
			mBaseMetaTileEntity.setMetaTile(this);
		}
	}
	
	@Override
	public ItemStack get(long amount) {
		return new ItemStack(API.sBlockMachines, (int) amount, getBaseMetaTileEntity().getMetaTileID());
	}
	
	public String getLocalName() {
		return LanguageManager.getTranslation("sa.blockmachines." + mName + ".name");
	}
	
	@Override
	public void onServerStart() {/*Do nothing*/}
	
	@Override
	public void onWorldSave(File aSaveDirectory) {/*Do nothing*/}
	
	@Override
	public void onWorldLoad(File aSaveDirectory) {/*Do nothing*/}
	
	@Override
	public void onConfigLoad(Config aConfig) {/*Do nothing*/}
	
	@Override
	public void setItemNBT(NBTTagCompound nbt) {/*Do nothing*/}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister aBlockIconRegister) {/*Do nothing*/}
	
	@Override
	public boolean allowCoverOnSide(int side, ItemStackData stack) {
		return true;
	}
	
	@Override
	public void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z) {/*Do nothing*/}
	
	@Override
	public boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (getBaseMetaTileEntity().isValidFace(aWrenchingSide)) {
			getBaseMetaTileEntity().setFrontFace(aWrenchingSide);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onWireCutterRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (!player.isSneaking()) return false;
		int tSide = NumberUtils.getOppositeSide(aWrenchingSide);
		TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(aWrenchingSide);
		if ((tTileEntity instanceof ITile) && (((ITile) tTileEntity).getMetaTile() instanceof Cable_Electricity)) {
			// The tile entity we're facing is a cable, let's try to connect to it
			return ((ITile) tTileEntity).getMetaTile().onWireCutterRightClick(aWrenchingSide, tSide, player, x, y, z);
		}
		return false;
	}
	
	@Override
	public boolean onSolderingToolRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (!player.isSneaking()) return false;
		int tSide = NumberUtils.getOppositeSide(aWrenchingSide);
		TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(aWrenchingSide);
		if ((tTileEntity instanceof ITile) && (((ITile) tTileEntity).getMetaTile() instanceof Cable_Electricity)) {
			// The tile entity we're facing is a cable, let's try to connect to it
			return ((ITile) tTileEntity).getMetaTile().onSolderingToolRightClick(aWrenchingSide, tSide, player, x, y, z);
		}
		return false;
	}
	
	@Override
	public void onExplosion() {
		SpaceLog.exp.println("Machine at " + this.getBaseMetaTileEntity().getX() + " | " + this.getBaseMetaTileEntity().getY() + " | " + this.getBaseMetaTileEntity().getZ() + " DIMID: " + this.getBaseMetaTileEntity().getWorld().provider.dimensionId + " exploded.");
	}
	
	@Override
	public void onFirstTick(ITile baseTile) {/*Do nothing*/}
	
	@Override
	public void onPreTick(ITile baseTile, long aTick) {/*Do nothing*/}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		if (baseTile.isClientSide() && ClientEvents.changeDetected == 4) {
			/* Client tick counter that is set to 5 on hiding pipes and covers.
			 * It triggers a texture update next client tick when reaching 4, with provision for 3 more update tasks,
			 * spreading client change detection related work and network traffic on different ticks, until it reaches 0.
			 */
			baseTile.issueTextureUpdate();
		}
	}
	
	@Override
	public void inValidate() {/*Do nothing*/}
	
	@Override
	public void onRemoval() {/*Do nothing*/}
	
	@Override
	public void initDefaultModes(NBTTagCompound nbt) {/*Do nothing*/}
	
	/**
	 * When a GUI is opened
	 */
	public void onOpenGUI() {/*Do nothing*/}
	
	/**
	 * When a GUI is closed
	 */
	public void onCloseGUI() {/*Do nothing*/}
	
	/**
	 * a Player rightclicks the Machine
	 * Sneaky rightclicks are not getting passed to this!
	 */
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		return false;
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player, int side, float x, float y, float z) {
		return onRightclick(baseTile, player);
	}
	
	@Override
	public void onLeftclick(ITile baseTile, EntityPlayer player) {/*Do nothing*/}
	
	@Override
	public void onValueUpdate(int value) {/*Do nothing*/}
	
	@Override
	public int getUpdateData() {
		return 0;
	}
	
	@Override
	public void doSound(int index, double x, double y, double z) {/*Do nothing*/}
	
	@Override
	public void startSoundLoop(int index, double x, double y, double z) {/*Do nothing*/}
	
	@Override
	public void stopSoundLoop(int value, double x, double y, double z) {/*Do nothing*/}
	
	@Override
	public final void sendSound(int index) {
		if (!getBaseMetaTileEntity().hasMufflerUpgrade()) getBaseMetaTileEntity().sendBlockEvent(ClientEvents.DO_SOUND, index);
	}
	
	@Override
	public final void sendLoopStart(int index) {
		if (!getBaseMetaTileEntity().hasMufflerUpgrade()) getBaseMetaTileEntity().sendBlockEvent(ClientEvents.START_SOUND_LOOP, index);
		mSoundRequests++;
	}
	
	@Override
	public final void sendLoopEnd(int index) {
		if (!getBaseMetaTileEntity().hasMufflerUpgrade()) getBaseMetaTileEntity().sendBlockEvent(ClientEvents.STOP_SOUND_LOOP, index);
	}
	
	/**
	 * @return true if this Device emits Energy at all
	 */
	public boolean isElectric() {
		return true;
	}
	
	/**
	 * @return true if this Device emits Energy at all
	 */
	public boolean isPneumatic() {
		return false;
	}
	
	/**
	 * @return true if this Device emits Energy at all
	 */
	public boolean isSteampowered() {
		return false;
	}
	
	/**
	 * @return true if this Device emits Energy at all
	 */
	public boolean isEnetOutput() {
		return false;
	}
	
	/**
	 * @return true if this Device consumes Energy at all
	 */
	public boolean isEnetInput() {
		return false;
	}
	
	/**
	 * @return the amount of EU, which can be stored in this Device. Default is 0 EU.
	 */
	public long maxEUStore() {
		return 0;
	}
	
	/**
	 * @return the amount of EU/t, which can be accepted by this Device before it explodes.
	 */
	public long maxEUInput() {
		return 0;
	}
	
	/**
	 * @return the amount of EU/t, which can be outputted by this Device.
	 */
	public long maxEUOutput() {
		return 0;
	}
	
	/**
	 * @return the amount of E-net Impulses of the maxEUOutput size, which can be outputted by this Device.
	 * Default is 1 Pulse, this shouldn't be set to smaller Values than 1, as it won't output anything in that Case!
	 */
	public long maxAmperesOut() {
		return 1;
	}
	
	/**
	 * How many Amperes this Block can suck at max. Surpassing this value won't blow it up.
	 */
	public long maxAmperesIn() {
		return 1;
	}
	
	/**
	 * @return true if that Side is an Output.
	 */
	public boolean isOutputFacing(int side) {
		return false;
	}
	
	/**
	 * @return true if that Side is an Input.
	 */
	public boolean isInputFacing(int side) {
		return false;
	}
	
	/**
	 * @return true if Transformer Upgrades increase Packet Amount.
	 */
	public boolean isTransformingLowEnergy() {
		return true;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return false;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return false;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return true;
	}
	
	@Override
	public boolean setStackToZeroInsteadOfNull(int index) {
		return false;
	}
	
	/**
	 * This is used to get the internal Energy. I use this for the IDSU.
	 */
	public long getEUVar() {
		return ((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredEnergy;
	}
	
	/**
	 * This is used to set the internal Energy to the given Parameter. I use this for the IDSU.
	 */
	public void setEUVar(long aEnergy) {
		if (aEnergy != ((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredEnergy) {
			markDirty();
			((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredEnergy = aEnergy;
		}
	}
	
	/**
	 * This is used to get the internal Steam Energy.
	 */
	public long getSteamVar() {
		return ((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredSteam;
	}
	
	/**
	 * This is used to set the internal Steam Energy to the given Parameter.
	 */
	public void setSteamVar(long aSteam) {
		if (((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredSteam != aSteam) {
			markDirty();
			((BaseMetaTileEntity) mBaseMetaTileEntity).mStoredSteam = aSteam;
		}
	}
	
	/**
	 * @return the amount of Steam, which can be stored in this Device. Default is 0 EU.
	 */
	public long maxSteamStore() {
		return 0;
	}
	
	/**
	 * @return the amount of EU, which this Device stores before starting to emit Energy.
	 * useful if you don't want to emit stored Energy until a certain Level is reached.
	 */
	public long getMinimumStoredEU() {
		return 512;
	}
	
	/**
	 * Determines the Tier of the Machine, used for de-charging Tools.
	 */
	public long getInputTier() {
		return NumberUtils.getTier(getBaseMetaTileEntity().getInputVoltage());
	}
	
	/**
	 * Determines the Tier of the Machine, used for charging Tools.
	 */
	public long getOutputTier() {
		return NumberUtils.getTier(getBaseMetaTileEntity().getOutputVoltage());
	}
	
	/**
	 * gets the first RechargerSlot
	 */
	public int rechargerSlotStartIndex() {
		return 0;
	}
	
	/**
	 * gets the amount of RechargerSlots
	 */
	public int rechargerSlotCount() {
		return 0;
	}
	
	/**
	 * gets the first DechargerSlot
	 */
	public int dechargerSlotStartIndex() {
		return 0;
	}
	
	/**
	 * gets the amount of DechargerSlots
	 */
	public int dechargerSlotCount() {
		return 0;
	}
	
	/**
	 * gets if this is protected from other Players per default or not
	 */
	public boolean ownerControl() {
		return false;
	}
	
	@Override
	public ArrayList<String> getSpecialDebugInfo(ITile baseTile, EntityPlayer player, int logLevel, ArrayList<String> aList) {
		return aList;
	}
	
	@Override
	public boolean isLiquidInput(int side) {
		return true;
	}
	
	@Override
	public boolean isLiquidOutput(int side) {
		return true;
	}
	
	/**
	 * gets the contained Liquid
	 */
	@Override
	public FluidStack getFluid() {
		return null;
	}
	
	/**
	 * tries to fill this Tank
	 */
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
	}
	
	/**
	 * tries to empty this Tank
	 */
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}
	
	/**
	 * Tank pressure
	 */
	public int getTankPressure() {
		return 0;
	}
	
	/**
	 * Liquid Capacity
	 */
	@Override
	public int getCapacity() {
		return 0;
	}
	
	@Override
	public void onMachineBlockUpdate() {/*Do nothing*/}
	
	@Override
	public void receiveClientEvent(int aEventID, int value) {/*Do nothing*/}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
	}
	
	/**
	 * If this accepts up to 4 Overclockers
	 */
	public boolean isOverclockerUpgradable() {
		return false;
	}
	
	/**
	 * If this accepts Transformer Upgrades
	 */
	public boolean isTransformerUpgradable() {
		return false;
	}
	
	/**
	 * Progress this machine has already made
	 */
	public int getProgresstime() {
		return 0;
	}
	
	/**
	 * Progress this Machine has to do to produce something
	 */
	public int maxProgresstime() {
		return 0;
	}
	
	/**
	 * Increases the Progress, returns the overflown Progress.
	 */
	public int increaseProgress(int aProgress) {
		return 0;
	}
	
	/**
	 * If this TileEntity makes use of Sided RedStone behaviors.
	 * Determines only, if the Output RedStone Array is getting filled with 0 for true, or 15 for false.
	 */
	public boolean hasSidedRedstoneOutputBehavior() {
		return false;
	}
	
	/**
	 * When the Facing gets changed.
	 */
	public void onFacingChange() {/*Do nothing*/}
	
	/**
	 * if the IC2 Teleporter can drain from this.
	 */
	public boolean isTeleporterCompatible() {
		return isEnetOutput() && getBaseMetaTileEntity().getOutputVoltage() >= 128 && getBaseMetaTileEntity().getUniversalEnergyCapacity() >= 500000;
	}
	
	/**
	 * Gets the Output for the comparator on the given Side
	 */
	@Override
	public int getComparatorValue(int side) {
		return 0;
	}
	
	@Override
	public boolean acceptsRotationalEnergy(int side) {
		return false;
	}
	
	@Override
	public boolean injectRotationalEnergy(int side, long aSpeed, long aEnergy) {
		return false;
	}
	
	@Override
	public String getSpecialVoltageToolTip() {
		return null;
	}
	
	@Override
	public boolean isGivingInformation() {
		return false;
	}
	
	@Override
	public String[] getInfoData() {
		return new String[]{};
	}
	
	public boolean isDigitalChest() {
		return false;
	}
	
	public ItemStack[] getStoredItemData() {
		return null;
	}
	
	public void setItemCount(int count) {/*Do nothing*/}
	
	public int getMaxItemCount() {
		return 0;
	}
	
	@Override
	public int getSizeInventory() {
		return mInventory.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		if (index >= 0 && index < mInventory.length) return mInventory[index];
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		markDirty();
		if (index >= 0 && index < mInventory.length) mInventory[index] = stack;
	}
	
	@Override
	public String getInventoryName() {
		if (API.METATILEENTITIES[getBaseMetaTileEntity().getMetaTileID()] != null) return API.METATILEENTITIES[getBaseMetaTileEntity().getMetaTileID()].getMetaName();
		return "";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return getBaseMetaTileEntity().isValidSlot(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int amount) {
		ItemStack tStack = getStackInSlot(index), rStack = Utility.copyOrNull(tStack);
		if (tStack != null) {
			if (tStack.stackSize <= amount) {
				if (setStackToZeroInsteadOfNull(index)) {
					tStack.stackSize = 0;
					markDirty();
				} else setInventorySlotContents(index, null);
			} else {
				rStack = tStack.splitStack(amount);
				markDirty();
				if (tStack.stackSize == 0 && !setStackToZeroInsteadOfNull(index)) setInventorySlotContents(index, null);
			}
		}
		return rStack;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		TIntList tList = new TIntArrayList();
		ITile tTileEntity = getBaseMetaTileEntity();
		boolean tSkip = tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsIn(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), -2, tTileEntity) || tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsOut(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), -2, tTileEntity);
		for (int i = 0; i < getSizeInventory(); i++)
			if (isValidSlot(i) && (tSkip || tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsOut(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), i, tTileEntity) || tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsIn(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), i, tTileEntity))) tList.add(i);
		return tList.toArray();
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack stack, int side) {
		return isValidSlot(index) && stack != null && index < mInventory.length && (mInventory[index] == null || Utility.areStacksEqual(stack, mInventory[index])) && allowPutStack(getBaseMetaTileEntity(), index, side, stack);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return isValidSlot(index) && stack != null && index < mInventory.length && allowPullStack(getBaseMetaTileEntity(), index, side, stack);
	}
	
	@Override
	public boolean canFill(ForgeDirection side, Fluid aFluid) {
		return fill(side, new FluidStack(aFluid, 1), false) == 1;
	}
	
	@Override
	public boolean canDrain(ForgeDirection side, Fluid aFluid) {
		return drain(side, new FluidStack(aFluid, 1), false) != null;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		if (getCapacity() <= 0) return new FluidTankInfo[]{};
		return new FluidTankInfo[]{getInfo()};
	}
	
	public int fill_default(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		markDirty();
		return fill(aFluid, doFill);
	}
	
	@Override
	public int fill(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		if (ModHandler.isSteam(aFluid) && aFluid.amount > 1) {
			int tSteam = (int) Math.min(Integer.MAX_VALUE, Math.min(aFluid.amount / 2, getBaseMetaTileEntity().getSteamCapacity() - getBaseMetaTileEntity().getStoredSteam()));
			if (tSteam > 0) {
				markDirty();
				if (doFill) getBaseMetaTileEntity().increaseStoredSteam(tSteam, true);
				return tSteam * 2;
			}
		} else {
			return fill_default(side, aFluid, doFill);
		}
		return 0;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
		if (getFluid() != null && aFluid != null && getFluid().isFluidEqual(aFluid)) return drain(aFluid.amount, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}
	
	@Override
	public int getFluidAmount() {
		return 0;
	}
	
	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}
	
	@Override
	public String getMetaName() {
		return mName;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public boolean doTickProfilingMessageDuringThisTick() {
		return doTickProfilingInThisTick;
	}
	
	@Override
	public void markDirty() {
		if (mBaseMetaTileEntity != null) {
			mBaseMetaTileEntity.markDirty();
		}
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}
	
	@Override
	public void openInventory() {
		//
	}
	
	@Override
	public void closeInventory() {
		//
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return null;
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return null;
	}
	
	@Override
	public boolean connectsToItemPipe(int side) {
		return false;
	}
	
	@Override
	public float getExplosionResistance(int side) {
		return 10.0F;
	}
	
	@Override
	public ItemStack[] getRealInventory() {
		return mInventory;
	}
	
	@Override
	public void onColorChangeServer(int color) {
		final ITile meta = getBaseMetaTileEntity();
		final int x = meta.getX(), y = meta.getY(), z = meta.getZ();
		for (int side = 0; side < 6; side++) {
			// Flag surrounding pipes/cables to revaluate their connection with us if we got painted
			final TileEntity tTileEntity = meta.getTileEntityAtSide(side);
			if ((tTileEntity instanceof BaseMetaPipeEntity)) {
				((BaseMetaPipeEntity) tTileEntity).onNeighborBlockChange(x, y, z);
			}
		}
	}
	
	@Override
	public void onColorChangeClient(int color) {
		// Do nothing apparently
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderInInventory(Block block, int aMeta, RenderBlocks aRenderer) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderInWorld(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks aRenderer) {
		return false;
	}
	
	@Override
	public void doExplosion(long aExplosionPower) {
		float tStrength = getStrength(aExplosionPower);
		int tX = getBaseMetaTileEntity().getX(), tY = getBaseMetaTileEntity().getY(), tZ = getBaseMetaTileEntity().getZ();
		World tWorld = getBaseMetaTileEntity().getWorld();
		Utility.sendSoundToPlayers(tWorld, API.sSoundList.get(209), 1.0F, -1, tX, tY, tZ);
		tWorld.setBlock(tX, tY, tZ, Blocks.air);
		if (API.sMachineExplosions) tWorld.createExplosion(null, tX + 0.5, tY + 0.5, tZ + 0.5, tStrength, true);
	}
	
	@Override
	public int getLightOpacity() {
		return ((BaseMetaTileEntity) getBaseMetaTileEntity()).getLightValue() > 0 ? 0 : 255;
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {
		AxisAlignedBB axisalignedbb1 = getCollisionBoundingBoxFromPool(world, x, y, z);
		if (axisalignedbb1 != null && inputAABB.intersectsWith(axisalignedbb1)) outputAABB.add(axisalignedbb1);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider) {
		//
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		//
	}
	
	@Override
	public boolean allowGeneralRedstoneOutput() {
		return false;
	}
	
	@Override
	public boolean hasAlternativeModeText() {
		return false;
	}
	
	@Override
	public String getAlternativeModeText() {
		return "";
	}

	public boolean shouldTriggerBlockUpdate() {
		return false;
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
		return AECableType.NONE;
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	public AENetworkProxy getProxy() {
		return null;
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	public void gridChanged() {}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		currentTip.add(String.format("Facing: %s", ForgeDirection.getOrientation(mBaseMetaTileEntity.getFrontFace()).name()));
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		/* Empty */
	}
}