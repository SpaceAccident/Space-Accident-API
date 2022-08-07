package space.accident.api.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.objects.ItemStackData;
import space.accident.main.network.Packet_TileEntityCoverGUI;

import static space.accident.main.SpaceAccidentApi.NETWORK;

/**
 * For Covers with a special behavior.
 *
 * @author glease
 */
public abstract class SA_CoverBehaviorBase<T extends ISerializableObject> {
	
	private final Class<T> typeToken;
	public EntityPlayer lastPlayer = null;
	
	protected SA_CoverBehaviorBase(Class<T> typeToken) {
		this.typeToken = typeToken;
	}
	
	public abstract T createDataObject(int aLegacyData);
	
	public abstract T createDataObject();
	
	public final T createDataObject(NBTBase nbt) {
		// Handle legacy data (migrating from SA_CoverBehavior to SA_CoverBehaviorBase)
		if (nbt instanceof NBTTagInt) {
			return createDataObject(((NBTTagInt) nbt).func_150287_d());
		}
		
		T ret = createDataObject();
		ret.loadDataFromNBT(nbt);
		return ret;
	}
	
	public final T cast(ISerializableObject aData) {
		if (typeToken.isInstance(aData)) return forceCast(aData);
		return null;
	}
	
	private T forceCast(ISerializableObject aData) {
		try {
			return typeToken.cast(aData);
		} catch (Exception e) {
			throw new RuntimeException("Casting data in " + this.getClass() + ", data " + aData, e);
		}
	}
	
	// region facade
	
	/**
	 * Get target facade block. Does not affect rendering of **this** block. It is only used as a hint for other block
	 * in case of CTM
	 *
	 * @return null if none, otherwise return facade target block
	 */
	public final Block getFacadeBlock(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getFacadeBlockImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Get target facade block. Does not affect rendering of **this** block. It is only used as a hint for other block
	 * in case of CTM
	 *
	 * @return 0 if none, otherwise return facade target meta
	 */
	public final int getFacadeMeta(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getFacadeMetaImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Get the display stack. Default to {@code int2Stack(coverId)}
	 */
	public final ItemStack getDisplayStack(int coverId, ISerializableObject aCoverVariable) {
		return getDisplayStackImpl(coverId, forceCast(aCoverVariable));
	}
	
	/**
	 * Get the special cover texture associated with this cover. Return null if one should use the texture passed to
	 * {@link space.accident.api.API#registerCover(ItemStack, ITexture, SA_CoverBehaviorBase)} or its overloads.
	 */
	public final ITexture getSpecialCoverTexture(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getSpecialCoverTextureImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Return whether cover data needs to be synced to client upon tile entity creation or cover placement.
	 * <p>
	 * Note if you want to sync the data afterwards you will have to manually do it by calling {@link ICoverable#issueCoverUpdate(int)}
	 * This option only affects the initial sync.
	 */
	public final boolean isDataNeededOnClient(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return isDataNeededOnClientImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Called upon receiving data from network. Use {@link ICoverable#isClientSide()} to determine the side.
	 */
	public final void onDataChanged(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		onDataChangedImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Called upon cover being removed. Called on both server and client.
	 */
	public final void onDropped(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		onDroppedImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	public final boolean isRedstoneSensitive(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return isRedstoneSensitiveImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, aTimer);
	}
	
	/**
	 * Called by updateEntity inside the covered TileEntity. aCoverVariable is the Value you returned last time.
	 */
	public final T doCoverThings(int side, int aInputRedstone, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return doCoverThingsImpl(side, aInputRedstone, coverId, forceCast(aCoverVariable), aTileEntity, aTimer);
	}
	
	/**
	 * Called when someone rightclicks this Cover.
	 * <p/>
	 * return true, if something actually happens.
	 */
	public final boolean onCoverRightClick(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return onCoverRightClickImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, player, x, y, z);
	}
	
	/**
	 * Called when someone rightclicks this Cover with a Screwdriver. Doesn't call @onCoverRightclick in this Case.
	 * <p/>
	 * return the new Value of the Cover Variable
	 */
	public final T onCoverScrewdriverClick(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return onCoverScrewdriverClickImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, player, x, y, z);
	}
	
	/**
	 * Called when someone shift-rightclicks this Cover with no tool. Doesn't call @onCoverRightclick in this Case.
	 */
	public final boolean onCoverShiftRightClick(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, EntityPlayer player) {
		return onCoverShiftRightClickImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, player);
	}
	
	public final Object getClientGUI(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, EntityPlayer player, World world) {
		return getClientGUIImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, player, world);
	}
	
	/**
	 * Removes the Cover if this returns true, or if aForced is true.
	 * Doesn't get called when the Machine Block is getting broken, only if you break the Cover away from the Machine.
	 */
	public final boolean onCoverRemoval(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return onCoverRemovalImpl(side, coverId, forceCast(aCoverVariable), aTileEntity, aForced);
	}
	
	/**
	 * Gives a small Text for the status of the Cover.
	 */
	public final String getDescription(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getDescriptionImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * How Blast Proof the Cover is. 30 is normal.
	 */
	public final float getBlastProofLevel(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getBlastProofLevelImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets RS-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	public final boolean letsRedstoneGoIn(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoInImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets RS-Signals out of the Block
	 */
	public final boolean letsRedstoneGoOut(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoOutImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets Fibre-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	public final boolean letsFibreGoIn(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsFibreGoInImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets Fibre-Signals out of the Block
	 */
	public final boolean letsFibreGoOut(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsFibreGoOutImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets Energy into the Block
	 */
	public final boolean letsEnergyIn(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsEnergyInImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets Energy out of the Block
	 */
	public final boolean letsEnergyOut(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return letsEnergyOutImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * If it lets Liquids into the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	public final boolean letsFluidIn(int side, int coverId, ISerializableObject aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return letsFluidInImpl(side, coverId, forceCast(aCoverVariable), aFluid, aTileEntity);
	}
	
	/**
	 * If it lets Liquids out of the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	public final boolean letsFluidOut(int side, int coverId, ISerializableObject aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return letsFluidOutImpl(side, coverId, forceCast(aCoverVariable), aFluid, aTileEntity);
	}
	
	/**
	 * If it lets Items into the Block, aSlot = -1 means if it is generally accepting Items (return false for no reaction at all), aSlot = -2 means if it would accept for all Slots Impl(return true to skip the Checks for each Slot).
	 */
	public final boolean letsItemsIn(int side, int coverId, ISerializableObject aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return letsItemsInImpl(side, coverId, forceCast(aCoverVariable), aSlot, aTileEntity);
	}
	
	/**
	 * If it lets Items out of the Block, aSlot = -1 means if it is generally accepting Items (return false for no reaction at all), aSlot = -2 means if it would accept for all Slots Impl(return true to skip the Checks for each Slot).
	 */
	public final boolean letsItemsOut(int side, int coverId, ISerializableObject aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return letsItemsOutImpl(side, coverId, forceCast(aCoverVariable), aSlot, aTileEntity);
	}
	
	/**
	 * If it lets you rightclick the Machine normally
	 */
	public final boolean isGUIClickable(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return isGUIClickableImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Needs to return true for Covers, which have a RedStone Output on their Facing.
	 */
	public final boolean manipulatesSidedRedstoneOutput(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return manipulatesSidedRedstoneOutputImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * if this Cover should let Pipe Connections look connected even if it is not the case.
	 */
	public final boolean alwaysLookConnected(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return alwaysLookConnectedImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Called to determine the incoming RedStone Signal of a Machine.
	 * Returns the original RedStone per default.
	 * The Cover should @letsRedstoneGoIn or the aInputRedstone Parameter is always 0.
	 */
	public final int getRedstoneInput(int side, int aInputRedstone, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getRedstoneInputImpl(side, aInputRedstone, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * Gets the Tick Rate for doCoverThings of the Cover
	 * <p/>
	 * 0 = No Ticks! Yes, 0 is Default, you have to override this
	 */
	public final int getTickRate(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getTickRateImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	
	/**
	 * The MC Color of this Lens. -1 for no Color (meaning this isn't a Lens then).
	 */
	public final int getLensColor(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getLensColorImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	
	/**
	 * @return the ItemStack dropped by this Cover
	 */
	public final ItemStack getDrop(int side, int coverId, ISerializableObject aCoverVariable, ICoverable aTileEntity) {
		return getDropImpl(side, coverId, forceCast(aCoverVariable), aTileEntity);
	}
	// endregion
	
	// region impl
	
	protected Block getFacadeBlockImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return null;
	}
	
	protected int getFacadeMetaImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return 0;
	}
	
	protected ItemStack getDisplayStackImpl(int coverId, T aCoverVariable) {
		return Utility.intToStack(coverId);
	}
	
	protected ITexture getSpecialCoverTextureImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return null;
	}
	
	protected boolean isDataNeededOnClientImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	protected void onDataChangedImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
	}
	
	
	protected void onDroppedImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
	}
	
	protected boolean isRedstoneSensitiveImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return true;
	}
	
	/**
	 * Called by updateEntity inside the covered TileEntity. aCoverVariable is the Value you returned last time.
	 */
	protected T doCoverThingsImpl(int side, int aInputRedstone, int coverId, T aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return aCoverVariable;
	}
	
	/**
	 * Called when someone rightclicks this Cover.
	 * <p/>
	 * return true, if something actually happens.
	 */
	protected boolean onCoverRightClickImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	/**
	 * Called when someone rightclicks this Cover with a Screwdriver. Doesn't call @onCoverRightclick in this Case.
	 * <p/>
	 * return the new Value of the Cover Variable
	 */
	protected T onCoverScrewdriverClickImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return aCoverVariable;
	}
	
	/**
	 * Called when someone shift-rightclicks this Cover with no tool. Doesn't call @onCoverRightclick in this Case.
	 */
	protected boolean onCoverShiftRightClickImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, EntityPlayer player) {
		if (hasCoverGUI() && player instanceof EntityPlayerMP) {
			lastPlayer = player;
			NETWORK.sendToPlayer(new Packet_TileEntityCoverGUI(side, coverId, aCoverVariable, aTileEntity, (EntityPlayerMP) player), (EntityPlayerMP) player);
			return true;
		}
		return false;
	}
	
	protected Object getClientGUIImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, EntityPlayer player, World world) {
		return null;
	}
	
	/**
	 * Removes the Cover if this returns true, or if aForced is true.
	 * Doesn't get called when the Machine Block is getting broken, only if you break the Cover away from the Machine.
	 */
	protected boolean onCoverRemovalImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return true;
	}
	
	/**
	 * Gives a small Text for the status of the Cover.
	 */
	protected String getDescriptionImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return "";
	}
	
	/**
	 * How Blast Proof the Cover is. 30 is normal.
	 */
	protected float getBlastProofLevelImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return 10.0F;
	}
	
	/**
	 * If it lets RS-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	protected boolean letsRedstoneGoInImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets RS-Signals out of the Block
	 */
	protected boolean letsRedstoneGoOutImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Fibre-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	protected boolean letsFibreGoInImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Fibre-Signals out of the Block
	 */
	protected boolean letsFibreGoOutImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Energy into the Block
	 */
	protected boolean letsEnergyInImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Energy out of the Block
	 */
	protected boolean letsEnergyOutImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Liquids into the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	protected boolean letsFluidInImpl(int side, int coverId, T aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Liquids out of the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	protected boolean letsFluidOutImpl(int side, int coverId, T aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Items into the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
	 */
	protected boolean letsItemsInImpl(int side, int coverId, T aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Items out of the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
	 */
	protected boolean letsItemsOutImpl(int side, int coverId, T aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets you rightclick the Machine normally
	 */
	protected boolean isGUIClickableImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * Needs to return true for Covers, which have a RedStone Output on their Facing.
	 */
	protected boolean manipulatesSidedRedstoneOutputImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * if this Cover should let Pipe Connections look connected even if it is not the case.
	 */
	protected boolean alwaysLookConnectedImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * Called to determine the incoming RedStone Signal of a Machine.
	 * Returns the original RedStone per default.
	 * The Cover should @letsRedstoneGoIn or the aInputRedstone Parameter is always 0.
	 */
	protected int getRedstoneInputImpl(int side, int aInputRedstone, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoIn(side, coverId, aCoverVariable, aTileEntity) ? aInputRedstone : 0;
	}
	
	/**
	 * Gets the Tick Rate for doCoverThings of the Cover
	 * <p/>
	 * 0 = No Ticks! Yes, 0 is Default, you have to override this
	 */
	protected int getTickRateImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return 0;
	}
	
	
	/**
	 * The MC Color of this Lens. -1 for no Color (meaning this isn't a Lens then).
	 */
	protected int getLensColorImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return -1;
	}
	
	/**
	 * @return the ItemStack dropped by this Cover
	 */
	protected ItemStack getDropImpl(int side, int coverId, T aCoverVariable, ICoverable aTileEntity) {
		return OreDictUnifier.get(true, aTileEntity.getCoverItemAtSide(side));
	}
	
	//endregion
	
	// region no data
	
	/**
	 * Checks if the Cover can be placed on this.
	 */
	public boolean isCoverPlaceable(int side, ItemStack stack, ICoverable aTileEntity) {
		return isCoverPlaceable(side, new ItemStackData(stack), aTileEntity);
	}
	
	/**
	 * Checks if the Cover can be placed on this. You will probably want to call {@link #isCoverPlaceable(int, ItemStack, ICoverable)} instead.
	 */
	@Deprecated
	public boolean isCoverPlaceable(int side, ItemStackData stack, ICoverable aTileEntity) {
		return true;
	}
	
	public boolean hasCoverGUI() {
		return false;
	}
	
	/**
	 * Called when someone rightclicks this Cover Client Side
	 * <p/>
	 * return true, if something actually happens.
	 */
	public boolean onCoverRightclickClient(int side, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	/**
	 * If this is a simple Cover, which can also be used on Bronze Machines and similar.
	 */
	public boolean isSimpleCover() {
		return false;
	}
	
	/**
	 * sets the Cover upon placement.
	 */
	public void placeCover(int side, ItemStack aCover, ICoverable aTileEntity) {
		aTileEntity.setCoverIDAtSide(side, Utility.stackToInt(aCover));
	}
	
	// endregion
}
