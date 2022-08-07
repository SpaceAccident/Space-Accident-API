package space.accident.api.util;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.main.network.Packet_TileEntityCoverGUI;

import static space.accident.main.SpaceAccidentApi.NETWORK;

/**
 * For Covers with a special behavior. Has fixed storage format of 4 int. Not very convenient...
 */
public abstract class SA_CoverBehavior extends SA_CoverBehaviorBase<ISerializableObject.LegacyCoverData> {
	
	public EntityPlayer lastPlayer = null;
	
	public SA_CoverBehavior() {
		super(ISerializableObject.LegacyCoverData.class);
	}
	
	private static int convert(ISerializableObject.LegacyCoverData data) {
		return data == null ? 0 : data.get();
	}
	
	// region bridge the parent call to legacy calls
	
	@Override
	public final ISerializableObject.LegacyCoverData createDataObject() {
		return new ISerializableObject.LegacyCoverData();
	}
	
	@Override
	public ISerializableObject.LegacyCoverData createDataObject(int aLegacyData) {
		return new ISerializableObject.LegacyCoverData(aLegacyData);
	}
	
	@Override
	protected boolean isRedstoneSensitiveImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return isRedstoneSensitive(side, coverId, aCoverVariable.get(), aTileEntity, aTimer);
	}
	
	@Override
	protected ISerializableObject.LegacyCoverData doCoverThingsImpl(int side, int aInputRedstone, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, long aTimer) {
		if (aCoverVariable == null)
			aCoverVariable = new ISerializableObject.LegacyCoverData();
		aCoverVariable.set(doCoverThings(side, aInputRedstone, coverId, aCoverVariable.get(), aTileEntity, aTimer));
		return aCoverVariable;
	}
	
	@Override
	protected boolean onCoverRightClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return onCoverRightclick(side, coverId, convert(aCoverVariable), aTileEntity, player, x, y, z);
	}
	
	@Override
	protected ISerializableObject.LegacyCoverData onCoverScrewdriverClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		if (aCoverVariable == null)
			aCoverVariable = new ISerializableObject.LegacyCoverData();
		aCoverVariable.set(onCoverScrewdriverclick(side, coverId, convert(aCoverVariable), aTileEntity, player, x, y, z));
		return aCoverVariable;
	}
	
	@Override
	protected boolean onCoverShiftRightClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player) {
		return onCoverShiftRightclick(side, coverId, convert(aCoverVariable), aTileEntity, player);
	}
	
	@Override
	protected Object getClientGUIImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, World world) {
		return getClientGUI(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean onCoverRemovalImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return onCoverRemoval(side, coverId, convert(aCoverVariable), aTileEntity, aForced);
	}
	
	@Override
	protected String getDescriptionImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getDescription(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected float getBlastProofLevelImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getBlastProofLevel(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsRedstoneGoInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoIn(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsRedstoneGoOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoOut(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsFibreGoInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsFibreGoIn(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsFibreGoOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsFibreGoOut(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsEnergyInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsEnergyIn(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsEnergyOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return letsEnergyOut(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean letsFluidInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return letsFluidIn(side, coverId, convert(aCoverVariable), aFluid, aTileEntity);
	}
	
	@Override
	protected boolean letsFluidOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return letsFluidOut(side, coverId, convert(aCoverVariable), aFluid, aTileEntity);
	}
	
	@Override
	protected boolean letsItemsInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return letsItemsIn(side, coverId, convert(aCoverVariable), aSlot, aTileEntity);
	}
	
	@Override
	protected boolean letsItemsOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return letsItemsOut(side, coverId, convert(aCoverVariable), aSlot, aTileEntity);
	}
	
	@Override
	protected boolean isGUIClickableImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return isGUIClickable(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean manipulatesSidedRedstoneOutputImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return manipulatesSidedRedstoneOutput(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected boolean alwaysLookConnectedImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return alwaysLookConnected(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected int getRedstoneInputImpl(int side, int aInputRedstone, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getRedstoneInput(side, aInputRedstone, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected int getTickRateImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getTickRate(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected int getLensColorImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getLensColor(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	@Override
	protected ItemStack getDropImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return getDrop(side, coverId, convert(aCoverVariable), aTileEntity);
	}
	
	// endregion
	
	public boolean isRedstoneSensitive(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return true;
	}
	
	/**
	 * Called by updateEntity inside the covered TileEntity. aCoverVariable is the Value you returned last time.
	 */
	public int doCoverThings(int side, int aInputRedstone, int coverId, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return aCoverVariable;
	}
	
	/**
	 * Called when someone rightclicks this Cover.
	 * <p/>
	 * return true, if something actually happens.
	 */
	public boolean onCoverRightclick(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	/**
	 * Called when someone rightclicks this Cover with a Screwdriver. Doesn't call @onCoverRightclick in this Case.
	 * <p/>
	 * return the new Value of the Cover Variable
	 */
	public int onCoverScrewdriverclick(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return aCoverVariable;
	}
	
	/**
	 * Called when someone shift-rightclicks this Cover with no tool. Doesn't call @onCoverRightclick in this Case.
	 */
	public boolean onCoverShiftRightclick(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, EntityPlayer player) {
		if(hasCoverGUI() && player instanceof EntityPlayerMP) {
			lastPlayer = player;
			NETWORK.sendToPlayer(new Packet_TileEntityCoverGUI(side, coverId, aCoverVariable, aTileEntity, (EntityPlayerMP) player), (EntityPlayerMP) player);
			return true;
		}
		return false;
	}
	
	public Object getClientGUI(int side, int coverId, int coverData, ICoverable aTileEntity) {
		return null;
	}
	
	/**
	 * Removes the Cover if this returns true, or if aForced is true.
	 * Doesn't get called when the Machine Block is getting broken, only if you break the Cover away from the Machine.
	 */
	public boolean onCoverRemoval(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return true;
	}
	
	/**
	 * Gives a small Text for the status of the Cover.
	 */
	public String getDescription(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return "";
	}
	
	/**
	 * How Blast Proof the Cover is. 30 is normal.
	 */
	public float getBlastProofLevel(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return 10.0F;
	}
	
	/**
	 * If it lets RS-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	public boolean letsRedstoneGoIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets RS-Signals out of the Block
	 */
	public boolean letsRedstoneGoOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Fibre-Signals into the Block
	 * <p/>
	 * This is just Informative so that Machines know if their RedStone Input is blocked or not
	 */
	public boolean letsFibreGoIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Fibre-Signals out of the Block
	 */
	public boolean letsFibreGoOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Energy into the Block
	 */
	public boolean letsEnergyIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Energy out of the Block
	 */
	public boolean letsEnergyOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Liquids into the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	public boolean letsFluidIn(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Liquids out of the Block, aFluid can be null meaning if this is generally allowing Fluids or not.
	 */
	public boolean letsFluidOut(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Items into the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
	 */
	public boolean letsItemsIn(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets Items out of the Block, aSlot = -1 means if it is generally accepting Items (return false for no Interaction at all), aSlot = -2 means if it would accept for all Slots (return true to skip the Checks for each Slot).
	 */
	public boolean letsItemsOut(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * If it lets you rightclick the Machine normally
	 */
	public boolean isGUIClickable(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * Needs to return true for Covers, which have a RedStone Output on their Facing.
	 */
	public boolean manipulatesSidedRedstoneOutput(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * if this Cover should let Pipe Connections look connected even if it is not the case.
	 */
	public boolean alwaysLookConnected(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	/**
	 * Called to determine the incoming RedStone Signal of a Machine.
	 * Returns the original RedStone per default.
	 * The Cover should @letsRedstoneGoIn or the aInputRedstone Parameter is always 0.
	 */
	public int getRedstoneInput(int side, int aInputRedstone, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return letsRedstoneGoIn(side, coverId, aCoverVariable, aTileEntity) ? aInputRedstone : 0;
	}
	
	/**
	 * Gets the Tick Rate for doCoverThings of the Cover
	 * <p/>
	 * 0 = No Ticks! Yes, 0 is Default, you have to override this
	 */
	public int getTickRate(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return 0;
	}
	
	/**
	 * The MC Color of this Lens. -1 for no Color (meaning this isn't a Lens then).
	 */
	public int getLensColor(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return -1;
	}
	
	/**
	 * @return the ItemStack dropped by this Cover
	 */
	public ItemStack getDrop(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return OreDictUnifier.get(true, aTileEntity.getCoverItemAtSide(side));
	}
}