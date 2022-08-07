package space.accident.main.common.covers;

import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.ISerializableObject;
import space.accident.api.util.RenderingWorld;
import space.accident.api.util.SA_CoverBehaviorBase;

import javax.annotation.Nonnull;

import static space.accident.api.util.Utility.copyAmount;
import static space.accident.api.util.Utility.stackToInt;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;

public abstract class SA_Cover_FacadeBase extends SA_CoverBehaviorBase<SA_Cover_FacadeBase.FacadeData> {
	/**
	 * This is the Dummy, if there is a generic Cover without behavior
	 */
	public SA_Cover_FacadeBase() {
		super(FacadeData.class);
	}
	
	@Override
	public boolean isSimpleCover() {
		return true;
	}
	
	@Override
	public FacadeData createDataObject(int aLegacyData) {
		return new FacadeData();
	}
	
	@Override
	public FacadeData createDataObject() {
		return new FacadeData();
	}
	
	@Override
	protected FacadeData onCoverScrewdriverClickImpl(int side, int coverId, FacadeData aCoverVariable,
													 ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		aCoverVariable.mFlags = ((aCoverVariable.mFlags + 1) & 15);
		sendChat(player, ((aCoverVariable.mFlags & 1) != 0 ?
				trans("128", "RedStone ") : "") + ((aCoverVariable.mFlags & 2) != 0 ?
				trans("129", "Energy ") : "") + ((aCoverVariable.mFlags & 4) != 0 ?
				trans("130", "Fluids ") : "") + ((aCoverVariable.mFlags & 8) != 0 ?
				trans("131", "Items ") : "")
		);
		return aCoverVariable;
	}
	
	@Override
	protected boolean letsRedstoneGoInImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 1) != 0;
	}
	
	@Override
	protected boolean letsRedstoneGoOutImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 1) != 0;
	}
	
	@Override
	protected boolean letsEnergyInImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 2) != 0;
	}
	
	@Override
	protected boolean letsEnergyOutImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 2) != 0;
	}
	
	@Override
	protected boolean letsFluidInImpl(int side, int coverId, FacadeData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 4) != 0;
	}
	
	@Override
	protected boolean letsFluidOutImpl(int side, int coverId, FacadeData aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 4) != 0;
	}
	
	@Override
	protected boolean letsItemsInImpl(int side, int coverId, FacadeData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 8) != 0;
	}
	
	@Override
	protected boolean letsItemsOutImpl(int side, int coverId, FacadeData aCoverVariable, int aSlot, ICoverable aTileEntity) {
		return (aCoverVariable.mFlags & 8) != 0;
	}
	
	@Override
	public void placeCover(int side, ItemStack aCover, ICoverable aTileEntity) {
		aTileEntity.setCoverIdAndDataAtSide(side, stackToInt(aCover), new FacadeData(copyAmount(1, aCover), 0));
		if (aTileEntity.isClientSide())
			RenderingWorld.getInstance().register(aTileEntity.getX(), aTileEntity.getY(), aTileEntity.getZ(), getTargetBlock(aCover), getTargetMeta(aCover));
	}
	
	@Override
	protected ItemStack getDropImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return aCoverVariable.mStack;
	}
	
	@Override
	protected ITexture getSpecialCoverTextureImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		if (isStackInvalid(aCoverVariable.mStack)) return Textures.BlockIcons.ERROR_RENDERING[0];
		Block block = getTargetBlock(aCoverVariable.mStack);
		if (block == null) return Textures.BlockIcons.ERROR_RENDERING[0];
		if (block.getRenderBlockPass() != 0)
			return Textures.BlockIcons.ERROR_RENDERING[0];
		return TextureFactory.builder().setFromBlock(block, getTargetMeta(aCoverVariable.mStack)).useWorldCoord().setFromSide(ForgeDirection.getOrientation(side)).build();
	}
	
	@Override
	protected Block getFacadeBlockImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		if (isStackInvalid(aCoverVariable.mStack)) return null;
		return getTargetBlock(aCoverVariable.mStack);
	}
	
	@Override
	protected int getFacadeMetaImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		if (isStackInvalid(aCoverVariable.mStack)) return 0;
		return getTargetMeta(aCoverVariable.mStack);
	}
	
	protected abstract Block getTargetBlock(ItemStack aFacadeStack);
	
	protected abstract int getTargetMeta(ItemStack aFacadeStack);
	
	@Override
	protected boolean isDataNeededOnClientImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		return true;
	}
	
	@Override
	protected void onDataChangedImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		if (aTileEntity.isClientSide())
			RenderingWorld.getInstance().register(aTileEntity.getX(), aTileEntity.getY(), aTileEntity.getZ(), getTargetBlock(aCoverVariable.mStack), getTargetMeta(aCoverVariable.mStack));
	}
	
	@Override
	protected void onDroppedImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity) {
		if (aTileEntity.isClientSide()) {
			for (int i = 0; i < 6; i++) {
				if (i == side) continue;
				// since we do not allow multiple type of facade per block, this check would be enough.
				if (aTileEntity.getCoverBehaviorAtSideNew(i) instanceof SA_Cover_FacadeBase) return;
			}
			if (aCoverVariable.mStack != null)
				// mStack == null -> cover removed before data reach client
				RenderingWorld.getInstance().unregister(aTileEntity.getX(), aTileEntity.getY(), aTileEntity.getZ(), getTargetBlock(aCoverVariable.mStack), getTargetMeta(aCoverVariable.mStack));
		}
	}
	
	@Override
	protected boolean onCoverRightClickImpl(int side, int coverId, FacadeData aCoverVariable, ICoverable aTileEntity, EntityPlayer player, float x, float y, float z) {
		// in case cover data didn't hit client somehow. maybe he had a ridiculous view distance
		aTileEntity.issueCoverUpdate(side);
		return super.onCoverRightClickImpl(side, coverId, aCoverVariable, aTileEntity, player, x, y, z);
	}
	
	@Override
	public boolean isCoverPlaceable(int side, ItemStack stack, ICoverable aTileEntity) {
		// blocks that are not rendered in pass 0 are now accepted but rendered awkwardly
		// to render it correctly require changing GT_Block_Machine to render in both pass, which is not really a good idea...
		if (!super.isCoverPlaceable(side, stack, aTileEntity)) return false;
		Block targetBlock = getTargetBlock(stack);
		if (targetBlock == null) return false;
		// we allow one single type of facade on the same block for now
		// otherwise it's not clear which block this block should impersonate
		// this restriction can be lifted later by specifying a certain facade as dominate one as an extension to this class
		for (int i = 0; i < 6; i++) {
			if (i == side) continue;
			SA_CoverBehaviorBase<?> behavior = aTileEntity.getCoverBehaviorAtSideNew(i);
			if (behavior == null) continue;
			Block facadeBlock = behavior.getFacadeBlock(i, aTileEntity.getCoverIDAtSide(i), aTileEntity.getComplexCoverDataAtSide(i), aTileEntity);
			if (facadeBlock == null) continue;
			if (facadeBlock != targetBlock) return false;
			if (behavior.getFacadeMeta(i, aTileEntity.getCoverIDAtSide(i), aTileEntity.getComplexCoverDataAtSide(i), aTileEntity) != getTargetMeta(stack)) return false;
		}
		return true;
	}
	
	public static class FacadeData implements ISerializableObject {
		ItemStack mStack;
		int mFlags;
		
		public FacadeData() {
		}
		
		public FacadeData(ItemStack mStack, int mFlags) {
			this.mStack = mStack;
			this.mFlags = mFlags;
		}
		
		@Nonnull
		@Override
		public ISerializableObject copy() {
			return new FacadeData(mStack, mFlags);
		}
		
		@Nonnull
		@Override
		public NBTBase saveDataToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			if (mStack != null) tag.setTag("mStack", mStack.writeToNBT(new NBTTagCompound()));
			tag.setInteger("mFlags", mFlags);
			return tag;
		}
		
		@Override
		public void writeToByteBuf(ByteBuf aBuf) {
			ByteBufUtils.writeItemStack(aBuf, mStack);
			aBuf.writeByte(mFlags);
		}
		
		@Override
		public void loadDataFromNBT(NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			mStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("mStack"));
			mFlags = tag.getInteger("mFlags");
		}
		
		@Nonnull
		@Override
		public ISerializableObject readFromPacket(ByteArrayDataInput aBuf, EntityPlayerMP player) {
			mStack = ISerializableObject.readItemStackFromGreggyByteBuf(aBuf);
			mFlags = aBuf.readByte();
			return this;
		}
	}
}
