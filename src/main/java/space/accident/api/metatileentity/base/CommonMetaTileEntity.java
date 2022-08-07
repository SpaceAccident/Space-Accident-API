package space.accident.api.metatileentity.base;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import space.accident.api.API;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.objects.ItemStackData;
import space.accident.api.util.SpaceLog;
import space.accident.api.util.Utility;

public abstract class CommonMetaTileEntity extends CoverableTileEntity implements ITile {
	protected boolean mNeedsBlockUpdate = true, mNeedsUpdate = true, mSendClientData = false, mInventoryChanged = false;
	
	protected boolean createNewMetatileEntity(short id) {
		if (id <= 0 || id >= API.METATILEENTITIES.length || API.METATILEENTITIES[id] == null) {
			SpaceLog.err.println("MetaID " + id + " not loadable => locking TileEntity!");
		} else {
			if (hasValidMetaTileEntity()) getMetaTile().setBaseMetaTileEntity(null);
			API.METATILEENTITIES[id].newMetaEntity(this).setBaseMetaTileEntity(this);
			mTickTimer = 0;
			mID = id;
			return true;
		}
		return false;
	}
	protected void saveMetaTileNBT(NBTTagCompound nbt) {
		try {
			if (hasValidMetaTileEntity()) {
				final NBTTagList tItemList = new NBTTagList();
				for (int i = 0; i < getMetaTile().getRealInventory().length; i++) {
					final ItemStack tStack = getMetaTile().getRealInventory()[i];
					if (tStack != null) {
						final NBTTagCompound tTag = new NBTTagCompound();
						tTag.setInteger("IntSlot", i);
						tStack.writeToNBT(tTag);
						tItemList.appendTag(tTag);
					}
				}
				nbt.setTag("Inventory", tItemList);
				
				try {
					getMetaTile().writeToNBT(nbt);
				} catch (Throwable e) {
					SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity.");
					e.printStackTrace(SpaceLog.err);
				}
			}
		} catch (Throwable e) {
			SpaceLog.FML_LOGGER.error("Encountered CRITICAL ERROR while saving MetaTileEntity.");
			e.printStackTrace(SpaceLog.err);
		}
	}
	
	protected void loadMetaTileNBT(NBTTagCompound nbt) {
		if (mID != 0 && createNewMetatileEntity(mID)) {
			final NBTTagList tItemList = nbt.getTagList("Inventory", 10);
			for (int i = 0; i < tItemList.tagCount(); i++) {
				final NBTTagCompound tTag = tItemList.getCompoundTagAt(i);
				final int tSlot = tTag.getInteger("IntSlot");
				if (tSlot >= 0 && tSlot < getMetaTile().getRealInventory().length) {
					getMetaTile().getRealInventory()[tSlot] = Utility.loadItem(tTag);
				}
			}
			
			try {
				getMetaTile().readFromNBT(nbt);
			} catch (Throwable e) {
				SpaceLog.FML_LOGGER.error("Encountered Exception while loading MetaTileEntity.");
				e.printStackTrace(SpaceLog.err);
			}
		}
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		mInventoryChanged = true;
	}
	
	@Override
	public boolean hasInventoryBeenModified() {
		return mInventoryChanged;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		if (canAccessData()) return getMetaTile().isValidSlot(index);
		return false;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		issueClientUpdate();
		return null;
	}
	
	@Override
	public void issueTextureUpdate() {
		mNeedsUpdate = true;
	}
	
	@Override
	public void issueClientUpdate() {
		mSendClientData = true;
	}
	
	@Override
	public void issueBlockUpdate() {
		mNeedsBlockUpdate = true;
	}
	
	@Override
	public boolean isValidFace(int side) {
		if (canAccessData()) return getMetaTile().isFacingValid(side);
		return false;
	}
	
	protected boolean canAccessData() {
		return !isDead && hasValidMetaTileEntity();
	}
	
	protected abstract boolean hasValidMetaTileEntity();
	
	@Override
	public String[] getDescription() {
		if (canAccessData()) return getMetaTile().getDescription();
		return new String[0];
	}
	
	@Override
	public boolean isStillValid() {
		return hasValidMetaTileEntity();
	}
	
	@Override
	public boolean allowCoverOnSide(int side, ItemStackData coverId) {
		return hasValidMetaTileEntity() && getMetaTile().allowCoverOnSide(side, coverId);
	}
	
	@Override
	public void issueCoverUpdate(int side) {
		super.issueCoverUpdate(side);
		issueClientUpdate();
	}
}