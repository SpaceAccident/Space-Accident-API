package space.accident.api.util

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagInt
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import space.accident.api.interfaces.te.ICoverable


/**
 * For Covers with a special behavior.
 *
 * @author glease
 */
abstract class GT_CoverBehaviorBase<T : ISerializableObject> protected constructor(private val typeToken: Class<T>) {
    var lastPlayer: EntityPlayer? = null
    abstract fun createDataObject(aLegacyData: Int): T
    abstract fun createDataObject(): T
    fun createDataObject(aNBT: NBTBase): T {
        // Handle legacy data (migrating from GT_CoverBehavior to GT_CoverBehaviorBase)
        if (aNBT is NBTTagInt) {
            return createDataObject(aNBT.func_150287_d())
        }
        val ret = createDataObject()
        ret.loadDataFromNBT(aNBT)
        return ret
    }

    fun cast(aData: ISerializableObject): T? {
        return if (typeToken.isInstance(aData)) forceCast(aData) else null
    }

    private fun forceCast(aData: ISerializableObject): T {
        return try {
            typeToken.cast(aData)
        } catch (e: Exception) {
            throw RuntimeException("Casting data in " + this.javaClass + ", data " + aData, e)
        }
    }
    // region facade
    /**
     * Get target facade block. Does not affect rendering of **this** block. It is only used as a hint for other block
     * in case of CTM
     * @return null if none, otherwise return facade target block
     */
    fun getFacadeBlock(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Block? {
        return getFacadeBlockImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Get target facade block. Does not affect rendering of **this** block. It is only used as a hint for other block
     * in case of CTM
     * @return 0 if none, otherwise return facade target meta
     */
    fun getFacadeMeta(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable?): Int {
        return getFacadeMetaImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Get the display stack. Default to `int2Stack(aCoverID)`
     */
    fun getDisplayStack(aCoverID: Int, aCoverVariable: ISerializableObject): ItemStack {
        return getDisplayStackImpl(aCoverID, forceCast(aCoverVariable))
    }

    /**
     * Get the special cover texture associated with this cover. Return null if one should use the texture passed to
     * [gregtech.api.GregTech_API.registerCover] or its overloads.
     */
    fun getSpecialCoverTexture(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): ITexture? {
        return getSpecialCoverTextureImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Return whether cover data needs to be synced to client upon tile entity creation or cover placement.
     *
     * Note if you want to sync the data afterwards you will have to manually do it by calling [ICoverable.issueCoverUpdate]
     * This option only affects the initial sync.
     */
    fun isDataNeededOnClient(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return isDataNeededOnClientImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Called upon receiving data from network. Use [ICoverable.isClientSide] to determine the side.
     */
    fun onDataChanged(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable?) {
        onDataChangedImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Called upon cover being removed. Called on both server and client.
     */
    fun onDropped(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable?) {
        onDroppedImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    fun isRedstoneSensitive(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aTimer: Long
    ): Boolean {
        return isRedstoneSensitiveImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aTimer)
    }

    /**
     * Called by updateEntity inside the covered TileEntity. aCoverVariable is the Value you returned last time.
     */
    fun doCoverThings(
        aSide: Byte,
        aInputRedstone: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aTimer: Long
    ): T {
        return doCoverThingsImpl(aSide, aInputRedstone, aCoverID, forceCast(aCoverVariable), aTileEntity, aTimer)
    }

    /**
     * Called when someone rightclicks this Cover.
     *
     *
     * return true, if something actually happens.
     */
    fun onCoverRightClick(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean {
        return onCoverRightClickImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aPlayer, aX, aY, aZ)
    }

    /**
     * Called when someone rightclicks this Cover with a Screwdriver. Doesn't call @onCoverRightclick in this Case.
     *
     *
     * return the new Value of the Cover Variable
     */
    fun onCoverScrewdriverClick(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aX: Float,
        aY: Float,
        aZ: Float
    ): T {
        return onCoverScrewdriverClickImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aPlayer, aX, aY, aZ)
    }

    /**
     * Called when someone shift-rightclicks this Cover with no tool. Doesn't call @onCoverRightclick in this Case.
     */
    fun onCoverShiftRightClick(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?
    ): Boolean {
        return onCoverShiftRightClickImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aPlayer)
    }

    fun getClientGUI(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aWorld: World?
    ): Any? {
        return getClientGUIImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aPlayer, aWorld)
    }

    /**
     * Removes the Cover if this returns true, or if aForced is true.
     * Doesn't get called when the Machine Block is getting broken, only if you break the Cover away from the Machine.
     */
    fun onCoverRemoval(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?,
        aForced: Boolean
    ): Boolean {
        return onCoverRemovalImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity, aForced)
    }

    /**
     * Gives a small Text for the status of the Cover.
     */
    fun getDescription(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): String {
        return getDescriptionImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * How Blast Proof the Cover is. 30 is normal.
     */
    fun getBlastProofLevel(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Float {
        return getBlastProofLevelImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets RS-Signals into the Block
     *
     *
     * This is just Informative so that Machines know if their Redstone Input is blocked or not
     */
    fun letsRedstoneGoIn(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsRedstoneGoInImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets RS-Signals out of the Block
     */
    fun letsRedstoneGoOut(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsRedstoneGoOutImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets Fibre-Signals into the Block
     *
     *
     * This is just Informative so that Machines know if their Redstone Input is blocked or not
     */
    fun letsFibreGoIn(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsFibreGoInImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets Fibre-Signals out of the Block
     */
    fun letsFibreGoOut(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsFibreGoOutImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets Energy into the Block
     */
    fun letsEnergyIn(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsEnergyInImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets Energy out of the Block
     */
    fun letsEnergyOut(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsEnergyOutImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * If it lets Liquids into the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
     */
    fun letsFluidIn(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aFluid: Fluid?,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsFluidInImpl(aSide, aCoverID, forceCast(aCoverVariable), aFluid, aTileEntity)
    }

    /**
     * If it lets Liquids out of the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
     */
    fun letsFluidOut(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aFluid: Fluid?,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsFluidOutImpl(aSide, aCoverID, forceCast(aCoverVariable), aFluid, aTileEntity)
    }

    /**
     * If it lets Items into the Block, aSlot = -1 means if it is generally accepting Items (return false for no reaction at all), aSlot = -2 means if it would accept for all Slots Impl(return true to skip the Checks for each Slot).
     */
    fun letsItemsIn(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aSlot: Int,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsItemsInImpl(aSide, aCoverID, forceCast(aCoverVariable), aSlot, aTileEntity)
    }

    /**
     * If it lets Items out of the Block, aSlot = -1 means if it is generally accepting Items (return false for no reaction at all), aSlot = -2 means if it would accept for all Slots Impl(return true to skip the Checks for each Slot).
     */
    fun letsItemsOut(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aSlot: Int,
        aTileEntity: ICoverable?
    ): Boolean {
        return letsItemsOutImpl(aSide, aCoverID, forceCast(aCoverVariable), aSlot, aTileEntity)
    }

    /**
     * If it lets you rightclick the Machine normally
     */
    fun isGUIClickable(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return isGUIClickableImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Needs to return true for Covers, which have a Redstone Output on their Facing.
     */
    fun manipulatesSidedRedstoneOutput(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return manipulatesSidedRedstoneOutputImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * if this Cover should let Pipe Connections look connected even if it is not the case.
     */
    fun alwaysLookConnected(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Boolean {
        return alwaysLookConnectedImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Called to determine the incoming Redstone Signal of a Machine.
     * Returns the original Redstone per default.
     * The Cover should @letsRedstoneGoIn or the aInputRedstone Parameter is always 0.
     */
    fun getRedstoneInput(
        aSide: Byte,
        aInputRedstone: Byte,
        aCoverID: Int,
        aCoverVariable: ISerializableObject,
        aTileEntity: ICoverable?
    ): Byte {
        return getRedstoneInputImpl(aSide, aInputRedstone, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * Gets the Tick Rate for doCoverThings of the Cover
     *
     *
     * 0 = No Ticks! Yes, 0 is Default, you have to override this
     */
    fun getTickRate(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable?): Int {
        return getTickRateImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * The MC Color of this Lens. -1 for no Color (meaning this isn't a Lens then).
     */
    fun getLensColor(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable?): Byte {
        return getLensColorImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    /**
     * @return the ItemStack dropped by this Cover
     */
    fun getDrop(aSide: Byte, aCoverID: Int, aCoverVariable: ISerializableObject, aTileEntity: ICoverable): ItemStack {
        return getDropImpl(aSide, aCoverID, forceCast(aCoverVariable), aTileEntity)
    }

    // endregion
    // region impl
    protected fun getFacadeBlockImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Block? {
        return null
    }

    protected fun getFacadeMetaImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Int {
        return 0
    }

    protected fun getDisplayStackImpl(aCoverID: Int, aCoverVariable: T): ItemStack {
        return GT_Utility.intToStack(aCoverID)
    }

    protected fun getSpecialCoverTextureImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): ITexture? {
        return null
    }

    protected fun isDataNeededOnClientImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    protected fun onDataChangedImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?) {}
    protected fun onDroppedImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?) {}
    protected fun isRedstoneSensitiveImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aTimer: Long
    ): Boolean {
        return true
    }

    /**
     * Called by updateEntity inside the covered TileEntity. aCoverVariable is the Value you returned last time.
     */
    protected fun doCoverThingsImpl(
        aSide: Byte,
        aInputRedstone: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aTimer: Long
    ): T {
        return aCoverVariable
    }

    /**
     * Called when someone rightclicks this Cover.
     *
     *
     * return true, if something actually happens.
     */
    protected fun onCoverRightClickImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean {
        return false
    }

    /**
     * Called when someone rightclicks this Cover with a Screwdriver. Doesn't call @onCoverRightclick in this Case.
     *
     *
     * return the new Value of the Cover Variable
     */
    protected fun onCoverScrewdriverClickImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aX: Float,
        aY: Float,
        aZ: Float
    ): T {
        return aCoverVariable
    }

    /**
     * Called when someone shift-rightclicks this Cover with no tool. Doesn't call @onCoverRightclick in this Case.
     */
    protected fun onCoverShiftRightClickImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?
    ): Boolean {
        if (hasCoverGUI() && aPlayer is EntityPlayerMP) {
            lastPlayer = aPlayer
            GT_Values.NW.sendToPlayer(
                GT_Packet_TileEntityCoverGUI(
                    aSide,
                    aCoverID,
                    aCoverVariable,
                    aTileEntity,
                    aPlayer as EntityPlayerMP?
                ), aPlayer as EntityPlayerMP?
            )
            return true
        }
        return false
    }

    protected fun getClientGUIImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aWorld: World?
    ): Any? {
        return null
    }

    /**
     * Removes the Cover if this returns true, or if aForced is true.
     * Doesn't get called when the Machine Block is getting broken, only if you break the Cover away from the Machine.
     */
    protected fun onCoverRemovalImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?,
        aForced: Boolean
    ): Boolean {
        return true
    }

    /**
     * Gives a small Text for the status of the Cover.
     */
    protected fun getDescriptionImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): String {
        return E
    }

    /**
     * How Blast Proof the Cover is. 30 is normal.
     */
    protected fun getBlastProofLevelImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Float {
        return 10.0f
    }

    /**
     * If it lets RS-Signals into the Block
     *
     *
     * This is just Informative so that Machines know if their Redstone Input is blocked or not
     */
    protected fun letsRedstoneGoInImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets RS-Signals out of the Block
     */
    protected fun letsRedstoneGoOutImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets Fibre-Signals into the Block
     *
     *
     * This is just Informative so that Machines know if their Redstone Input is blocked or not
     */
    protected fun letsFibreGoInImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Boolean {
        return false
    }

    /**
     * If it lets Fibre-Signals out of the Block
     */
    protected fun letsFibreGoOutImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Boolean {
        return false
    }

    /**
     * If it lets Energy into the Block
     */
    protected fun letsEnergyInImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Boolean {
        return false
    }

    /**
     * If it lets Energy out of the Block
     */
    protected fun letsEnergyOutImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Boolean {
        return false
    }

    /**
     * If it lets Liquids into the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
     */
    protected fun letsFluidInImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aFluid: Fluid?,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets Liquids out of the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
     */
    protected fun letsFluidOutImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aFluid: Fluid?,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets Items into the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
     */
    protected fun letsItemsInImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aSlot: Int,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets Items out of the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
     */
    protected fun letsItemsOutImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aSlot: Int,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * If it lets you rightclick the Machine normally
     */
    protected fun isGUIClickableImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Boolean {
        return false
    }

    /**
     * Needs to return true for Covers, which have a Redstone Output on their Facing.
     */
    protected fun manipulatesSidedRedstoneOutputImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * if this Cover should let Pipe Connections look connected even if it is not the case.
     */
    protected fun alwaysLookConnectedImpl(
        aSide: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Boolean {
        return false
    }

    /**
     * Called to determine the incoming Redstone Signal of a Machine.
     * Returns the original Redstone per default.
     * The Cover should @letsRedstoneGoIn or the aInputRedstone Parameter is always 0.
     */
    protected fun getRedstoneInputImpl(
        aSide: Byte,
        aInputRedstone: Byte,
        aCoverID: Int,
        aCoverVariable: T,
        aTileEntity: ICoverable?
    ): Byte {
        return if (letsRedstoneGoIn(aSide, aCoverID, aCoverVariable, aTileEntity)) aInputRedstone else 0
    }

    /**
     * Gets the Tick Rate for doCoverThings of the Cover
     *
     *
     * 0 = No Ticks! Yes, 0 is Default, you have to override this
     */
    protected fun getTickRateImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Int {
        return 0
    }

    /**
     * The MC Color of this Lens. -1 for no Color (meaning this isn't a Lens then).
     */
    protected fun getLensColorImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable?): Byte {
        return -1
    }

    /**
     * @return the ItemStack dropped by this Cover
     */
    protected fun getDropImpl(aSide: Byte, aCoverID: Int, aCoverVariable: T, aTileEntity: ICoverable): ItemStack {
        return GT_OreDictUnificator.get(true, aTileEntity.getCoverItemAtSide(aSide))
    }
    //endregion
    // region no data
    /**
     * Checks if the Cover can be placed on this.
     */
    fun isCoverPlaceable(aSide: Byte, aStack: ItemStack?, aTileEntity: ICoverable?): Boolean {
        return isCoverPlaceable(aSide, GT_ItemStack(aStack), aTileEntity)
    }

    /**
     * Checks if the Cover can be placed on this. You will probably want to call [.isCoverPlaceable] instead.
     */
    @Deprecated("")
    fun isCoverPlaceable(aSide: Byte, aStack: GT_ItemStack?, aTileEntity: ICoverable?): Boolean {
        return true
    }

    fun hasCoverGUI(): Boolean {
        return false
    }

    /**
     * Called when someone rightclicks this Cover Client Side
     *
     *
     * return true, if something actually happens.
     */
    fun onCoverRightclickClient(
        aSide: Byte,
        aTileEntity: ICoverable?,
        aPlayer: EntityPlayer?,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean {
        return false
    }

    /**
     * If this is a simple Cover, which can also be used on Bronze Machines and similar.
     */
    val isSimpleCover: Boolean
        get() = false

    /**
     * sets the Cover upon placement.
     */
    fun placeCover(aSide: Byte, aCover: ItemStack?, aTileEntity: ICoverable) {
        aTileEntity.setCoverIDAtSide(aSide, GT_Utility.stackToInt(aCover))
    }

    @Deprecated("")
    fun trans(aNr: String?, aEnglish: String?): String {
        return GT_Utility.trans(aNr, aEnglish)
    } // endregion
}
