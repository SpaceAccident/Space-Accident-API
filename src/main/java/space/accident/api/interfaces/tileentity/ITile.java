package space.accident.api.interfaces.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.energy.IEnergyTransferTile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ITile extends ITexturedTileEntity, IEnergyTransferTile, ICoverable, IFluidHandler, ITurn, ITileDeviceInfo, IUpgradableMachine, IDigitalChest, IDescribable, IMachineBlockUpdate, ITileWailaProvider {
	/**
	 * gets the Error displayed on the GUI
	 */
	int getErrorDisplayID();
	
	/**
	 * sets the Error displayed on the GUI
	 */
	void setErrorDisplayID(int errorId);
	
	/**
	 * @return the MetaID of the Block or the MetaTileEntity ID.
	 */
	int getMetaTileID();
	
	/**
	 * Internal Usage only!
	 */
	int setMetaTileID(short id);
	
	/**
	 * @return the MetaTileEntity which is belonging to this, or null if it does has not one.
	 */
	IMetaTile getMetaTile();
	
	/**
	 * Sets the MetaTileEntity.
	 * Even though this uses the Universal Interface, certain BaseMetaTileEntities only accept one kind of MetaTileEntity
	 * so only use this if you are sure its the correct one or you will get a Class cast Error.
	 *
	 * @param metaTile
	 */
	void setMetaTile(IMetaTile metaTile);
	
	/**
	 * Causes a general Texture update.
	 * <p/>
	 * Only used Client Side to mark Blocks dirty.
	 */
	void issueTextureUpdate();
	
	/**
	 * Causes the Machine to send its initial Data, like Covers and its ID.
	 */
	void issueClientUpdate();
	
	/**
	 * causes Explosion. Strength in Overload-EU
	 */
	void doExplosion(long explosionEU);
	
	/**
	 * Sets the Block on Fire in all 6 Directions
	 */
	void setOnFire();
	
	/**
	 * Sets the Block to Fire
	 */
	void setToFire();
	
	/**
	 * Sets the Owner of the Machine. Returns the set Name.
	 */
	String setOwnerName(String name);
	
	/**
	 * gets the Name of the Machines Owner or "Player" if not set.
	 */
	String getOwnerName();
	
	/**
	 * Gets the UniqueID of the Machines Owner.
	 */
	UUID getOwnerUuid();
	
	/**
	 * Sets the UniqueID of the Machines Owner.
	 */
	void setOwnerUuid(UUID uuid);
	
	/**
	 * Sets initial values from NBT
	 *
	 * @param nbt is the NBTTag of readFromNBT
	 * @param id  is the MetaTileEntityID
	 */
	void setInitialValuesAsNBT(NBTTagCompound nbt, short id);
	
	/**
	 * Called when left-click the TileEntity
	 */
	void onLeftClick(EntityPlayer player);
	
	/**
	 * Called when right-click the TileEntity
	 */
	boolean onRightClick(EntityPlayer player, int side, float x, float y, float z);
	
	float getBlastResistance(int side);
	
	ArrayList<ItemStack> getDrops();
	
	/**
	 * 255 = 100%
	 */
	int getLightOpacity();
	
	void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider);
	
	AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z);
	
	void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider);
	
	/**
	 * Checks validity of meta tile and delegates to it
	 */
	@Override
	default void onMachineBlockUpdate() {
		if (!isDead() && getMetaTile() != null && getMetaTile().getBaseMetaTileEntity() == this) {
			getMetaTile().onMachineBlockUpdate();
		}
	}
	
	/**
	 * Checks validity of meta tile and delegates to it
	 */
	@Override
	default boolean isMachineBlockUpdateRecursive() {
		return !isDead() && getMetaTile() != null && getMetaTile().getBaseMetaTileEntity() == this && getMetaTile().isMachineBlockUpdateRecursive();
	}
	
	default void setShutdownStatus(boolean newStatus) {
	}
}
