package space.accident.api.metatileentity.base;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.implementations.mutlis.Multiblock_Tooltip_Builder;
import space.accident.structurelib.StructureLibAPI;
import space.accident.structurelib.alignment.IAlignment;
import space.accident.structurelib.alignment.IAlignmentLimits;
import space.accident.structurelib.alignment.IAlignmentProvider;
import space.accident.structurelib.alignment.constructable.IConstructable;
import space.accident.structurelib.alignment.enumerable.ExtendedFacing;
import space.accident.structurelib.alignment.enumerable.Flip;
import space.accident.structurelib.alignment.enumerable.Rotation;
import space.accident.structurelib.structure.IStructureDefinition;

public abstract class EnhancedMultiBlockBase<T extends EnhancedMultiBlockBase<T>> extends TooltipMultiBlockBase implements IAlignment, IConstructable {
	private ExtendedFacing mExtendedFacing = ExtendedFacing.DEFAULT;
	private IAlignmentLimits mLimits = getInitialAlignmentLimits();
	
	protected EnhancedMultiBlockBase(int id, String name, String aNameRegional) {
		super(id, name, aNameRegional);
	}
	
	protected EnhancedMultiBlockBase(String name) {
		super(name);
	}
	
	@Override
	public ExtendedFacing getExtendedFacing() {
		return mExtendedFacing;
	}
	
	@Override
	public void setExtendedFacing(ExtendedFacing newExtendedFacing) {
		if (mExtendedFacing != newExtendedFacing) {
			if(isCompletedStructure)
				stopMachine();
			mExtendedFacing = newExtendedFacing;
			final ITile base = getBaseMetaTileEntity();
			isCompletedStructure = false;
			isUpdated = false;
			update = 100;
			if (getBaseMetaTileEntity().isServerSide()) {
				StructureLibAPI.sendAlignment((IAlignmentProvider) base,
						new NetworkRegistry.TargetPoint(base.getWorld().provider.dimensionId, base.getX(), base.getY(), base.getZ(), 512));
			} else {
				base.issueTextureUpdate();
			}
		}
	}
	
	@Override
	public final boolean isFacingValid(int face) {
		return canSetToDirectionAny(ForgeDirection.getOrientation(face));
	}
	
	@Override
	public boolean onWrenchRightClick(int side, int aWrenchingSide, EntityPlayer player, float x, float y, float z) {
		if (aWrenchingSide != getBaseMetaTileEntity().getFrontFace())
			return super.onWrenchRightClick(side, aWrenchingSide, player, x, y, z);
		if (player.isSneaking()) {
			// we won't be allowing horizontal flips, as it can be perfectly emulated by rotating twice and flipping horizontally
			toolSetFlip(getFlip().isHorizontallyFlipped() ? Flip.NONE : Flip.HORIZONTAL);
		} else {
			toolSetRotation(null);
		}
		return true;
	}
	
	@Override
	public void onFacingChange() {
		toolSetDirection(ForgeDirection.getOrientation(getBaseMetaTileEntity().getFrontFace()));
	}
	
	@Override
	public IAlignmentLimits getAlignmentLimits() {
		return mLimits;
	}
	
	protected void setAlignmentLimits(IAlignmentLimits mLimits) {
		this.mLimits = mLimits;
	}
	
	/**
	 * Due to limitation of Java type system, you might need to do an unchecked cast.
	 * HOWEVER, the returned IStructureDefinition is expected to be evaluated against current instance only, and should
	 * not be used against other instances, even for those of the same class.
	 */
	public abstract IStructureDefinition<T> getStructureDefinition();
	
	protected abstract Multiblock_Tooltip_Builder createTooltip();
	
	@Override
	public String[] getStructureDescription(ItemStack stackSize) {
		return getTooltip().getStructureHint();
	}
	
	protected IAlignmentLimits getInitialAlignmentLimits() {
		return (d, r, f) -> !f.isVerticallyFliped();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("rotation", mExtendedFacing.getRotation().getIndex());
		nbt.setInteger("flip", mExtendedFacing.getFlip().getIndex());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		mExtendedFacing = ExtendedFacing.of(ForgeDirection.getOrientation(getBaseMetaTileEntity().getFrontFace()),
				Rotation.byIndex(nbt.getInteger("rotation")),
				Flip.byIndex(nbt.getInteger("flip")));
	}
	
	@SuppressWarnings("unchecked")
	private IStructureDefinition<EnhancedMultiBlockBase<T>> getCastedStructureDefinition() {
		return (IStructureDefinition<EnhancedMultiBlockBase<T>>) getStructureDefinition();
	}
	
	/**
	 * Explanation of the world coordinate these offset means:
	 *
	 * Imagine you stand in front of the controller, with controller facing towards you not rotated or flipped.
	 *
	 * The horizontalOffset would be the number of blocks on the left side of the controller, not counting controller itself.
	 * The verticalOffset would be the number of blocks on the top side of the controller, not counting controller itself.
	 * The depthOffset would be the number of blocks between you and controller, not counting controller itself.
	 *
	 * All these offsets can be negative.
	 */
	protected final boolean checkPiece(String piece, int horizontalOffset, int verticalOffset, int depthOffset) {
		final ITile tTile = getBaseMetaTileEntity();
		return getCastedStructureDefinition().check(this, piece, tTile.getWorld(), getExtendedFacing(), tTile.getX(), tTile.getY(), tTile.getZ(), horizontalOffset, verticalOffset, depthOffset, !isCompletedStructure);
	}
	
	protected final boolean buildPiece(String piece, ItemStack trigger, boolean hintOnly, int horizontalOffset, int verticalOffset, int depthOffset) {
		final ITile tTile = getBaseMetaTileEntity();
		return getCastedStructureDefinition().buildOrHints(this, trigger, piece, tTile.getWorld(), getExtendedFacing(), tTile.getX(), tTile.getY(), tTile.getZ(), horizontalOffset, verticalOffset, depthOffset, hintOnly);
	}
	
	@Override
	public void onFirstTick(ITile baseTile) {
		super.onFirstTick(baseTile);
		if (baseTile.isClientSide())
			StructureLibAPI.queryAlignment((IAlignmentProvider) baseTile);
	}
}
