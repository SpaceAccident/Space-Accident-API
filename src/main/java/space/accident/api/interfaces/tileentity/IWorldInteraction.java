package space.accident.api.interfaces.tileentity;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidHandler;

public interface IWorldInteraction {
	World getWorld();
	
	int getX();
	
	int getY();
	
	int getZ();
	
	default ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(getX(), getY(), getZ());
	}
	
	boolean isServerSide();
	
	boolean isClientSide();
	
	int getRandomNumber(int range);
	
	TileEntity getTileEntity(int x, int y, int z);
	
	TileEntity getTileEntityOffset(int x, int y, int z);
	
	TileEntity getTileEntityAtSide(int side);
	
	TileEntity getTileEntityAtSideAndDistance(int side, int distance);
	
	IInventory getIInventory(int x, int y, int z);
	
	IInventory getIInventoryOffset(int x, int y, int z);
	
	IInventory getIInventoryAtSide(int side);
	
	IInventory getIInventoryAtSideAndDistance(int side, int distance);
	
	IFluidHandler getITankContainer(int x, int y, int z);
	
	IFluidHandler getITankContainerOffset(int x, int y, int z);
	
	IFluidHandler getITankContainerAtSide(int side);
	
	IFluidHandler getITankContainerAtSideAndDistance(int side, int distance);
	
	ITile getITile(int x, int y, int z);
	
	ITile getITileOffset(int x, int y, int z);
	
	ITile getITileAtSide(int side);
	
	ITile getITileAtSideAndDistance(int side, int distance);
	
	Block getBlock(int x, int y, int z);
	
	Block getBlockOffset(int x, int y, int z);
	
	Block getBlockAtSide(int side);
	
	Block getBlockAtSideAndDistance(int side, int distance);
	
	int getMetaID(int x, int y, int z);
	
	int getMetaIDOffset(int x, int y, int z);
	
	int getMetaIDAtSide(int side);
	
	int getMetaIDAtSideAndDistance(int side, int distance);
	
	boolean getAir(int x, int y, int z);
	
	boolean getAirOffset(int x, int y, int z);
	
	boolean getAirAtSide(int side);
	
	boolean getAirAtSideAndDistance(int side, int distance);
	
	BiomeGenBase getCurrentBiome();
	
	BiomeGenBase getCurrentBiome(int x, int z);
	
	int getOffsetX(int side, int multiplier);
	
	int getOffsetY(int side, int multiplier);
	
	int getOffsetZ(int side, int multiplier);
	
	/**
	 * Checks if the TileEntity is Invalid or Unloaded. Stupid Minecraft cannot do that btw.
	 */
	boolean isDead();
	
	/**
	 * Sends a Block Event to the Client TileEntity, the int Parameters are only for validation as Minecraft doesn't properly write Packet Data.
	 */
	void sendBlockEvent(int id, int value);
	
	/**
	 * @return the Time this TileEntity has been loaded.
	 */
	long getTick();
	
	/**
	 * Function of the regular TileEntity
	 */
	void writeToNBT(NBTTagCompound nbt);
	
	/**
	 * Function of the regular TileEntity
	 */
	void readFromNBT(NBTTagCompound nbt);
	
	/**
	 * Function of the regular TileEntity
	 */
	boolean isInvalidTile();
	
	/**
	 * Opens the GUI with this ID of this MetaTileEntity
	 */
	boolean openGUI(EntityPlayer player, int id);
	
	/**
	 * Opens the GUI with the ID = 0 of this TileEntity
	 */
	boolean openGUI(EntityPlayer player);
}
