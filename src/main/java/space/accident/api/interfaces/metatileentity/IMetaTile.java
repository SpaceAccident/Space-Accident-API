package space.accident.api.interfaces.metatileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.energy.IEnergyTransferTile;
import space.accident.api.interfaces.tileentity.ITileWailaProvider;
import space.accident.api.interfaces.tileentity.IMachineBlockUpdate;
import space.accident.api.objects.ItemStackData;
import space.accident.main.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface IMetaTile extends ISidedInventory, IFluidTank, IFluidHandler, IEnergyTransferTile, IMachineBlockUpdate, ITileWailaProvider {
    /**
     * This determines the BaseMetaTileEntity belonging to this MetaTileEntity by using the Meta ID of the Block itself.
     * <p/>
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
     * 12 = ?
     * 13 = ?
     * 14 = ?
     * 15 = ?
     */
    int getTileEntityBaseType();
    
    /**
     * @param aTileEntity is just because the internal Variable "mBaseMetaTileEntity" is set after this Call.
     * @return a newly created and ready MetaTileEntity
     */
    IMetaTile newMetaEntity(ITile aTileEntity);
    
    /**
     * @return an ItemStack representing this MetaTileEntity.
     */
    ItemStack get(long amount);
    
    /**
     * new getter for the BaseMetaTileEntity, which restricts usage to certain Functions.
     */
    ITile getBaseMetaTileEntity();
    
    /**
     * Sets the BaseMetaTileEntity of this
     */
    void setBaseMetaTileEntity(ITile te);
    
    /**
     * when placing a Machine in World, to initialize default Modes. nbt can be null!
     */
    void initDefaultModes(NBTTagCompound nbt);
    
    /**
     * ^= writeToNBT
     */
    void writeToNBT(NBTTagCompound nbt);
    
    /**
     * ^= readFromNBT
     */
    void readFromNBT(NBTTagCompound nbt);
    
    /**
     * Adds the NBT-Information to the ItemStack, when being dismanteled properly
     * Used to store Machine specific Upgrade Data.
     */
    void setItemNBT(NBTTagCompound nbt);
    
    /**
     * Called in the registered MetaTileEntity when the Server starts, to reset static variables
     */
    void onServerStart();
    
    /**
     * Called in the registered MetaTileEntity when the Server ticks a World the first time, to load things from the World Save
     */
    void onWorldLoad(File aSaveDirectory);
    
    /**
     * Called in the registered MetaTileEntity when the Server stops, to save the Game.
     */
    void onWorldSave(File aSaveDirectory);
    
    /**
     * Called to set Configuration values for this MetaTileEntity.
     * Use aConfig.get(ConfigCategories.machineconfig, "MetaTileEntityName.Ability", DEFAULT_VALUE); to set the Values.
     */
    void onConfigLoad(Config aConfig);
    
    /**
     * If a Cover of that Type can be placed on this Side.
     * Also Called when the Facing of the Block Changes and a Cover is on said Side.
     */
    boolean allowCoverOnSide(int side, ItemStackData stack);
    
    /**
     * When a Player rightclicks the Facing with a Screwdriver.
     */
    void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z);
    
    /**
     * When a Player rightclicks the Facing with a Wrench.
     */
    boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z);
    
    /**
     * When a Player rightclicks the Facing with a wire cutter.
     */
    boolean onWireCutterRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z);
    
    /**
     * When a Player rightclicks the Facing with a soldering iron.
     */
    boolean onSolderingToolRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z);
    
    /**
     * Called right before this Machine explodes
     */
    void onExplosion();
    
    /**
     * The First processed Tick which was passed to this MetaTileEntity
     */
    void onFirstTick(ITile te);
    
    /**
     * The Tick before all the generic handling happens, what gives a slightly faster reaction speed.
     * Don't use this if you really don't need to. @onPostTick is better suited for ticks.
     * This happens still after the Cover handling.
     */
    void onPreTick(ITile te, long aTick);
    
    /**
     * The Tick after all the generic handling happened.
     * Recommended to use this like updateEntity.
     */
    void onPostTick(ITile te, long tick);
    
    /**
     * Called when this MetaTileEntity gets (intentionally) disconnected from the BaseMetaTileEntity.
     * Doesn't get called when this thing is moved by Frames or similar hacks.
     */
    void inValidate();
    
    /**
     * Called when the BaseMetaTileEntity gets invalidated, what happens right before the @inValidate above gets called
     */
    void onRemoval();
    
    /**
     * @param face
     * @return if face would be a valid Facing for this Device. Used for wrenching.
     */
    boolean isFacingValid(int face);
    
    /**
     * @return the Server Side Container
     */
    Container getServerGUI(int id, InventoryPlayer inv, ITile te);
    
    /**
     * @return the Client Side GUI Container
     */
    GuiContainer getClientGUI(int id, InventoryPlayer inv, ITile te);
    
    /**
     * From new ISidedInventory
     */
    boolean allowPullStack(ITile te, int index, int side, ItemStack stack);
    
    /**
     * From new ISidedInventory
     */
    boolean allowPutStack(ITile te, int index, int side, ItemStack stack);
    
    /**
     * @return if index is a valid Slot. false for things like HoloSlots. Is used for determining if an Item is dropped upon Block destruction and for Inventory Access Management
     */
    boolean isValidSlot(int index);
    
    /**
     * @return if index can be set to Zero stackSize, when being removed.
     */
    boolean setStackToZeroInsteadOfNull(int index);
    
    /**
     * If this Side can connect to inputting pipes
     */
    boolean isLiquidInput(int side);
    
    /**
     * If this Side can connect to outputting pipes
     */
    boolean isLiquidOutput(int side);
    
    /**
     * Just an Accessor for the Name variable.
     */
    String getMetaName();
    
    /**
     * @return true if the Machine can be accessed
     */
    boolean isAccessAllowed(EntityPlayer player);
    
    /**
     * a Player rightclicks the Machine
     * Sneaky rightclicks are not getting passed to this!
     *
     * @return
     */
    boolean onRightclick(ITile te, EntityPlayer player, int side, float x, float y, float z);
    
    /**
     * a Player leftclicks the Machine
     * Sneaky leftclicks are getting passed to this unlike with the rightclicks.
     */
    void onLeftclick(ITile te, EntityPlayer player);
    
    /**
     * Called Clientside with the Data got from @getUpdateData
     */
    void onValueUpdate(int value);
    
    /**
     * return a small bit of Data, like a secondary Facing for example with this Function, for the Client.
     * The BaseMetaTileEntity detects changes to this Value and will then send an Update.
     * This is only for Information, which is visible as Texture to the outside.
     * <p/>
     * If you just want to have an Active/RedStone State then set the Active State inside the BaseMetaTileEntity instead.
     */
    int getUpdateData();
    
    /**
     * For the rare case you need this Function
     */
    void receiveClientEvent(int aEventID, int value);
    
    /**
     * Called to actually play the Sound.
     * Do not insert Client/Server checks. That is already done for you.
     * Do not use @playSoundEffect, Minecraft doesn't like that at all. Use @playSound instead.
     */
    void doSound(int index, double x, double y, double z);
    
    void startSoundLoop(int index, double x, double y, double z);
    
    void stopSoundLoop(int value, double x, double y, double z);
    
    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    void sendSound(int index);
    
    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    void sendLoopStart(int index);
    
    /**
     * Sends the Event for the Sound Triggers, only usable Server Side!
     */
    void sendLoopEnd(int index);
    
    /**
     * Called when the Machine explodes, override Explosion Code here.
     *
     * @param aExplosionPower
     */
    void doExplosion(long aExplosionPower);
    
    /**
     * If this is just a simple Machine, which can be wrenched at 100%
     */
    boolean isSimpleMachine();
    
    /**
     * If there should be a Lag Warning if something laggy happens during this Tick.
     * <p/>
     * The Advanced Pump uses this to not cause the Lag Message, while it scans for all close Fluids.
     * The Item Pipes and Retrievers neither send this Message, when scanning for Pipes.
     */
    boolean doTickProfilingMessageDuringThisTick();
    
    /**
     * returns the DebugLog
     */
    ArrayList<String> getSpecialDebugInfo(ITile te, EntityPlayer player, int logLevel, ArrayList<String> aList);
    
    /**
     * get a small Description
     */
    String[] getDescription();
    
    /**
     * In case the Output Voltage varies.
     */
    String getSpecialVoltageToolTip();
    
    /**
     * Icon of the Texture. If this returns null then it falls back to getTextureIndex.
     *
     * @param side       is the Side of the Block
     * @param face     is the direction the Block is facing (or a Bitmask of all Connections in case of Pipes)
     * @param aColorIndex The Minecraft Color the Block is having
     * @param active     if the Machine is currently active (use this instead of calling mBaseMetaTileEntity.mActive!!!). Note: In case of Pipes this means if this Side is connected to something or not.
     * @param aRedstone   if the Machine is currently outputting a RedstoneSignal (use this instead of calling mBaseMetaTileEntity.mRedstone!!!)
     */
    ITexture[] getTexture(ITile te, int side, int face, int aColorIndex, boolean active, boolean aRedstone);
    
    /**
     * The Textures used for the Item rendering. Return null if you want the regular 3D Block Rendering.
     */
    //public ITexture[] getItemTexture(ItemStack stack);
    
    /**
     * Register Icons here. This gets called when the Icons get initialized by the Base Block
     * Best is you put your Icons in a static Array for quick and easy access without relying on the MetaTileList.
     *
     * @param aBlockIconRegister The Block Icon Register
     */
    @SideOnly(Side.CLIENT)
    void registerIcons(IIconRegister aBlockIconRegister);
    
    /**
     * @return true if you override the Rendering.
     */
    @SideOnly(Side.CLIENT)
    boolean renderInInventory(Block block, int aMeta, RenderBlocks aRenderer);
    
    /**
     * @return true if you override the Rendering.
     */
    @SideOnly(Side.CLIENT)
    boolean renderInWorld(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks aRenderer);
    
    /**
     * Gets the Output for the comparator on the given Side
     */
    int getComparatorValue(int side);
    
    float getExplosionResistance(int side);
    
    String[] getInfoData();
    
    boolean isGivingInformation();
    
    ItemStack[] getRealInventory();
    
    boolean connectsToItemPipe(int side);
    
    void onColorChangeServer(int color);
    
    void onColorChangeClient(int color);
    
    int getLightOpacity();
    
    boolean allowGeneralRedstoneOutput();
    
    void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider);
    
    AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z);
    
    void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider);
    
    /**
     * The onCreated Function of the Item Class redirects here
     */
    void onCreated(ItemStack stack, World world, EntityPlayer player);
    
    boolean hasAlternativeModeText();
    
    String getAlternativeModeText();
    
    /**
     * The Machine Update, which is called when the Machine needs an Update of its Parts.
     * I suggest to wait 1-5 seconds before actually checking the Machine Parts.
     * RP-Frames could for example cause Problems when you instacheck the Machine Parts.
     * <p>
     * just do stuff since we are already in meta tile...
     */
    @Override
    void onMachineBlockUpdate();
    
    /**
     * just return in should recurse since we are already in meta tile...
     */
    @Override
    default boolean isMachineBlockUpdateRecursive() {
        return true;
    }
}
