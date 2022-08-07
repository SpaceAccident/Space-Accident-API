package space.accident.api.metatileentity.base;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.energy.IElectricityNetwork;
import space.accident.api.interfaces.tileentity.IWorldInteraction;
import space.accident.extensions.WorldUtil;
import space.accident.main.SpaceAccidentApi;
import space.accident.main.network.Packet_Block_Event;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static space.accident.api.enums.Values.*;
import static space.accident.main.SpaceAccidentApi.NETWORK;

/**
 * The Functions my old TileEntities and my BaseMetaTileEntities have in common.
 * <p/>
 * Basically everything a TileEntity should have.
 */
public abstract class BaseTileEntity extends TileEntity implements IWorldInteraction, IElectricityNetwork {
	/**
	 * Buffers adjacent TileEntities for faster access
	 * <p/>
	 * "this" means that there is no TileEntity, while "null" means that it doesn't know if there is even a TileEntity and still needs to check that if needed.
	 */
	private final TileEntity[] mBufferedTileEntities = new TileEntity[6];
	private final ChunkCoordinates mReturnedCoordinates = new ChunkCoordinates();
	/**
	 * If this TileEntity checks for the Chunk to be loaded before returning World based values.
	 * The AdvPump hacks this to false to ensure everything runs properly even when far Chunks are not actively loaded.
	 * But anything else should not cause worfin' Chunks, uhh I mean orphan Chunks.
	 */
	public boolean ignoreUnloadedChunks = true;
	/**
	 * This Variable checks if this TileEntity is dead, because Minecraft is too stupid to have proper TileEntity unloading.
	 */
	public boolean isDead = false;
	protected boolean mInventoryChanged = false;
	
	public static int getSideForPlayerPlacing(Entity player, int aDefaultFacing, boolean[] aAllowedFacings) {
		if (player != null) {
			if (player.rotationPitch >= 65 && aAllowedFacings[SIDE_UP]) return SIDE_UP;
			if (player.rotationPitch <= -65 && aAllowedFacings[SIDE_DOWN]) return SIDE_DOWN;
			final int rFacing = COMPASS_DIRECTIONS[MathHelper.floor_double(0.5D + 4.0F * player.rotationYaw / 360.0F) & 0x3];
			if (aAllowedFacings[rFacing]) return rFacing;
		}
		for (final int tSide : ALL_VALID_SIDES) if (aAllowedFacings[tSide]) return tSide;
		return aDefaultFacing;
	}
	
	private void clearNullMarkersFromTileEntityBuffer() {
		for (int i = 0; i < mBufferedTileEntities.length; i++)
			if (mBufferedTileEntities[i] == this) mBufferedTileEntities[i] = null;
	}
	
	/**
	 * Called automatically when the Coordinates of this TileEntity have been changed
	 */
	protected final void clearTileEntityBuffer() {
		Arrays.fill(mBufferedTileEntities, null);
	}
	
	@Override
	public final World getWorld() {
		return worldObj;
	}
	
	@Override
	public final int getX() {
		return xCoord;
	}
	
	@Override
	public final int getY() {
		return (short) yCoord;
	}
	
	@Override
	public final int getZ() {
		return zCoord;
	}
	
	@Override
	public ChunkCoordinates getChunkCoordinates() {
		mReturnedCoordinates.posX = xCoord;
		mReturnedCoordinates.posY = yCoord;
		mReturnedCoordinates.posZ = zCoord;
		return mReturnedCoordinates;
	}
	
	@Override
	public final int getOffsetX(int side, int multiplier) {
		return xCoord + ForgeDirection.getOrientation(side).offsetX * multiplier;
	}
	
	@Override
	public final int getOffsetY(int side, int multiplier) {
		return yCoord + ForgeDirection.getOrientation(side).offsetY * multiplier;
	}
	
	@Override
	public final int getOffsetZ(int side, int multiplier) {
		return zCoord + ForgeDirection.getOrientation(side).offsetZ * multiplier;
	}
	
	@Override
	public final boolean isServerSide() {
		return !worldObj.isRemote;
	}
	
	@Override
	public final boolean isClientSide() {
		return worldObj.isRemote;
	}
	
	@Override
	public final boolean openGUI(EntityPlayer player) {
		return openGUI(player, 0);
	}
	
	@Override
	public final boolean openGUI(EntityPlayer player, int id) {
		if (player == null) return false;
		player.openGui(SpaceAccidentApi.INSTANCE, id, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public boolean isInvalidTile() {
		return this.isInvalid();
	}
	
	@Override
	public int getRandomNumber(int range) {
		return ThreadLocalRandom.current().nextInt(range);
	}
	
	@Override
	public final BiomeGenBase getCurrentBiome(int x, int z) {
		return worldObj.getBiomeGenForCoords(x, z);
	}
	
	@Override
	public final BiomeGenBase getCurrentBiome() {
		return getCurrentBiome(xCoord, zCoord);
	}
	
	@Override
	public final Block getBlockOffset(int x, int y, int z) {
		return getBlock(xCoord + x, yCoord + y, zCoord + z);
	}
	
	@Override
	public final Block getBlockAtSide(int side) {
		return getBlockAtSideAndDistance(side, 1);
	}
	
	@Override
	public final Block getBlockAtSideAndDistance(int side, int distance) {
		return getBlock(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}
	
	@Override
	public final int getMetaIDOffset(int x, int y, int z) {
		return getMetaID(xCoord + x, yCoord + y, zCoord + z);
	}
	
	@Override
	public final int getMetaIDAtSide(int side) {
		return getMetaIDAtSideAndDistance(side, 1);
	}
	
	@Override
	public final int getMetaIDAtSideAndDistance(int side, int distance) {
		return getMetaID(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}
	
	@Override
	public final boolean getAirOffset(int x, int y, int z) {
		return getAir(xCoord + x, yCoord + y, zCoord + z);
	}
	
	@Override
	public final boolean getAirAtSide(int side) {
		return getAirAtSideAndDistance(side, 1);
	}
	
	@Override
	public final boolean getAirAtSideAndDistance(int side, int distance) {
		return getAir(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}
	
	@Override
	public final TileEntity getTileEntityOffset(int x, int y, int z) {
		return getTileEntity(xCoord + x, yCoord + y, zCoord + z);
	}
	
	@Override
	public final TileEntity getTileEntityAtSideAndDistance(int side, int distance) {
		if (distance == 1) return getTileEntityAtSide(side);
		return getTileEntity(getOffsetX(side, distance), getOffsetY(side, distance), getOffsetZ(side, distance));
	}
	
	@Override
	public final IInventory getIInventory(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntity(x, y, z);
		if (tTileEntity instanceof IInventory) return (IInventory) tTileEntity;
		return null;
	}
	
	@Override
	public final IInventory getIInventoryOffset(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntityOffset(x, y, z);
		if (tTileEntity instanceof IInventory) return (IInventory) tTileEntity;
		return null;
	}
	
	@Override
	public final IInventory getIInventoryAtSide(int side) {
		final TileEntity tTileEntity = getTileEntityAtSide(side);
		if (tTileEntity instanceof IInventory) return (IInventory) tTileEntity;
		return null;
	}
	
	@Override
	public final IInventory getIInventoryAtSideAndDistance(int side, int distance) {
		final TileEntity tTileEntity = getTileEntityAtSideAndDistance(side, distance);
		if (tTileEntity instanceof IInventory) return (IInventory) tTileEntity;
		return null;
	}
	
	@Override
	public final IFluidHandler getITankContainer(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntity(x, y, z);
		if (tTileEntity instanceof IFluidHandler) return (IFluidHandler) tTileEntity;
		return null;
	}
	
	@Override
	public final IFluidHandler getITankContainerOffset(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntityOffset(x, y, z);
		if (tTileEntity instanceof IFluidHandler) return (IFluidHandler) tTileEntity;
		return null;
	}
	
	@Override
	public final IFluidHandler getITankContainerAtSide(int side) {
		final TileEntity tTileEntity = getTileEntityAtSide(side);
		if (tTileEntity instanceof IFluidHandler) return (IFluidHandler) tTileEntity;
		return null;
	}
	
	@Override
	public final IFluidHandler getITankContainerAtSideAndDistance(int side, int distance) {
		final TileEntity tTileEntity = getTileEntityAtSideAndDistance(side, distance);
		if (tTileEntity instanceof IFluidHandler) return (IFluidHandler) tTileEntity;
		return null;
	}
	
	@Override
	public final ITile getITile(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntity(x, y, z);
		if (tTileEntity instanceof ITile) return (ITile) tTileEntity;
		return null;
	}
	
	@Override
	public final ITile getITileOffset(int x, int y, int z) {
		final TileEntity tTileEntity = getTileEntityOffset(x, y, z);
		if (tTileEntity instanceof ITile) return (ITile) tTileEntity;
		return null;
	}
	
	@Override
	public final ITile getITileAtSide(int side) {
		final TileEntity tTileEntity = getTileEntityAtSide(side);
		if (tTileEntity instanceof ITile) return (ITile) tTileEntity;
		return null;
	}
	
	@Override
	public final ITile getITileAtSideAndDistance(int side, int distance) {
		final TileEntity tTileEntity = getTileEntityAtSideAndDistance(side, distance);
		if (tTileEntity instanceof ITile) return (ITile) tTileEntity;
		return null;
	}
	
	@Override
	public final Block getBlock(int x, int y, int z) {
		if (ignoreUnloadedChunks && crossedChunkBorder(x, z) && !worldObj.blockExists(x, y, z)) return Blocks.air;
		return worldObj.getBlock(x, y, z);
	}
	
	public Block getBlock(ChunkCoordinates aCoords) {
		if (worldObj == null) return Blocks.air;
		if (ignoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return Blocks.air;
		return worldObj.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ);
	}
	
	@Override
	public final int getMetaID(int x, int y, int z) {
		if (ignoreUnloadedChunks && crossedChunkBorder(x, z) && !worldObj.blockExists(x, y, z)) return 0;
		return (int) worldObj.getBlockMetadata(x, y, z);
	}
	
	@Override
	public final boolean getAir(int x, int y, int z) {
		if (ignoreUnloadedChunks && crossedChunkBorder(x, z) && !worldObj.blockExists(x, y, z)) return true;
		return WorldUtil.isBlockAir(worldObj, x, y, z);
	}
	
	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		if (ignoreUnloadedChunks && crossedChunkBorder(x, z) && !worldObj.blockExists(x, y, z)) return null;
		return worldObj.getTileEntity(x, y, z);
	}
	
	@Override
	public final TileEntity getTileEntityAtSide(int side) {
		if (side < 0 || side >= 6 || mBufferedTileEntities[side] == this) return null;
		final int tX = getOffsetX(side, 1);
		final int tY = getOffsetY(side, 1);
		final int tZ = getOffsetZ(side, 1);
		if (crossedChunkBorder(tX, tZ)) {
			mBufferedTileEntities[side] = null;
			if (ignoreUnloadedChunks && !worldObj.blockExists(tX, tY, tZ)) return null;
		}
		if (mBufferedTileEntities[side] == null) {
			mBufferedTileEntities[side] = worldObj.getTileEntity(tX, tY, tZ);
			if (mBufferedTileEntities[side] == null) {
				mBufferedTileEntities[side] = this;
				return null;
			}
			return mBufferedTileEntities[side];
		}
		if (mBufferedTileEntities[side].isInvalid()) {
			mBufferedTileEntities[side] = null;
			return getTileEntityAtSide(side);
		}
		if (mBufferedTileEntities[side].xCoord == tX && mBufferedTileEntities[side].yCoord == tY && mBufferedTileEntities[side].zCoord == tZ) {
			return mBufferedTileEntities[side];
		}
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}
	
	@Override
	public boolean isDead() {
		return isDead || this.isInvalidTile();
	}
	
	@Override
	public void validate() {
		clearNullMarkersFromTileEntityBuffer();
		super.validate();
	}
	
	@Override
	public void invalidate() {
		clearNullMarkersFromTileEntityBuffer();
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload() {
		clearNullMarkersFromTileEntityBuffer();
		super.onChunkUnload();
		isDead = true;
	}
	
	@Override
	public void updateEntity() {
		// Well if the TileEntity gets ticked it is alive.
		isDead = false;
	}
	
	public final void onAdjacentBlockChange(int x, int y, int z) {
		clearNullMarkersFromTileEntityBuffer();
	}
	
	public void updateNeighbours(int mStrongRedstone, int oStrongRedstone) {
		final Block thisBlock = getBlockOffset(0, 0, 0);
		for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			final int x1 = xCoord + dir.offsetX, y1 = yCoord + dir.offsetY, z1 = zCoord + dir.offsetZ;
			
			if (worldObj.blockExists(x1, y1, z1)) {
				worldObj.notifyBlockOfNeighborChange(x1, y1, z1, thisBlock);
				
				// update if it was / is strong powered.
				if (((((mStrongRedstone | oStrongRedstone) >>> dir.ordinal()) & 1) != 0) && getBlock(x1, y1, z1).isNormalCube()) {
					final int skipUpdateSide = dir.getOpposite().ordinal(); //Don't update this block. Still updates diagonal blocks twice if conditions meet.
					
					for (final ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
						final int x2 = x1 + dir2.offsetX, y2 = y1 + dir2.offsetY, z2 = z1 + dir2.offsetZ;
						if (dir2.ordinal() != skipUpdateSide && worldObj.blockExists(x2, y2, z2))
							worldObj.notifyBlockOfNeighborChange(x2, y2, z2, thisBlock);
					}
				}
			}
		}
	}
	
	@Override
	public final void sendBlockEvent(int id, int value) {
		NETWORK.sendPacketToAllPlayersInRange(worldObj, new Packet_Block_Event(xCoord, yCoord, zCoord, id, value), xCoord, zCoord);
	}
	
	protected boolean crossedChunkBorder(int x, int z) {
		return x >> 4 != xCoord >> 4 || z >> 4 != zCoord >> 4;
	}
	
	public final boolean crossedChunkBorder(ChunkCoordinates aCoords) {
		return aCoords.posX >> 4 != xCoord >> 4 || aCoords.posZ >> 4 != zCoord >> 4;
	}
	
	public final void setOnFire() {
		WorldUtil.setCoordsOnFire(worldObj, xCoord, yCoord, zCoord, false);
	}
	
	public final void setToFire() {
		worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.fire);
	}

	@Override
	public void markDirty() {
		// Avoid sending neighbor updates, just mark the chunk as dirty to make sure it gets saved
		final Chunk chunk = worldObj.getChunkFromBlockCoords(xCoord, zCoord);
		if (chunk != null) {
			chunk.setChunkModified();
		}
	}
}
