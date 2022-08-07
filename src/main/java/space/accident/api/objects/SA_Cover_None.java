package space.accident.api.objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.util.SA_CoverBehavior;
import space.accident.api.util.ISerializableObject;

public class SA_Cover_None extends SA_CoverBehavior {
	
	/**
	 * This is the Dummy, if there is no Cover
	 */
	public SA_Cover_None() {
	}
	
	@Override
	public float getBlastProofLevel(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return 10.0F;
	}
	
	@Override
	public boolean letsRedstoneGoIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsRedstoneGoOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsEnergyIn(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsEnergyOut(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsFluidIn(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsFluidOut(int side, int coverId, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsItemsIn(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean letsItemsOut(int side, int coverId, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean isGUIClickable(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	public boolean manipulatesSidedRedstoneOutput(int side, int coverId, int aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	@Override
	public boolean onCoverRightclick(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	@Override
	public boolean onCoverRemoval(int side, int coverId, int aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return true;
	}
	
	@Override
	public int doCoverThings(int side, int aInputRedstone, int coverId, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return 0;
	}
	
	@Override
	public boolean isSimpleCover() {
		return true;
	}
	
	@Override
	protected boolean isRedstoneSensitiveImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return false;
	}
	
	@Override
	protected ISerializableObject.LegacyCoverData doCoverThingsImpl(int side, int aInputRedstone, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, long aTimer) {
		return aCoverVariable;
	}
	
	@Override
	protected boolean onCoverRightClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return false;
	}
	
	@Override
	protected ISerializableObject.LegacyCoverData onCoverScrewdriverClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		return aCoverVariable;
	}
	
	@Override
	protected boolean onCoverShiftRightClickImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player) {
		return false;
	}
	
	@Override
	protected Object getClientGUIImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, World world) {
		return null;
	}
	
	@Override
	protected boolean onCoverRemovalImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity, boolean aForced) {
		return true;
	}
	
	@Override
	protected String getDescriptionImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return "";
	}
	
	@Override
	protected float getBlastProofLevelImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return 10.0F;
	}
	
	@Override
	protected boolean letsRedstoneGoInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsRedstoneGoOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsFibreGoInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsFibreGoOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsEnergyInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsEnergyOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsFluidInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsFluidOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsItemsInImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean letsItemsOutImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean isGUIClickableImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected boolean manipulatesSidedRedstoneOutputImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	@Override
	protected boolean alwaysLookConnectedImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return false;
	}
	
	@Override
	protected int getRedstoneInputImpl(int side, int aInputRedstone, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return aInputRedstone;
	}
	
	@Override
	protected int getTickRateImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return 0;
	}
	
	@Override
	protected int getLensColorImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return -1;
	}
	
	@Override
	protected ItemStack getDropImpl(int side, int coverId, ISerializableObject.LegacyCoverData aCoverVariable, ICoverable aTileEntity) {
		return null;
	}
}
