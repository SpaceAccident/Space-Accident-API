package space.accident.api.interfaces.mte

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidHandler
import net.minecraftforge.fluids.IFluidTank
import space.accident.api.interfaces.te.IGearEnergyTileEntity
import space.accident.api.interfaces.te.IGregTechTileEntity
import space.accident.api.interfaces.te.IGregtechWailaProvider
import space.accident.api.interfaces.te.IMachineBlockUpdateable
import java.io.File

interface IMetaTileEntity : ISidedInventory, IFluidTank, IFluidHandler, IGearEnergyTileEntity, IMachineBlockUpdateable,
    IGregtechWailaProvider {

    /**
     * This determines the BaseMetaTileEntity belonging to this MetaTileEntity by using the Meta ID of the Block itself.
     *
     * 0 = BaseMetaTileEntity, Wrench lvl 0 to dismantle
     * 1 = BaseMetaTileEntity, Wrench lvl 1 to dismantle
     * 2 = BaseMetaTileEntity, Wrench lvl 2 to dismantle
     * 3 = BaseMetaTileEntity, Wrench lvl 3 to dismantle
     * 4 = BaseMetaPipeEntity, Wrench lvl 0 to dismantle
     * 5 = BaseMetaPipeEntity, Wrench lvl 1 to dismantle
     * 6 = BaseMetaPipeEntity, Wrench lvl 2 to dismantle
     * 7 = BaseMetaPipeEntity, Wrench lvl 3 to dismantle
     * 8 = BaseMetaPipeEntity, Cutter lvl 0 to dismantle
     * 9 = BaseMetaPipeEntity, Cutter lvl 1 to dismantle
     * 10 = BaseMetaPipeEntity, Cutter lvl 2 to dismantle
     * 11 = BaseMetaPipeEntity, Cutter lvl 3 to dismantle
     */
    fun getTileEntityBaseType(): Byte


    /**
     * @param aTileEntity is just because the internal Variable "mBaseMetaTileEntity" is set after this Call.
     * @return a newly created and ready MetaTileEntity
     */
    fun newMetaEntity(aTileEntity: IGregTechTileEntity): IMetaTileEntity

    /**
     * @return an ItemStack representing this MetaTileEntity.
     */
    fun getStackForm(aAmount: Long): ItemStack

    /**
     * new getter for the BaseMetaTileEntity, which restricts usage to certain Functions.
     */
    fun getBaseMetaTileEntity(): IGregTechTileEntity

    /**
     * Sets the BaseMetaTileEntity of this
     */
    fun setBaseMetaTileEntity(aBaseMetaTileEntity: IGregTechTileEntity)

    /**
     * when placing a Machine in World, to initialize default Modes. aNBT can be null!
     */
    fun initDefaultModes(aNBT: NBTTagCompound)

    /**
     * ^= writeToNBT
     */
    fun saveNBTData(aNBT: NBTTagCompound)

    /**
     * ^= readFromNBT
     */
    fun loadNBTData(aNBT: NBTTagCompound)

    /**
     * Adds the NBT-Information to the ItemStack, when being dismanteled properly
     * Used to store Machine specific Upgrade Data.
     */
    fun setItemNBT(aNBT: NBTTagCompound)

    /**
     * Called in the registered MetaTileEntity when the Server starts, to reset static variables
     */
    fun onServerStart()

    /**
     * Called in the registered MetaTileEntity when the Server ticks a World the first time, to load things from the World Save
     */
    fun onWorldLoad(aSaveDirectory: File)

    /**
     * Called in the registered MetaTileEntity when the Server stops, to save the Game.
     */
    fun onWorldSave(aSaveDirectory: File)

    /**
     * If a Cover of that Type can be placed on this Side.
     * Also Called when the Facing of the Block Changes and a Cover is on said Side.
     */
    fun allowCoverOnSide(aSide: Byte, aStack: GT_ItemStack): Boolean

    /**
     * When a Player rightclicks the Facing with a Screwdriver.
     */
    fun onScrewdriverRightClick(aSide: Byte, aPlayer: EntityPlayer, aX: Float, aY: Float, aZ: Float)

    /**
     * When a Player rightclicks the Facing with a Wrench.
     */
    fun onWrenchRightClick(
        aSide: Byte,
        aWrenchingSide: Byte,
        aPlayer: EntityPlayer,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean

    /**
     * When a Player rightclicks the Facing with a wire cutter.
     */
    fun onWireCutterRightClick(
        aSide: Byte,
        aWrenchingSide: Byte,
        aPlayer: EntityPlayer,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean

    /**
     * When a Player rightclicks the Facing with a soldering iron.
     */
    fun onSolderingToolRightClick(
        aSide: Byte,
        aWrenchingSide: Byte,
        aPlayer: EntityPlayer,
        aX: Float,
        aY: Float,
        aZ: Float
    ): Boolean

    /**
     * Called right before this Machine explodes
     */
    fun onExplosion()

    /**
     * The First processed Tick which was passed to this MetaTileEntity
     */
    fun onFirstTick(aBaseMetaTileEntity: IGregTechTileEntity)

    /**
     * The Tick before all the generic handling happens, what gives a slightly faster reaction speed.
     * Don't use this if you really don't need to. @onPostTick is better suited for ticks.
     * This happens still after the Cover handling.
     */
    fun onPreTick(aBaseMetaTileEntity: IGregTechTileEntity, aTick: Long)

    /**
     * The Tick after all the generic handling happened.
     * Recommended to use this like updateEntity.
     */
    fun onPostTick(aBaseMetaTileEntity: IGregTechTileEntity, aTick: Long)

    /**
     * Called when this MetaTileEntity gets (intentionally) disconnected from the BaseMetaTileEntity.
     * Doesn't get called when this thing is moved by Frames or similar hacks.
     */
    fun inValidate()

    /**
     * Called when the BaseMetaTileEntity gets invalidated, what happens right before the @inValidate above gets called
     */
    fun onRemoval()

    /**
     * @param aFacing
     * @return if aFacing would be a valid Facing for this Device. Used for wrenching.
     */
    fun isFacingValid(aFacing: Byte): Boolean

    /**
     * @return the Server Side Container
     */
    fun getServerGUI(aID: Int, aPlayerInventory: InventoryPlayer, aBaseMetaTileEntity: IGregTechTileEntity): Any?

    /**
     * @return the Client Side GUI Container
     */
    fun getClientGUI(aID: Int, aPlayerInventory: InventoryPlayer, aBaseMetaTileEntity: IGregTechTileEntity): Any?

    /**
     * From new ISidedInventory
     */
    fun allowPullStack(aBaseMetaTileEntity: IGregTechTileEntity, aIndex: Int, aSide: Byte, aStack: ItemStack): Boolean

    /**
     * From new ISidedInventory
     */
    fun allowPutStack(aBaseMetaTileEntity: IGregTechTileEntity, aIndex: Int, aSide: Byte, aStack: ItemStack): Boolean

    /**
     * @return if aIndex is a valid Slot. false for things like HoloSlots. Is used for determining if an Item is dropped upon Block destruction and for Inventory Access Management
     */
    fun isValidSlot(aIndex: Int): Boolean

    /**
     * @return if aIndex can be set to Zero stackSize, when being removed.
     */
    fun setStackToZeroInsteadOfNull(aIndex: Int): Boolean

    /**
     * If this Side can connect to inputting pipes
     */
    fun isLiquidInput(aSide: Byte): Boolean

    /**
     * If this Side can connect to outputting pipes
     */
    fun isLiquidOutput(aSide: Byte): Boolean

    /**
     * Just an Accessor for the Name variable.
     */
    fun getMetaName(): String

    /**
     * @return true if the Machine can be accessed
     */
    fun isAccessAllowed(aPlayer: EntityPlayer): Boolean

    /**
     * a Player rightclicks the Machine
     * Sneaky rightclicks are not getting passed to this!
     *
     * @return
     */
    fun onRightclick(
        aBaseMetaTileEntity: IGregTechTileEntity, aPlayer: EntityPlayer,
        aSide: Byte, aX: Float, aY: Float, aZ: Float
    ): Boolean

    /**
     * a Player leftclicks the Machine
     * Sneaky leftclicks are getting passed to this unlike with the rightclicks.
     */
    fun onLeftclick(aBaseMetaTileEntity: IGregTechTileEntity, aPlayer: EntityPlayer)

    /**
     * Called Clientside with the Data got from @getUpdateData
     */
    fun onValueUpdate(aValue: Byte)

    /**
     * return a small bit of Data, like a secondary Facing for example with this Function, for the Client.
     * The BaseMetaTileEntity detects changes to this Value and will then send an Update.
     * This is only for Information, which is visible as Texture to the outside.
     *
     *
     * If you just want to have an Active/Redstone State then set the Active State inside the BaseMetaTileEntity instead.
     */
    fun getUpdateData(): Byte

    /**
     * For the rare case you need this Function
     */
    fun receiveClientEvent(aEventID: Byte, aValue: Byte)

    /**
     * Called to actually play the Sound.
     * Do not insert Client/Server checks. That is already done for you.
     * Do not use @playSoundEffect, Minecraft doesn't like that at all. Use @playSound instead.
     */
    fun doSound(aIndex: Byte, aX: Double, aY: Double, aZ: Double)

    fun startSoundLoop(aIndex: Byte, aX: Double, aY: Double, aZ: Double)

    fun stopSoundLoop(aValue: Byte, aX: Double, aY: Double, aZ: Double)

    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    fun sendSound(aIndex: Byte)

    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    fun sendLoopStart(aIndex: Byte)

    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    fun sendLoopEnd(aIndex: Byte)

    /**
     * Called when the Machine explodes, override Explosion Code here.
     *
     * @param aExplosionPower
     */
    fun doExplosion(aExplosionPower: Long)

    /**
     * If this is just a simple Machine, which can be wrenched at 100%
     */
    fun isSimpleMachine(): Boolean

    /**
     * If there should be a Lag Warning if something laggy happens during this Tick.
     *
     *
     * The Advanced Pump uses this to not cause the Lag Message, while it scans for all close Fluids.
     * The Item Pipes and Retrievers neither send this Message, when scanning for Pipes.
     */
    fun doTickProfilingMessageDuringThisTick(): Boolean

    /**
     * returns the DebugLog
     */
    fun getSpecialDebugInfo(
        aBaseMetaTileEntity: IGregTechTileEntity,
        aPlayer: EntityPlayer,
        aLogLevel: Int,
        aList: ArrayList<String>
    ): ArrayList<String>

    /**
     * get a small Description
     */
    fun getDescription(): Array<String>

    /**
     * In case the Output Voltage varies.
     */
    fun getSpecialVoltageToolTip(): String

    /**
     * Icon of the Texture. If this returns null then it falls back to getTextureIndex.
     *
     * @param aSide       is the Side of the Block
     * @param aFacing     is the direction the Block is facing (or a Bitmask of all Connections in case of Pipes)
     * @param aColorIndex The Minecraft Color the Block is having
     * @param aActive     if the Machine is currently active (use this instead of calling mBaseMetaTileEntity.mActive!!!). Note: In case of Pipes this means if this Side is connected to something or not.
     * @param aRedstone   if the Machine is currently outputting a RedstoneSignal (use this instead of calling mBaseMetaTileEntity.mRedstone!!!)
     */
    fun getTexture(
        aBaseMetaTileEntity: IGregTechTileEntity,
        aSide: Byte,
        aFacing: Byte,
        aColorIndex: Byte,
        aActive: Boolean,
        aRedstone: Boolean
    ): Array<ITexture>
    /**
     * Register Icons here. This gets called when the Icons get initialized by the Base Block
     * Best is you put your Icons in a static Array for quick and easy access without relying on the MetaTileList.
     *
     * @param aBlockIconRegister The Block Icon Register
     */
    @SideOnly(Side.CLIENT)
    fun registerIcons(aBlockIconRegister: IIconRegister)

    /**
     * @return true if you override the Rendering.
     */
    @SideOnly(Side.CLIENT)
    fun renderInInventory(aBlock: Block, aMeta: Int, aRenderer: RenderBlocks): Boolean

    /**
     * @return true if you override the Rendering.
     */
    @SideOnly(Side.CLIENT)
    fun renderInWorld(
        aWorld: IBlockAccess, aX: Int, aY: Int, aZ: Int,
        aBlock: Block, aRenderer: RenderBlocks
    ): Boolean

    /**
     * Gets the Output for the comparator on the given Side
     */
    fun getComparatorValue(aSide: Byte): Byte

    fun getExplosionResistance(aSide: Byte): Float

    fun getInfoData(): Array<String>

    fun isGivingInformation(): Boolean

    fun getRealInventory(): Array<ItemStack?>

    fun connectsToItemPipe(aSide: Byte): Boolean

    fun onColorChangeServer(aColor: Byte)

    fun onColorChangeClient(aColor: Byte)

    fun getLightOpacity(): Int

    fun allowGeneralRedstoneOutput(): Boolean

    fun addCollisionBoxesToList(
        aWorld: World?, aX: Int, aY: Int, aZ: Int,
        inputAABB: AxisAlignedBB, outputAABB: List<AxisAlignedBB>, collider: Entity
    )

    fun getCollisionBoundingBoxFromPool(aWorld: World, aX: Int, aY: Int, aZ: Int): AxisAlignedBB

    fun onEntityCollidedWithBlock(aWorld: World, aX: Int, aY: Int, aZ: Int, collider: Entity)

    /**
     * The onCreated Function of the Item Class redirects here
     */
    fun onCreated(aStack: ItemStack, aWorld: World, aPlayer: EntityPlayer?)

    fun hasAlternativeModeText(): Boolean

    fun getAlternativeModeText(): String

    fun shouldJoinIc2Enet(): Boolean

    /**
     * The Machine Update, which is called when the Machine needs an Update of its Parts.
     * I suggest to wait 1-5 seconds before actually checking the Machine Parts.
     * RP-Frames could for example cause Problems when you instacheck the Machine Parts.
     *
     * just do stuff since we are already in meta tile...
     */
    override fun onMachineBlockUpdate()

    /**
     * just return in should recurse since we are already in meta tile...
     */
    override fun isMachineBlockUpdateRecursive(): Boolean {
        return true
    }
}