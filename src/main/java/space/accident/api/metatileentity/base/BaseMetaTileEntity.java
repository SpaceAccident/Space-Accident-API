package space.accident.api.metatileentity.base;


import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.ReflectionHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import org.jetbrains.annotations.NotNull;
import space.accident.api.API;
import space.accident.api.enums.Textures;
import space.accident.api.enums.Values;
import space.accident.api.graphs.GenerateNodeMap;
import space.accident.api.graphs.GenerateNodeMapPower;
import space.accident.api.graphs.Node;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.IDebugableTileEntity;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.ITileWailaProvider;
import space.accident.api.interfaces.tileentity.energy.IEnergyTileConnected;
import space.accident.api.objects.ItemStackData;
import space.accident.api.sound.Sounds;
import space.accident.api.util.*;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.NumberUtils;
import space.accident.extensions.PlayerUtils;
import space.accident.extensions.StringUtils;
import space.accident.main.events.ClientEvents;
import space.accident.main.network.Packet_TileEntity;
import space.accident.structurelib.alignment.IAlignment;
import space.accident.structurelib.alignment.IAlignmentLimits;
import space.accident.structurelib.alignment.IAlignmentProvider;
import space.accident.structurelib.alignment.constructable.IConstructable;
import space.accident.structurelib.alignment.constructable.IConstructableProvider;
import space.accident.structurelib.alignment.enumerable.ExtendedFacing;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

import static ic2.api.info.Info.itemEnergy;
import static space.accident.api.API.sSoundList;
import static space.accident.api.enums.Values.V;
import static space.accident.main.IntegrationConstants.AE2;
import static space.accident.main.IntegrationConstants.isAE2Loaded;
import static space.accident.main.SpaceAccidentApi.NETWORK;
import static space.accident.structurelib.util.XSTR.XSTR_INSTANCE;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the main TileEntity for EVERYTHING.
 */
@Optional.InterfaceList(value = {@Optional.Interface(iface = "appeng.api.networking.security.IActionHost", modid = AE2, striprefs = true), @Optional.Interface(iface = "appeng.me.helpers.IGridProxyable", modid = AE2, striprefs = true)})
public class BaseMetaTileEntity extends CommonMetaTileEntity implements ITile, IActionHost, IGridProxyable, IAlignmentProvider, IConstructableProvider, IDebugableTileEntity, ITileWailaProvider {
	private static final Field ENTITY_ITEM_HEALTH_FIELD = ReflectionHelper.findField(EntityItem.class, "health", "field_70291_e");
	private final boolean[] mActiveEUInputs = new boolean[]{false, false, false, false, false, false};
	private final boolean[] mActiveEUOutputs = new boolean[]{false, false, false, false, false, false};
	private final int[] mTimeStatistics = new int[API.TICKS_FOR_LAG_AVERAGING];
	public long mLastSoundTick = 0;
	public boolean mWasShutdown = false;
	protected MetaTileEntity mMetaTileEntity;
	protected long mStoredEnergy = 0, mStoredSteam = 0;
	protected int mAverageEUInputIndex = 0, mAverageEUOutputIndex = 0;
	protected boolean mReleaseEnergy = false;
	protected long[] mAverageEUInput = new long[]{0, 0, 0, 0, 0}, mAverageEUOutput = new long[]{0, 0, 0, 0, 0};
	private boolean mHasEnoughEnergy = true, mRunningThroughTick = false, mInputDisabled = false, mOutputDisabled = false, mMuffler = false, mLockUpgrade = false;
	private boolean mActive = false, mWorkUpdate = false, mSteamConverter = false, mWorks = true;
	private boolean oRedstone = false;
	private int mColor = 0, oColor = 0, oStrongRedstone = 0, oRedstoneData = 63, oTextureData = 0, oUpdateData = 0, oTexturePage = 0;
	private int oLightValueClient = -1, oLightValue = -1, mLightValue = 0, mOtherUpgrades = 0, mFacing = 0, oFacing = 0, mWorkData = 0;
	private int mDisplayErrorCode = 0, oX = 0, oY = 0, oZ = 0, mTimeStatisticsIndex = 0, mLagWarningCount = 0;
	private long oOutput = 0, mAcceptedAmperes = Long.MAX_VALUE;
	private long mLastCheckTick = 0;
	private String mOwnerName = "";
	private UUID mOwnerUuid = Utility.defaultUuid;
	private int cableUpdateDelay = 30;
	
	public BaseMetaTileEntity() {
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		try {
			super.writeToNBT(nbt);
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity.", e);
		}
		try {
			nbt.setInteger("mID", mID);
			nbt.setLong("mStoredSteam", mStoredSteam);
			nbt.setLong("mStoredEnergy", mStoredEnergy);
			writeCoverNBT(nbt, false);
			nbt.setInteger("mColor", mColor);
			nbt.setInteger("mLightValue", mLightValue);
			nbt.setInteger("mOtherUpgrades", mOtherUpgrades);
			nbt.setInteger("mWorkData", mWorkData);
			nbt.setInteger("mFacing", mFacing);
			nbt.setString("mOwnerName", mOwnerName);
			nbt.setString("mOwnerUuid", mOwnerUuid == null ? "" : mOwnerUuid.toString());
			nbt.setBoolean("mLockUpgrade", mLockUpgrade);
			nbt.setBoolean("mMuffler", mMuffler);
			nbt.setBoolean("mSteamConverter", mSteamConverter);
			nbt.setBoolean("mActive", mActive);
			nbt.setBoolean("mWorks", !mWorks);
			nbt.setBoolean("mInputDisabled", mInputDisabled);
			nbt.setBoolean("mOutputDisabled", mOutputDisabled);
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity.", e);
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
			mSidedRedstone = (hasValidMetaTileEntity() && mMetaTileEntity.hasSidedRedstoneOutputBehavior() ? new int[]{0, 0, 0, 0, 0, 0} : new int[]{15, 15, 15, 15, 15, 15});
		} else {
			if (id <= 0) mID = (short) nbt.getInteger("mID");
			else mID = id;
			mStoredSteam  = nbt.getLong("mStoredSteam");
			mStoredEnergy = nbt.getLong("mStoredEnergy");
			mColor        = nbt.getInteger("mColor");
			mLightValue   = nbt.getInteger("mLightValue");
			mWorkData     = nbt.getInteger("mWorkData");
			mFacing       = oFacing = nbt.getInteger("mFacing");
			mOwnerName    = nbt.getString("mOwnerName");
			try {
				mOwnerUuid = UUID.fromString(nbt.getString("mOwnerUuid"));
			} catch (IllegalArgumentException e) {
				mOwnerUuid = null;
			}
			mLockUpgrade    = nbt.getBoolean("mLockUpgrade");
			mMuffler        = nbt.getBoolean("mMuffler");
			mSteamConverter = nbt.getBoolean("mSteamConverter");
			mActive         = nbt.getBoolean("mActive");
			mWorks          = !nbt.getBoolean("mWorks");
			mInputDisabled  = nbt.getBoolean("mInputDisabled");
			mOutputDisabled = nbt.getBoolean("mOutputDisabled");
			mOtherUpgrades  = nbt.getInteger("mOtherUpgrades") + nbt.getInteger("mBatteries") + nbt.getInteger("mLiBatteries");
			
			readCoverNBT(nbt);
			loadMetaTileNBT(nbt);
		}
		
		if (mCoverData == null || mCoverData.length != 6) mCoverData = new ISerializableObject[6];
		if (mCoverSides.length != 6) mCoverSides = new int[]{0, 0, 0, 0, 0, 0};
		if (mSidedRedstone.length != 6) if (hasValidMetaTileEntity() && mMetaTileEntity.hasSidedRedstoneOutputBehavior()) mSidedRedstone = new int[]{0, 0, 0, 0, 0, 0};
		else mSidedRedstone = new int[]{15, 15, 15, 15, 15, 15};
		
		updateCoverBehavior();
	}
	
	/**
	 * Used for ticking special BaseMetaTileEntities, which need that for Energy Conversion
	 * It's called right before onPostTick()
	 */
	public void updateStatus() {
		//
	}
	
	/**
	 * Called when trying to charge Items
	 */
	public void chargeItem(ItemStack stack) {
		decreaseStoredEU(ModHandler.chargeElectricItem(stack, (int) Math.min(Integer.MAX_VALUE, getStoredEU()), (int) Math.min(Integer.MAX_VALUE, mMetaTileEntity.getOutputTier()), false, false), true);
	}
	
	/**
	 * Called when trying to discharge Items
	 */
	public void dischargeItem(ItemStack stack) {
		increaseStoredEnergyUnits(ModHandler.dischargeElectricItem(stack, (int) Math.min(Integer.MAX_VALUE, getEUCapacity() - getStoredEU()), (int) Math.min(Integer.MAX_VALUE, mMetaTileEntity.getInputTier()), false, false, false), true);
	}
	
	protected boolean isRainPossible() {
		BiomeGenBase biome = getCurrentBiome();
		// see net.minecraft.client.renderer.EntityRenderer.renderRainSnow
		return biome.rainfall > 0 && (biome.canSpawnLightningBolt() || biome.getEnableSnow());
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (!hasValidMetaTileEntity()) {
			if (mMetaTileEntity == null) return;
			mMetaTileEntity.setBaseMetaTileEntity(this);
		}
		
		mRunningThroughTick = true;
		long tTime = System.nanoTime();
		final boolean aSideServer = isServerSide();
		final boolean aSideClient = isClientSide();
		
		try {
			if (hasValidMetaTileEntity()) {
				if (mTickTimer++ == 0) {
					oX = xCoord;
					oY = yCoord;
					oZ = zCoord;
					if (aSideServer) {
						checkDropCover();
					} else {
						requestCoverDataIfNeeded();
					}
					worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
					mMetaTileEntity.onFirstTick(this);
					if (!hasValidMetaTileEntity()) {
						mRunningThroughTick = false;
						return;
					}
				}
				if (aSideClient) {
					if (mColor != oColor) {
						mMetaTileEntity.onColorChangeClient(oColor = mColor);
						issueTextureUpdate();
					}
					
					if (mLightValue != oLightValueClient) {
						worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, mLightValue);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord + 1, yCoord, zCoord);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord - 1, yCoord, zCoord);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord + 1, zCoord);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord - 1, zCoord);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord + 1);
						worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord - 1);
						oLightValueClient = mLightValue;
						issueTextureUpdate();
					}
					
					if (mNeedsUpdate) {
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						//worldObj.func_147479_m(xCoord, yCoord, zCoord);
						mNeedsUpdate = false;
					}
				}
				if (aSideServer && mTickTimer > 10) {
					if (!doCoverThings()) {
						mRunningThroughTick = false;
						return;
					}
				}
				if (aSideServer) {
					if (++mAverageEUInputIndex >= mAverageEUInput.length) mAverageEUInputIndex = 0;
					if (++mAverageEUOutputIndex >= mAverageEUOutput.length) mAverageEUOutputIndex = 0;
					
					mAverageEUInput[mAverageEUInputIndex]   = 0;
					mAverageEUOutput[mAverageEUOutputIndex] = 0;
				}
				
				mMetaTileEntity.onPreTick(this, mTickTimer);
				
				if (!hasValidMetaTileEntity()) {
					mRunningThroughTick = false;
					return;
				}
				if (aSideServer) {
					if (mRedstone != oRedstone || mTickTimer == 10) {
						updateCoverBehavior();
						oRedstone = mRedstone;
						issueBlockUpdate();
					}
					
					if (xCoord != oX || yCoord != oY || zCoord != oZ) {
						oX = xCoord;
						oY = yCoord;
						oZ = zCoord;
						issueClientUpdate();
						clearTileEntityBuffer();
					}
					
					if (mFacing != oFacing) {
						oFacing = mFacing;
						checkDropCover();
						issueBlockUpdate();
					}
					
					if (mTickTimer > 20 && mMetaTileEntity.isElectric()) {
						mAcceptedAmperes = 0;
						
						if (getOutputVoltage() != oOutput) {
							oOutput = getOutputVoltage();
						}
						
						if (mMetaTileEntity.isEnetOutput() || mMetaTileEntity.isEnetInput()) {
							for (int i = 0; i < 6; i++) {
								boolean temp = isEnergyInputSide(i);
								if (temp != mActiveEUInputs[i]) {
									mActiveEUInputs[i] = temp;
								}
								temp = isEnergyOutputSide(i);
								if (temp != mActiveEUOutputs[i]) {
									mActiveEUOutputs[i] = temp;
								}
							}
						}
						
						
						if (mMetaTileEntity.isEnetOutput() && oOutput > 0) {
							final long tOutputVoltage = Math.max(oOutput, oOutput + (1L << Math.max(0, NumberUtils.getTier(oOutput) - 1)));
							final long tUsableAmperage = Math.min(getOutputAmperage(), (getStoredEU() - mMetaTileEntity.getMinimumStoredEU()) / tOutputVoltage);
							if (tUsableAmperage > 0) {
								final long tEU = tOutputVoltage * Util.emitEnergyToNetwork(oOutput, tUsableAmperage, this);
								mAverageEUOutput[mAverageEUOutputIndex] += tEU;
								decreaseStoredEU(tEU, true);
							}
						}
						if (getEUCapacity() > 0) {
							if (API.sMachineFireExplosions && getRandomNumber(1000) == 0) {
								final Block tBlock = getBlockAtSide(getRandomNumber(6));
								if (tBlock instanceof BlockFire) doEnergyExplosion();
							}
							
							if (!hasValidMetaTileEntity()) {
								mRunningThroughTick = false;
								return;
							}
							
							if (getRandomNumber(1000) == 0 && isRainPossible()) {
								final int precipitationHeightAtSide2 = worldObj.getPrecipitationHeight(xCoord, zCoord - 1);
								final int precipitationHeightAtSide3 = worldObj.getPrecipitationHeight(xCoord, zCoord + 1);
								final int precipitationHeightAtSide4 = worldObj.getPrecipitationHeight(xCoord - 1, zCoord);
								final int precipitationHeightAtSide5 = worldObj.getPrecipitationHeight(xCoord + 1, zCoord);
								
								if ((getCoverIDAtSide(1) == 0 && worldObj.getPrecipitationHeight(xCoord, zCoord) - 2 < yCoord) || (getCoverIDAtSide(2) == 0 && precipitationHeightAtSide2 - 1 < yCoord && precipitationHeightAtSide2 > -1) || (getCoverIDAtSide(3) == 0 && precipitationHeightAtSide3 - 1 < yCoord && precipitationHeightAtSide3 > -1) || (getCoverIDAtSide(4) == 0 && precipitationHeightAtSide4 - 1 < yCoord && precipitationHeightAtSide4 > -1) || (getCoverIDAtSide(5) == 0 && precipitationHeightAtSide5 - 1 < yCoord && precipitationHeightAtSide5 > -1)) {
									if (API.sMachineRainExplosions && worldObj.isRaining()) {
										if (getRandomNumber(10) == 0) {
											SpaceLog.exp.println("Machine at: " + this.getX() + " | " + this.getY() + " | " + this.getZ() + " DIMID: " + this.worldObj.provider.dimensionId + " explosion due to rain!");
											doEnergyExplosion();
										} else {
											SpaceLog.exp.println("Machine at: " + this.getX() + " | " + this.getY() + " | " + this.getZ() + " DIMID: " + this.worldObj.provider.dimensionId + "  set to Fire due to rain!");
											setOnFire();
										}
									}
									if (!hasValidMetaTileEntity()) {
										mRunningThroughTick = false;
										return;
									}
									if (API.sMachineThunderExplosions && worldObj.isThundering() && getRandomNumber(3) == 0) {
										SpaceLog.exp.println("Machine at: " + this.getX() + " | " + this.getY() + " | " + this.getZ() + " DIMID: " + this.worldObj.provider.dimensionId + " explosion due to Thunderstorm!");
										doEnergyExplosion();
									}
								}
							}
						}
					}
					
					if (!hasValidMetaTileEntity()) {
						mRunningThroughTick = false;
						return;
					}
				}
				if (aSideServer) {
					if (mMetaTileEntity.dechargerSlotCount() > 0 && getStoredEU() < getEUCapacity()) {
						for (int i = mMetaTileEntity.dechargerSlotStartIndex(), k = mMetaTileEntity.dechargerSlotCount() + i; i < k; i++) {
							if (mMetaTileEntity.mInventory[i] != null && getStoredEU() < getEUCapacity()) {
								dischargeItem(mMetaTileEntity.mInventory[i]);
								if (itemEnergy.getEnergyValue(mMetaTileEntity.mInventory[i]) > 0) {
									if ((getStoredEU() + itemEnergy.getEnergyValue(mMetaTileEntity.mInventory[i])) < getEUCapacity()) {
										increaseStoredEnergyUnits((long) itemEnergy.getEnergyValue(mMetaTileEntity.mInventory[i]), false);
										mMetaTileEntity.mInventory[i].stackSize--;
										mInventoryChanged = true;
									}
								}
								if (mMetaTileEntity.mInventory[i].stackSize <= 0) {
									mMetaTileEntity.mInventory[i] = null;
									mInventoryChanged             = true;
								}
							}
						}
					}
				}
				if (aSideServer) {
					if (mMetaTileEntity.rechargerSlotCount() > 0 && getStoredEU() > 0) {
						for (int i = mMetaTileEntity.rechargerSlotStartIndex(), k = mMetaTileEntity.rechargerSlotCount() + i; i < k; i++) {
							if (getStoredEU() > 0 && mMetaTileEntity.mInventory[i] != null) {
								chargeItem(mMetaTileEntity.mInventory[i]);
								if (mMetaTileEntity.mInventory[i].stackSize <= 0) {
									mMetaTileEntity.mInventory[i] = null;
									mInventoryChanged             = true;
								}
							}
						}
					}
				}
				updateStatus();
				if (!hasValidMetaTileEntity()) {
					mRunningThroughTick = false;
					return;
				}
				mMetaTileEntity.onPostTick(this, mTickTimer);
				if (!hasValidMetaTileEntity()) {
					mRunningThroughTick = false;
					return;
				}
				if (aSideServer) {
					if (mTickTimer > 20 && cableUpdateDelay == 0) {
						generatePowerNodes();
					}
					cableUpdateDelay--;
					if (mTickTimer % 10 == 0) {
						sendClientData();
					}
					
					if (mTickTimer > 10) {
						int tData = (mFacing & 7) | (mActive ? 8 : 0) | (mRedstone ? 16 : 0) | (mLockUpgrade ? 32 : 0) | (mWorks ? 64 : 0);
						if (tData != oTextureData) sendBlockEvent(ClientEvents.CHANGE_COMMON_DATA, oTextureData = tData);
						
						tData = mMetaTileEntity.getUpdateData();
						if (tData != oUpdateData) sendBlockEvent(ClientEvents.CHANGE_CUSTOM_DATA, oUpdateData = tData);
						if (mMetaTileEntity instanceof HatchBase) {
							tData = ((HatchBase) mMetaTileEntity).getTexturePage();
							if (tData != oTexturePage) sendBlockEvent(ClientEvents.CHANGE_CUSTOM_DATA, (oTexturePage = tData) | 0x80);//set last bit as a flag for page
						}
						if (mColor != oColor) sendBlockEvent(ClientEvents.CHANGE_COLOR, oColor = mColor);
						tData = ((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0);
						if (tData != oRedstoneData) sendBlockEvent(ClientEvents.CHANGE_REDSTONE_OUTPUT, oRedstoneData = tData);
						if (mLightValue != oLightValue) {
							worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, mLightValue);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord + 1, yCoord, zCoord);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord - 1, yCoord, zCoord);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord + 1, zCoord);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord - 1, zCoord);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord + 1);
							worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord - 1);
							issueTextureUpdate();
							sendBlockEvent(ClientEvents.CHANGE_LIGHT, oLightValue = mLightValue);
						}
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
		
		if (aSideServer && hasValidMetaTileEntity()) {
			tTime = System.nanoTime() - tTime;
			if (mTimeStatistics.length > 0) mTimeStatistics[mTimeStatisticsIndex = (mTimeStatisticsIndex + 1) % mTimeStatistics.length] = (int) tTime;
			if (tTime > 0 && tTime > (API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING * 1000000L) && mTickTimer > 1000 && getMetaTile().doTickProfilingMessageDuringThisTick() && mLagWarningCount++ < 10) SpaceLog.FML_LOGGER.warn("WARNING: Possible Lag Source at [" + xCoord + ", " + yCoord + ", " + zCoord + "] in Dimension " + worldObj.provider.dimensionId + " with " + tTime + "ns caused by an instance of " + getMetaTile().getClass());
		}
		
		mWorkUpdate = mInventoryChanged = mRunningThroughTick = false;
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (hasValidMetaTileEntity() && getMetaTile() != null) {
			getMetaTile().getWailaBody(itemStack, currentTip, accessor, config);
		}
		super.getWailaBody(itemStack, currentTip, accessor, config);
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		super.getWailaNBTData(player, tile, tag, world, x, y, z);
		if (hasValidMetaTileEntity() && getMetaTile() != null) {
			getMetaTile().getWailaNBTData(player, tile, tag, world, x, y, z);
		}
	}
	
	private void sendClientData() {
		if (mSendClientData) {
			NETWORK.sendPacketToAllPlayersInRange(worldObj, new Packet_TileEntity(xCoord, (short) yCoord, zCoord, mID, mCoverSides[0], mCoverSides[1], mCoverSides[2], mCoverSides[3], mCoverSides[4], mCoverSides[5], oTextureData = (mFacing & 7) | (mActive ? 8 : 0) | (mRedstone ? 16 : 0) | (mLockUpgrade ? 32 : 0) | (mWorks ? 64 : 0), oTexturePage = (hasValidMetaTileEntity() && mMetaTileEntity instanceof HatchBase) ? ((HatchBase) mMetaTileEntity).getTexturePage() : 0, oUpdateData = hasValidMetaTileEntity() ? mMetaTileEntity.getUpdateData() : 0, oRedstoneData = ((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0), oColor = mColor), xCoord, zCoord);
			mSendClientData = false;
		}
		sendCoverDataIfNeeded();
	}
	
	public final void receiveMetaTileEntityData(short id, int aCover0, int aCover1, int aCover2, int aCover3, int aCover4, int aCover5, int aTextureData, int aTexturePage, int aUpdateData, int aRedstoneData, int aColorData) {
		issueTextureUpdate();
		if (mID != id && id > 0) {
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
		receiveClientEvent(ClientEvents.CHANGE_CUSTOM_DATA, aUpdateData & 0x7F);
		receiveClientEvent(ClientEvents.CHANGE_CUSTOM_DATA, aTexturePage | 0x80);
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
				SpaceLog.err.println("Encountered Exception while receiving Data from the Server, the Client should've been crashed by now, but I prevented that. Please report bug!");
				e.printStackTrace(SpaceLog.err);
			}
		}
		
		if (isClientSide()) {
			issueTextureUpdate();
			switch (aEventID) {
				case ClientEvents.CHANGE_COMMON_DATA:
					mFacing = value & 7;
					mActive = ((value & 8) != 0);
					mRedstone = ((value & 16) != 0);
					//mLockUpgrade	= ((value&32) != 0);
					mWorks = ((value & 64) != 0);
					break;
				case ClientEvents.CHANGE_CUSTOM_DATA:
					if (hasValidMetaTileEntity()) {
						if ((value & 0x80) == 0) //Is texture index
							mMetaTileEntity.onValueUpdate(value & 0x7F);
						else if (mMetaTileEntity instanceof HatchBase)//is texture page and hatch
							((HatchBase) mMetaTileEntity).onTexturePageUpdate(value & 0x7F);
					}
					break;
				case ClientEvents.CHANGE_COLOR:
					if (value > 16 || value < 0) value = 0;
					mColor = value;
					break;
				case ClientEvents.CHANGE_REDSTONE_OUTPUT:
					mSidedRedstone[0] = (value & 1) == 1 ? 15 : 0;
					mSidedRedstone[1] = (value & 2) == 2 ? 15 : 0;
					mSidedRedstone[2] = (value & 4) == 4 ? 15 : 0;
					mSidedRedstone[3] = (value & 8) == 8 ? 15 : 0;
					mSidedRedstone[4] = (value & 16) == 16 ? 15 : 0;
					mSidedRedstone[5] = (value & 32) == 32 ? 15 : 0;
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
				case ClientEvents.CHANGE_LIGHT:
					mLightValue = value;
					break;
			}
		}
		return true;
	}
	
	@Override
	public ArrayList<String> getDebugInfo(EntityPlayer player, int logLevel) {
		final ArrayList<String> tList = new ArrayList<>();
		if (logLevel > 2) {
			tList.add("Meta-ID: " + EnumChatFormatting.BLUE + mID + EnumChatFormatting.RESET + (canAccessData() ? EnumChatFormatting.GREEN + " valid" + EnumChatFormatting.RESET : EnumChatFormatting.RED + " invalid" + EnumChatFormatting.RESET) + (mMetaTileEntity == null ? EnumChatFormatting.RED + " MetaTileEntity == null!" + EnumChatFormatting.RESET : " "));
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
					// Uncomment this line to print out tick-by-tick times.
					//tList.add("tTime " + tTime);
				}
				tList.add("Average CPU load of ~" + NumberUtils.format(tAverageTime / mTimeStatistics.length) + "ns over " + NumberUtils.format(mTimeStatistics.length) + " ticks with worst" + " time of " + NumberUtils.format(tWorstTime) + "ns.");
				tList.add("Recorded " + NumberUtils.format(mMetaTileEntity.mSoundRequests) + " sound requests in " + NumberUtils.format(mTickTimer - mLastCheckTick) + " ticks.");
				mLastCheckTick                 = mTickTimer;
				mMetaTileEntity.mSoundRequests = 0;
			}
			if (mLagWarningCount > 0) {
				tList.add("Caused " + (mLagWarningCount >= 10 ? "more than 10" : mLagWarningCount) + " Lag Spike Warnings (anything taking longer than " + API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING + "ms) on the Server.");
			}
			tList.add("Is" + (mMetaTileEntity.isAccessAllowed(player) ? " " : EnumChatFormatting.RED + " not " + EnumChatFormatting.RESET) + "accessible for you");
		}
		if (logLevel > 0) {
			if (getSteamCapacity() > 0) tList.add(NumberUtils.format(getStoredSteam()) + " of " + NumberUtils.format(getSteamCapacity()) + " Steam");
			tList.add("Machine is " + (mActive ? EnumChatFormatting.GREEN + "active" + EnumChatFormatting.RESET : EnumChatFormatting.RED + "inactive" + EnumChatFormatting.RESET));
			if (!mHasEnoughEnergy) tList.add(EnumChatFormatting.RED + "ATTENTION: This Device needs more power." + EnumChatFormatting.RESET);
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
		return NumberUtils.getOppositeSide(mFacing);
	}
	
	@Override
	public int getFrontFace() {
		return mFacing;
	}
	
	@Override
	public void setFrontFace(int face) {
		if (isValidFace(face)) {
			mFacing = face;
			mMetaTileEntity.onFacingChange();
			
			cableUpdateDelay = 10;
			
			if (mMetaTileEntity.shouldTriggerBlockUpdate()) {
				// If we're triggering a block update this will call onMachineBlockUpdate()
				API.causeMachineUpdate(worldObj, xCoord, yCoord, zCoord);
			} else {
				// If we're not trigger a cascading one, call the update here.
				onMachineBlockUpdate();
			}
		}
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
		mInventoryChanged = true;
		if (canAccessData()) {
			markDirty();
			mMetaTileEntity.setInventorySlotContents(index, worldObj.isRemote ? stack : OreDictUnifier.setStack(true, stack));
		}
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
	public void openInventory() {
		if (canAccessData()) mMetaTileEntity.onOpenGUI();
	}
	
	@Override
	public void closeInventory() {
		if (canAccessData()) mMetaTileEntity.onCloseGUI();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return canAccessData() && playerOwnsThis(player, false) && mTickTimer > 40 && getTileEntityOffset(0, 0, 0) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64 && mMetaTileEntity.isAccessAllowed(player);
	}
	
	@Override
	public void validate() {
		super.validate();
		mTickTimer = 0;
	}
	
	@Override
	public void invalidate() {
		tileEntityInvalid = false;
		if (canAccessData()) {
			if (isAE2Loaded) invalidateAE();
			mMetaTileEntity.onRemoval();
			mMetaTileEntity.setBaseMetaTileEntity(null);
		}
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (isAE2Loaded) onChunkUnloadAE();
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		final ItemStack stack = getStackInSlot(slot);
		if (stack != null) setInventorySlotContents(slot, null);
		return stack;
	}
	
	/**
	 * Checks validity of meta tile and delegates to it
	 */
	@Override
	public void onMachineBlockUpdate() {
		if (canAccessData()) mMetaTileEntity.onMachineBlockUpdate();
		cableUpdateDelay = 10;
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
		mWorks       = true;
		mWasShutdown = false;
	}
	
	@Override
	public void disableWorking() {
		mWorks = false;
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
		return mWorkData;
	}
	
	@Override
	public void setWorkDataValue(int value) {
		mWorkData = value;
	}
	
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
		return mActive;
	}
	
	@Override
	public void setActive(boolean active) {
		mActive = active;
	}
	
	@Override
	public long getTick() {
		return mTickTimer;
	}
	
	@Override
	public boolean decreaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooLessEnergy) {
		if (!canAccessData()) return false;
		return mHasEnoughEnergy = decreaseStoredEU(aEnergy, aIgnoreTooLessEnergy) || decreaseStoredSteam(aEnergy, false) || (aIgnoreTooLessEnergy && (decreaseStoredSteam(aEnergy, true)));
	}
	
	@Override
	public boolean increaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooMuchEnergy) {
		if (!canAccessData()) return false;
		if (getStoredEU() < getEUCapacity() || aIgnoreTooMuchEnergy) {
			setStoredEU(mMetaTileEntity.getEUVar() + aEnergy);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean inputEnergyFrom(int side) {
		return inputEnergyFrom(side, true);
	}
	
	@Override
	public boolean inputEnergyFrom(int side, boolean waitForActive) {
		if (side == 6) return true;
		if (isServerSide() && waitForActive) return ((side >= 0 && side < 6) && mActiveEUInputs[side]) && !mReleaseEnergy;
		return isEnergyInputSide(side);
	}
	
	@Override
	public boolean outputsEnergyTo(int side) {
		return outputsEnergyTo(side, true);
	}
	
	@Override
	public boolean outputsEnergyTo(int side, boolean waitForActive) {
		if (side == 6) return true;
		if (isServerSide() && waitForActive) return ((side >= 0 && side < 6) && mActiveEUOutputs[side]) || mReleaseEnergy;
		return isEnergyOutputSide(side);
	}
	
	@Override
	public boolean isEnergyOutput() {
		return mMetaTileEntity != null && mMetaTileEntity.isEnetOutput();
	}
	
	@Override
	public boolean isEnergyInput() {
		return mMetaTileEntity != null && mMetaTileEntity.isEnetInput();
	}
	
	public void generatePowerNodes() {
		if (isServerSide() && (isEnergyInput() || isEnergyOutput())) {
			final int time = MinecraftServer.getServer().getTickCounter();
			for (int i = 0; i < 6; i++) {
				if (outputsEnergyTo(i, false) || inputEnergyFrom(i, false)) {
					final ITile TE = getITileAtSide(i);
					if (TE instanceof BaseMetaPipeEntity) {
						final Node node = ((BaseMetaPipeEntity) TE).getNode();
						if (node == null) {
							new GenerateNodeMapPower((BaseMetaPipeEntity) TE);
						} else if (node.mCreationTime != time) {
							GenerateNodeMap.clearNodeMap(node, -1);
							new GenerateNodeMapPower((BaseMetaPipeEntity) TE);
						}
					}
				}
			}
		}
	}
	
	@Override
	public long getOutputAmperage() {
		if (canAccessData() && mMetaTileEntity.isElectric()) return mMetaTileEntity.maxAmperesOut();
		return 0;
	}
	
	@Override
	public long getOutputVoltage() {
		if (canAccessData() && mMetaTileEntity.isElectric() && mMetaTileEntity.isEnetOutput()) return mMetaTileEntity.maxEUOutput();
		return 0;
	}
	
	@Override
	public long getInputAmperage() {
		if (canAccessData() && mMetaTileEntity.isElectric()) return mMetaTileEntity.maxAmperesIn();
		return 0;
	}
	
	@Override
	public long getInputVoltage() {
		if (canAccessData() && mMetaTileEntity.isElectric()) return mMetaTileEntity.maxEUInput();
		return Integer.MAX_VALUE;
	}
	
	@Override
	public boolean increaseStoredSteam(long aEnergy, boolean aIgnoreTooMuchEnergy) {
		if (!canAccessData()) return false;
		if (mMetaTileEntity.getSteamVar() < getSteamCapacity() || aIgnoreTooMuchEnergy) {
			setStoredSteam(mMetaTileEntity.getSteamVar() + aEnergy);
			return true;
		}
		return false;
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
		if (canAccessData()) return Math.min(mMetaTileEntity.getEUVar(), getEUCapacity());
		return 0;
	}
	
	@Override
	public long getEUCapacity() {
		if (canAccessData()) return mMetaTileEntity.maxEUStore();
		return 0;
	}
	
	@Override
	public long getStoredSteam() {
		if (canAccessData()) return Math.min(mMetaTileEntity.getSteamVar(), getSteamCapacity());
		return 0;
	}
	
	@Override
	public long getSteamCapacity() {
		if (canAccessData()) return mMetaTileEntity.maxSteamStore();
		return 0;
	}
	
	@Override
	public ITexture[] getTexture(Block block, int side) {
		final ITexture coverTexture = getCoverTexture(side);
		final ITexture[] textureUncovered = hasValidMetaTileEntity() ? mMetaTileEntity.getTexture(this, side, mFacing, mColor - 1, mActive, getOutputRedStoneSignal(side) > 0) : Textures.BlockIcons.ERROR_RENDERING;
		final ITexture[] textureCovered;
		if (coverTexture != null) {
			textureCovered                          = Arrays.copyOf(textureUncovered, textureUncovered.length + 1);
			textureCovered[textureUncovered.length] = coverTexture;
			return textureCovered;
		} else {
			return textureUncovered;
		}
	}
	
	private boolean isEnergyInputSide(int side) {
		if (side >= 0 && side < 6) {
			if (!getCoverBehaviorAtSideNew(side).letsEnergyIn(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)) return false;
			if (isInvalidTile() || mReleaseEnergy) return false;
			if (canAccessData() && mMetaTileEntity.isElectric() && mMetaTileEntity.isEnetInput()) return mMetaTileEntity.isInputFacing(side);
		}
		return false;
	}
	
	private boolean isEnergyOutputSide(int side) {
		if (side >= 0 && side < 6) {
			if (!getCoverBehaviorAtSideNew(side).letsEnergyOut(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)) return false;
			if (isInvalidTile() || mReleaseEnergy) return mReleaseEnergy;
			if (canAccessData() && mMetaTileEntity.isElectric() && mMetaTileEntity.isEnetOutput()) return mMetaTileEntity.isOutputFacing(side);
		}
		return false;
	}
	
	@Override
	protected boolean hasValidMetaTileEntity() {
		return mMetaTileEntity != null && mMetaTileEntity.getBaseMetaTileEntity() == this;
	}
	
	@Override
	protected boolean canAccessData() {
		return !isDead && hasValidMetaTileEntity();
	}
	
	public boolean setStoredEU(long aEnergy) {
		if (!canAccessData()) return false;
		if (aEnergy < 0) aEnergy = 0;
		mMetaTileEntity.setEUVar(aEnergy);
		return true;
	}
	
	public boolean setStoredSteam(long aEnergy) {
		if (!canAccessData()) return false;
		if (aEnergy < 0) aEnergy = 0;
		mMetaTileEntity.setSteamVar(aEnergy);
		return true;
	}
	
	public boolean decreaseStoredEU(long aEnergy, boolean aIgnoreTooLessEnergy) {
		if (!canAccessData()) {
			return false;
		}
		if (mMetaTileEntity.getEUVar() - aEnergy >= 0 || aIgnoreTooLessEnergy) {
			setStoredEU(mMetaTileEntity.getEUVar() - aEnergy);
			if (mMetaTileEntity.getEUVar() < 0) {
				setStoredEU(0);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean decreaseStoredSteam(long aEnergy, boolean aIgnoreTooLessEnergy) {
		if (!canAccessData()) return false;
		if (mMetaTileEntity.getSteamVar() - aEnergy >= 0 || aIgnoreTooLessEnergy) {
			setStoredSteam(mMetaTileEntity.getSteamVar() - aEnergy);
			if (mMetaTileEntity.getSteamVar() < 0) {
				setStoredSteam(0);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean playerOwnsThis(EntityPlayer player, boolean aCheckPrecicely) {
		if (!canAccessData()) return false;
		if (aCheckPrecicely || privateAccess() || (mOwnerName.length() == 0)) if ((mOwnerName.length() == 0) && isServerSide()) {
			setOwnerName(player.getDisplayName());
			setOwnerUuid(player.getUniqueID());
		} else return !privateAccess() || player.getDisplayName().equals("Player") || mOwnerName.equals("Player") || mOwnerName.equals(player.getDisplayName());
		return true;
	}
	
	public boolean privateAccess() {
		if (!canAccessData()) return mLockUpgrade;
		return mLockUpgrade || mMetaTileEntity.ownerControl();
	}
	
	public void doEnergyExplosion() {
		if (getUniversalEnergyCapacity() > 0 && getUniversalEnergyStored() >= getUniversalEnergyCapacity() / 5) {
			SpaceLog.exp.println("Energy Explosion, injected " + getUniversalEnergyStored() + "EU >= " + getUniversalEnergyCapacity() / 5D + "Capacity of the Machine!");
			
			doExplosion(oOutput * (getUniversalEnergyStored() >= getUniversalEnergyCapacity() ? 4 : getUniversalEnergyStored() >= getUniversalEnergyCapacity() / 2 ? 2 : 1));
		}
	}
	
	@Override
	public void doExplosion(long amount) {
		if (canAccessData()) {
			// This is only for Electric Machines
			if (API.sMachineWireFire && mMetaTileEntity.isElectric()) {
				try {
					mReleaseEnergy = true;
					IEnergyTileConnected.Util.emitEnergyToNetwork(V[5], Math.max(1, getStoredEU() / V[5]), this);
				} catch (Exception ignored) {
				}
			}
			mReleaseEnergy = false;
			// Normal Explosion Code
			mMetaTileEntity.onExplosion();
			if (API.mExplosionItemDrop) {
				for (int i = 0; i < this.getSizeInventory(); i++) {
					final ItemStack tItem = this.getStackInSlot(i);
					if ((tItem != null) && (tItem.stackSize > 0) && (this.isValidSlot(i))) {
						dropItems(tItem);
						this.setInventorySlotContents(i, null);
					}
				}
			}
			
			mMetaTileEntity.doExplosion(amount);
		}
	}
	
	public void dropItems(ItemStack tItem) {
		if (tItem == null) return;
		final EntityItem tItemEntity = new EntityItem(this.worldObj, this.xCoord + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, this.yCoord + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, this.zCoord + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F, new ItemStack(tItem.getItem(), tItem.stackSize, tItem.getItemDamage()));
		if (tItem.hasTagCompound()) {
			tItemEntity.getEntityItem().setTagCompound((NBTTagCompound) tItem.getTagCompound().copy());
		}
		tItemEntity.motionX           = (XSTR_INSTANCE.nextGaussian() * 0.0500000007450581D);
		tItemEntity.motionY           = (XSTR_INSTANCE.nextGaussian() * 0.0500000007450581D + 0.2000000029802322D);
		tItemEntity.motionZ           = (XSTR_INSTANCE.nextGaussian() * 0.0500000007450581D);
		tItemEntity.hurtResistantTime = 999999;
		tItemEntity.lifespan          = 60000;
		try {
			if (ENTITY_ITEM_HEALTH_FIELD != null) ENTITY_ITEM_HEALTH_FIELD.setInt(tItemEntity, 99999999);
		} catch (Exception ignored) {
		}
		this.worldObj.spawnEntityInWorld(tItemEntity);
		tItem.stackSize = 0;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops() {
		final ItemStack rStack = new ItemStack(API.sBlockMachines, 1, mID);
		final NBTTagCompound tNBT = new NBTTagCompound();
		if (mMuffler) tNBT.setBoolean("mMuffler", mMuffler);
		if (mLockUpgrade) tNBT.setBoolean("mLockUpgrade", mLockUpgrade);
		if (mSteamConverter) tNBT.setBoolean("mSteamConverter", mSteamConverter);
		if (mColor > 0) tNBT.setInteger("mColor", mColor);
		if (mOtherUpgrades > 0) tNBT.setInteger("mOtherUpgrades", mOtherUpgrades);
		
		writeCoverNBT(tNBT, true);
		
		if (hasValidMetaTileEntity()) mMetaTileEntity.setItemNBT(tNBT);
		if (!tNBT.hasNoTags()) rStack.setTagCompound(tNBT);
		return new ArrayList<>(Collections.singletonList(rStack));
	}
	
	public int getUpgradeCount() {
		return (mMuffler ? 1 : 0) + (mLockUpgrade ? 1 : 0) + (mSteamConverter ? 1 : 0) + mOtherUpgrades;
	}
	
	@Override
	public boolean onRightClick(EntityPlayer player, int side, float x, float y, float z) {
		if (isClientSide()) {
			//Configure Cover, sneak can also be: screwdriver, wrench, side cutter, soldering iron
			if (player.isSneaking()) {
				final int tSide = (getCoverIDAtSide(side) == 0) ? Utility.determineWrenchingSide(side, x, y, z) : side;
				return (getCoverBehaviorAtSideNew(tSide).hasCoverGUI());
			} else if (getCoverBehaviorAtSideNew(side).onCoverRightclickClient(side, this, player, x, y, z)) {
				return true;
			}
			
			if (!getCoverBehaviorAtSideNew(side).isGUIClickable(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)) return false;
		}
		if (isServerSide()) {
			if (!privateAccess() || player.getDisplayName().equalsIgnoreCase(getOwnerName())) {
				final ItemStack tCurrentItem = player.inventory.getCurrentItem();
				if (tCurrentItem != null) {
					if (getColorization() >= 0 && Utility.areStacksEqual(new ItemStack(Items.water_bucket, 1), tCurrentItem)) {
						tCurrentItem.func_150996_a(Items.bucket);
						setColorization(getColorization() >= 16 ? -2 : -1);
						return true;
					}
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sWrenchList)) {
						if (player.isSneaking() && mMetaTileEntity instanceof BasicMachineBase && ((BasicMachineBase) mMetaTileEntity).setMainFacing(Utility.determineWrenchingSide(side, x, y, z))) {
							ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player);
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
							cableUpdateDelay = 10;
						} else if (mMetaTileEntity.onWrenchRightClick(side, Utility.determineWrenchingSide(side, x, y, z), player, x, y, z)) {
							ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player);
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
							cableUpdateDelay = 10;
						}
						return true;
					}
					
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sScrewdriverList)) {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 200, player)) {
							setCoverDataAtSide(side, getCoverBehaviorAtSideNew(side).onCoverScrewdriverClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player, x, y, z));
							mMetaTileEntity.onScrewdriverRightClick(side, player, x, y, z);
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
						}
						return true;
					}
					
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sHardHammerList)) {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
							mInputDisabled = !mInputDisabled;
							if (mInputDisabled) mOutputDisabled = !mOutputDisabled;
							PlayerUtils.sendChat(player, StringUtils.trans("086", "Auto-Input: ") + (mInputDisabled ? StringUtils.trans("087", "Disabled") : StringUtils.trans("088", "Enabled") + StringUtils.trans("089", "  Auto-Output: ") + (mOutputDisabled ? StringUtils.trans("087", "Disabled") : StringUtils.trans("088", "Enabled"))));
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.ANVIL_USE), 1.0F, -1, xCoord, yCoord, zCoord);
						}
						return true;
					}
					
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sSoftHammerList)) {
						if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
							if (mWorks) disableWorking();
							else enableWorking();
							{
								String tChat = StringUtils.trans("090", "Machine Processing: ") + (isAllowedToWork() ? StringUtils.trans("088", "Enabled") : StringUtils.trans("087", "Disabled"));
								if (getMetaTile() != null && getMetaTile().hasAlternativeModeText()) tChat = getMetaTile().getAlternativeModeText();
								PlayerUtils.sendChat(player, tChat);
							}
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.RUBBER_TRAMPOLINE), 1.0F, -1, xCoord, yCoord, zCoord);
						}
						return true;
					}
					
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sSolderingToolList)) {
						final int tSide = Utility.determineWrenchingSide(side, x, y, z);
						if (mMetaTileEntity.onSolderingToolRightClick(side, tSide, player, x, y, z)) {
							//logic handled internally
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.BATTERY_USE), 1.0F, -1, xCoord, yCoord, zCoord);
						} else if (ModHandler.useSolderingIron(tCurrentItem, player)) {
							mStrongRedstone ^= (1 << tSide);
							PlayerUtils.sendChat(player, StringUtils.trans("091", "RedStone Output at Side ") + tSide + StringUtils.trans("092", " set to: ") + ((mStrongRedstone & (1 << tSide)) != 0 ? StringUtils.trans("093", "Strong") : StringUtils.trans("094", "Weak")));
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.BATTERY_USE), 3.0F, -1, xCoord, yCoord, zCoord);
							issueBlockUpdate();
						}
						cableUpdateDelay = 10;
						return true;
					}
					
					if (ItemStackUtils.isStackInList(tCurrentItem, API.sWireCutterList)) {
						final int tSide = Utility.determineWrenchingSide(side, x, y, z);
						if (mMetaTileEntity.onWireCutterRightClick(side, tSide, player, x, y, z)) {
							//logic handled internally
							Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
						}
						cableUpdateDelay = 10;
						return true;
					}
					
					int coverSide = side;
					if (getCoverIDAtSide(side) == 0) coverSide = Utility.determineWrenchingSide(side, x, y, z);
					
					if (getCoverIDAtSide(coverSide) == 0) {
						if (ItemStackUtils.isStackInList(tCurrentItem, API.sCovers.keySet())) {
							if (API.getCoverBehaviorNew(tCurrentItem).isCoverPlaceable(coverSide, tCurrentItem, this) && mMetaTileEntity.allowCoverOnSide(coverSide, new ItemStackData(tCurrentItem))) {
								setCoverItemAtSide(coverSide, tCurrentItem);
								if (!player.capabilities.isCreativeMode) tCurrentItem.stackSize--;
								Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.WRENCH), 1.0F, -1, xCoord, yCoord, zCoord);
							}
							return true;
						}
					} else {
						if (ItemStackUtils.isStackInList(tCurrentItem, API.sCrowbarList)) {
							if (ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, player)) {
								Utility.sendSoundToPlayers(worldObj, sSoundList.get(Sounds.BREAK), 1.0F, -1, xCoord, yCoord, zCoord);
								dropCover(coverSide, side, false);
							}
							return true;
						}
					}
				} else if (player.isSneaking()) { //Sneak click, no tool -> open cover config if possible.
					side = (getCoverIDAtSide(side) == 0) ? Utility.determineWrenchingSide(side, x, y, z) : side;
					return getCoverIDAtSide(side) > 0 && getCoverBehaviorAtSideNew(side).onCoverShiftRightClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player);
				}
				
				if (getCoverBehaviorAtSideNew(side).onCoverRightClick(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this, player, x, y, z)) return true;
				
				if (!getCoverBehaviorAtSideNew(side).isGUIClickable(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)) return false;
				
				if (isUpgradable() && tCurrentItem != null) {
					//TODO
//					if (ItemList.Upgrade_Muffler.isStackEqual(player.inventory.getCurrentItem())) {
//						if (addMufflerUpgrade()) {
//							Utility.sendSoundToPlayers(worldObj, API.sSoundList.get(3), 1.0F, -1, xCoord, yCoord, zCoord);
//							if (!player.capabilities.isCreativeMode) player.inventory.getCurrentItem().stackSize--;
//						}
//						return true;
//					}
//					if (ItemList.Upgrade_Lock.isStackEqual(player.inventory.getCurrentItem())) {
//						if (isUpgradable() && !mLockUpgrade) {
//							mLockUpgrade = true;
//							setOwnerName(player.getDisplayName());
//							setOwnerUuid(player.getUniqueID());
//							Utility.sendSoundToPlayers(worldObj, API.sSoundList.get(3), 1.0F, -1, xCoord, yCoord, zCoord);
//							if (!player.capabilities.isCreativeMode) player.inventory.getCurrentItem().stackSize--;
//						}
//						return true;
//					}
				}
			}
		}
		
		try {
			if (!player.isSneaking() && hasValidMetaTileEntity()) return mMetaTileEntity.onRightclick(this, player, side, x, y, z);
		} catch (Throwable e) {
			SpaceLog.err.println("Encountered Exception while rightclicking TileEntity, the Game should've crashed now, but I prevented that. Please report bug!");
			e.printStackTrace(SpaceLog.err);
		}
		
		return false;
	}
	
	@Override
	public void onLeftClick(EntityPlayer player) {
		try {
			if (player != null && hasValidMetaTileEntity()) mMetaTileEntity.onLeftclick(this, player);
		} catch (Throwable e) {
			SpaceLog.err.println("Encountered Exception while leftclicking TileEntity, the Game should've crashed now, but I prevented that. Please report bug!");
			e.printStackTrace(SpaceLog.err);
		}
	}
	
	@Override
	public boolean isDigitalChest() {
		if (canAccessData()) return mMetaTileEntity.isDigitalChest();
		return false;
	}
	
	@Override
	public ItemStack[] getStoredItemData() {
		if (canAccessData()) return mMetaTileEntity.getStoredItemData();
		return null;
	}
	
	@Override
	public void setItemCount(int count) {
		if (canAccessData()) mMetaTileEntity.setItemCount(count);
	}
	
	@Override
	public int getMaxItemCount() {
		if (canAccessData()) return mMetaTileEntity.getMaxItemCount();
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
		return canAccessData() && (mRunningThroughTick || !mInputDisabled) && getCoverBehaviorAtSideNew(side).letsItemsIn(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), index, this) && mMetaTileEntity.canInsertItem(index, stack, side);
	}
	
	/**
	 * Can pull stack out of Slot from Side
	 */
	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return canAccessData() && (mRunningThroughTick || !mOutputDisabled) && getCoverBehaviorAtSideNew(side).letsItemsOut(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), index, this) && mMetaTileEntity.canExtractItem(index, stack, side);
	}
	
	@Override
	public boolean isUpgradable() {
		return canAccessData() && getUpgradeCount() < 8;
	}
	
	@Override
	public int getSideRedStone(int side) {
		if (mMetaTileEntity == null) return 0;
		return mMetaTileEntity.allowGeneralRedstoneOutput() ? mSidedRedstone[side] : 0;
	}
	
	@Override
	public boolean hasMufflerUpgrade() {
		return mMuffler;
	}
	
	@Override
	public boolean isMufflerUpgradable() {
		return isUpgradable() && !hasMufflerUpgrade();
	}
	
	@Override
	public boolean addMufflerUpgrade() {
		if (isMufflerUpgradable()) return mMuffler = true;
		return false;
	}
	
	@Override
	public void markInventoryBeenModified() {
		mInventoryChanged = true;
	}
	
	@Override
	public int getErrorDisplayID() {
		return mDisplayErrorCode;
	}
	
	@Override
	public void setErrorDisplayID(int errorId) {
		mDisplayErrorCode = errorId;
	}
	
	@Override
	public IMetaTile getMetaTile() {
		return hasValidMetaTileEntity() ? mMetaTileEntity : null;
	}
	
	@Override
	public void setMetaTile(IMetaTile metaTile) {
		mMetaTileEntity = (MetaTileEntity) metaTile;
	}
	
	public int getLightValue() {
		return mLightValue;
	}
	
	@Override
	public long getAverageElectricInput() {
		long rEU = 0;
		for (int i = 0; i < mAverageEUInput.length; ++i)
			if (i != mAverageEUInputIndex) rEU += mAverageEUInput[i];
		return rEU / (mAverageEUInput.length - 1);
	}
	
	@Override
	public long getAverageElectricOutput() {
		long rEU = 0;
		for (int i = 0; i < mAverageEUOutput.length; ++i)
			if (i != mAverageEUOutputIndex) rEU += mAverageEUOutput[i];
		return rEU / (mAverageEUOutput.length - 1);
	}
	
	
	@Override
	protected void updateOutputRedstoneSignal(int side) {
		if (mMetaTileEntity.hasSidedRedstoneOutputBehavior()) {
			setOutputRedStoneSignal(side, 0);
		} else {
			setOutputRedStoneSignal(side, 15);
		}
	}
	
	@Override
	public String getOwnerName() {
		if (StringUtils.isStringInvalid(mOwnerName)) return "Player";
		return mOwnerName;
	}
	
	@Override
	public String setOwnerName(String name) {
		if (StringUtils.isStringInvalid(name)) return mOwnerName = "Player";
		return mOwnerName = name;
	}
	
	@Override
	public UUID getOwnerUuid() {
		return mOwnerUuid;
	}
	
	@Override
	public void setOwnerUuid(UUID uuid) {
		mOwnerUuid = uuid;
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
		if (!canAccessData() || !mMetaTileEntity.isElectric() || !inputEnergyFrom(side) || aAmperage <= 0 || aVoltage <= 0 || getStoredEU() >= getEUCapacity() || mMetaTileEntity.maxAmperesIn() <= mAcceptedAmperes)
			return 0;
		if (aVoltage > getInputVoltage()) {
			SpaceLog.exp.println("Energy Explosion, injected " + aVoltage + "EU/t in a " + getInputVoltage() + "EU/t Machine!");
			doExplosion(aVoltage);
			return 0;
		}
		if (increaseStoredEnergyUnits(aVoltage * (aAmperage = Math.min(aAmperage, Math.min(mMetaTileEntity.maxAmperesIn() - mAcceptedAmperes, 1 + ((getEUCapacity() - getStoredEU()) / aVoltage)))), true)) {
			mAverageEUInput[mAverageEUInputIndex] += aVoltage * aAmperage;
			mAcceptedAmperes += aAmperage;
			return aAmperage;
		}
		return 0;
	}
	
	@Override
	public boolean drainEnergyUnits(int side, long aVoltage, long aAmperage) {
		if (!canAccessData() || !mMetaTileEntity.isElectric() || !outputsEnergyTo(side) || getStoredEU() - (aVoltage * aAmperage) < mMetaTileEntity.getMinimumStoredEU()) return false;
		if (decreaseStoredEU(aVoltage * aAmperage, false)) {
			mAverageEUOutput[mAverageEUOutputIndex] += aVoltage * aAmperage;
			return true;
		}
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
	
	@Override
	public int fill(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		if (mTickTimer > 5 && canAccessData() && (mRunningThroughTick || !mInputDisabled) && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidInput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidIn(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid == null ? null : aFluid.getFluid(), this))))
			return mMetaTileEntity.fill(side, aFluid, doFill);
		return 0;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, int maxDrain, boolean doDrain) {
		if (mTickTimer > 5 && canAccessData() && (mRunningThroughTick || !mOutputDisabled) && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidOutput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidOut(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), mMetaTileEntity.getFluid() == null ? null : mMetaTileEntity.getFluid().getFluid(), this))))
			return mMetaTileEntity.drain(side, maxDrain, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
		if (mTickTimer > 5 && canAccessData() && (mRunningThroughTick || !mOutputDisabled) && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidOutput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidOut(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid == null ? null : aFluid.getFluid(), this))))
			return mMetaTileEntity.drain(side, aFluid, doDrain);
		return null;
	}
	
	@Override
	public boolean canFill(ForgeDirection side, Fluid aFluid) {
		if (mTickTimer > 5 && canAccessData() && (mRunningThroughTick || !mInputDisabled) && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidInput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidIn(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid, this))))
			return mMetaTileEntity.canFill(side, aFluid);
		return false;
	}
	
	@Override
	public boolean canDrain(ForgeDirection side, Fluid aFluid) {
		if (mTickTimer > 5 && canAccessData() && (mRunningThroughTick || !mOutputDisabled) && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidOutput(side.ordinal()) && getCoverBehaviorAtSideNew(side.ordinal()).letsFluidOut(side.ordinal(), getCoverIDAtSide(side.ordinal()), getComplexCoverDataAtSide(side.ordinal()), aFluid, this))))
			return mMetaTileEntity.canDrain(side, aFluid);
		return false;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		final int tSide = side.ordinal();
		
		if (canAccessData() && (side == ForgeDirection.UNKNOWN || (mMetaTileEntity.isLiquidInput(tSide) && getCoverBehaviorAtSideNew(tSide).letsFluidIn(tSide, getCoverIDAtSide(tSide), getComplexCoverDataAtSide(tSide), null, this)) || (mMetaTileEntity.isLiquidOutput(tSide) && getCoverBehaviorAtSideNew(tSide).letsFluidOut(tSide, getCoverIDAtSide(tSide), getComplexCoverDataAtSide(tSide), null, this))))
			return mMetaTileEntity.getTankInfo(side);
		return new FluidTankInfo[]{};
	}
	
	public double getOutputEnergyUnitsPerTick() {
		return oOutput;
	}
	
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return canAccessData() && mMetaTileEntity.isTeleporterCompatible();
	}
	
	public double demandedEnergyUnits() {
		if (mReleaseEnergy || !canAccessData() || !mMetaTileEntity.isEnetInput()) return 0;
		return getEUCapacity() - getStoredEU();
	}
	
	public double injectEnergyUnits(ForgeDirection aDirection, double amount) {
		return injectEnergyUnits(aDirection.ordinal(), (int) amount, 1) > 0 ? 0 : amount;
	}
	
	public boolean acceptsEnergyFrom(TileEntity aEmitter, ForgeDirection aDirection) {
		return inputEnergyFrom(aDirection.ordinal());
	}
	
	public boolean emitsEnergyTo(TileEntity aReceiver, ForgeDirection aDirection) {
		return outputsEnergyTo(aDirection.ordinal());
	}
	
	public double getOfferedEnergy() {
		return (canAccessData() && getStoredEU() - mMetaTileEntity.getMinimumStoredEU() >= oOutput) ? Math.max(0, oOutput) : 0;
	}
	
	public void drawEnergy(double amount) {
		mAverageEUOutput[mAverageEUOutputIndex] += amount;
		decreaseStoredEU((int) amount, true);
	}
	
	public int injectEnergy(ForgeDirection aForgeDirection, int amount) {
		return injectEnergyUnits(aForgeDirection.ordinal(), amount, 1) > 0 ? 0 : amount;
	}
	
	public int addEnergy(int aEnergy) {
		if (!canAccessData()) return 0;
		if (aEnergy > 0) increaseStoredEnergyUnits(aEnergy, true);
		else decreaseStoredEU(-aEnergy, true);
		return (int) Math.min(Integer.MAX_VALUE, mMetaTileEntity.getEUVar());
	}
	
	public boolean isAddedToEnergyNet() {
		return false;
	}
	
	public int demandsEnergy() {
		if (mReleaseEnergy || !canAccessData() || !mMetaTileEntity.isEnetInput()) return 0;
		return getCapacity() - getStored();
	}
	
	public int getCapacity() {
		return (int) Math.min(Integer.MAX_VALUE, getEUCapacity());
	}
	
	public int getStored() {
		return (int) Math.min(Integer.MAX_VALUE, Math.min(getStoredEU(), getCapacity()));
	}
	
	public void setStored(int aEU) {
		if (canAccessData()) setStoredEU(aEU);
	}
	
	public int getMaxSafeInput() {
		return (int) Math.min(Integer.MAX_VALUE, getInputVoltage());
	}
	
	public int getMaxEnergyOutput() {
		if (mReleaseEnergy) return Integer.MAX_VALUE;
		return getOutput();
	}
	
	public int getOutput() {
		return (int) Math.min(Integer.MAX_VALUE, oOutput);
	}
	
	@Override
	public boolean addStackToSlot(int index, ItemStack stack) {
		if (ItemStackUtils.isStackInvalid(stack)) return true;
		if (index < 0 || index >= getSizeInventory()) return false;
		final ItemStack tStack = getStackInSlot(index);
		if (ItemStackUtils.isStackInvalid(tStack)) {
			setInventorySlotContents(index, stack);
			return true;
		}
		stack = OreDictUnifier.get(stack);
		if (Utility.areStacksEqual(tStack, stack) && tStack.stackSize + stack.stackSize <= Math.min(stack.getMaxStackSize(), getInventoryStackLimit())) {
			tStack.stackSize += stack.stackSize;
			markDirty();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addStackToSlot(int index, ItemStack stack, int amount) {
		return addStackToSlot(index, Utility.copyAmount(amount, stack));
	}
	
	@Override
	public int getColorization() {
		return mColor - 1;
	}
	
	@Override
	public int setColorization(int color) {
		if (color > 15 || color < -1) color = -1;
		mColor = color + 1;
		if (canAccessData()) mMetaTileEntity.onColorChangeServer(color);
		return mColor;
	}
	
	@Override
	public float getBlastResistance(int side) {
		return canAccessData() ? Math.max(0, getMetaTile().getExplosionResistance(side)) : 10.0F;
	}
	
	@Override
	public boolean isUniversalEnergyStored(long aEnergyAmount) {
		if (getUniversalEnergyStored() >= aEnergyAmount) return true;
		mHasEnoughEnergy = false;
		return false;
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
	public int getLightOpacity() {
		return mMetaTileEntity == null ? getLightValue() > 0 ? 0 : 255 : mMetaTileEntity.getLightOpacity();
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
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public IGridNode getGridNode(ForgeDirection forgeDirection) {
		if (mFacing != forgeDirection.ordinal()) return null;
		final AENetworkProxy gp = getProxy();
		return gp != null ? gp.getNode() : null;
	}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
		return mMetaTileEntity == null ? AECableType.NONE : mMetaTileEntity.getCableConnectionType(forgeDirection);
	}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public void securityBreak() {}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public IGridNode getActionableNode() {
		final AENetworkProxy gp = getProxy();
		return gp != null ? gp.getNode() : null;
	}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public AENetworkProxy getProxy() {
		return mMetaTileEntity == null ? null : mMetaTileEntity.getProxy();
	}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public DimensionalCoord getLocation() {return new DimensionalCoord(this);}
	
	@Override
	@Optional.Method(modid = "appliedenergistics2")
	public void gridChanged() {
		if (mMetaTileEntity != null) mMetaTileEntity.gridChanged();
	}
	
	@TileEvent(TileEventType.WORLD_NBT_READ)
	@Optional.Method(modid = "appliedenergistics2")
	public void readFromNBT_AENetwork(final NBTTagCompound data) {
		final AENetworkProxy gp = getProxy();
		if (gp != null) getProxy().readFromNBT(data);
	}
	
	@TileEvent(TileEventType.WORLD_NBT_WRITE)
	@Optional.Method(modid = "appliedenergistics2")
	public void writeToNBT_AENetwork(final NBTTagCompound data) {
		final AENetworkProxy gp = getProxy();
		if (gp != null) gp.writeToNBT(data);
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	void onChunkUnloadAE() {
		final AENetworkProxy gp = getProxy();
		if (gp != null) gp.onChunkUnload();
	}
	
	@Optional.Method(modid = "appliedenergistics2")
	void invalidateAE() {
		final AENetworkProxy gp = getProxy();
		if (gp != null) gp.invalidate();
	}
	
	@Override
	public boolean wasShutdown() {
		return mWasShutdown;
	}
	
	@Override
	public void setShutdownStatus(boolean newStatus) {
		mWasShutdown = newStatus;
	}
	
	@Override
	public IAlignment getAlignment() {
		return getMetaTile() instanceof IAlignmentProvider ? ((IAlignmentProvider) getMetaTile()).getAlignment() : new BasicAlignment();
	}
	
	@Nullable
	@Override
	public IConstructable getConstructable() {
		return getMetaTile() instanceof IConstructable ? (IConstructable) getMetaTile() : null;
	}
	
	private class BasicAlignment implements IAlignment {
		
		@Override
		public ExtendedFacing getExtendedFacing() {
			return ExtendedFacing.of(ForgeDirection.getOrientation(getFrontFace()));
		}
		
		@Override
		public void setExtendedFacing(ExtendedFacing alignment) {
			setFrontFace(Math.min(alignment.getDirection().ordinal(), ForgeDirection.UNKNOWN.ordinal() - 1));
		}
		
		@Override
		public IAlignmentLimits getAlignmentLimits() {
			return (direction, rotation, flip) -> rotation.isNotRotated() && flip.isNotFlipped();
		}
	}
}
