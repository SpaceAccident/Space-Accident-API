package space.accident.api.interfaces.te

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import space.accident.api.util.GT_CoverBehaviorBase
import space.accident.api.util.ISerializableObject

interface ICoverable : IRedstoneTileEntity, IHasInventory, IBasicEnergyContainer {

    fun canPlaceCoverIDAtSide(aSide: Byte, aID: Int): Boolean
    fun canPlaceCoverItemAtSide(aSide: Byte, aCover: ItemStack?): Boolean
    fun dropCover(aSide: Byte, aDroppedSide: Byte, aForced: Boolean): Boolean

    fun setCoverDataAtSide(aSide: Byte, aData: ISerializableObject)
    fun setCoverIdAndDataAtSide(aSide: Byte, aId: Int, aData: ISerializableObject?)
    fun setCoverIDAtSide(aSide: Byte, aID: Int)
    fun setCoverIDAtSideNoUpdate(aSide: Byte, aID: Int): Boolean
    fun setCoverItemAtSide(aSide: Byte, aCover: ItemStack?)

    fun getComplexCoverDataAtSide(aSide: Byte): ISerializableObject

    fun getCoverIDAtSide(aSide: Byte): Int
    fun getCoverItemAtSide(aSide: Byte): ItemStack?


    fun <CoverBase : ISerializableObject> getCoverBehaviorAtSideNew(aSide: Byte): GT_CoverBehaviorBase<CoverBase>

    /**
     * For use by the regular MetaTileEntities. Returns the Cover Manipulated input Redstone.
     * Don't use this if you are a Cover Behavior. Only for MetaTileEntities.
     */
    fun getInternalInputRedstoneSignal(aSide: Byte): Byte

    /**
     * For use by the regular MetaTileEntities. This makes it not conflict with Cover based Redstone Signals.
     * Don't use this if you are a Cover Behavior. Only for MetaTileEntities.
     */
    fun setInternalOutputRedstoneSignal(aSide: Byte, aStrength: Byte)

    /**
     * Causes a general Cover Texture update.
     * Sends 6 Integers to Client + causes @issueTextureUpdate()
     */
    fun issueCoverUpdate(aSide: Byte)

    /**
     * Receiving a packet with cover data.
     */
    fun receiveCoverData(coverSide: Byte, coverID: Int, coverData: Int)

    /**
     * Receiving a packet with cover data.
     * @param aPlayer the player who made the change
     */
    fun receiveCoverData(aCoverSide: Byte, aCoverID: Int, aCoverData: ISerializableObject, aPlayer: EntityPlayerMP?) {
        if (aCoverData is ISerializableObject.LegacyCoverData) {
            receiveCoverData(aCoverSide, aCoverID, aCoverData.get())
        }
    }
}
