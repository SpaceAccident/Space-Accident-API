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
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.IMultiFluidHatch;
import space.accident.api.interfaces.tools.ISpecialToolMultiBlock;
import space.accident.api.metatileentity.implementations.hathes.*;
import space.accident.api.objects.ItemStackData;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.ModHandler;
import space.accident.api.util.RecipeMap;
import space.accident.api.util.SpaceLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;
import static space.accident.api.enums.Values.V;
import static space.accident.api.util.Utility.*;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.NumberUtils.getTier;

public abstract class MultiBlockBase extends MetaTileEntity {
	
	private static final String INCOMPLETE_STRUCTURE_LANG = LanguageManager.addOverlayLocalization("incomplete_structure", "=== INCOMPLETE STRUCTURE ===");
	private static final String NEED_MAINTENANCE = LanguageManager.addOverlayLocalization("need_maintenance", "NEED MAINTENANCE");
	private static final String RUNNING_FINE = LanguageManager.addOverlayLocalization("running_fine", "Running Fine");
	private static final String EFFICIENCY = LanguageManager.addOverlayLocalization("efficiency", "Efficiency: %s %");
	private static final String PROGRESS = LanguageManager.addOverlayLocalization("progress", "Progress: %d s / %d s");
	
	public boolean isNeedMaintenance = false, isDisableMaintenance = false;
	public boolean isCompletedStructure = false;
	public boolean isRunOnLoad = false;
	public boolean isStructureChanged = false;
	public volatile boolean isUpdated = false;
	public int progressTime = 0, maxProgressTime = 0, eUt = 0, efficiencyIncrease = 0, efficiency = 0;
	public int startUpDelay = 100, runtime = 0, update = 0;
	public int pollution = 0;
	
	public ItemStack[] mOutputItems = null;
	public FluidStack[] mOutputFluids = null;
	public String mNEI = "";
	
	public HashSet<Hatch_Input_Fluid> mInputHatches = new HashSet<>();
	public HashSet<Hatch_Output_Fluid> mOutputHatches = new HashSet<>();
	public HashSet<Hatch_Input_Item> mInputBusses = new HashSet<>();
	public HashSet<Hatch_Output_Item> mOutputBusses = new HashSet<>();
	public HashSet<Hatch_Out> mDynamoHatches = new HashSet<>();
	public HashSet<Hatch_Muffler> mMufflerHatches = new HashSet<>();
	public HashSet<Hatch_Energy_In> mEnergyHatches = new HashSet<>();
	public HashSet<Hatch_Maintenance> mMaintenanceHatches = new HashSet<>();
	public HashSet<HatchBase> otherHatches = new HashSet<>();
	
	public MultiBlockBase(int id, String name, String aNameRegional) {
		super(id, name, aNameRegional, 2);
	}
	
	public MultiBlockBase(int id, String name, String aNameRegional, int countSlots) {
		super(id, name, aNameRegional, countSlots);
	}
	
	public MultiBlockBase(String name) {
		super(name, 2);
	}
	
	public static boolean isValidMetaTileEntity(MetaTileEntity metaTile) {
		return metaTile.getBaseMetaTileEntity() != null && metaTile.getBaseMetaTileEntity().getMetaTile() == metaTile && !metaTile.getBaseMetaTileEntity().isDead();
	}
	
	protected static boolean dumpFluid(HashSet<Hatch_Output_Fluid> aOutputHatches, FluidStack copiedFluidStack, boolean restrictiveHatchesOnly) {
		for (Hatch_Output_Fluid tHatch : aOutputHatches) {
			if (!isValidMetaTileEntity(tHatch) || (restrictiveHatchesOnly && tHatch.mMode == 0)) {
				continue;
			}
			if (ModHandler.isSteam(copiedFluidStack)) {
				if (!tHatch.outputsSteam()) {
					continue;
				}
			} else {
				if (!tHatch.outputsLiquids()) {
					continue;
				}
				if (tHatch.isFluidLocked() && tHatch.getLockedFluidName() != null && !tHatch.getLockedFluidName().equals(copiedFluidStack.getFluid().getName())) {
					continue;
				}
			}
			int tAmount = tHatch.fill(copiedFluidStack, false);
			if (tAmount >= copiedFluidStack.amount) {
				boolean filled = tHatch.fill(copiedFluidStack, true) >= copiedFluidStack.amount;
				tHatch.onEmptyingContainerWhenEmpty();
				return filled;
			} else if (tAmount > 0) {
				copiedFluidStack.amount = copiedFluidStack.amount - tHatch.fill(copiedFluidStack, true);
				tHatch.onEmptyingContainerWhenEmpty();
			}
		}
		return false;
	}
	
	protected static <T extends HatchBase> T identifyHatch(ITile aTileEntity, int aBaseCasingIndex, Class<T> clazz) {
		if (aTileEntity == null) return null;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (!clazz.isInstance(metaTile)) return null;
		T hatch = clazz.cast(metaTile);
		hatch.updateTexture(aBaseCasingIndex);
		return hatch;
	}
	
	void setNeiName(String nei) {
		mNEI = nei;
	}
	
	@Override
	public boolean allowCoverOnSide(int side, ItemStackData coverId) {
		return side != getBaseMetaTileEntity().getFrontFace();
	}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
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
	public boolean isValidSlot(int index) {
		return index > 0;
	}
	
	@Override
	public int getProgresstime() {
		return progressTime;
	}
	
	@Override
	public int maxProgresstime() {
		return maxProgressTime;
	}
	
	@Override
	public int increaseProgress(int aProgress) {
		return aProgress;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("eUt", eUt);
		nbt.setInteger("progressTime", progressTime);
		nbt.setInteger("maxProgressTime", maxProgressTime);
		nbt.setInteger("efficiencyIncrease", efficiencyIncrease);
		nbt.setInteger("efficiency", efficiency);
		nbt.setInteger("pollution", pollution);
		nbt.setInteger("runtime", runtime);
		
		if (mOutputItems != null) {
			nbt.setInteger("mOutputItemsLength", mOutputItems.length);
			for (int i = 0; i < mOutputItems.length; i++)
				if (mOutputItems[i] != null) {
					NBTTagCompound tNBT = new NBTTagCompound();
					mOutputItems[i].writeToNBT(tNBT);
					nbt.setTag("mOutputItem" + i, tNBT);
				}
		}
		if (mOutputFluids != null) {
			nbt.setInteger("mOutputFluidsLength", mOutputFluids.length);
			for (int i = 0; i < mOutputFluids.length; i++)
				if (mOutputFluids[i] != null) {
					NBTTagCompound tNBT = new NBTTagCompound();
					mOutputFluids[i].writeToNBT(tNBT);
					nbt.setTag("mOutputFluids" + i, tNBT);
				}
		}
		nbt.setBoolean("isNeedMaintenance", isNeedMaintenance);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		isNeedMaintenance = nbt.getBoolean("isNeedMaintenance");
		eUt               = nbt.getInteger("eUt");
		progressTime      = nbt.getInteger("progressTime");
		maxProgressTime   = nbt.getInteger("maxProgressTime");
		if (maxProgressTime > 0) {
			isRunOnLoad = true;
		}
		efficiencyIncrease = nbt.getInteger("efficiencyIncrease");
		efficiency         = nbt.getInteger("efficiency");
		pollution          = nbt.getInteger("pollution");
		runtime            = nbt.getInteger("runtime");
		
		int aOutputItemsLength = nbt.getInteger("mOutputItemsLength");
		if (aOutputItemsLength > 0) {
			mOutputItems = new ItemStack[aOutputItemsLength];
			for (int i = 0; i < mOutputItems.length; i++)
				 mOutputItems[i] = loadItem(nbt, "mOutputItem" + i);
		}
		
		int aOutputFluidsLength = nbt.getInteger("mOutputFluidsLength");
		if (aOutputFluidsLength > 0) {
			mOutputFluids = new FluidStack[aOutputFluidsLength];
			for (int i = 0; i < mOutputFluids.length; i++)
				 mOutputFluids[i] = loadFluid(nbt, "mOutputFluids" + i);
		}
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		if (baseTile.isClientSide()) return true;
		baseTile.openGUI(player);
		return true;
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
	public int getTileEntityBaseType() {
		return 2;
	}
	
	/**
	 * Set the structure as having changed, and trigger an update.
	 */
	public void onStructureChange() {
		isStructureChanged = true;
	}
	
	@Override
	public void onMachineBlockUpdate() {
		isUpdated = true;
	}
	
	public void clearHatches() {
		mInputHatches.clear();
		mInputBusses.clear();
		mOutputHatches.clear();
		mOutputBusses.clear();
		mDynamoHatches.clear();
		mEnergyHatches.clear();
		mMufflerHatches.clear();
		mMaintenanceHatches.clear();
	}
	
	public boolean checkStructure(boolean aForceReset) {
		return checkStructure(aForceReset, getBaseMetaTileEntity());
	}
	
	public boolean checkStructure(boolean aForceReset, ITile baseTile) {
		if (!baseTile.isServerSide()) return isCompletedStructure;
		// Only trigger an update if forced (from onPostTick, generally), or if the structure has changed
		if ((isStructureChanged || aForceReset)) {
			clearHatches();
			isCompletedStructure = checkMachine(baseTile, mInventory[1]);
		}
		isStructureChanged = false;
		return isCompletedStructure;
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		if (baseTile.isServerSide()) {
			if (efficiency < 0) {
				efficiency = 0;
			}
			if (isUpdated) {
				update    = 50;
				isUpdated = false;
			}
			if (--update == 0 || --startUpDelay == 0) {
				checkStructure(true, baseTile);
			}
			if (startUpDelay < 0) {
				if (isStructureChanged) {
					checkMaintenance();
					if (getRepairStatus() > 0) {
						runMachine(baseTile, tick);
					} else {
						stopMachine();
					}
				} else {
					stopMachine();
				}
			}
			baseTile.setErrorDisplayID((baseTile.getErrorDisplayID() & ~127) | (isCompletedStructure ? 0 : 64));
			baseTile.setActive(maxProgressTime > 0);
			
			boolean active = baseTile.isActive() && pollution > 0;
			for (Hatch_Muffler aMuffler : mMufflerHatches) {
				ITile gte = aMuffler.getBaseMetaTileEntity();
				if (gte != null && !gte.isDead()) {
					gte.setActive(active);
				}
			}
		}
	}
	
	private void checkMaintenance() {
		if (isDisableMaintenance) {
			isNeedMaintenance = false;
			return;
		}
		for (Hatch_Maintenance tHatch : mMaintenanceHatches) {
			if (isValidMetaTileEntity(tHatch)) {
				if (tHatch.isNeedMaintenance) {
					isNeedMaintenance = true;
				}
				tHatch.isNeedMaintenance = false;
			}
		}
	}
	
	protected void runMachine(ITile baseTile, long aTick) {
		if (maxProgressTime > 0 && doRandomDamage()) {
			if (onRunningTick(mInventory[1])) {
				markDirty();
				if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
					stopMachine();
				}
				if (maxProgressTime > 0 && ++progressTime >= maxProgressTime) {
					if (mOutputItems != null) for (ItemStack tStack : mOutputItems)
						if (tStack != null) {
							addOutput(tStack);
						}
					if (mOutputFluids != null) {
						addFluidOutputs(mOutputFluids);
					}
					efficiency         = Math.max(0, Math.min(efficiency + efficiencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
					mOutputItems       = null;
					progressTime       = 0;
					maxProgressTime    = 0;
					efficiencyIncrease = 0;
					if (baseTile.isAllowedToWork()) {
						checkRecipe(mInventory[1]);
					}
				}
			}
		} else {
			if (aTick % 100 == 0 || baseTile.hasWorkJustBeenEnabled() || baseTile.hasInventoryBeenModified()) {
				if (baseTile.isAllowedToWork()) {
					if (checkRecipe(mInventory[1])) {
						markDirty();
					}
				}
				if (maxProgressTime <= 0) {
					efficiency = Math.max(0, efficiency - 1000);
				}
			}
		}
	}
	
	public boolean polluteEnvironment(int aPollutionLevel) {
		pollution += aPollutionLevel;
		for (Hatch_Muffler tHatch : mMufflerHatches) {
			if (isValidMetaTileEntity(tHatch)) {
				if (pollution >= 10000) {
					if (tHatch.polluteEnvironment(this)) {
						pollution -= 10000;
					}
				} else {
					break;
				}
			}
		}
		return pollution < 10000;
	}
	
	/**
	 * Called every tick the Machine runs
	 */
	public boolean onRunningTick(ItemStack stack) {
		if (eUt > 0) {
			addEnergyOutput(((long) eUt * efficiency) / 10000);
			return true;
		}
		if (eUt < 0) {
			if (!drainEnergyInput(((long) -eUt * 10000) / Math.max(1000, efficiency))) {
				criticalStopMachine();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if this is a Correct Machine Part for this kind of Machine (Turbine Rotor for example)
	 */
	public abstract boolean isCorrectMachinePart(ItemStack stack);
	
	/**
	 * Checks the Recipe
	 */
	public abstract boolean checkRecipe(ItemStack stack);
	
	/**
	 * Checks the Machine. You have to assign the MetaTileEntities for the Hatches here.
	 */
	public abstract boolean checkMachine(ITile baseTile, ItemStack stack);
	
	/**
	 * Gets the maximum Efficiency that spare Part can get (0 - 10000)
	 */
	public abstract int getMaxEfficiency(ItemStack stack);
	
	/**
	 * Gets the pollution this Device outputs to a Muffler per tick (10000 = one Pullution Block)
	 */
	public int getPollutionPerTick(ItemStack stack) {
		return getPollutionPerSecond(stack) / 20;
	}
	
	/**
	 * Gets the pollution produced per second by this multiblock, default to 0. Override this with
	 * its actual value in the code of the multiblock.
	 */
	public int getPollutionPerSecond(ItemStack stack) {
		return 0;
	}
	
	/**
	 * Gets the damage to the ItemStack, usually 0 or 1.
	 */
	public abstract int getDamageToComponent(ItemStack stack);
	
	/**
	 * If it explodes when the Component has to be replaced.
	 */
	public abstract boolean explodesOnComponentBreak(ItemStack stack);
	
	public void stopMachine() {
		mOutputItems       = null;
		eUt                = 0;
		efficiency         = 0;
		progressTime       = 0;
		maxProgressTime    = 0;
		efficiencyIncrease = 0;
		getBaseMetaTileEntity().disableWorking();
	}
	
	protected void calculateOverclockedNessMultiInternal(int aEUt, int aDuration, int mAmperage, long maxInputVoltage, boolean perfectOC) {
		int mTier = Math.max(0, getTier(maxInputVoltage));
		if (mTier == 0) {
			//Long time calculation
			long xMaxProgresstime = ((long) aDuration) << 1;
			if (xMaxProgresstime > Integer.MAX_VALUE - 1) {
				//make impossible if too long
				eUt             = Integer.MAX_VALUE - 1;
				maxProgressTime = Integer.MAX_VALUE - 1;
			} else {
				eUt             = aEUt >> 2;
				maxProgressTime = (int) xMaxProgresstime;
			}
		} else {
			//Long EUt calculation
			long xEUt = aEUt;
			//Isnt too low EUt check?
			long tempEUt = Math.max(xEUt, V[1]);
			
			maxProgressTime = aDuration;
			
			final int ocTimeShift = perfectOC ? 2 : 1;
			
			while (tempEUt <= V[mTier - 1] * mAmperage) {
				tempEUt <<= 2;//this actually controls overclocking
				//xEUt *= 4;//this is effect of everclocking
				int oldTime = maxProgressTime;
				maxProgressTime >>= ocTimeShift;//this is effect of overclocking
				if (maxProgressTime < 1) {
					if (oldTime == 1) break;
					xEUt *= oldTime * (perfectOC ? 1 : 2L);
					break;
				} else {
					xEUt <<= 2;
				}
			}
			if (xEUt > Integer.MAX_VALUE - 1) {
				eUt             = Integer.MAX_VALUE - 1;
				maxProgressTime = Integer.MAX_VALUE - 1;
			} else {
				eUt = (int) xEUt;
				if (eUt == 0) eUt = 1;
				if (maxProgressTime == 0) maxProgressTime = 1;//set time to 1 tick
			}
		}
	}
	
	public void criticalStopMachine() {
		stopMachine();
		getBaseMetaTileEntity().setShutdownStatus(true);
	}
	
	public int getRepairStatus() {
		return (isNeedMaintenance ? 1 : 0);
	}
	
	public int getIdealStatus() {
		return 1;
	}
	
	public int getCurrentEfficiency(ItemStack itemStack) {
		int maxEff = getMaxEfficiency(itemStack);
		return maxEff - (getIdealStatus() - getRepairStatus()) * maxEff / 10;
	}
	
	public boolean doRandomDamage() {
		if (!isCorrectMachinePart(mInventory[1]) || getRepairStatus() == 0) {
			stopMachine();
			return false;
		}
		if (runtime++ > 1000) {
			runtime = 0;
			if (getBaseMetaTileEntity().getRandomNumber(6000) == 0) {
				if (getBaseMetaTileEntity().getRandomNumber(6) == 5) {
					isNeedMaintenance = true;
				}
			}
			ItemStack toolStack = mInventory[1];
			if (toolStack != null && getBaseMetaTileEntity().getRandomNumber(2) == 0) {
				if (toolStack.getItem() instanceof ISpecialToolMultiBlock) {
					ISpecialToolMultiBlock<?> tool = (ISpecialToolMultiBlock<?>) toolStack.getItem();
					tool.doWork(mInventory[1]);
				}
			}
		}
		return true;
	}
	
	public void explodeMultiblock() {
		
		SpaceLog.exp.println("MultiBlockExplosion at: " + getBaseMetaTileEntity().getX() + " | " + getBaseMetaTileEntity().getY() + " | " + getBaseMetaTileEntity().getZ() + " " + "DIMID: " + getBaseMetaTileEntity().getWorld().provider.dimensionId + ".");

//		Pollution.addPollution(getBaseMetaTileEntity(), API.pollutionOnExplosion);
		mInventory[1] = null;
		for (MetaTileEntity tTileEntity : mInputBusses) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mOutputBusses) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mInputHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mOutputHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mDynamoHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mMufflerHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mEnergyHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		for (MetaTileEntity tTileEntity : mMaintenanceHatches) tTileEntity.getBaseMetaTileEntity().doExplosion(V[8]);
		getBaseMetaTileEntity().doExplosion(V[8]);
	}
	
	public boolean addEnergyOutput(long aEU) {
		if (aEU <= 0) {
			return true;
		}
		if (mDynamoHatches.size() > 0) {
			return addEnergyOutputMultipleDynamos(aEU, true);
		}
		return false;
	}
	
	public boolean addEnergyOutputMultipleDynamos(long aEU, boolean aAllowMixedVoltageDynamos) {
		int injected = 0;
		long totalOutput = 0;
		long aFirstVoltageFound = -1;
		boolean aFoundMixedDynamos = false;
		for (Hatch_Out aDynamo : mDynamoHatches) {
			if (aDynamo == null) {
				return false;
			}
			if (isValidMetaTileEntity(aDynamo)) {
				long aVoltage = aDynamo.maxEUOutput();
				long aTotal = aDynamo.maxAmperesOut() * aVoltage;
				// Check against voltage to check when hatch mixing
				if (aFirstVoltageFound == -1) {
					aFirstVoltageFound = aVoltage;
				} else {
					if (aFirstVoltageFound != aVoltage) {
						aFoundMixedDynamos = true;
					}
				}
				totalOutput += aTotal;
			}
		}
		
		if (totalOutput < aEU || (aFoundMixedDynamos && !aAllowMixedVoltageDynamos)) {
			explodeMultiblock();
			return false;
		}
		
		long leftToInject;
		long aVoltage;
		int aAmpsToInject;
		int aRemainder;
		int ampsOnCurrentHatch;
		for (Hatch_Out aDynamo : mDynamoHatches) {
			if (isValidMetaTileEntity(aDynamo)) {
				leftToInject       = aEU - injected;
				aVoltage           = aDynamo.maxEUOutput();
				aAmpsToInject      = (int) (leftToInject / aVoltage);
				aRemainder         = (int) (leftToInject - (aAmpsToInject * aVoltage));
				ampsOnCurrentHatch = (int) Math.min(aDynamo.maxAmperesOut(), aAmpsToInject);
				for (int i = 0; i < ampsOnCurrentHatch; i++) {
					aDynamo.getBaseMetaTileEntity().increaseStoredEnergyUnits(aVoltage, false);
				}
				injected += aVoltage * ampsOnCurrentHatch;
				if (aRemainder > 0 && ampsOnCurrentHatch < aDynamo.maxAmperesOut()) {
					aDynamo.getBaseMetaTileEntity().increaseStoredEnergyUnits(aRemainder, false);
					injected += aRemainder;
				}
			}
		}
		return injected > 0;
	}
	
	public long getMaxInputVoltage() {
		long rVoltage = 0;
		for (Hatch_Energy_In tHatch : mEnergyHatches)
			if (isValidMetaTileEntity(tHatch)) rVoltage += tHatch.getBaseMetaTileEntity().getInputVoltage();
		return rVoltage;
	}
	
	public void calculateOverclockedNessMulti(int aEUt, int aDuration, int mAmperage, long maxInputVoltage) {
		calculateOverclockedNessMultiInternal(aEUt, aDuration, mAmperage, maxInputVoltage, false);
	}
	
	protected void calculatePerfectOverclockedNessMulti(int aEUt, int aDuration, int mAmperage, long maxInputVoltage) {
		calculateOverclockedNessMultiInternal(aEUt, aDuration, mAmperage, maxInputVoltage, true);
	}
	
	public boolean drainEnergyInput(long aEU) {
		if (aEU <= 0) return true;
		for (Hatch_Energy_In tHatch : mEnergyHatches)
			if (isValidMetaTileEntity(tHatch)) {
				if (tHatch.getBaseMetaTileEntity().decreaseStoredEnergyUnits(aEU, false)) return true;
			}
		return false;
	}
	
	public boolean addOutput(FluidStack aLiquid) {
		if (aLiquid == null) return false;
		FluidStack copiedFluidStack = aLiquid.copy();
		if (!dumpFluid(mOutputHatches, copiedFluidStack, true)) {
			dumpFluid(mOutputHatches, copiedFluidStack, false);
		}
		return false;
	}
	
	protected void addFluidOutputs(FluidStack[] mOutputFluids2) {
		for (FluidStack outputFluidStack : mOutputFluids2) {
			addOutput(outputFluidStack);
		}
	}
	
	public boolean depleteInput(FluidStack aLiquid) {
		if (aLiquid == null) return false;
		for (Hatch_Input_Fluid tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				
				if (tHatch instanceof IMultiFluidHatch) {
					if (((IMultiFluidHatch) tHatch).hasFluid(aLiquid)) {
						FluidStack tLiquid = tHatch.drain(aLiquid.amount, false);
						if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
							tLiquid = tHatch.drain(aLiquid.amount, true);
							return tLiquid != null && tLiquid.amount >= aLiquid.amount;
						}
					}
				} else {
					FluidStack tLiquid = tHatch.getFluid();
					if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
						tLiquid = tHatch.drain(aLiquid.amount, false);
						if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
							tLiquid = tHatch.drain(aLiquid.amount, true);
							return tLiquid != null && tLiquid.amount >= aLiquid.amount;
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean addOutput(ItemStack stack) {
		if (isStackInvalid(stack)) return false;
		stack = copyOrNull(stack);
		for (Hatch_Output_Item tHatch : mOutputBusses) {
			if (isValidMetaTileEntity(tHatch) && tHatch.storeAll(stack)) {
				return true;
			}
		}
		boolean outputSuccess = true;
		while (outputSuccess && stack.stackSize > 0) {
			outputSuccess = false;
			ItemStack single = stack.splitStack(1);
			for (Hatch_Output_Fluid tHatch : mOutputHatches) {
				if (!outputSuccess && isValidMetaTileEntity(tHatch) && tHatch.outputsItems()) {
					if (tHatch.getBaseMetaTileEntity().addStackToSlot(1, single)) outputSuccess = true;
				}
			}
		}
		return outputSuccess;
	}
	
	public boolean depleteInput(ItemStack stack) {
		if (isStackInvalid(stack)) return false;
		FluidStack aLiquid = getFluidForFilledItem(stack, true);
		if (aLiquid != null) return depleteInput(aLiquid);
		for (Hatch_Input_Fluid tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				if (areStacksEqual(stack, tHatch.getBaseMetaTileEntity().getStackInSlot(0))) {
					if (tHatch.getBaseMetaTileEntity().getStackInSlot(0).stackSize >= stack.stackSize) {
						tHatch.getBaseMetaTileEntity().decrStackSize(0, stack.stackSize);
						return true;
					}
				}
			}
		}
		for (Hatch_Input_Item tHatch : mInputBusses) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					if (areStacksEqual(stack, tHatch.getBaseMetaTileEntity().getStackInSlot(i))) {
						if (tHatch.getBaseMetaTileEntity().getStackInSlot(i).stackSize >= stack.stackSize) {
							tHatch.getBaseMetaTileEntity().decrStackSize(i, stack.stackSize);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public ArrayList<ItemStack> getStoredOutputs() {
		ArrayList<ItemStack> rList = new ArrayList<>();
//        for (Hatch_Output_Fluid tHatch : mOutputHatches) {
//            if (isValidMetaTileEntity(tHatch)) {
//                rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(1));
//            }
//        }
		for (Hatch_Output_Item tHatch : mOutputBusses) {
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
				}
			}
		}
		return rList;
	}
	
	public ArrayList<FluidStack> getStoredFluids() {
		ArrayList<FluidStack> rList = new ArrayList<>();
		for (Hatch_Input_Fluid tHatch : mInputHatches) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				if (tHatch instanceof IMultiFluidHatch) {
					IMultiFluidHatch multiHatch = (IMultiFluidHatch) tHatch;
					for (FluidStack tFluid : multiHatch.getStoredFluid()) {
						if (tFluid != null) {
							rList.add(tFluid);
						}
					}
				} else {
					rList.add(tHatch.getFillableStack());
				}
			}
		}
		return rList;
	}
	
	public ArrayList<ItemStack> getStoredInputs() {
		ArrayList<ItemStack> rList = new ArrayList<>();
		for (Hatch_Input_Item tHatch : mInputBusses) {
			tHatch.mRecipeMap = getRecipeMap();
			if (isValidMetaTileEntity(tHatch)) {
				for (int i = tHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
					if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null) rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
				}
			}
		}
		if (getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName().startsWith("integrated_circuit")) rList.add(getStackInSlot(1));
		return rList;
	}
	
	public RecipeMap getRecipeMap() {
		return null;
	}
	
	public void updateSlots() {
		for (Hatch_Input_Fluid tHatch : mInputHatches)
			if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
		for (Hatch_Input_Item tHatch : mInputBusses)
			if (isValidMetaTileEntity(tHatch)) tHatch.updateSlots();
	}
	
	public boolean addToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof HatchBase) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
		}
		if (metaTile instanceof Hatch_Input_Fluid) {
			((Hatch_Input_Fluid) metaTile).mRecipeMap = getRecipeMap();
			return mInputHatches.add((Hatch_Input_Fluid) metaTile);
		}
		if (metaTile instanceof Hatch_Input_Item) {
			((Hatch_Input_Item) metaTile).mRecipeMap = getRecipeMap();
			return mInputBusses.add((Hatch_Input_Item) metaTile);
		}
		if (metaTile instanceof Hatch_Output_Fluid) return mOutputHatches.add((Hatch_Output_Fluid) metaTile);
		if (metaTile instanceof Hatch_Output_Item) return mOutputBusses.add((Hatch_Output_Item) metaTile);
		if (metaTile instanceof Hatch_Energy_In) return mEnergyHatches.add((Hatch_Energy_In) metaTile);
		if (metaTile instanceof Hatch_Out) return mDynamoHatches.add((Hatch_Out) metaTile);
		if (metaTile instanceof Hatch_Maintenance) return mMaintenanceHatches.add((Hatch_Maintenance) metaTile);
		if (metaTile instanceof Hatch_Muffler) return mMufflerHatches.add((Hatch_Muffler) metaTile);
		return false;
	}
	
	public boolean addMaintenanceToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Maintenance) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mMaintenanceHatches.add((Hatch_Maintenance) metaTile);
		}
		return false;
	}
	
	public boolean addEnergyInputToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) {
			return false;
		}
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Energy_In) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mEnergyHatches.add((Hatch_Energy_In) metaTile);
		}
		return false;
	}
	
	public boolean addOtherToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof HatchBase) {
			HatchBase hatch = (HatchBase) metaTile;
			hatch.updateTexture(aBaseCasingIndex);
			return otherHatches.add(hatch);
		}
		return false;
	}
	
	public boolean addDynamoToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Out) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mDynamoHatches.add((Hatch_Out) metaTile);
		}
		return false;
	}
	
	public boolean addMufflerToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Muffler) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mMufflerHatches.add((Hatch_Muffler) metaTile);
		}
		return false;
	}
	
	public boolean addInputToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Input_Fluid) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			((Hatch_Input_Fluid) metaTile).mRecipeMap = getRecipeMap();
			return mInputHatches.add((Hatch_Input_Fluid) metaTile);
		}
		if (metaTile instanceof Hatch_Input_Item) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			((Hatch_Input_Item) metaTile).mRecipeMap = getRecipeMap();
			return mInputBusses.add((Hatch_Input_Item) metaTile);
		}
		return false;
	}
	
	public boolean addOutputToMachineList(ITile aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTile metaTile = aTileEntity.getMetaTile();
		if (metaTile == null) return false;
		if (metaTile instanceof Hatch_Output_Fluid) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mOutputHatches.add((Hatch_Output_Fluid) metaTile);
		}
		if (metaTile instanceof Hatch_Output_Item) {
			((HatchBase) metaTile).updateTexture(aBaseCasingIndex);
			return mOutputBusses.add((Hatch_Output_Item) metaTile);
		}
		return false;
	}
	
	@Override
	public String[] getInfoData() {
		return new String[0];
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	public ItemStack[] getCompactedInputs() {
		ArrayList<ItemStack> tInputList = getStoredInputs();
		int tInputList_sS = tInputList.size();
		for (int i = 0; i < tInputList_sS - 1; i++) {
			for (int j = i + 1; j < tInputList_sS; j++) {
				if (!areStacksEqual(tInputList.get(i), tInputList.get(j))) continue;
				if (tInputList.get(i).stackSize >= tInputList.get(j).stackSize) {
					tInputList.remove(j--);
					tInputList_sS = tInputList.size();
				} else {
					tInputList.remove(i--);
					tInputList_sS = tInputList.size();
					break;
				}
			}
		}
		return tInputList.toArray(new ItemStack[0]);
	}
	
	public FluidStack[] getCompactedFluids() {
		ArrayList<FluidStack> tFluidList = getStoredFluids();
		int tFluidList_sS = tFluidList.size();
		for (int i = 0; i < tFluidList_sS - 1; i++) {
			for (int j = i + 1; j < tFluidList_sS; j++) {
				if (!areFluidsEqual(tFluidList.get(i), tFluidList.get(j))) continue;
				
				if (tFluidList.get(i).amount >= tFluidList.get(j).amount) {
					tFluidList.remove(j--);
					tFluidList_sS = tFluidList.size();
				} else {
					tFluidList.remove(i--);
					tFluidList_sS = tFluidList.size();
					break;
				}
			}
		}
		return tFluidList.toArray(new FluidStack[0]);
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		final NBTTagCompound tag = accessor.getNBTData();
		
		if (tag.getBoolean("incompleteStructure")) {
			currentTip.add(RED + INCOMPLETE_STRUCTURE_LANG + RESET);
		}
		String efficiency = " " + String.format(EFFICIENCY, tag.getFloat("efficiency") + "");
		if (tag.getBoolean("hasProblems")) {
			currentTip.add(RED + NEED_MAINTENANCE + RESET + efficiency);
		} else {
			currentTip.add(GREEN + RUNNING_FINE + RESET + efficiency);
		}
		currentTip.add(String.format(PROGRESS, tag.getInteger("progress"), tag.getInteger("maxProgress")));
		super.getWailaBody(itemStack, currentTip, accessor, config);
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		super.getWailaNBTData(player, tile, tag, world, x, y, z);
		tag.setBoolean("hasProblems", (getIdealStatus() - getRepairStatus()) > 0);
		tag.setFloat("efficiency", efficiency / 100.0F);
		tag.setInteger("progress", progressTime / 20);
		tag.setInteger("maxProgress", maxProgressTime / 20);
		tag.setBoolean("incompleteStructure", (getBaseMetaTileEntity().getErrorDisplayID() & 64) != 0);
	}
}
