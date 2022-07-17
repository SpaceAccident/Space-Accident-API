package space.accident.api.interfaces.te

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidHandler
import space.accident.api.interfaces.mte.IMetaTileEntity
import java.util.*

/**
 * A simple compound Interface for all my TileEntities.
 * Also delivers most of the Information about my TileEntities.
 * It can cause Problems to include this Interface!
 */
interface IGregTechTileEntity : ITexturedTileEntity, IGearEnergyTileEntity, ICoverable, IFluidHandler, ITurnable,
    IGregTechDeviceInformation, IUpgradableMachine, IDescribable, IMachineBlockUpdateable, IGregtechWailaProvider {

    /**
     * gets the Error displayed on the GUI
     */
    fun getErrorDisplayID(): Int

    /**
     * sets the Error displayed on the GUI
     */
    fun setErrorDisplayID(aErrorID: Int)

    /**
     * @return the MetaID of the Block or the MetaTileEntity ID.
     */
    fun getMetaTileID(): Int

    /**
     * Internal Usage only!
     */
    fun setMetaTileID(aID: Short): Int

    /**
     * @return the MetaTileEntity which is belonging to this, or null if it doesnt has one.
     */
    fun getMetaTileEntity(): IMetaTileEntity?

    /**
     * Sets the MetaTileEntity.
     * Even though this uses the Universal Interface, certain BaseMetaTileEntities only accept one kind of MetaTileEntity
     * so only use this if you are sure its the correct one or you will get a Class cast Error.
     *
     * @param aMetaTileEntity
     */
    fun setMetaTileEntity(aMetaTileEntity: IMetaTileEntity?)

    /**
     * Causes a general Texture update.
     *
     *
     * Only used Client Side to mark Blocks dirty.
     */
    fun issueTextureUpdate()

    /**
     * Causes the Machine to send its initial Data, like Covers and its ID.
     */
    fun issueClientUpdate()

    /**
     * causes Explosion. Strength in Overload-EU
     */
    fun doExplosion(aExplosionEU: Long)

    /**
     * Sets the Block on Fire in all 6 Directions
     */
    fun setOnFire()

    /**
     * Sets the Block to Fire
     */
    fun setToFire()

    /**
     * Sets the Owner of the Machine. Returns the set Name.
     */
    fun setOwnerName(aName: String?): String?

    /**
     * gets the Name of the Machines Owner or "Player" if not set.
     */
    fun getOwnerName(): String?

    /**
     * Gets the UniqueID of the Machines Owner.
     */
    fun getOwnerUuid(): UUID?

    /**
     * Sets the UniqueID of the Machines Owner.
     */
    fun setOwnerUuid(uuid: UUID?)

    /**
     * Sets initial Values from NBT
     *
     * @param aNBT is the NBTTag of readFromNBT
     * @param aID  is the MetaTileEntityID
     */
    fun setInitialValuesAsNBT(aNBT: NBTTagCompound?, aID: Short)

    /**
     * Called when leftclicking the TileEntity
     */
    fun onLeftclick(aPlayer: EntityPlayer?)

    /**
     * Called when rightclicking the TileEntity
     */
    fun onRightclick(aPlayer: EntityPlayer?, aSide: Byte, aX: Float, aY: Float, aZ: Float): Boolean

    fun getBlastResistance(aSide: Byte): Float

    fun getDrops(): ArrayList<ItemStack?>?

    /**
     * 255 = 100%
     */
    fun getLightOpacity(): Int

    fun addCollisionBoxesToList(
        aWorld: World?,
        aX: Int,
        aY: Int,
        aZ: Int,
        inputAABB: AxisAlignedBB,
        outputAABB: List<AxisAlignedBB>,
        collider: Entity
    )

    fun getCollisionBoundingBoxFromPool(aWorld: World, aX: Int, aY: Int, aZ: Int): AxisAlignedBB

    fun onEntityCollidedWithBlock(aWorld: World, aX: Int, aY: Int, aZ: Int, collider: Entity)

    /**
     * Checks validity of meta tile and delegates to it
     */
    override fun onMachineBlockUpdate() {
        if (!isDead() && getMetaTileEntity()?.getBaseMetaTileEntity() == this) {
            getMetaTileEntity()?.onMachineBlockUpdate()
        }
    }

    /**
     * Checks validity of meta tile and delegates to it
     */
    override fun isMachineBlockUpdateRecursive(): Boolean {
        return !isDead() && getMetaTileEntity()?.getBaseMetaTileEntity() ==
                this && getMetaTileEntity()?.isMachineBlockUpdateRecursive() == true
    }

    fun setShutdownStatus(newStatus: Boolean) {
        return
    }
}