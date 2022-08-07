package space.accident.api.metatileentity.implementations.logistic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.enums.Colors;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.TypeTileEntity;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.metatileentity.IMetaTileEntityItemPipe;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.MetaPipeEntity;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.ISerializableObject;
import space.accident.api.util.SA_CoverBehavior;
import space.accident.api.util.SA_CoverBehaviorBase;
import space.accident.main.events.ClientEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static space.accident.api.API.gt6Pipe;
import static space.accident.api.enums.Textures.BlockIcons.PIPE_RESTRICTOR;
import static space.accident.api.enums.Values.emptyIntArray;
import static space.accident.api.util.Utility.*;
import static space.accident.extensions.NumberUtils.format;
import static space.accident.extensions.NumberUtils.getOppositeSide;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;
import static space.accident.main.SpaceAccidentApi.proxy;

public class Pipe_Item extends MetaPipeEntity implements IMetaTileEntityItemPipe, TypeTileEntity.IMetaPipeItem {
	public final float mThickNess;
	public final Materials mMaterial;
	public final int mStepSize;
	public final int mTickTime;
	public int mTransferredItems = 0;
	public int mLastReceivedFrom = 0, oLastReceivedFrom = 0;
	public boolean mIsRestrictive = false;
	private int[] cacheSides;
	
	public Pipe_Item(int id, String name, String aNameRegional, float aThickNess, Materials aMaterial, int aInvSlotCount, int aStepSize, boolean aIsRestrictive, int aTickTime) {
		super(id, name, aNameRegional, aInvSlotCount, false);
		mIsRestrictive = aIsRestrictive;
		mThickNess     = aThickNess;
		mMaterial      = aMaterial;
		mStepSize      = aStepSize;
		mTickTime      = aTickTime;
		addInfo(id);
	}
	
	public Pipe_Item(int id, String name, String aNameRegional, float aThickNess, Materials aMaterial, int aInvSlotCount, int aStepSize, boolean aIsRestrictive) {
		this(id, name, aNameRegional, aThickNess, aMaterial, aInvSlotCount, aStepSize, aIsRestrictive, 20);
	}
	
	public Pipe_Item(String name, float aThickNess, Materials aMaterial, int aInvSlotCount, int aStepSize, boolean aIsRestrictive, int aTickTime) {
		super(name, aInvSlotCount);
		mIsRestrictive = aIsRestrictive;
		mThickNess     = aThickNess;
		mMaterial      = aMaterial;
		mStepSize      = aStepSize;
		mTickTime      = aTickTime;
	}
	
	@Override
	public int getTileEntityBaseType() {
		return mMaterial == null ? 4 : 4 + Math.max(0, Math.min(3, mMaterial.mToolQuality));
	}
	
	@Override
	public IMetaTile newMetaEntity(ITile aTileEntity) {
		return new Pipe_Item(mName, mThickNess, mMaterial, mInventory.length, mStepSize, mIsRestrictive, mTickTime);
	}
	
	@Override
	public ITexture[] getTexture(ITile baseTile, int side, int aConnections, int aColorIndex, boolean aConnected, boolean aRedstone) {
		if (mIsRestrictive) {
			if (aConnected) {
				float tThickNess = getThickNess();
				if (tThickNess < 0.124F)
					return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
				if (tThickNess < 0.374F)//0.375
					return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeTiny.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
				if (tThickNess < 0.499F)//0.500
					return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeSmall.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
				if (tThickNess < 0.749F)//0.750
					return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeMedium.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
				if (tThickNess < 0.874F)//0.825
					return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeLarge.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeHuge.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
			}
			return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa)), TextureFactory.of(PIPE_RESTRICTOR)};
		}
		if (aConnected) {
			float tThickNess = getThickNess();
			if (tThickNess < 0.124F)
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
			if (tThickNess < 0.374F)//0.375
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeTiny.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
			if (tThickNess < 0.499F)//0.500
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeSmall.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
			if (tThickNess < 0.749F)//0.750
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeMedium.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
			if (tThickNess < 0.874F)//0.825
				return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeLarge.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
			return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipeHuge.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
		}
		return new ITexture[]{TextureFactory.of(mMaterial.icon.textures[OrePrefixes.pipe.textureId], Colors.getModulation(aColorIndex, mMaterial.mRGBa))};
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
		return getPipeContent() * 64;
	}
	
	@Override
	public int maxProgresstime() {
		return getMaxPipeCapacity() * 64;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("mLastReceivedFrom", mLastReceivedFrom);
		if (gt6Pipe)
			nbt.setInteger("mConnections", mConnections);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		mLastReceivedFrom = nbt.getInteger("mLastReceivedFrom");
		if (gt6Pipe) {
			mConnections = nbt.getInteger("mConnections");
		}
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		if (baseTile.isServerSide() && tick % 10 == 0) {
			if (tick % mTickTime == 0) mTransferredItems = 0;
			
			if (!gt6Pipe || mCheckConnections) checkConnections();
			
			if (oLastReceivedFrom == mLastReceivedFrom) {
				doTickProfilingInThisTick = false;
				
				ArrayList<IMetaTileEntityItemPipe> tPipeList = new ArrayList<IMetaTileEntityItemPipe>();
				
				for (boolean temp = true; temp && !isInventoryEmpty() && pipeCapacityCheck(); ) {
					temp = false;
					tPipeList.clear();
					for (IMetaTileEntityItemPipe tTileEntity : sortMapByValuesAcending(IMetaTileEntityItemPipe.Util.scanPipes(this, new HashMap<>(), 0, false, false)).keySet()) {
						if (temp) break;
						tPipeList.add(tTileEntity);
						while (!temp && !isInventoryEmpty() && tTileEntity.sendItemStack(baseTile))
							for (IMetaTileEntityItemPipe tPipe : tPipeList)
								if (!tPipe.incrementTransferCounter(1)) temp = true;
					}
				}
			}
			
			if (isInventoryEmpty()) mLastReceivedFrom = 6;
			oLastReceivedFrom = mLastReceivedFrom;
		}
	}
	
	@Override
	public boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (gt6Pipe) {
			int tSide = determineWrenchingSide(side, x, y, z);
			if (isConnectedAtSide(tSide)) {
				disconnect(tSide);
				sendChat(player, trans("215", "Disconnected"));
			} else {
				if (connect(tSide) > 0)
					sendChat(player, trans("214", "Connected"));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean letsIn(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsItemsIn(side, coverId, aCoverVariable, -1, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehavior coverBehavior, int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsItemsOut(side, coverId, aCoverVariable, -1, aTileEntity);
	}
	
	@Override
	public boolean letsIn(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsItemsIn(side, coverId, aCoverVariable, -1, aTileEntity);
	}
	
	@Override
	public boolean letsOut(SA_CoverBehaviorBase<?> coverBehavior, int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return coverBehavior.letsItemsOut(side, coverId, aCoverVariable, -1, aTileEntity);
	}
	
	@Override
	public boolean canConnect(int side, TileEntity tTileEntity) {
		if (tTileEntity == null) return false;
		
		final int tSide = getOppositeSide(side);
		boolean connectable = isConnectableNonInventoryPipe(tTileEntity, tSide);
		
		final ITile gTileEntity = (tTileEntity instanceof ITile) ? (ITile) tTileEntity : null;
		if (gTileEntity != null) {
			if (gTileEntity.getMetaTile() == null) return false;
			if (gTileEntity.getMetaTile().connectsToItemPipe(tSide)) return true;
			connectable = true;
		}
		
		if (tTileEntity instanceof IInventory) {
			if (((IInventory) tTileEntity).getSizeInventory() <= 0) return false;
			connectable = true;
		}
		if (tTileEntity instanceof ISidedInventory) {
			int[] tSlots = ((ISidedInventory) tTileEntity).getAccessibleSlotsFromSide(tSide);
			if (tSlots == null || tSlots.length <= 0) return false;
			connectable = true;
		}
		
		return connectable;
	}
	
	@Override
	public boolean getGT6StyleConnection() {
		// Yes if GT6 pipes are enabled
		return gt6Pipe;
	}
	
	
	@Override
	public boolean incrementTransferCounter(int aIncrement) {
		mTransferredItems += aIncrement;
		return pipeCapacityCheck();
	}
	
	@Override
	public boolean sendItemStack(Object aSender) {
		if (pipeCapacityCheck()) {
			int tOffset = getBaseMetaTileEntity().getRandomNumber(6), tSide = 0;
			for (int i = 0; i < 6; i++) {
				tSide = (i + tOffset) % 6;
				if (isConnectedAtSide(tSide) && (isInventoryEmpty() || (tSide != mLastReceivedFrom || aSender != getBaseMetaTileEntity()))) {
					if (insertItemStackIntoTileEntity(aSender, tSide)) return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean insertItemStackIntoTileEntity(Object aSender, int side) {
		if (getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).letsItemsOut(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), -1, getBaseMetaTileEntity())) {
			TileEntity tInventory = getBaseMetaTileEntity().getTileEntityAtSide(side);
			if (tInventory != null && !(tInventory instanceof BaseMetaPipeEntity)) {
				if ((!(tInventory instanceof TileEntityHopper) && !(tInventory instanceof TileEntityDispenser)) || getBaseMetaTileEntity().getMetaIDAtSide(side) != getOppositeSide(side)) {
					return moveMultipleItemStacks(aSender, tInventory, 6, getOppositeSide(side), null, false, 64, 1, 64, 1, 1) > 0;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean pipeCapacityCheck() {
		return mTransferredItems <= 0 || getPipeContent() < getMaxPipeCapacity();
	}
	
	private int getPipeContent() {
		return mTransferredItems;
	}
	
	private int getMaxPipeCapacity() {
		return Math.max(1, getPipeCapacity());
	}
	
	/**
	 * Amount of ItemStacks this Pipe can conduct per Second.
	 */
	public int getPipeCapacity() {
		return mInventory.length;
	}
	
	@Override
	public int getStepSize() {
		return mStepSize;
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack stack, int side) {
		return isConnectedAtSide(side) && super.canInsertItem(index, stack, side);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return isConnectedAtSide(side);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		ITile tTileEntity = getBaseMetaTileEntity();
		boolean tAllow = tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsIn(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), -2, tTileEntity) || tTileEntity.getCoverBehaviorAtSideNew(side).letsItemsOut(side, tTileEntity.getCoverIDAtSide(side), tTileEntity.getComplexCoverDataAtSide(side), -2, tTileEntity);
		if (tAllow) {
			if (cacheSides == null)
				cacheSides = super.getAccessibleSlotsFromSide(side);
			return cacheSides;
		} else {
			return emptyIntArray;
		}
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return isConnectedAtSide(side);
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		if (!isConnectedAtSide(side)) return false;
		if (isInventoryEmpty()) mLastReceivedFrom = side;
		return mLastReceivedFrom == side && mInventory[index] == null;
	}
	
	@Override
	public String[] getDescription() {
		if (mTickTime == 20)
			return new String[]{"Item Capacity: %%%" + getMaxPipeCapacity() + "%%% Stacks/sec", "Routing Value: %%%" + format(mStepSize)};
		else if (mTickTime % 20 == 0)
			return new String[]{"Item Capacity: %%%" + getMaxPipeCapacity() + "%%% Stacks/%%%" + (mTickTime / 20) + "%%% sec", "Routing Value: %%%" + format(mStepSize)};
		else
			return new String[]{"Item Capacity: %%%" + getMaxPipeCapacity() + "%%% Stacks/%%%" + mTickTime + "%%% ticks", "Routing Value: %%%" + format(mStepSize)};
	}
	
	private boolean isInventoryEmpty() {
		for (ItemStack tStack : mInventory) if (tStack != null) return false;
		return true;
	}
	
	@Override
	public float getThickNess() {
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x1) != 0) return 0.0625F;
		return mThickNess;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x2) != 0)
			return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
		else
			return getActualCollisionBoundingBoxFromPool(world, x, y, z);
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
	public Materials getMaterial() {
		return mMaterial;
	}
}
