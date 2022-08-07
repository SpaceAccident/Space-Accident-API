package space.accident.api.metatileentity.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import space.accident.api.interfaces.metatileentity.IConnectable;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.IColoredTileEntity;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.objects.ItemStackData;
import space.accident.api.util.*;
import space.accident.main.Config;
import space.accident.main.events.ClientEvents;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static space.accident.api.util.Utility.getStrength;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.main.SpaceAccidentApi.proxy;

public abstract class MetaPipeEntity implements IMetaTile, IConnectable {
	/**
	 * The Inventory of the MetaTileEntity. Amount of Slots can be larger than 256. HAYO!
	 */
	public final ItemStack[] mInventory;
	/**
	 * This variable tells, which directions the Block is connected to. It is a Bitmask.
	 */
	public int mConnections = 0;
	/**
	 * Only assigned for the MetaTileEntity in the List! Also only used to get the localized Name for the ItemStack and for getInvName.
	 */
	public String mName;
	public boolean doTickProfilingInThisTick = true;
	protected boolean mCheckConnections = false;
	/**
	 * accessibility to this Field is no longer given, see below
	 */
	private ITile mBaseMetaTileEntity;
	
	public MetaPipeEntity(int id, String aBasicName, String aRegionalName, int aInvSlotCount) {
		this(id, aBasicName, aRegionalName, aInvSlotCount, true);
	}
	
	public MetaPipeEntity(int id, String aBasicName, String aRegionalName, int aInvSlotCount, boolean aAddInfo) {
		if (API.sPostloadStarted || !API.sPreloadStarted) throw new IllegalAccessError("This Constructor has to be called in the load Phase");
		if (API.METATILEENTITIES[id] == null) {
			API.METATILEENTITIES[id] = this;
		} else {
			throw new IllegalArgumentException("MetaMachine-Slot Nr. " + id + " is already occupied!");
		}
		mName = aBasicName.replaceAll(" ", "_").toLowerCase(Locale.ENGLISH);
		setBaseMetaTileEntity(new BaseMetaPipeEntity());
		getBaseMetaTileEntity().setMetaTileID((short) id);
		LanguageManager.addStringLocalization("sa.blockmachines." + mName + ".name", aRegionalName);
		mInventory = new ItemStack[aInvSlotCount];
		
		if (aAddInfo && proxy.isClientSide()) {
			addInfo(id);
		}
	}
	
	/**
	 * This is the normal Constructor.
	 */
	public MetaPipeEntity(String name, int aInvSlotCount) {
		mInventory = new ItemStack[aInvSlotCount];
		mName      = name;
	}
	
	protected final void addInfo(int id) {
		if (!proxy.isClientSide()) return;
		
		ItemStack tStack = new ItemStack(API.sBlockMachines, 1, id);
		tStack
				.getItem()
				.addInformation(
						tStack,
						null,
						new ArrayList<String>(),
						true
				);
	}
	
	/**
	 * For Pipe Rendering
	 */
	public abstract float getThickNess();
	
	/**
	 * For Pipe Rendering
	 */
	public abstract boolean renderInside(int side);
	
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
	
	public boolean isCoverOnSide(BaseMetaPipeEntity aPipe, EntityLivingBase entity) {
		int side = 6;
		double difference = entity.posY - (double) aPipe.yCoord;
		if (difference > 0.6 && difference < 0.99) {
			side = 1;
		}
		if (difference < -1.5 && difference > -1.99) {
			side = 0;
		}
		difference = entity.posZ - (double) aPipe.zCoord;
		if (difference < -0.05 && difference > -0.4) {
			side = 2;
		}
		if (difference > 1.05 && difference < 1.4) {
			side = 3;
		}
		difference = entity.posX - (double) aPipe.xCoord;
		if (difference < -0.05 && difference > -0.4) {
			side = 4;
		}
		if (difference > 1.05 && difference < 1.4) {
			side = 5;
		}
		boolean tCovered = side < 6 && mBaseMetaTileEntity.getCoverIDAtSide(side) > 0;
		if (isConnectedAtSide(side)) {
			tCovered = true;
		}
		return tCovered;
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
	public boolean allowCoverOnSide(int side, ItemStackData coverId) {
		return true;
	}
	
	@Override
	public void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z) {/*Do nothing*/}
	
	@Override
	public boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	@Override
	public boolean onWireCutterRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	@Override
	public boolean onSolderingToolRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	@Override
	public void onExplosion() {/*Do nothing*/}
	
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
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player, int side, float x, float y, float z) {
		return false;
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
	}
	
	@Override
	public final void sendLoopEnd(int index) {
		if (!getBaseMetaTileEntity().hasMufflerUpgrade()) getBaseMetaTileEntity().sendBlockEvent(ClientEvents.STOP_SOUND_LOOP, index);
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return false;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return true;
	}
	
	@Override
	public boolean setStackToZeroInsteadOfNull(int index) {
		return false;
	}
	
	@Override
	public ArrayList<String> getSpecialDebugInfo(ITile baseTile, EntityPlayer player, int logLevel, ArrayList<String> aList) {
		return aList;
	}
	
	@Override
	public boolean isLiquidInput(int side) {
		return false;
	}
	
	@Override
	public boolean isLiquidOutput(int side) {
		return false;
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
	
	@Override
	public void onMachineBlockUpdate() {/*Do nothing*/}
	
	@Override
	public void receiveClientEvent(int aEventID, int value) {/*Do nothing*/}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
	}
	
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
				if (setStackToZeroInsteadOfNull(index)) tStack.stackSize = 0;
				else setInventorySlotContents(index, null);
			} else {
				rStack = tStack.splitStack(amount);
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
		return fill(aFluid, doFill);
	}
	
	@Override
	public int fill(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		return fill_default(side, aFluid, doFill);
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
	public boolean doTickProfilingMessageDuringThisTick() {
		return doTickProfilingInThisTick;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}
	
	@Override
	public boolean connectsToItemPipe(int side) {
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
	public float getExplosionResistance(int side) {
		return 10.0F;
	}
	
	@Override
	public ItemStack[] getRealInventory() {
		return mInventory;
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public void markDirty() {
		//
	}
	
	@Override
	public void onColorChangeServer(int color) {
		setCheckConnections();
	}
	
	@Override
	public void onColorChangeClient(int color) {
		// Do nothing apparently
	}
	
	public void setCheckConnections() {
		mCheckConnections = true;
	}
	
	public long injectEnergyUnits(int side, long aVoltage, long aAmperage) {
		return 0;
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
		tWorld.setBlock(tX, tY, tZ, Blocks.air);
		if (API.sMachineExplosions) {
			new WorldSpawnedEventBuilder.ExplosionEffectEventBuilder().setStrength(tStrength).setSmoking(true).setPosition(tX + 0.5, tY + 0.5, tZ + 0.5).setWorld(tWorld).run();
		}
	}
	
	@Override
	public int getLightOpacity() {
		return 0;
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
	
	private boolean connectableColor(TileEntity tTileEntity) {
		// Determine if two entities are connectable based on their colorization:
		//  Uncolored can connect to anything
		//  If both are colored they must be the same color to connect.
		if (tTileEntity instanceof IColoredTileEntity) {
			if (getBaseMetaTileEntity().getColorization() >= 0) {
				int tColor = ((IColoredTileEntity) tTileEntity).getColorization();
				return tColor < 0 || tColor == getBaseMetaTileEntity().getColorization();
			}
		}
		
		return true;
	}
	
	@Override
	public int connect(int side) {
		if (side >= 6) return 0;
		
		final int tSide = getOppositeSide(side);
		final ITile baseMetaTile = getBaseMetaTileEntity();
		if (baseMetaTile == null || !baseMetaTile.isServerSide()) return 0;
		
		final SA_CoverBehaviorBase<?> coverBehavior = baseMetaTile.getCoverBehaviorAtSideNew(side);
		final int coverId = baseMetaTile.getCoverIDAtSide(side);
		ISerializableObject coverData = baseMetaTile.getComplexCoverDataAtSide(side);
		
		boolean alwaysLookConnected = coverBehavior.alwaysLookConnected(side, coverId, coverData, baseMetaTile);
		boolean letsIn = letsIn(coverBehavior, side, coverId, coverData, baseMetaTile);
		boolean letsOut = letsOut(coverBehavior, side, coverId, coverData, baseMetaTile);
		
		// Careful - tTileEntity might be null, and that's ok -- so handle it
		TileEntity tTileEntity = baseMetaTile.getTileEntityAtSide(side);
		if (!connectableColor(tTileEntity)) return 0;
		
		if ((alwaysLookConnected || letsIn || letsOut)) {
			// Are we trying to connect to a pipe? let's do it!
			IMetaTile tPipe = tTileEntity instanceof ITile ? ((ITile) tTileEntity).getMetaTile() : null;
			if (getClass().isInstance(tPipe) || (tPipe != null && tPipe.getClass().isInstance(this))) {
				connectAtSide(side);
				if (!((MetaPipeEntity) tPipe).isConnectedAtSide(tSide)) {
					// Make sure pipes all get together -- connect back to us if we're connecting to a pipe
					((MetaPipeEntity) tPipe).connect(tSide);
				}
				return 1;
			} else if ((getGT6StyleConnection() && baseMetaTile.getAirAtSide(side)) || canConnect(side, tTileEntity)) {
				// Allow open connections to Air, if the GT6 style pipe/cables are enabled, so that it'll connect to the next block placed down next to it
				connectAtSide(side);
				return 1;
			}
			if (!baseMetaTile.getWorld().getChunkProvider().chunkExists(baseMetaTile.getOffsetX(side, 1) >> 4, baseMetaTile.getOffsetZ(side, 1) >> 4)) {
				// Target chunk unloaded
				return -1;
			}
			
		}
		return 0;
	}
	
	protected void checkConnections() {
		// Verify connections around us.  If GT6 style cables are not enabled then revert to old behavior and try
		// connecting to everything around us
		for (int side = 0; side < 6; side++) {
			if ((!getGT6StyleConnection() || isConnectedAtSide(side)) && connect(side) == 0) {
				disconnect(side);
			}
		}
		mCheckConnections = false;
	}
	
	private void connectAtSide(int side) {
		mConnections |= (1 << side);
	}
	
	@Override
	public void disconnect(int side) {
		if (side >= 6) return;
		mConnections &= ~(1 << side);
		int tSide = getOppositeSide(side);
		ITile tTileEntity = getBaseMetaTileEntity().getITileAtSide(side);
		IMetaTile tPipe = tTileEntity == null ? null : tTileEntity.getMetaTile();
		if ((this.getClass().isInstance(tPipe) || (tPipe != null && tPipe.getClass().isInstance(this))) && ((MetaPipeEntity) tPipe).isConnectedAtSide(tSide)) ((MetaPipeEntity) tPipe).disconnect(tSide);
	}
	
	@Override
	public boolean isConnectedAtSide(int side) {
		return (mConnections & (1 << side)) != 0;
	}
	
	
	public boolean letsIn(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {return false;}
	
	public boolean letsOut(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {return false;}
	
	public boolean letsIn(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {return false;}
	
	public boolean letsOut(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {return false;}
	
	public boolean canConnect(int side, TileEntity tTileEntity) {
		return false;
	}
	
	public boolean getGT6StyleConnection() {
		return false;
	}
	
	@Override
	public boolean isMachineBlockUpdateRecursive() {
		return false;
	}
	
	public void reloadLocks() {
	}
}
