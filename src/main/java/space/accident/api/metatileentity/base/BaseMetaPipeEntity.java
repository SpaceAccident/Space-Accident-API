package space.accident.api.metatileentity.base;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import space.accident.api.API;
import space.accident.api.enums.Textures;
import space.accident.api.enums.Values;
import space.accident.api.graphs.Lock;
import space.accident.api.graphs.Node;
import space.accident.api.graphs.paths.NodePath;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IConnectable;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.IDebugableTileEntity;
import space.accident.api.interfaces.tileentity.IPipeRenderedTileEntity;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.objects.ItemStackData;
import space.accident.api.sound.Sounds;
import space.accident.api.util.ModHandler;
import space.accident.api.util.OreDictUnifier;
import space.accident.api.util.SpaceLog;
import space.accident.main.events.ClientEvents;
import space.accident.main.network.Packet_TileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static space.accident.api.util.Utility.*;
import static space.accident.extensions.ItemStackUtils.isStackInList;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;
import static space.accident.main.SpaceAccidentApi.NETWORK;

public class BaseMetaPipeEntity extends CommonMetaTileEntity implements ITile, IPipeRenderedTileEntity, IDebugableTileEntity {
	private final boolean mCheckConnections = false;
	private final int[] mTimeStatistics = new int[API.TICKS_FOR_LAG_AVERAGING];
	public int mConnections = IConnectable.NO_CONNECTION;
	protected MetaPipeEntity mMetaTileEntity;
	protected Node node;
	protected NodePath nodePath;
	private boolean mWorkUpdate = false, mWorks = true;
	private int mColor = 0, oColor = 0, oStrongRedstone = 0, oRedstoneData = 63, oTextureData = 0, oUpdateData = 0, mLagWarningCount = 0;
	private int oX = 0, oY = 0, oZ = 0, mTimeStatisticsIndex = 0;
	
	public BaseMetaPipeEntity() {
	}
	
	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public NodePath getNodePath() {
		return nodePath;
	}
	
	public void setNodePath(NodePath nodePath) {
		this.nodePath = nodePath;
	}
	
	public void addToLock(TileEntity tileEntity, int side) {
		if (node != null) {
			Lock lock = node.locks[side];
			if (lock != null) {
				lock.addTileEntity(tileEntity);
			}
		} else if (nodePath != null) {
			nodePath.lock.addTileEntity(tileEntity);
		}
	}
	
	public void removeFromLock(TileEntity tileEntity, int side) {
		if (node != null) {
			Lock lock = node.locks[side];
			if (lock != null) {
				lock.removeTileEntity(tileEntity);
			}
		} else if (nodePath != null) {
			nodePath.lock.removeTileEntity(tileEntity);
		}
	}
	
	public void reloadLocks() {
		IMetaTile meta = getMetaTile();
		if (meta instanceof MetaPipeEntity) {
			((MetaPipeEntity) meta).reloadLocks();
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		try {
			super.writeToNBT(nbt);
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity", e);
		}
		try {
			nbt.setInteger("mID", mID);
			writeCoverNBT(nbt, false);
			nbt.setInteger("mConnections", mConnections);
			nbt.setInteger("mColor", mColor);
			nbt.setBoolean("mWorks", !mWorks);
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity", e);
		}
		saveMetaTileNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		setInitialValuesAsNBT(nbt, (short) 0);
	}
	
	@Override
	public void setInitialValuesAsNBT(NBTTagCompound nbt, short id) {
		if (nbt == null) {
			if (id > 0) mID = id;
			else mID = mID > 0 ? mID : 0;
			if (mID != 0) createNewMetatileEntity(mID);
		} else {
			if (id <= 0) mID = (short) nbt.getInteger("mID");
			else mID = id;
			mConnections = nbt.getInteger("mConnections");
			mColor       = nbt.getInteger("mColor");
			mWorks       = !nbt.getBoolean("mWorks");
			
			if (mSidedRedstone.length != 6) mSidedRedstone = new int[]{0, 0, 0, 0, 0, 0};
			
			readCoverNBT(nbt);
			loadMetaTileNBT(nbt);
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (!hasValidMetaTileEntity()) {
			if (mMetaTileEntity == null) return;
			mMetaTileEntity.setBaseMetaTileEntity(this);
		}
		
		long tTime = System.nanoTime();
		try {
			if (hasValidMetaTileEntity()) {
				if (mTickTimer++ == 0) {
					oX = xCoord;
					oY = yCoord;
					oZ = zCoord;
					if (isServerSide()) checkDropCover();
					else {
						requestCoverDataIfNeeded();
					}
					worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
					mMetaTileEntity.onFirstTick(this);
					if (!hasValidMetaTileEntity()) return;
				}
				
				if (isClientSide()) {
					if (mColor != oColor) {
						mMetaTileEntity.onColorChangeClient(oColor = mColor);
						issueTextureUpdate();
					}
					
					if (mNeedsUpdate) {
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						mNeedsUpdate = false;
					}
				}
				if (isServerSide() && mTickTimer > 10) {
					if (!doCoverThings()) return;
					
					final int oldConnections = mConnections;
					// Mask-out connection direction bits to keep only Foam related connections
					mConnections = (mMetaTileEntity.mConnections | (mConnections & ~IConnectable.CONNECTED_ALL));
					// If foam not hardened, tries roll chance to harden
					if ((mConnections & IConnectable.HAS_FOAM) == IConnectable.HAS_FRESHFOAM && getRandomNumber(1000) == 0) {
						mConnections = ((mConnections & ~IConnectable.HAS_FRESHFOAM) | IConnectable.HAS_HARDENEDFOAM);
					}
					if (mTickTimer > 12 && oldConnections != mConnections) API.causeCableUpdate(worldObj, xCoord, yCoord, zCoord);
				}
				mMetaTileEntity.onPreTick(this, mTickTimer);
				if (!hasValidMetaTileEntity()) return;
				if (isServerSide()) {
					if (mTickTimer == 10) {
						updateCoverBehavior();
						issueBlockUpdate();
					}
					
					if (xCoord != oX || yCoord != oY || zCoord != oZ) {
						oX = xCoord;
						oY = yCoord;
						oZ = zCoord;
						issueClientUpdate();
						clearTileEntityBuffer();
					}
				}
				
				mMetaTileEntity.onPostTick(this, mTickTimer);
				if (!hasValidMetaTileEntity()) return;
				
				if (isServerSide()) {
					if (mTickTimer % 10 == 0) {
						sendClientData();
					}
					
					if (mTickTimer > 10) {
						if (mConnections != oTextureData) sendBlockEvent(0, oTextureData = mConnections);
						int tData = mMetaTileEntity.getUpdateData();
						if (tData != oUpdateData) sendBlockEvent(1, oUpdateData = tData);
						if (mColor != oColor) sendBlockEvent(2, oColor = mColor);
						tData = (((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0));
						if (tData != oRedstoneData) sendBlockEvent(3, oRedstoneData = tData);
					}
					
					if (mNeedsBlockUpdate) {
						updateNeighbours(mStrongRedstone, oStrongRedstone);
						oStrongRedstone   = mStrongRedstone;
						mNeedsBlockUpdate = false;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
		
		if (isServerSide() && hasValidMetaTileEntity()) {
			tTime = System.nanoTime() - tTime;
			if (mTimeStatistics.length > 0) mTimeStatistics[mTimeStatisticsIndex = (mTimeStatisticsIndex + 1) % mTimeStatistics.length] = (int) tTime;
			if (tTime > 0 && tTime > (API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING * 1000000L) && mTickTimer > 1000 && getMetaTile().doTickProfilingMessageDuringThisTick() && mLagWarningCount++ < 10) SpaceLog.FML_LOGGER.warn("WARNING: Possible Lag Source at [" + xCoord + "," + yCoord + "," + zCoord + "] in Dimension " + worldObj.provider.dimensionId + " with " + tTime + " ns caused by an instance of " + getMetaTile().getClass());
		}
		
		mWorkUpdate = mInventoryChanged = false;
	}
	
	private void sendClientData() {
		if (mSendClientData) {
			NETWORK.sendPacketToAllPlayersInRange(worldObj, new Packet_TileEntity(xCoord, (short) yCoord, zCoord, mID, mCoverSides[0], mCoverSides[1], mCoverSides[2], mCoverSides[3], mCoverSides[4], mCoverSides[5], oTextureData = mConnections, oUpdateData = hasValidMetaTileEntity() ? mMetaTileEntity.getUpdateData() : 0, oRedstoneData = (((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0)), oColor = mColor), xCoord, zCoord);
			mSendClientData = false;
		}
		sendCoverDataIfNeeded();
	}
	
	public final void receiveMetaTileEntityData(short id, int aCover0, int aCover1, int aCover2, int aCover3, int aCover4, int aCover5, int aTextureData, int aUpdateData, int aRedstoneData, int aColorData) {
		issueTextureUpdate();
		if (id > 0 && mID != id) {
			mID = id;
			createNewMetatileEntity(mID);
		}
		
		setCoverIDAtSide(0, aCover0);
		setCoverIDAtSide(1, aCover1);
		setCoverIDAtSide(2, aCover2);
		setCoverIDAtSide(3, aCover3);
		setCoverIDAtSide(4, aCover4);
		setCoverIDAtSide(5, aCover5);
		
		receiveClientEvent(ClientEvents.CHANGE_COMMON_DATA, aTextureData);
		receiveClientEvent(ClientEvents.CHANGE_CUSTOM_DATA, aUpdateData);
		receiveClientEvent(ClientEvents.CHANGE_COLOR, aColorData);
		receiveClientEvent(ClientEvents.CHANGE_REDSTONE_OUTPUT, aRedstoneData);
	}
	
	@Override
	public boolean receiveClientEvent(int aEventID, int value) {
		super.receiveClientEvent(aEventID, value);
		
		if (hasValidMetaTileEntity()) {
			try {
				mMetaTileEntity.receiveClientEvent(aEventID, value);
			} catch (Throwable e) {
				SpaceLog.FML_LOGGER.error("Encountered Exception while receiving Data from the Server", e);
			}
		}
		
		if (isClientSide()) {
			issueTextureUpdate();
			switch (aEventID) {
				case ClientEvents.CHANGE_COMMON_DATA:
					mConnections = value;
					break;
				case ClientEvents.CHANGE_CUSTOM_DATA:
					if (hasValidMetaTileEntity()) mMetaTileEntity.onValueUpdate(value);
					break;
				case ClientEvents.CHANGE_COLOR:
					if (value > 16 || value < 0) value = 0;
					mColor = value;
					break;
				case ClientEvents.CHANGE_REDSTONE_OUTPUT:
					mSidedRedstone[0] = ((value & 1) == 1 ? 15 : 0);
					mSidedRedstone[1] = ((value & 2) == 2 ? 15 : 0);
					mSidedRedstone[2] = ((value & 4) == 4 ? 15 : 0);
					mSidedRedstone[3] = ((value & 8) == 8 ? 15 : 0);
					mSidedRedstone[4] = ((value & 16) == 16 ? 15 : 0);
					mSidedRedstone[5] = ((value & 32) == 32 ? 15 : 0);
					break;
				case ClientEvents.DO_SOUND:
					if (hasValidMetaTileEntity() && mTickTimer > 20) mMetaTileEntity.doSound(value, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					break;
				case ClientEvents.START_SOUND_LOOP:
					if (hasValidMetaTileEntity() && mTickTimer > 20) mMetaTileEntity.startSoundLoop(value, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					break;
				case ClientEvents.STOP_SOUND_LOOP:
					if (hasValidMetaTileEntity() && mTickTimer > 20) mMetaTileEntity.stopSoundLoop(value, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					break;
			}
		}
		return true;
	}
	
	@Override
	public ArrayList<String> getDebugInfo(EntityPlayer player, int logLevel) {
		final ArrayList<String> tList = new ArrayList<>();
		if (logLevel > 2) {
			tList.add("Meta-ID: " + EnumChatFormatting.BLUE + mID + EnumChatFormatting.RESET + (hasValidMetaTileEntity() ? EnumChatFormatting.GREEN + " valid" + EnumChatFormatting.RESET : EnumChatFormatting.RED + " invalid" + EnumChatFormatting.RESET) + (mMetaTileEntity == null ? EnumChatFormatting.RED + " MetaTileEntity == null!" + EnumChatFormatting.RESET : " "));
		}
		if (logLevel > 1) {
			if (mTimeStatistics.length > 0) {
				double tAverageTime = 0;
				double tWorstTime = 0;
				for (int tTime : mTimeStatistics) {
					tAverageTime += tTime;
					if (tTime > tWorstTime) {
						tWorstTime = tTime;
					}
				}
				tList.add("Average CPU-load of ~" + (tAverageTime / mTimeStatistics.length) + "ns since " + mTimeStatistics.length + " ticks with worst time of " + tWorstTime + "ns.");
			}
			if (mLagWarningCount > 0) {
				tList.add("Caused " + (mLagWarningCount >= 10 ? "more than 10" : mLagWarningCount) + " Lag Spike Warnings (anything taking longer than " + API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING + "ms) on the Server.");
			}
			tList.add("Is" + (mMetaTileEntity.isAccessAllowed(player) ? " " : EnumChatFormatting.RED + " not " + EnumChatFormatting.RESET) + "accessible for you");
		}
		
		return mMetaTileEntity.getSpecialDebugInfo(this, player, logLevel, tList);
	}
	
	@Override
	public boolean isGivingInformation() {
		if (canAccessData()) return mMetaTileEntity.isGivingInformation();
		return false;
	}
	
	
	@Override
	public int getBackFace() {
		return getOppositeSide(getFrontFace());
	}
	
	@Override
	public int getFrontFace() {
		return 6;
	}
	
	@Override
	public void setFrontFace(int face) {
	}
	
	@Override
	public int getSizeInventory() {
		if (canAccessData()) return mMetaTileEntity.getSizeInventory();
		return 0;
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		if (canAccessData()) return mMetaTileEntity.getStackInSlot(index);
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		markDirty();
		mInventoryChanged = true;
		if (canAccessData()) mMetaTileEntity.setInventorySlotContents(index, worldObj.isRemote ? stack : OreDictUnifier.setStack(true, stack));
	}
	
	@Override
	public String getInventoryName() {
		if (canAccessData()) return mMetaTileEntity.getInventoryName();
		if (API.METATILEENTITIES[mID] != null) return API.METATILEENTITIES[mID].getInventoryName();
		return "";
	}
	
	@Override
	public int getInventoryStackLimit() {
		if (canAccessData()) return mMetaTileEntity.getInventoryStackLimit();
		return 64;
	}
	
	@Override
	public void openInventory() {/*Do nothing*/}
	
	@Override
	public void closeInventory() {/*Do nothing*/}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return hasValidMetaTileEntity() && mTickTimer > 40 && getTileEntityOffset(0, 0, 0) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64 && mMetaTileEntity.isAccessAllowed(player);
	}
	
	@Override
	public void validate() {
		super.validate();
		mTickTimer = 0;
	}
	
	@Override
	public void invalidate() {
		tileEntityInvalid = false;
		if (hasValidMetaTileEntity()) {
			mMetaTileEntity.onRemoval();
			mMetaTileEntity.setBaseMetaTileEntity(null);
		}
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) setInventorySlotContents(slot, null);
		return stack;
	}
	
	/**
	 * Checks validity of meta tile and delegates to it
	 */
	@Override
	public void onMachineBlockUpdate() {
		if (canAccessData()) mMetaTileEntity.onMachineBlockUpdate();
	}
	
	/**
	 * Checks validity of meta tile and delegates to it
	 */
	@Override
	public boolean isMachineBlockUpdateRecursive() {
		return canAccessData() && mMetaTileEntity.isMachineBlockUpdateRecursive();
	}
	
	@Override
	public int getProgress() {
		return canAccessData() ? mMetaTileEntity.getProgresstime() : 0;
	}
	
	@Override
	public int getMaxProgress() {
		return canAccessData() ? mMetaTileEntity.maxProgresstime() : 0;
	}
	
	@Override
	public boolean increaseProgress(int progressInTicks) {
		return canAccessData() && mMetaTileEntity.increaseProgress(progressInTicks) != progressInTicks;
	}
	
	@Override
	public boolean hasThingsToDo() {
		return getMaxProgress() > 0;
	}
	
	@Override
	public void enableWorking() {
		if (!mWorks) mWorkUpdate = true;
		mWorks = true;
		reloadLocks();
	}
	
	@Override
	public void disableWorking() {
		mWorks = false;
		reloadLocks();
	}
	
	@Override
	public boolean isAllowedToWork() {
		return mWorks;
	}
	
	@Override
	public boolean hasWorkJustBeenEnabled() {
		return mWorkUpdate;
	}
	
	@Override
	public int getWorkDataValue() {
		return 0;
	}
	
	@Override
	public void setWorkDataValue(int value) {/*Do nothing*/}
	
	@Override
	public int getMetaTileID() {
		return mID;
	}
	
	@Override
	public int setMetaTileID(short id) {
		return mID = id;
	}
	
	@Override
	public boolean isActive() {
		return false;
	}
	
	@Override
	public void setActive(boolean active) {/*Do nothing*/}
	
	@Override
	public long getTick() {
		return mTickTimer;
	}
	
	@Override
	public boolean decreaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooLessEnergy) {
		return false;
	}
	
	@Override
	public boolean increaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooMuchEnergy) {
		return false;
	}
	
	@Override
	public boolean inputEnergyFrom(int side) {
		return false;
	}
	
	@Override
	public boolean inputEnergyFrom(int side, boolean waitForActive) {
		return false;
	}
	
	@Override
	public boolean outputsEnergyTo(int side) {
		return false;
	}
	
	@Override
	public boolean outputsEnergyTo(int side, boolean waitForActive) {
		return false;
	}
	
	@Override
	public long getOutputAmperage() {
		return 0;
	}
	
	@Override
	public long getOutputVoltage() {
		return 0;
	}
	
	@Override
	public long getInputAmperage() {
		return 0;
	}
	
	@Override
	public long getInputVoltage() {
		return 0;
	}
	
	@Override
	public long getUniversalEnergyStored() {
		return Math.max(getStoredEU(), getStoredSteam());
	}
	
	@Override
	public long getUniversalEnergyCapacity() {
		return Math.max(getEUCapacity(), getSteamCapacity());
	}
	
	@Override
	public long getStoredEU() {
		return 0;
	}
	
	@Override
	public long getEUCapacity() {
		return 0;
	}
	
	
	@Override
	public ITexture[] getTexture(Block block, int side) {
		ITexture rIcon = getCoverTexture(side);
		if (rIcon != null) return new ITexture[]{rIcon};
		return getTextureUncovered(side);
	}
	
	@Override
	public ITexture[] getTextureCovered(int side) {
		ITexture coverTexture = getCoverTexture(side);
		ITexture[] textureUncovered = getTextureUncovered(side);
		ITexture[] textureCovered;
		if (coverTexture != null) {
			textureCovered                          = Arrays.copyOf(textureUncovered, textureUncovered.length + 1);
			textureCovered[textureUncovered.length] = coverTexture;
			return textureCovered;
		} else {
			return textureUncovered;
		}
	}
	
	@Override
	public ITexture[] getTextureUncovered(int side) {
		if ((mConnections & IConnectable.HAS_FRESHFOAM) != 0) return Textures.BlockIcons.FRESHFOAM;
		if ((mConnections & IConnectable.HAS_HARDENEDFOAM) != 0) return Textures.BlockIcons.HARDENEDFOAMS[mColor];
		if ((mConnections & IConnectable.HAS_FOAM) != 0) return Textures.BlockIcons.ERROR_RENDERING;
		int tConnections = mConnections;
		if (tConnections == IConnectable.CONNECTED_WEST || tConnections == IConnectable.CONNECTED_EAST) tConnections = (IConnectable.CONNECTED_WEST | IConnectable.CONNECTED_EAST);
		else if (tConnections == IConnectable.CONNECTED_DOWN || tConnections == IConnectable.CONNECTED_UP) tConnections = (IConnectable.CONNECTED_DOWN | IConnectable.CONNECTED_UP);
		else if (tConnections == IConnectable.CONNECTED_NORTH || tConnections == IConnectable.CONNECTED_SOUTH) tConnections = (IConnectable.CONNECTED_NORTH | IConnectable.CONNECTED_SOUTH);
		if (hasValidMetaTileEntity())
			return mMetaTileEntity.getTexture(this, side, tConnections, (mColor - 1), tConnections == 0 || (tConnections & (1 << side)) != 0, getOutputRedStoneSignal(side) > 0);
		return Textures.BlockIcons.ERROR_RENDERING;
	}
	
	@Override
	protected boolean hasValidMetaTileEntity() {
		return mMetaTileEntity != null && mMetaTileEntity.getBaseMetaTileEntity() == this;
	}
	
	
	@Override
	public void doExplosion(long amount) {
		if (canAccessData()) {
			mMetaTileEntity.onExplosion();
			mMetaTileEntity.doExplosion(amount);
		}
	}
	
	@Override
	public ArrayList<ItemStack> getDrops() {
		ItemStack rStack = new ItemStack(API.sBlockMachines, 1, mID);
		NBTTagCompound tNBT = new NBTTagCompound();
		if (mStrongRedstone > 0) tNBT.setInteger("mStrongRedstone", mStrongRedstone);
		boolean hasCover = false;
		for (int i = 0; i < mCoverSides.length; i++) {
			if (mCoverSides[i] != 0) {
				if (mCoverData[i] != null) // this really shouldn't be null if a cover is there already, but whatever
					tNBT.setTag(COVER_DATA_NBT_KEYS[i], mCoverData[i].saveDataToNBT());
				hasCover = true;
			}
		}
		if (hasCover) tNBT.setIntArray("mCoverSides", mCoverSides);
		if (hasValidMetaTileEntity()) mMetaTileEntity.setItemNBT(tNBT);
		if (!tNBT.hasNoTags()) rStack.setTagCompound(tNBT);
		return new ArrayList<ItemStack>(Arrays.asList(rStack));
	}
	
	@Override
	public boolean onRightClick(EntityPlayer player, int side, float x, float y, float z) {
		if (isClientSide()) {
			//Configure Cover, sneak can also be: screwdriver, wrench, side cutter, soldering iron
			if (player.isSneaking()) {
				int tSide = (getCoverIDAtSide(side) == 0) ? determineWrenchingSide(side, x, y, z) : side;
				return (getCoverBehaviorAtSideNew(tSide).hasCoverGUI());
			} else if (getCoverBehaviorAtSideNew(side).onCoverRightclickClient(side, this, player, x, y, z)) {
				return true;
			}
		}
		if (isServerSide()) {
			ItemStack tCurrentItem = player.inventory.getCurrentItem();
			if (tCurrentItem != null) {
				if (getColorization() >= 0 && areStacksEqual(new ItemStack(Items.water_bucket, 1), tCurrentItem)) {
					mMetaTileEntity.markDirty();
					tCurrentItem.func_150996_a(Items.bucket);
					setColorization(-1);
					return true;
				}
				int tSide = determineWrenchingSide(side, x, y, z);
				if (isStackInList(tCurrentItem, API.sWrenchList)) {
					if (mMetaTileEntity.onWrenchRightClick(side, tSide, player, x, y, z)) {
						mMetaTileEntity.markDirty();
						ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player);
						sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
					}
					return true;
				}
				if (isStackInList(tCurrentItem, API.sScrewdriverList)) {
					if (getCoverIDAtSide(side) == 0 && getCoverIDAtSide(tSide) != 0) {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 200, player)) {
							setCoverDataAtSide(tSide, getCoverBehaviorAtSideNew(tSide).onCoverScrewdriverClick(tSide, getCoverIDAtSide(tSide), getComplexCoverDataAtSide(tSide), this, player, 0.5F, 0.5F, 0.5F));
							mMetaTileEntity.onScrewdriverRightClick(tSide, player, x, y, z);
							mMetaTileEntity.markDirty();
							sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
						}
					} else {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
							setCoverDataAtSide(side, getCoverBehaviorAtSideNew(side).onCoverScrewdriverClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player, x, y, z));
							mMetaTileEntity.onScrewdriverRightClick(side, player, x, y, z);
							mMetaTileEntity.markDirty();
							sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
						}
					}
					return true;
				}
				
				if (isStackInList(tCurrentItem, API.sHardHammerList)) {
					//if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
					//	sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.ANVIL_USE), 1.0F, -1, xCoord, yCoord, zCoord);
					//}
					return true;
				}
				
				if (isStackInList(tCurrentItem, API.sSoftHammerList)) {
					if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
						if (mWorks) disableWorking();
						else enableWorking();
						mMetaTileEntity.markDirty();
						sendChat(player, trans("090", "Machine Processing: ") + (isAllowedToWork() ? trans("088", "Enabled") : trans("087", "Disabled")));
						sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.RUBBER_TRAMPOLINE), 1.0F, -1, xCoord, yCoord, zCoord);
					}
					return true;
				}
				
				if (isStackInList(tCurrentItem, API.sWireCutterList)) {
					if (mMetaTileEntity.onWireCutterRightClick(side, tSide, player, x, y, z)) {
						mMetaTileEntity.markDirty();
						//logic handled internally
						sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
					}
					return true;
				}
				
				if (isStackInList(tCurrentItem, API.sSolderingToolList)) {
					if (mMetaTileEntity.onSolderingToolRightClick(side, tSide, player, x, y, z)) {
						mMetaTileEntity.markDirty();
						//logic handled internally
						sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.BATTERY_USE), 1.0F, -1, xCoord, yCoord, zCoord);
					} else if (useSolderingIron(tCurrentItem, player)) {
						mMetaTileEntity.markDirty();
						mStrongRedstone ^= (1 << tSide);
						sendChat(player, trans("091", "RedStone Output at Side ") + tSide + trans("092", " set to: ") + ((mStrongRedstone & (1 << tSide)) != 0 ? trans("093", "Strong") : trans("094", "Weak")));
						sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.BATTERY_USE), 3.0F, -1, xCoord, yCoord, zCoord);
						issueBlockUpdate();
					}
					return true;
				}
				
				int coverSide = side;
				if (getCoverIDAtSide(side) == 0) coverSide = tSide;
				
				if (getCoverIDAtSide(coverSide) == 0) {
					if (isStackInList(tCurrentItem, API.sCovers.keySet())) {
						if (API.getCoverBehaviorNew(tCurrentItem).isCoverPlaceable(coverSide, tCurrentItem, this) && mMetaTileEntity.allowCoverOnSide(coverSide, new ItemStackData(tCurrentItem))) {
							setCoverItemAtSide(coverSide, tCurrentItem);
							mMetaTileEntity.markDirty();
							if (!player.capabilities.isCreativeMode) tCurrentItem.stackSize--;
							sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
						}
						return true;
					}
				} else {
					if (isStackInList(tCurrentItem, API.sCrowbarList)) {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
							sendSoundToPlayers(worldObj, API.sSoundList.get(Sounds.BREAK), 1.0F, -1, xCoord, yCoord, zCoord);
							dropCover(coverSide, side, false);
							mMetaTileEntity.markDirty();
						}
						return true;
					}
				}
			} else if (player.isSneaking()) { //Sneak click, no tool -> open cover config or turn back.
				side = (getCoverIDAtSide(side) == 0) ? determineWrenchingSide(side, x, y, z) : side;
				return getCoverIDAtSide(side) > 0 && getCoverBehaviorAtSideNew(side).onCoverShiftRightClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player);
			}
			
			if (getCoverBehaviorAtSideNew(side).onCoverRightClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player, x, y, z)) return true;
		}
		
		if (!getCoverBehaviorAtSideNew(side).isGUIClickable(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)) return false;
		
		try {
			if (!player.isSneaking() && hasValidMetaTileEntity()) {
				boolean handled = mMetaTileEntity.onRightclick(this, player, side, x, y, z);
				if (handled) {
					mMetaTileEntity.markDirty();
				}
				return handled;
			}
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered Exception while right clicking TileEntity", e);
		}
		
		return false;
	}
	
	@Override
	public void onLeftClick(EntityPlayer player) {
		try {
			if (player != null && hasValidMetaTileEntity()) mMetaTileEntity.onLeftclick(this, player);
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered Exception while left clicking TileEntity", e);
		}
	}
	
	@Override
	public boolean isDigitalChest() {
		return false;
	}
	
	@Override
	public ItemStack[] getStoredItemData() {
		return null;
	}
	
	@Override
	public void setItemCount(int count) {
		//
	}
	
	@Override
	public int getMaxItemCount() {
		return 0;
	}
	
	/**
	 * Can put stack into Slot
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return canAccessData() && mMetaTileEntity.isItemValidForSlot(index, stack);
	}
	
	/**
	 * returns all valid Inventory Slots, no matter which Side (Unless it's covered).
	 * The Side Stuff is done in the following two Functions.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (canAccessData() && (getCoverBehaviorAtSideNew(side).letsItemsOut(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), -1, this) || getCoverBehaviorAtSideNew(side).letsItemsIn(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), -1, this)))
			return mMetaTileEntity.getAccessibleSlotsFromSide(side);
		return Values.emptyIntArray;
	}
	
	/**
	 * Can put stack into Slot at Side
	 */
	@Override
	public boolean canInsertItem(int index, ItemStack stack, int side) {
		return canAccessData() && getCoverBehaviorAtSideNew(side).letsItemsIn(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), index, this) && mMetaTileEntity.canInsertItem(index, stack, side);
	}
	
	/**
	 * Can pull stack out of Slot from Side
	 */
	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return canAccessData() && getCoverBehaviorAtSideNew(side).letsItemsOut(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), index, this) && mMetaTileEntity.canExtractItem(index, stack, side);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public void setGenericRedstoneOutput(boolean aOnOff) {
		// Do nothing
	}
	
	@Override
	public int getErrorDisplayID() {
		return 0;
	}
	
	@Override
	public void setErrorDisplayID(int errorId) {
		//
	}
	
	@Override
	public IMetaTile getMetaTile() {
		return hasValidMetaTileEntity() ? mMetaTileEntity : null;
	}
	
	@Override
	public void setMetaTile(IMetaTile metaTile) {
		mMetaTileEntity = (MetaPipeEntity) metaTile;
	}
	
	@Override
	public long getAverageElectricInput() {
		return 0;
	}
	
	@Override
	public long getAverageElectricOutput() {
		return 0;
	}
	
	@Override
	public String getOwnerName() {
		return "Player";
	}
	
	@Override
	public String setOwnerName(String name) {
		return "Player";
	}
	
	@Override
	public UUID getOwnerUuid() {
		return defaultUuid;
	}
	
	@Override
	public void setOwnerUuid(UUID uuid) {
		
	}
	
	@Override
	public int getComparatorValue(int side) {
		return canAccessData() ? mMetaTileEntity.getComparatorValue(side) : 0;
	}
	
	@Override
	public ItemStack decrStackSize(int index, int amount) {
		if (canAccessData()) {
			mInventoryChanged = true;
			return mMetaTileEntity.decrStackSize(index, amount);
		}
		return null;
	}
	
	@Override
	public long injectEnergyUnits(int side, long aVoltage, long aAmperage) {
		if (canAccessData()) return mMetaTileEntity.injectEnergyUnits(side, aVoltage, aAmperage);
		return 0;
	}
	
	@Override
	public boolean drainEnergyUnits(int side, long aVoltage, long aAmperage) {
		return false;
	}
	
	@Override
	public boolean acceptsRotationalEnergy(int side) {
		if (!canAccessData() || getCoverIDAtSide(side) != 0) return false;
		return mMetaTileEntity.acceptsRotationalEnergy(side);
	}
	
	@Override
	public boolean injectRotationalEnergy(int side, long aSpeed, long aEnergy) {
		if (!canAccessData() || getCoverIDAtSide(side) != 0) return false;
		return mMetaTileEntity.injectRotationalEnergy(side, aSpeed, aEnergy);
	}
	
	private boolean canMoveFluidOnSide(ForgeDirection side, Fluid aFluid, boolean isFill) {
		if (side == ForgeDirection.UNKNOWN) return true;
		
		IFluidHandler tTileEntity = getITankContainerAtSide(side.ordinal());
		// Only require a connection if there's something to connect to - Allows fluid cells & buckets to interact with the pipe
		if (tTileEntity != null && !mMetaTileEntity.isConnectedAtSide(side.ordinal())) return false;
		
		if (isFill && mMetaTileEntity.isLiquidInput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidIn(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid, this))
			return true;
		
		return !isFill && mMetaTileEntity.isLiquidOutput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidOut(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid, this);
	}
	
	@Override
	public int fill(ForgeDirection side, FluidStack aFluidStack, boolean doFill) {
		if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(side, aFluidStack == null ? null : aFluidStack.getFluid(), true)) return mMetaTileEntity.fill(side, aFluidStack, doFill);
		return 0;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, int maxDrain, boolean doDrain) {
		if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(side, mMetaTileEntity.getFluid() == null ? null : mMetaTileEntity.getFluid().getFluid(), false))
			return mMetaTileEntity.drain(side, maxDrain, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, FluidStack aFluidStack, boolean doDrain) {
		if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(side, aFluidStack == null ? null : aFluidStack.getFluid(), false)) return mMetaTileEntity.drain(side, aFluidStack, doDrain);
		return null;
	}
	
	@Override
	public boolean canFill(ForgeDirection side, Fluid aFluid) {
		if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(side, aFluid, true)) return mMetaTileEntity.canFill(side, aFluid);
		return false;
	}
	
	@Override
	public boolean canDrain(ForgeDirection side, Fluid aFluid) {
		if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(side, aFluid, false)) return mMetaTileEntity.canDrain(side, aFluid);
		return false;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		if (canAccessData() && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidInput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidIn(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), null, this)) || (mMetaTileEntity.isLiquidOutput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidOut(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), null, this))
				// Doesn't need to be connected to get Tank Info -- otherwise things can't connect
		)) return mMetaTileEntity.getTankInfo(side);
		return new FluidTankInfo[]{};
	}
	
	@Override
	public boolean addStackToSlot(int index, ItemStack stack) {
		if (isStackInvalid(stack)) return true;
		if (index < 0 || index >= getSizeInventory()) return false;
		ItemStack tStack = getStackInSlot(index);
		if (isStackInvalid(tStack)) {
			setInventorySlotContents(index, stack);
			return true;
		}
		stack = OreDictUnifier.get(stack);
		if (areStacksEqual(tStack, stack) && tStack.stackSize + stack.stackSize <= Math.min(stack.getMaxStackSize(), getInventoryStackLimit())) {
			markDirty();
			tStack.stackSize += stack.stackSize;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addStackToSlot(int index, ItemStack stack, int amount) {
		return addStackToSlot(index, copyAmount(amount, stack));
	}
	
	@Override
	public int getColorization() {
		return (mColor - 1);
	}
	
	@Override
	public int setColorization(int color) {
		if (color > 15 || color < -1) color = -1;
		mColor = (color + 1);
		if (canAccessData()) mMetaTileEntity.onColorChangeServer(color);
		return mColor;
	}
	
	@Override
	public float getThickNess() {
		if (canAccessData()) return mMetaTileEntity.getThickNess();
		return 1.0F;
	}
	
	public boolean renderInside(int side) {
		if (canAccessData()) return mMetaTileEntity.renderInside(side);
		return false;
	}
	
	@Override
	public float getBlastResistance(int side) {
		return (mConnections & IConnectable.HAS_FOAM) != 0 ? 50.0F : 5.0F;
	}
	
	@Override
	public boolean isMufflerUpgradable() {
		return false;
	}
	
	@Override
	public boolean addMufflerUpgrade() {
		return false;
	}
	
	@Override
	public boolean hasMufflerUpgrade() {
		return false;
	}
	
	@Override
	public boolean isUniversalEnergyStored(long aEnergyAmount) {
		return getUniversalEnergyStored() >= aEnergyAmount;
	}
	
	@NotNull
	@Override
	public String[] getInfoData() {
		{
			if (canAccessData()) return getMetaTile().getInfoData();
			return new String[]{};
		}
	}
	
	@Override
	public int getConnections() {
		return mConnections;
	}
	
	public void onNeighborBlockChange(int x, int y, int z) {
		if (canAccessData()) {
			final IMetaTile meta = getMetaTile();
			if (meta instanceof MetaPipeEntity) {
				// Trigger a checking of connections in case someone placed down a block that the pipe/wire shouldn't be connected to.
				// However; don't do it immediately in case the world isn't finished loading
				//  (This caused issues with AE2 GTEU p2p connections.
				((MetaPipeEntity) meta).setCheckConnections();
			}
		}
	}
	
	@Override
	public int getLightOpacity() {
		return mMetaTileEntity == null ? 0 : mMetaTileEntity.getLightOpacity();
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {
		mMetaTileEntity.addCollisionBoxesToList(world, x, y, z, inputAABB, outputAABB, collider);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return mMetaTileEntity.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider) {
		mMetaTileEntity.onEntityCollidedWithBlock(world, x, y, z, collider);
	}
}
