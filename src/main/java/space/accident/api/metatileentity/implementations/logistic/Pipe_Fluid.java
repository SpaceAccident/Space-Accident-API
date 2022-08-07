package space.accident.api.metatileentity.implementations.logistic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.apache.commons.lang3.tuple.MutableTriple;
import space.accident.api.API;
import space.accident.api.enums.Colors;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.TypeTileEntity;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.MetaPipeEntity;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.*;
import space.accident.main.events.ClientEvents;

import java.util.ArrayList;
import java.util.List;

import static space.accident.api.API.gt6Pipe;
import static space.accident.api.util.Utility.*;
import static space.accident.extensions.NumberUtils.format;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;
import static space.accident.main.SpaceAccidentApi.proxy;
import static space.accident.structurelib.util.XSTR.XSTR_INSTANCE;

public class Pipe_Fluid extends MetaPipeEntity implements TypeTileEntity.IMetaPipeFluid {
	public final float mThickNess;
	public final Materials mMaterial;
	public final int mCapacity, mHeatResistance, mPipeAmount;
	public final boolean mGasProof;
	public final FluidStack[] mFluids;
	public int mLastReceivedFrom = 0, oLastReceivedFrom = 0;
	/**
	 * Bitmask for whether disable fluid input form each side.
	 */
	public int mDisableInput = 0;
	
	public Pipe_Fluid(int id, String name, String aNameRegional, float aThickNess, Materials aMaterial, int aCapacity, int aHeatResistance, boolean aGasProof) {
		this(id, name, aNameRegional, aThickNess, aMaterial, aCapacity, aHeatResistance, aGasProof, 1);
	}
	
	public Pipe_Fluid(int id, String name, String aNameRegional, float aThickNess, Materials aMaterial, int aCapacity, int aHeatResistance, boolean aGasProof, int aFluidTypes) {
		super(id, name, aNameRegional, 0, false);
		mThickNess      = aThickNess;
		mMaterial       = aMaterial;
		mCapacity       = aCapacity;
		mGasProof       = aGasProof;
		mHeatResistance = aHeatResistance;
		mPipeAmount     = aFluidTypes;
		mFluids         = new FluidStack[mPipeAmount];
		addInfo(id);
	}
	
	@Deprecated
	public Pipe_Fluid(String name, float aThickNess, Materials aMaterial, int aCapacity, int aHeatResistance, boolean aGasProof) {
		this(name, aThickNess, aMaterial, aCapacity, aHeatResistance, aGasProof, 1);
	}
	
	public Pipe_Fluid(String name, float aThickNess, Materials aMaterial, int aCapacity, int aHeatResistance, boolean aGasProof, int aFluidTypes) {
		super(name, 0);
		mThickNess      = aThickNess;
		mMaterial       = aMaterial;
		mCapacity       = aCapacity;
		mGasProof       = aGasProof;
		mHeatResistance = aHeatResistance;
		mPipeAmount     = aFluidTypes;
		mFluids         = new FluidStack[mPipeAmount];
	}
	
	protected static ITexture getBaseTexture(float aThickNess, int aPipeAmount, Materials aMaterial, int aColorIndex) {
		if (aPipeAmount >= 9) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeNonuple.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aPipeAmount >= 4) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeQuadruple.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aThickNess < 0.124F) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aThickNess < 0.374F) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeTiny.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aThickNess < 0.499F) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeSmall.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aThickNess < 0.749F) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeMedium.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		if (aThickNess < 0.874F) return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeLarge.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
		return TextureFactory.of(aMaterial.icon.textures[OrePrefixes.pipeHuge.textureId], Colors.getModulation(aColorIndex, aMaterial.mRGBa));
	}
	
	protected static final ITexture getRestrictorTexture(int aMask) {
		switch (aMask) {
			case 1:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_UP);
			case 2:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_DOWN);
			case 3:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_UD);
			case 4:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_LEFT);
			case 5:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_UL);
			case 6:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_DL);
			case 7:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_NR);
			case 8:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_RIGHT);
			case 9:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_UR);
			case 10:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_DR);
			case 11:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_NL);
			case 12:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_LR);
			case 13:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_ND);
			case 14:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR_NU);
			case 15:
				return TextureFactory.of(Textures.BlockIcons.PIPE_RESTRICTOR);
			default:
				return null;
		}
	}
	
	@Override
	public int getTileEntityBaseType() {
		return (mMaterial == null ? 4 : (4) + Math.max(0, Math.min(3, mMaterial.mToolQuality)));
	}
	
	@Override
	public IMetaTile newMetaEntity(ITile aTileEntity) {
		return new Pipe_Fluid(mName, mThickNess, mMaterial, mCapacity, mHeatResistance, mGasProof, mPipeAmount);
	}
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int aConnections, int aColorIndex, boolean aConnected, boolean aRedstone) {
		float tThickNess = getThickNess();
		if (mDisableInput == 0)
			return new ITexture[]{aConnected ? getBaseTexture(tThickNess, mPipeAmount, mMaterial, aColorIndex) : TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
		int tMask = 0;
		int[][] sRestrictionArray = {{2, 3, 5, 4}, {2, 3, 4, 5}, {1, 0, 4, 5}, {1, 0, 4, 5}, {1, 0, 2, 3}, {1, 0, 2, 3}};
		if (side >= 0 && side < 6) {
			for (int i = 0; i < 4; i++) if (isInputDisabledAtSide(sRestrictionArray[side][i])) tMask |= 1 << i;
			//Full block size renderer flips side 5 and 2  textures, flip restrictor textures to compensate
			if (side == 5 || side == 2) if (tMask > 3 && tMask < 12) tMask = (tMask ^ 12);
		}
		return new ITexture[]{aConnected ? getBaseTexture(tThickNess, mPipeAmount, mMaterial, aColorIndex) : TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), getRestrictorTexture(tMask)};
	}
	
	@Override
	public void onValueUpdate(int value) {
		mDisableInput = value;
	}
	
	@Override
	public int getUpdateData() {
		return mDisableInput;
	}
	
	@Override
	public boolean isSimpleMachine() {
		return true;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return false;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return false;
	}
	
	@Override
	public final boolean renderInside(int side) {
		return false;
	}
	
	@Override
	public int getProgresstime() {
		return getFluidAmount();
	}
	
	@Override
	public int maxProgresstime() {
		return getCapacity();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		for (int i = 0; i < mPipeAmount; i++)
			if (mFluids[i] != null) nbt.setTag("mFluid" + (i == 0 ? "" : i), mFluids[i].writeToNBT(new NBTTagCompound()));
		nbt.setInteger("mLastReceivedFrom", mLastReceivedFrom);
		if (gt6Pipe) {
			nbt.setInteger("mConnections", mConnections);
			nbt.setInteger("mDisableInput", mDisableInput);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for (int i = 0; i < mPipeAmount; i++)
			 mFluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mFluid" + (i == 0 ? "" : i)));
		mLastReceivedFrom = nbt.getInteger("mLastReceivedFrom");
		if (gt6Pipe) {
			mConnections  = nbt.getInteger("mConnections");
			mDisableInput = nbt.getInteger("mDisableInput");
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if ((((BaseMetaPipeEntity) getBaseMetaTileEntity()).mConnections & -128) == 0 && entity instanceof EntityLivingBase) {
			for (FluidStack tFluid : mFluids) {
				if (tFluid != null) {
					int tTemperature = tFluid.getFluid().getTemperature(tFluid);
					if (tTemperature > 320 && !isCoverOnSide((BaseMetaPipeEntity) getBaseMetaTileEntity(), (EntityLivingBase) entity)) {
						applyHeatDamage((EntityLivingBase) entity, (tTemperature - 300) / 50.0F);
						break;
					} else if (tTemperature < 260 && !isCoverOnSide((BaseMetaPipeEntity) getBaseMetaTileEntity(), (EntityLivingBase) entity)) {
						applyFrostDamage((EntityLivingBase) entity, (270 - tTemperature) / 25.0F);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		if (baseTile.isServerSide() && tick % 5 == 0) {
			mLastReceivedFrom &= 63;
			if (mLastReceivedFrom == 63) {
				mLastReceivedFrom = 0;
			}
			
			if (!gt6Pipe || mCheckConnections) checkConnections();
			
			boolean shouldDistribute = (oLastReceivedFrom == mLastReceivedFrom);
			for (int i = 0, j = baseTile.getRandomNumber(mPipeAmount); i < mPipeAmount; i++) {
				int index = (i + j) % mPipeAmount;
				if (mFluids[index] != null && mFluids[index].amount <= 0) mFluids[index] = null;
				if (mFluids[index] == null) continue;
				
				if (checkEnvironment(index, baseTile)) return;
				
				if (shouldDistribute) {
					distributeFluid(index, baseTile);
					mLastReceivedFrom = 0;
				}
			}
			
			oLastReceivedFrom = mLastReceivedFrom;
			
		}
	}
	
	private boolean checkEnvironment(int index, ITile baseTile) {
		// Check for hot liquids that melt the pipe or gasses that escape and burn/freeze people
		final FluidStack tFluid = mFluids[index];
		
		if (tFluid != null && tFluid.amount > 0) {
			int tTemperature = tFluid.getFluid().getTemperature(tFluid);
			if (tTemperature > mHeatResistance) {
				if (baseTile.getRandomNumber(100) == 0) {
					// Poof
					SpaceLog.exp.println("Set Pipe to Fire due to to low heat resistance at " + baseTile.getX() + " | " + baseTile.getY() + " | " + baseTile.getZ() + " DIMID: " + baseTile.getWorld().provider.dimensionId);
					baseTile.setToFire();
					return true;
				}
				// Mmhmm, Fire
				baseTile.setOnFire();
				SpaceLog.exp.println("Set Blocks around Pipe to Fire due to to low heat resistance at " + baseTile.getX() + " | " + baseTile.getY() + " | " + baseTile.getZ() + " DIMID: " + baseTile.getWorld().provider.dimensionId);
				
			}
			if (!mGasProof && tFluid.getFluid().isGaseous(tFluid)) {
				tFluid.amount -= 5;
				sendSound(9);
				if (tTemperature > 320) {
					try {
						for (EntityLivingBase tLiving : (ArrayList<EntityLivingBase>) getBaseMetaTileEntity().getWorld().getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(getBaseMetaTileEntity().getX() - 2, getBaseMetaTileEntity().getY() - 2, getBaseMetaTileEntity().getZ() - 2, getBaseMetaTileEntity().getX() + 3, getBaseMetaTileEntity().getY() + 3, getBaseMetaTileEntity().getZ() + 3))) {
							applyHeatDamage(tLiving, (tTemperature - 300) / 25.0F);
						}
					} catch (Throwable e) {
						e.printStackTrace(SpaceLog.err);
					}
				} else if (tTemperature < 260) {
					try {
						for (EntityLivingBase tLiving : (ArrayList<EntityLivingBase>) getBaseMetaTileEntity().getWorld().getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(getBaseMetaTileEntity().getX() - 2, getBaseMetaTileEntity().getY() - 2, getBaseMetaTileEntity().getZ() - 2, getBaseMetaTileEntity().getX() + 3, getBaseMetaTileEntity().getY() + 3, getBaseMetaTileEntity().getZ() + 3))) {
							applyFrostDamage(tLiving, (270 - tTemperature) / 12.5F);
						}
					} catch (Throwable e) {
						e.printStackTrace(SpaceLog.err);
					}
				}
			}
			if (tFluid.amount <= 0) mFluids[index] = null;
		}
		return false;
	}
	
	private void distributeFluid(int index, ITile baseTile) {
		final FluidStack tFluid = mFluids[index];
		if (tFluid == null) return;
		
		// Tank, From, Amount to receive
		List<MutableTriple<IFluidHandler, ForgeDirection, Integer>> tTanks = new ArrayList<>();
		int amount = tFluid.amount;
		
		for (int side, i = 0, j = baseTile.getRandomNumber(6); i < 6; i++) {
			// Get a list of tanks accepting fluids, and what side they're on
			side = ((i + j) % 6);
			final int tSide = getOppositeSide(side);
			final IFluidHandler tTank = baseTile.getITankContainerAtSide(side);
			final ITile gTank = tTank instanceof ITile ? (ITile) tTank : null;
			
			if (isConnectedAtSide(side) && tTank != null && (mLastReceivedFrom & (1 << side)) == 0 && getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).letsFluidOut(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), tFluid.getFluid(), getBaseMetaTileEntity()) && (gTank == null || gTank.getCoverBehaviorAtSideNew(tSide).letsFluidIn(tSide, gTank.getCoverIDAtSide(tSide), gTank.getComplexCoverDataAtSide(tSide), tFluid.getFluid(), gTank))) {
				if (tTank.fill(ForgeDirection.getOrientation(tSide), tFluid, false) > 0) {
					tTanks.add(new MutableTriple<>(tTank, ForgeDirection.getOrientation(tSide), 0));
				}
				tFluid.amount = amount; // Because some mods do actually modify input fluid stack
			}
		}
		
		// How much of this fluid is available for distribution?
		double tAmount = Math.max(1, Math.min(mCapacity * 10, tFluid.amount)), tNumTanks = tTanks.size();
		FluidStack maxFluid = tFluid.copy();
		maxFluid.amount = Integer.MAX_VALUE;
		
		double availableCapacity = 0;
		// Calculate available capacity for distribution from all tanks
		for (MutableTriple<IFluidHandler, ForgeDirection, Integer> tEntry : tTanks) {
			tEntry.right = tEntry.left.fill(tEntry.middle, maxFluid, false);
			availableCapacity += tEntry.right;
		}
		
		// Now distribute
		for (MutableTriple<IFluidHandler, ForgeDirection, Integer> tEntry : tTanks) {
			if (availableCapacity > tAmount) tEntry.right = (int) Math.floor(tEntry.right * tAmount / availableCapacity); // Distribue fluids based on percentage available space at destination
			if (tEntry.right == 0) tEntry.right = (int) Math.min(1, tAmount); // If the percent is not enough to give at least 1L, try to give 1L
			if (tEntry.right <= 0) continue;
			
			int tFilledAmount = tEntry.left.fill(tEntry.middle, drainFromIndex(tEntry.right, false, index), false);
			
			if (tFilledAmount > 0) tEntry.left.fill(tEntry.middle, drainFromIndex(tFilledAmount, true, index), true);
		}
		
	}
	
	@Override
	public boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (gt6Pipe) {
			int tSide = determineWrenchingSide(side, x, y, z);
			int tMask = (1 << tSide);
			if (player.isSneaking()) {
				if (isInputDisabledAtSide(tSide)) {
					mDisableInput &= ~tMask;
					sendChat(player, trans("212", "Input enabled"));
					if (!isConnectedAtSide(tSide)) connect(tSide);
				} else {
					mDisableInput |= tMask;
					sendChat(player, trans("213", "Input disabled"));
				}
			} else {
				if (!isConnectedAtSide(tSide)) {
					if (connect(tSide) > 0) sendChat(player, trans("214", "Connected"));
				} else {
					disconnect(tSide);
					sendChat(player, trans("215", "Disconnected"));
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean letsIn(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsFluidIn(side, coverId, aCoverVariable, null, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsFluidOut(side, coverId, aCoverVariable, null, aTileEntity);
	}
	
	@Override
	public boolean letsIn(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsFluidIn(side, coverId, aCoverVariable, null, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsFluidOut(side, coverId, aCoverVariable, null, aTileEntity);
	}
	
	@Override
	public boolean canConnect(int side, TileEntity tTileEntity) {
		if (tTileEntity == null) return false;
		
		final int tSide = ForgeDirection.getOrientation(side).getOpposite().ordinal();
		final ITile baseMetaTile = getBaseMetaTileEntity();
		if (baseMetaTile == null) return false;
		
		final SA_CoverBehaviorBase<?> coverBehavior = baseMetaTile.getCoverBehaviorAtSideNew(side);
		final ITile gTileEntity = (tTileEntity instanceof ITile) ? (ITile) tTileEntity : null;

//		if (coverBehavior instanceof GT_Cover_Drain) return true;
		
		final IFluidHandler fTileEntity = (tTileEntity instanceof IFluidHandler) ? (IFluidHandler) tTileEntity : null;
		
		if (fTileEntity != null) {
			final FluidTankInfo[] tInfo = fTileEntity.getTankInfo(ForgeDirection.getOrientation(tSide));
			if (tInfo != null) {
				return tInfo.length > 0 /*|| gTileEntity != null && gTileEntity.getCoverBehaviorAtSideNew(tSide) instanceof GT_Cover_FluidRegulator*/;
			}
		}
		return false;
	}
	
	@Override
	public boolean getGT6StyleConnection() {
		// Yes if GT6 pipes are enabled
		return gt6Pipe;
	}
	
	@Override
	public void doSound(int index, double x, double y, double z) {
		super.doSound(index, x, y, z);
		if (index == 9) {
			doSoundAtClient(API.sSoundList.get(4), 5, 1.0F, x, y, z);
			
			new WorldSpawnedEventBuilder.ParticleEventBuilder().setIdentifier("largesmoke").setWorld(getBaseMetaTileEntity().getWorld()).<WorldSpawnedEventBuilder.ParticleEventBuilder>times(6, (xx, i) -> xx.setMotion(ForgeDirection.getOrientation(i).offsetX / 5.0, ForgeDirection.getOrientation(i).offsetY / 5.0, ForgeDirection.getOrientation(i).offsetZ / 5.0).setPosition(x - 0.5 + XSTR_INSTANCE.nextFloat(), y - 0.5 + XSTR_INSTANCE.nextFloat(), z - 0.5 + XSTR_INSTANCE.nextFloat()).run());
		}
	}
	
	@Override
	public final int getCapacity() {
		return mCapacity * 20 * mPipeAmount;
	}
	
	@Override
	public FluidTankInfo getInfo() {
		for (FluidStack tFluid : mFluids) {
			if (tFluid != null) return new FluidTankInfo(tFluid, mCapacity * 20);
		}
		return new FluidTankInfo(null, mCapacity * 20);
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection side) {
		if (getCapacity() <= 0) {
			return new FluidTankInfo[]{};
		}
		ArrayList<FluidTankInfo> tList = new ArrayList<>();
		for (FluidStack tFluid : mFluids) {
			tList.add(new FluidTankInfo(tFluid, mCapacity * 20));
		}
		return tList.toArray(new FluidTankInfo[mPipeAmount]);
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public final FluidStack getFluid() {
		for (FluidStack tFluid : mFluids) {
			if (tFluid != null) return tFluid;
		}
		return null;
	}
	
	@Override
	public final int getFluidAmount() {
		int rAmount = 0;
		for (FluidStack tFluid : mFluids) {
			if (tFluid != null) rAmount += tFluid.amount;
		}
		return rAmount;
	}
	
	@Override
	public final int fill_default(ForgeDirection side, FluidStack aFluid, boolean doFill) {
		if (aFluid == null || aFluid.getFluid().getID() <= 0) return 0;
		
		int index = -1;
		for (int i = 0; i < mPipeAmount; i++) {
			if (mFluids[i] != null && mFluids[i].isFluidEqual(aFluid)) {
				index = i;
				break;
			} else if ((mFluids[i] == null || mFluids[i].getFluid().getID() <= 0) && index < 0) {
				index = i;
			}
		}
		
		return fill_default_intoIndex(side, aFluid, doFill, index);
	}
	
	private final int fill_default_intoIndex(ForgeDirection side, FluidStack aFluid, boolean doFill, int index) {
		if (index < 0 || index >= mPipeAmount) return 0;
		if (aFluid == null || aFluid.getFluid().getID() <= 0) return 0;
		
		if (mFluids[index] == null || mFluids[index].getFluid().getID() <= 0) {
			if (aFluid.amount * mPipeAmount <= getCapacity()) {
				if (doFill) {
					mFluids[index] = aFluid.copy();
					mLastReceivedFrom |= (1 << side.ordinal());
				}
				return aFluid.amount;
			}
			if (doFill) {
				mFluids[index]        = aFluid.copy();
				mLastReceivedFrom |= (1 << side.ordinal());
				mFluids[index].amount = getCapacity() / mPipeAmount;
			}
			return getCapacity() / mPipeAmount;
		}
		
		if (!mFluids[index].isFluidEqual(aFluid)) return 0;
		
		int space = getCapacity() / mPipeAmount - mFluids[index].amount;
		if (aFluid.amount <= space) {
			if (doFill) {
				mFluids[index].amount += aFluid.amount;
				mLastReceivedFrom |= (1 << side.ordinal());
			}
			return aFluid.amount;
		}
		if (doFill) {
			mFluids[index].amount = getCapacity() / mPipeAmount;
			mLastReceivedFrom |= (1 << side.ordinal());
		}
		return space;
	}
	
	@Override
	public final FluidStack drain(int maxDrain, boolean doDrain) {
		FluidStack drained = null;
		for (int i = 0; i < mPipeAmount; i++) {
			if ((drained = drainFromIndex(maxDrain, doDrain, i)) != null) return drained;
		}
		return null;
	}
	
	private final FluidStack drainFromIndex(int maxDrain, boolean doDrain, int index) {
		if (index < 0 || index >= mPipeAmount) return null;
		if (mFluids[index] == null) return null;
		if (mFluids[index].amount <= 0) {
			mFluids[index] = null;
			return null;
		}
		
		int used = maxDrain;
		if (mFluids[index].amount < used) used = mFluids[index].amount;
		
		if (doDrain) {
			mFluids[index].amount -= used;
		}
		
		FluidStack drained = mFluids[index].copy();
		drained.amount = used;
		
		if (mFluids[index].amount <= 0) {
			mFluids[index] = null;
		}
		
		return drained;
	}
	
	@Override
	public int getTankPressure() {
		return getFluidAmount() - (getCapacity() / 2);
	}
	
	@Override
	public String[] getDescription() {
		if (mPipeAmount == 1) {
			return new String[]{EnumChatFormatting.BLUE + "Fluid Capacity: %%%" + format(mCapacity * 20) + "%%% L/sec" + EnumChatFormatting.GRAY, EnumChatFormatting.RED + "Heat Limit: %%%" + format(mHeatResistance) + "%%% K" + EnumChatFormatting.GRAY};
		} else {
			return new String[]{EnumChatFormatting.BLUE + "Fluid Capacity: %%%" + format(mCapacity * 20) + "%%% L/sec" + EnumChatFormatting.GRAY, EnumChatFormatting.RED + "Heat Limit: %%%" + format(mHeatResistance) + "%%% K" + EnumChatFormatting.GRAY, EnumChatFormatting.AQUA + "Pipe Amount: %%%" + mPipeAmount + EnumChatFormatting.GRAY};
		}
	}
	
	@Override
	public float getThickNess() {
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x1) != 0) return 0.0625F;
		return mThickNess;
	}
	
	@Override
	public boolean isLiquidInput(int side) {
		return !isInputDisabledAtSide(side);
	}
	
	@Override
	public boolean isLiquidOutput(int side) {
		return true;
	}
	
	public boolean isInputDisabledAtSide(int side) {
		return (mDisableInput & (1 << side)) != 0;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x2) != 0) return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
		else return getActualCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	private AxisAlignedBB getActualCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float tSpace = (1f - mThickNess) / 2;
		float tSide0 = tSpace;
		float tSide1 = 1f - tSpace;
		float tSide2 = tSpace;
		float tSide3 = 1f - tSpace;
		float tSide4 = tSpace;
		float tSide5 = 1f - tSpace;
		
		if (getBaseMetaTileEntity().getCoverIDAtSide(0) != 0) {
			tSide0 = tSide2 = tSide4 = 0;
			tSide3 = tSide5 = 1;
		}
		if (getBaseMetaTileEntity().getCoverIDAtSide(1) != 0) {
			tSide2 = tSide4 = 0;
			tSide1 = tSide3 = tSide5 = 1;
		}
		if (getBaseMetaTileEntity().getCoverIDAtSide(2) != 0) {
			tSide0 = tSide2 = tSide4 = 0;
			tSide1 = tSide5 = 1;
		}
		if (getBaseMetaTileEntity().getCoverIDAtSide(3) != 0) {
			tSide0 = tSide4 = 0;
			tSide1 = tSide3 = tSide5 = 1;
		}
		if (getBaseMetaTileEntity().getCoverIDAtSide(4) != 0) {
			tSide0 = tSide2 = tSide4 = 0;
			tSide1 = tSide3 = 1;
		}
		if (getBaseMetaTileEntity().getCoverIDAtSide(5) != 0) {
			tSide0 = tSide2 = 0;
			tSide1 = tSide3 = tSide5 = 1;
		}
		
		int tConn = ((BaseMetaPipeEntity) getBaseMetaTileEntity()).mConnections;
		if ((tConn & (1 << ForgeDirection.DOWN.ordinal())) != 0) tSide0 = 0f;
		if ((tConn & (1 << ForgeDirection.UP.ordinal())) != 0) tSide1 = 1f;
		if ((tConn & (1 << ForgeDirection.NORTH.ordinal())) != 0) tSide2 = 0f;
		if ((tConn & (1 << ForgeDirection.SOUTH.ordinal())) != 0) tSide3 = 1f;
		if ((tConn & (1 << ForgeDirection.WEST.ordinal())) != 0) tSide4 = 0f;
		if ((tConn & (1 << ForgeDirection.EAST.ordinal())) != 0) tSide5 = 1f;
		
		return AxisAlignedBB.getBoundingBox(x + tSide4, y + tSide0, z + tSide2, x + tSide5, y + tSide1, z + tSide3);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {
		super.addCollisionBoxesToList(world, x, y, z, inputAABB, outputAABB, collider);
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x2) != 0) {
			AxisAlignedBB aabb = getActualCollisionBoundingBoxFromPool(world, x, y, z);
			if (inputAABB.intersectsWith(aabb)) outputAABB.add(aabb);
		}
	}
	
	@Override
	public FluidStack drain(ForgeDirection side, FluidStack aFluid, boolean doDrain) {
		if (aFluid == null) return null;
		for (int i = 0; i < mFluids.length; ++i) {
			final FluidStack f = mFluids[i];
			if (f == null || !f.isFluidEqual(aFluid)) continue;
			return drainFromIndex(aFluid.amount, doDrain, i);
		}
		return null;
	}
	
	@Override
	public Materials getMaterial() {
		return mMaterial;
	}
	
}
