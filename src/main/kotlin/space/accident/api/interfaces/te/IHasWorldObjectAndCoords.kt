package space.accident.api.interfaces.te

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fluids.IFluidHandler

interface IHasWorldObjectAndCoords {
    fun getWorld(): World?

    fun getXCoord(): Int

    fun getYCoord(): Short

    fun getZCoord(): Int

    fun getCoords(): ChunkCoordinates? {
        return ChunkCoordinates(getXCoord(), getYCoord().toInt(), getZCoord())
    }

    fun isServerSide(): Boolean

    fun isClientSide(): Boolean

    fun getRandomNumber(aRange: Int): Int

    fun getTileEntity(aX: Int, aY: Int, aZ: Int): TileEntity?

    fun getTileEntityOffset(aX: Int, aY: Int, aZ: Int): TileEntity?

    fun getTileEntityAtSide(aSide: Byte): TileEntity?

    fun getTileEntityAtSideAndDistance(aSide: Byte, aDistance: Int): TileEntity?

    fun getIInventory(aX: Int, aY: Int, aZ: Int): IInventory?

    fun getIInventoryOffset(aX: Int, aY: Int, aZ: Int): IInventory?

    fun getIInventoryAtSide(aSide: Byte): IInventory?

    fun getIInventoryAtSideAndDistance(aSide: Byte, aDistance: Int): IInventory?

    fun getITankContainer(aX: Int, aY: Int, aZ: Int): IFluidHandler?

    fun getITankContainerOffset(aX: Int, aY: Int, aZ: Int): IFluidHandler?

    fun getITankContainerAtSide(aSide: Byte): IFluidHandler?

    fun getITankContainerAtSideAndDistance(aSide: Byte, aDistance: Int): IFluidHandler?

    fun getIGregTechTileEntity(aX: Int, aY: Int, aZ: Int): IGregTechTileEntity?

    fun getIGregTechTileEntityOffset(aX: Int, aY: Int, aZ: Int): IGregTechTileEntity?

    fun getIGregTechTileEntityAtSide(aSide: Byte): IGregTechTileEntity?

    fun getIGregTechTileEntityAtSideAndDistance(aSide: Byte, aDistance: Int): IGregTechTileEntity?

    fun getBlock(aX: Int, aY: Int, aZ: Int): Block?

    fun getBlockOffset(aX: Int, aY: Int, aZ: Int): Block?

    fun getBlockAtSide(aSide: Byte): Block?

    fun getBlockAtSideAndDistance(aSide: Byte, aDistance: Int): Block?

    fun getMetaID(aX: Int, aY: Int, aZ: Int): Byte

    fun getMetaIDOffset(aX: Int, aY: Int, aZ: Int): Byte

    fun getMetaIDAtSide(aSide: Byte): Byte

    fun getMetaIDAtSideAndDistance(aSide: Byte, aDistance: Int): Byte

    fun getLightLevel(aX: Int, aY: Int, aZ: Int): Byte

    fun getLightLevelOffset(aX: Int, aY: Int, aZ: Int): Byte

    fun getLightLevelAtSide(aSide: Byte): Byte

    fun getLightLevelAtSideAndDistance(aSide: Byte, aDistance: Int): Byte

    fun getOpacity(aX: Int, aY: Int, aZ: Int): Boolean

    fun getOpacityOffset(aX: Int, aY: Int, aZ: Int): Boolean

    fun getOpacityAtSide(aSide: Byte): Boolean

    fun getOpacityAtSideAndDistance(aSide: Byte, aDistance: Int): Boolean

    fun getSky(aX: Int, aY: Int, aZ: Int): Boolean

    fun getSkyOffset(aX: Int, aY: Int, aZ: Int): Boolean

    fun getSkyAtSide(aSide: Byte): Boolean

    fun getSkyAtSideAndDistance(aSide: Byte, aDistance: Int): Boolean

    fun getAir(aX: Int, aY: Int, aZ: Int): Boolean

    fun getAirOffset(aX: Int, aY: Int, aZ: Int): Boolean

    fun getAirAtSide(aSide: Byte): Boolean

    fun getAirAtSideAndDistance(aSide: Byte, aDistance: Int): Boolean

    fun getBiome(): BiomeGenBase?

    fun getBiome(aX: Int, aZ: Int): BiomeGenBase?

    fun getOffsetX(aSide: Byte, aMultiplier: Int): Int

    fun getOffsetY(aSide: Byte, aMultiplier: Int): Short

    fun getOffsetZ(aSide: Byte, aMultiplier: Int): Int

    /**
     * Checks if the TileEntity is Invalid or Unloaded. Stupid Minecraft cannot do that btw.
     */
    fun isDead(): Boolean

    /**
     * Sends a Block Event to the Client TileEntity, the byte Parameters are only for validation as Minecraft doesn't properly write Packet Data.
     */
    fun sendBlockEvent(aID: Byte, aValue: Byte)

    /**
     * @return the Time this TileEntity has been loaded.
     */
    fun getTimer(): Long

    /**
     * Sets the Light Level of this Block on a Scale of 0 - 15
     * It could be that it doesn't work. This is just for convenience.
     */
    fun setLightValue(aLightValue: Byte)

    /**
     * Function of the regular TileEntity
     */
    fun writeToNBT(aNBT: NBTTagCompound?)

    /**
     * Function of the regular TileEntity
     */
    fun readFromNBT(aNBT: NBTTagCompound?)

    /**
     * Function of the regular TileEntity
     */
    fun isInvalidTileEntity(): Boolean

    /**
     * Opens the GUI with this ID of this MetaTileEntity
     */
    fun openGUI(aPlayer: EntityPlayer?, aID: Int): Boolean

    /**
     * Opens the GUI with the ID = 0 of this TileEntity
     */
    fun openGUI(aPlayer: EntityPlayer?): Boolean
}
