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
import space.accident.api.API;
import space.accident.api.enums.Colors;
import space.accident.api.enums.Materials;
import space.accident.api.enums.TextureSet;
import space.accident.api.enums.Textures;
import space.accident.api.graphs.Node;
import space.accident.api.graphs.NodeList;
import space.accident.api.graphs.PowerNode;
import space.accident.api.graphs.PowerNodes;
import space.accident.api.graphs.consumers.ConsumerNode;
import space.accident.api.graphs.paths.PowerNodePath;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.TypeTileEntity;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.metatileentity.IMetaTileEntityCable;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.energy.IEnergyTileConnected;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.MetaPipeEntity;
import space.accident.api.objects.SA_Cover_None;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.*;
import space.accident.main.events.ClientEvents;

import java.util.HashSet;
import java.util.List;

import static space.accident.api.enums.Values.VN;
import static space.accident.api.util.Utility.consumeSolderingMaterial;
import static space.accident.extensions.NumberUtils.*;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;
import static space.accident.main.SpaceAccidentApi.proxy;

public class Cable_Electricity extends MetaPipeEntity implements IMetaTileEntityCable, TypeTileEntity.IMetaCable {
	public static short mMaxOverheat = (short) (API.mWireHeatingTicks * 100);
	public final float mThickNess;
	public final Materials mMaterial;
	public final long mCableLossPerMeter, mAmperage, mVoltage;
	public final boolean mInsulated, mCanShock;
	public int mTransferredAmperage = 0, mTransferredAmperageLast20 = 0, mTransferredAmperageLast20OK = 0, mTransferredAmperageOK = 0;
	public long mTransferredVoltageLast20 = 0, mTransferredVoltage = 0, mTransferredVoltageLast20OK = 0, mTransferredVoltageOK = 0;
	public long mRestRF;
	public int mOverheat;
	private int[] lastAmperage;
	private long lastWorldTick;
	
	public Cable_Electricity(int id, String name, String aNameRegional, float aThickNess, Materials aMaterial, long aCableLossPerMeter, long aAmperage, long aVoltage, boolean aInsulated, boolean aCanShock) {
		super(id, name, aNameRegional, 0);
		mThickNess         = aThickNess;
		mMaterial          = aMaterial;
		mAmperage          = aAmperage;
		mVoltage           = aVoltage;
		mInsulated         = aInsulated;
		mCanShock          = aCanShock;
		mCableLossPerMeter = aCableLossPerMeter;
	}
	
	public Cable_Electricity(String name, float aThickNess, Materials aMaterial, long aCableLossPerMeter, long aAmperage, long aVoltage, boolean aInsulated, boolean aCanShock) {
		super(name, 0);
		mThickNess         = aThickNess;
		mMaterial          = aMaterial;
		mAmperage          = aAmperage;
		mVoltage           = aVoltage;
		mInsulated         = aInsulated;
		mCanShock          = aCanShock;
		mCableLossPerMeter = aCableLossPerMeter;
	}
	
	@Override
	public int getTileEntityBaseType() {
		return (mInsulated ? 9 : 8);
	}
	
	@Override
	public IMetaTile newMetaEntity(ITile aTileEntity) {
		return new Cable_Electricity(mName, mThickNess, mMaterial, mCableLossPerMeter, mAmperage, mVoltage, mInsulated, mCanShock);
	}
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int aConnections, int aColorIndex, boolean aConnected, boolean aRedstone) {
		if (!mInsulated) return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[TextureSet.INDEX_wire], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
		if (aConnected) {
			float tThickNess = getThickNess();
			
			IIconContainer typeCable;
			
			if (tThickNess <= 0.062F) {
				typeCable = Textures.BlockIcons.INSULATION_FULL;
			} else if (tThickNess <= 0.310F) {
				typeCable = Textures.BlockIcons.INSULATION_TINY; //x1-x4
			} else if (tThickNess <= 0.434F) {
				typeCable = Textures.BlockIcons.INSULATION_SMALL; //x6
			} else if (tThickNess <= 0.558F) {
				typeCable = Textures.BlockIcons.INSULATION_MEDIUM; //x8
			} else if (tThickNess <= 0.620F) {
				typeCable = Textures.BlockIcons.INSULATION_MEDIUM_PLUS; //x9
			} else if (tThickNess <= 0.806F) {
				typeCable = Textures.BlockIcons.INSULATION_MEDIUM_PLUS; //x12
			} else if (tThickNess <= 0.992F) { //x16
				typeCable = Textures.BlockIcons.INSULATION_HUGE;
			} else {
				typeCable = Textures.BlockIcons.INSULATION_FULL;
			}
			
			if (typeCable == Textures.BlockIcons.INSULATION_FULL) {
				return new ITexture[]{TextureFactory.of(Textures.BlockIcons.INSULATION_FULL, Colors.getModulation(aColorIndex, Colors.CABLE_INSULATION.mRGBa))};
			} else {
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[TextureSet.INDEX_wire], mMaterial.mRGBa), TextureFactory.of(typeCable, Colors.getModulation(aColorIndex, Colors.CABLE_INSULATION.mRGBa))};
			}
		}
		return new ITexture[]{TextureFactory.of(Textures.BlockIcons.INSULATION_FULL, Colors.getModulation(aColorIndex, Colors.CABLE_INSULATION.mRGBa))};
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (mCanShock && (((BaseMetaPipeEntity) getBaseMetaTileEntity()).mConnections & -128) == 0 && entity instanceof EntityLivingBase && !isCoverOnSide((BaseMetaPipeEntity) getBaseMetaTileEntity(), (EntityLivingBase) entity)) Utility.applyElectricityDamage((EntityLivingBase) entity, mTransferredVoltageLast20, mTransferredAmperageLast20);
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
		return true;
	}
	
	@Override
	public final boolean renderInside(int side) {
		return false;
	}
	
	@Override
	public int getProgresstime() {
		return mTransferredAmperage * 64;
	}
	
	@Override
	public int maxProgresstime() {
		return (int) mAmperage * 64;
	}
	
	@Override
	public long injectEnergyUnits(int side, long aVoltage, long aAmperage) {
		if (!isConnectedAtSide(side) && side != 6) return 0;
		if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).letsEnergyIn(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), getBaseMetaTileEntity()))
			return 0;
		HashSet<TileEntity> nul = null;
		return transferElectricity(side, aVoltage, aAmperage, nul);
	}
	
	@Override
	public long transferElectricity(int side, long aVoltage, long aAmperage, HashSet<TileEntity> aAlreadyPassedSet) {
		if (!getBaseMetaTileEntity().isServerSide() || !isConnectedAtSide(side) && side != 6) return 0;
		BaseMetaPipeEntity tBase = (BaseMetaPipeEntity) getBaseMetaTileEntity();
		if (!(tBase.getNode() instanceof PowerNode)) return 0;
		PowerNode tNode = (PowerNode) tBase.getNode();
		if (tNode != null) {
			int tPlace = 0;
			Node[] tToPower = new Node[tNode.mConsumers.size()];
			if (tNode.mHadVoltage) {
				for (ConsumerNode consumer : tNode.mConsumers) {
					if (consumer.needsEnergy()) tToPower[tPlace++] = consumer;
				}
			} else {
				tNode.mHadVoltage = true;
				for (ConsumerNode consumer : tNode.mConsumers) {
					tToPower[tPlace++] = consumer;
				}
			}
			return PowerNodes.powerNode(tNode, null, new NodeList(tToPower), (int) aVoltage, (int) aAmperage);
		}
		return 0;
	}
	
	@Override
	public void onFirstTick(ITile baseTile) {
		if (baseTile.isServerSide()) {
			lastAmperage  = new int[16];
			lastWorldTick = baseTile.getWorld().getTotalWorldTime() - 1;//sets initial value -1 since it is in the same tick as first on post tick
		}
	}
	
	
	@Override
	public void onPostTick(ITile te, long tick) {
		super.onPostTick(te, tick);
		if (tick % 20 == 0 && te.isServerSide() && (!API.gt6Cable || mCheckConnections)) {
			checkConnections();
		}
	}
	
	@Override
	public boolean onWireCutterRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (API.gt6Cable && ModHandler.damageOrDechargeItem(player.inventory.getCurrentItem(), 1, 500, player)) {
			if (isConnectedAtSide(aWrenchingSide)) {
				disconnect(aWrenchingSide);
				sendChat(player, trans("215", "Disconnected"));
			} else if (!API.costlyCableConnection) {
				if (connect(aWrenchingSide) > 0) sendChat(player, trans("214", "Connected"));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onSolderingToolRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (API.gt6Cable && ModHandler.damageOrDechargeItem(player.inventory.getCurrentItem(), 1, 500, player)) {
			if (isConnectedAtSide(aWrenchingSide)) {
				disconnect(aWrenchingSide);
				sendChat(player, trans("215", "Disconnected"));
			} else if (!API.costlyCableConnection || consumeSolderingMaterial(player)) {
				if (connect(aWrenchingSide) > 0) sendChat(player, trans("214", "Connected"));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean letsIn(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsEnergyIn(side, coverId, aCoverVariable, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsEnergyOut(side, coverId, aCoverVariable, aTileEntity);
	}
	
	@Override
	public boolean letsIn(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsEnergyIn(side, coverId, aCoverVariable, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsEnergyOut(side, coverId, aCoverVariable, aTileEntity);
	}
	
	
	@Override
	public boolean canConnect(int side, TileEntity tTileEntity) {
		final int tSide = getOppositeSide(side);
		if (!(tTileEntity instanceof IEnergyTileConnected)) return false;
		IEnergyTileConnected connected = (IEnergyTileConnected) tTileEntity;
		return connected.inputEnergyFrom(tSide, false) || connected.outputsEnergyTo(tSide, false);
	}
	
	@Override
	public boolean getGT6StyleConnection() {
		return API.gt6Cable;
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
	public String[] getDescription() {
		return new String[]{"Max Voltage: %%%" + EnumChatFormatting.GREEN + format(mVoltage) + " (" + VN[getTier(mVoltage)] + EnumChatFormatting.GREEN + ")" + EnumChatFormatting.GRAY, "Max Amperage: %%%" + EnumChatFormatting.YELLOW + format(mAmperage) + EnumChatFormatting.GRAY, "Loss/Meter/Ampere: %%%" + EnumChatFormatting.RED + format(mCableLossPerMeter) + EnumChatFormatting.GRAY + "%%% EU-Volt"};
	}
	
	@Override
	public float getThickNess() {
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x1) != 0) return 0.0625F;
		return mThickNess;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if (API.gt6Cable) nbt.setInteger("mConnections", mConnections);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (API.gt6Cable) {
			mConnections = nbt.getInteger("mConnections");
		}
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
	@Override
	public String[] getInfoData() {
		BaseMetaPipeEntity base = (BaseMetaPipeEntity) getBaseMetaTileEntity();
		PowerNodePath path = (PowerNodePath) base.getNodePath();
		long amps = 0;
		long volts = 0;
		if (path != null) {
			amps  = path.getAmps();
			volts = path.getVoltage(this);
		}
		return new String[]{
				//EnumChatFormatting.BLUE + mName + EnumChatFormatting.RESET,
				"Heat: " + EnumChatFormatting.RED + format(mOverheat) + EnumChatFormatting.RESET + " / " + EnumChatFormatting.YELLOW + format(mMaxOverheat) + EnumChatFormatting.RESET, "Max Load (1t):", EnumChatFormatting.GREEN + format(amps) + EnumChatFormatting.RESET + " A / " + EnumChatFormatting.YELLOW + format(mAmperage) + EnumChatFormatting.RESET + " A", "Max EU/p (1t):", EnumChatFormatting.GREEN + format(volts) + EnumChatFormatting.RESET + " EU / " + EnumChatFormatting.YELLOW + format(mVoltage) + EnumChatFormatting.RESET + " EU", "Max Load (20t): " + EnumChatFormatting.GREEN + format(mTransferredAmperageLast20OK) + EnumChatFormatting.RESET + " A", "Max EU/p (20t): " + EnumChatFormatting.GREEN + format(mTransferredVoltageLast20OK) + EnumChatFormatting.RESET + " EU"};
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
	public void reloadLocks() {
		BaseMetaPipeEntity pipe = (BaseMetaPipeEntity) getBaseMetaTileEntity();
		if (pipe.getNode() != null) {
			for (int i = 0; i < 6; i++) {
				if (isConnectedAtSide(i)) {
					final SA_CoverBehaviorBase<?> coverBehavior = pipe.getCoverBehaviorAtSideNew(i);
					if (coverBehavior instanceof SA_Cover_None) continue;
					final int coverId = pipe.getCoverIDAtSide(i);
					ISerializableObject coverData = pipe.getComplexCoverDataAtSide(i);
					if (!letsIn(coverBehavior, i, coverId, coverData, pipe) || !letsOut(coverBehavior, i, coverId, coverData, pipe)) {
						pipe.addToLock(pipe, i);
					} else {
						pipe.removeFromLock(pipe, i);
					}
				}
			}
		} else {
			boolean dontAllow = false;
			for (int i = 0; i < 6; i++) {
				if (isConnectedAtSide(i)) {
					final SA_CoverBehaviorBase<?> coverBehavior = pipe.getCoverBehaviorAtSideNew(i);
					if (coverBehavior instanceof SA_Cover_None) continue;
					final int coverId = pipe.getCoverIDAtSide(i);
					ISerializableObject coverData = pipe.getComplexCoverDataAtSide(i);
					if (!letsIn(coverBehavior, i, coverId, coverData, pipe) || !letsOut(coverBehavior, i, coverId, coverData, pipe)) {
						dontAllow = true;
					}
				}
			}
			if (dontAllow) {
				pipe.addToLock(pipe, 0);
			} else {
				pipe.removeFromLock(pipe, 0);
			}
		}
	}
	
	@Override
	public Materials getMaterial() {
		return mMaterial;
	}
}
