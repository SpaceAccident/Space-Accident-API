package space.accident.api.metatileentity.base;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import space.accident.api.API;
import space.accident.api.enums.Textures;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITileWailaProvider;
import space.accident.api.objects.ItemStackData;
import space.accident.api.util.ISerializableObject;
import space.accident.api.util.SA_CoverBehaviorBase;
import space.accident.main.events.ClientEvents;
import space.accident.main.network.Packet_RequestCoverData;
import space.accident.main.network.Packet_SendCoverData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static space.accident.api.enums.Values.ALL_VALID_SIDES;
import static space.accident.api.util.LanguageManager.FACES;
import static space.accident.api.util.LanguageManager.getTranslation;
import static space.accident.main.SpaceAccidentApi.NETWORK;
import static space.accident.main.SpaceAccidentApi.proxy;

public abstract class CoverableTileEntity extends BaseTileEntity implements ICoverable, ITileWailaProvider {
	public static final String[] COVER_DATA_NBT_KEYS = Arrays.stream(ForgeDirection.VALID_DIRECTIONS).mapToInt(Enum::ordinal).mapToObj(i -> "mCoverData" + i).toArray(String[]::new);
	protected final SA_CoverBehaviorBase<?>[] mCoverBehaviors = new SA_CoverBehaviorBase<?>[]{API.sNoBehavior, API.sNoBehavior, API.sNoBehavior, API.sNoBehavior, API.sNoBehavior, API.sNoBehavior};
	protected final boolean[] mCoverNeedUpdate = new boolean[]{false, false, false, false, false, false};
	public long mTickTimer = 0;
	protected int[] mSidedRedstone = new int[]{15, 15, 15, 15, 15, 15};
	protected boolean mRedstone = false;
	protected int mStrongRedstone = 0;
	protected int[] mCoverSides = new int[]{0, 0, 0, 0, 0, 0};
	protected ISerializableObject[] mCoverData = new ISerializableObject[6];
	protected short mID = 0;
	
	/**
	 * Add installed cover information, generally called from ItemBlock
	 *
	 * @param nbt   - NBTTagCompound from the stack
	 * @param aList - List to add the information to
	 */
	public static void addInstalledCoversInformation(NBTTagCompound nbt, List<String> aList) {
		if (nbt.hasKey("mCoverSides")) {
			final int[] mCoverSides = nbt.getIntArray("mCoverSides");
			if (mCoverSides != null && mCoverSides.length == 6) {
				for (int tSide : ALL_VALID_SIDES) {
					final int coverId = mCoverSides[tSide];
					if (coverId == 0) continue;
					final SA_CoverBehaviorBase<?> behavior = API.getCoverBehaviorNew(coverId);
					if (behavior == null || behavior == API.sNoBehavior) continue;
					if (!nbt.hasKey(CoverableTileEntity.COVER_DATA_NBT_KEYS[tSide])) continue;
					final ISerializableObject dataObject = behavior.createDataObject(nbt.getTag(CoverableTileEntity.COVER_DATA_NBT_KEYS[tSide]));
					final ItemStack coverStack = behavior.getDisplayStack(coverId, dataObject);
					if (coverStack != null) {
						aList.add(String.format("Cover on %s side: %s", getTranslation(FACES[tSide]), coverStack.getDisplayName()));
					}
				}
			}
		}
	}
	
	protected void writeCoverNBT(NBTTagCompound nbt, boolean isDrop) {
		boolean hasCover = false;
		for (int i = 0; i < mCoverData.length; i++) {
			if (mCoverSides[i] != 0 && mCoverData[i] != null) {
				nbt.setTag(COVER_DATA_NBT_KEYS[i], mCoverData[i].saveDataToNBT());
				hasCover = true;
			}
		}
		if (mStrongRedstone > 0) nbt.setInteger("mStrongRedstone", mStrongRedstone);
		if (hasCover) nbt.setIntArray("mCoverSides", mCoverSides);
		
		if (!isDrop) {
			nbt.setIntArray("mRedstoneSided", mSidedRedstone);
			nbt.setBoolean("mRedstone", mRedstone);
		}
		
	}
	
	protected void readCoverNBT(NBTTagCompound nbt) {
		mCoverSides     = nbt.hasKey("mCoverSides") ? nbt.getIntArray("mCoverSides") : new int[]{0, 0, 0, 0, 0, 0};
		mRedstone       = nbt.getBoolean("mRedstone");
		mSidedRedstone  = nbt.hasKey("mRedstoneSided") ? nbt.getIntArray("mRedstoneSided") : new int[]{15, 15, 15, 15, 15, 15};
		mStrongRedstone = nbt.getInteger("mStrongRedstone");
		
		for (int i = 0; i < 6; i++) mCoverBehaviors[i] = API.getCoverBehaviorNew(mCoverSides[i]);
		
		// check old form of data
		mCoverData = new ISerializableObject[6];
		if (nbt.hasKey("mCoverData", 11) && nbt.getIntArray("mCoverData").length == 6) {
			final int[] tOldData = nbt.getIntArray("mCoverData");
			for (int i = 0; i < tOldData.length; i++) {
				/*if (mCoverBehaviors[i] instanceof Cover_Fluidfilter) {
					final String filterKey = String.format("fluidFilter%d", i);
					if (nbt.hasKey(filterKey)) {
						mCoverData[i] = mCoverBehaviors[i].createDataObject((tOldData[i] & 7) | (FluidRegistry.getFluidID(nbt.getString(filterKey)) << 3));
					}
				} else */
				if (mCoverBehaviors[i] != null && mCoverBehaviors[i] != API.sNoBehavior) {
					mCoverData[i] = mCoverBehaviors[i].createDataObject(tOldData[i]);
				}
			}
		} else {
			// no old data
			for (int i = 0; i < 6; i++) {
				if (mCoverBehaviors[i] == null) continue;
				if (nbt.hasKey(COVER_DATA_NBT_KEYS[i]))
					mCoverData[i] = mCoverBehaviors[i].createDataObject(nbt.getTag(COVER_DATA_NBT_KEYS[i]));
				else
					mCoverData[i] = mCoverBehaviors[i].createDataObject();
				if (mCoverBehaviors[i].isDataNeededOnClient(i, mCoverSides[i], mCoverData[i], this))
					issueCoverUpdate(i);
			}
		}
	}
	
	public abstract boolean isStillValid();
	
	protected boolean doCoverThings() {
		for (int i : ALL_VALID_SIDES) {
			if (getCoverIDAtSide(i) != 0) {
				final SA_CoverBehaviorBase<?> tCover = getCoverBehaviorAtSideNew(i);
				final int tCoverTickRate = tCover.getTickRate(i, getCoverIDAtSide(i), mCoverData[i], this);
				if (tCoverTickRate > 0 && mTickTimer % tCoverTickRate == 0) {
					final int tRedstone = tCover.isRedstoneSensitive(i, getCoverIDAtSide(i), mCoverData[i], this, mTickTimer) ? getInputRedStoneSignal(i) : 0;
					mCoverData[i] = tCover.doCoverThings(i, tRedstone, getCoverIDAtSide(i), mCoverData[i], this, mTickTimer);
					if (!isStillValid()) return false;
				}
			}
		}
		return true;
	}
	
	public abstract boolean allowCoverOnSide(int side, ItemStackData coverId);
	
	protected void checkDropCover() {
		for (int i : ALL_VALID_SIDES)
			if (getCoverIDAtSide(i) != 0)
				if (!allowCoverOnSide(i, new ItemStackData(getCoverIDAtSide(i))))
					dropCover(i, i, true);
	}
	
	protected void updateCoverBehavior() {
		for (int i : ALL_VALID_SIDES)
			mCoverBehaviors[i] = API.getCoverBehaviorNew(mCoverSides[i]);
	}
	
	@Override
	public void issueCoverUpdate(int side) {
		// If we've got a null worldObj we're getting called as a part of readingNBT from a non tickable MultiTileEntity on chunk load before the world is set
		// so we'll want to send a cover update.
		if (worldObj == null || (isServerSide() && getCoverBehaviorAtSideNew(side).isDataNeededOnClient(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this)))
			mCoverNeedUpdate[side] = true;
	}
	
	public final ITexture getCoverTexture(int side) {
		if (getCoverIDAtSide(side) == 0) return null;
		if (proxy.isClientSide() && (ClientEvents.hideValue & 0x1) != 0) {
			return Textures.BlockIcons.HIDDEN_TEXTURE[0]; // See through
		}
		final ITexture coverTexture = getCoverBehaviorAtSideNew(side).getSpecialCoverTexture(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this);
		return coverTexture != null ? coverTexture : API.sCovers.get(new ItemStackData(getCoverIDAtSide(side)));
	}
	
	protected void requestCoverDataIfNeeded() {
		if (worldObj == null || !worldObj.isRemote) return;
		for (int i : ALL_VALID_SIDES) {
			if (getCoverBehaviorAtSideNew(i).isDataNeededOnClient(i, getCoverIDAtSide(i), getComplexCoverDataAtSide(i), this))
				NETWORK.sendToServer(new Packet_RequestCoverData(i, getCoverIDAtSide(i), this));
		}
	}
	
	@Override
	public void setCoverIdAndDataAtSide(int side, int id, ISerializableObject aData) {
		if (setCoverIDAtSideNoUpdate(side, id)) {
			setCoverDataAtSide(side, aData);
			issueCoverUpdate(side);
			issueBlockUpdate();
		}
	}
	
	@Override
	public void setCoverIDAtSide(int side, int id) {
		if (setCoverIDAtSideNoUpdate(side, id)) {
			issueCoverUpdate(side);
			issueBlockUpdate();
		}
	}
	
	@Override
	public boolean setCoverIDAtSideNoUpdate(int side, int id) {
		if (side >= 0 && side < 6 && mCoverSides[side] != id) {
			if (id == 0 && isClientSide())
				mCoverBehaviors[side].onDropped(side, mCoverSides[side], mCoverData[side], this);
			mCoverSides[side]     = id;
			mCoverBehaviors[side] = API.getCoverBehaviorNew(id);
			mCoverData[side]      = mCoverBehaviors[side].createDataObject();
			return true;
		}
		return false;
	}
	
	@Override
	public void setCoverDataAtSide(int side, ISerializableObject aData) {
		if (side >= 0 && side < 6 && getCoverBehaviorAtSideNew(side) != null && getCoverBehaviorAtSideNew(side).cast(aData) != null)
			mCoverData[side] = aData;
	}
	
	@Override
	public void setCoverItemAtSide(int side, ItemStack aCover) {
		API.getCoverBehaviorNew(aCover).placeCover(side, aCover, this);
	}
	
	@Override
	public int getCoverIDAtSide(int side) {
		if (side >= 0 && side < 6) return mCoverSides[side];
		return 0;
	}
	
	@Override
	public ItemStack getCoverItemAtSide(int side) {
		return getCoverBehaviorAtSideNew(side).getDisplayStack(getCoverIDAtSide(side), getComplexCoverDataAtSide(side));
	}
	
	@Override
	public boolean canPlaceCoverIDAtSide(int side, int id) {
		return getCoverIDAtSide(side) == 0;
	}
	
	@Override
	public boolean canPlaceCoverItemAtSide(int side, ItemStack aCover) {
		return getCoverIDAtSide(side) == 0;
	}
	
	@Override
	public ISerializableObject getComplexCoverDataAtSide(int side) {
		if (side >= 0 && side < 6 && getCoverBehaviorAtSideNew(side) != null)
			return mCoverData[side];
		return API.sNoBehavior.createDataObject();
	}
	
	@Override
	public SA_CoverBehaviorBase<?> getCoverBehaviorAtSideNew(int side) {
		if (side >= 0 && side < 6)
			return mCoverBehaviors[side];
		return API.sNoBehavior;
	}
	
	@Override
	public boolean dropCover(int side, int aDroppedSide, boolean aForced) {
		if (getCoverBehaviorAtSideNew(side).onCoverRemoval(side, getCoverIDAtSide(side), mCoverData[side], this, aForced) || aForced) {
			final ItemStack tStack = getCoverBehaviorAtSideNew(side).getDrop(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this);
			if (tStack != null) {
				getCoverBehaviorAtSideNew(side).onDropped(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this);
				final EntityItem tEntity = new EntityItem(worldObj, getOffsetX(aDroppedSide, 1) + 0.5, getOffsetY(aDroppedSide, 1) + 0.5, getOffsetZ(aDroppedSide, 1) + 0.5, tStack);
				tEntity.motionX = 0;
				tEntity.motionY = 0;
				tEntity.motionZ = 0;
				worldObj.spawnEntityInWorld(tEntity);
			}
			setCoverIDAtSide(side, 0);
			updateOutputRedstoneSignal(side);
			
			return true;
		}
		return false;
	}
	
	@Override
	public void setOutputRedStoneSignal(int side, int aStrength) {
		aStrength = Math.min(Math.max(0, aStrength), 15);
		if (side >= 0 && side < 6 && mSidedRedstone[side] != aStrength) {
			mSidedRedstone[side] = aStrength;
			issueBlockUpdate();
		}
	}
	
	@Override
	public void setStrongOutputRedStoneSignal(int side, int aStrength) {
		mStrongRedstone |= (1 << side);
		setOutputRedStoneSignal(side, aStrength);
	}
	
	@Override
	public void setInternalOutputRedStoneSignal(int side, int aStrength) {
		if (!getCoverBehaviorAtSideNew(side).manipulatesSidedRedstoneOutput(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this))
			setOutputRedStoneSignal(side, aStrength);
	}
	
	@Override
	public boolean getRedStone() {
		return IntStream.range(1, 6).anyMatch(i -> getRedStone(i));
	}
	
	@Override
	public boolean getRedStone(int side) {
		return getInternalInputRedStoneSignal(side) > 0;
	}
	
	@Override
	public int getStrongestRedStone() {
		return IntStream.range(1, 6).map(i -> getInternalInputRedStoneSignal(i)).max().orElse(0);
	}
	
	@Override
	public int getStrongOutputRedStoneSignal(int side) {
		return side >= 0 && side < 6 && (mStrongRedstone & (1 << side)) != 0 ? (mSidedRedstone[side] & 15) : 0;
	}
	
	@Override
	public void setGenericRedstoneOutput(boolean aOnOff) {
		mRedstone = aOnOff;
	}
	
	@Override
	public int getInternalInputRedStoneSignal(int side) {
		return getCoverBehaviorAtSideNew(side).getRedstoneInput(side, getInputRedStoneSignal(side), getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this) & 15;
	}
	
	@Override
	public int getInputRedStoneSignal(int side) {
		return worldObj.getIndirectPowerLevelTo(getOffsetX(side, 1), getOffsetY(side, 1), getOffsetZ(side, 1), side) & 15;
	}
	
	@Override
	public int getOutputRedStoneSignal(int side) {
		return getCoverBehaviorAtSideNew(side).manipulatesSidedRedstoneOutput(side, getCoverIDAtSide(side), getComplexCoverDataAtSide(side), this) ? mSidedRedstone[side] : getSideRedStone(side);
	}
	
	protected void updateOutputRedstoneSignal(int side) {
		setOutputRedStoneSignal(side, 0);
	}
	
	@Override
	public void receiveCoverData(int coverSide, int coverId, int coverData) {
		if ((coverSide >= 0 && coverSide < 6))
			setCoverIDAtSideNoUpdate(coverSide, coverId);
	}
	
	@Override
	public void receiveCoverData(int coverSide, int coverId, ISerializableObject coverData, EntityPlayerMP player) {
		if ((coverSide >= 0 && coverSide < 6)) {
			setCoverIDAtSideNoUpdate(coverSide, coverId);
			setCoverDataAtSide(coverSide, coverData);
			if (isClientSide()) {
				getCoverBehaviorAtSideNew(coverSide).onDataChanged(coverSide, coverId, coverData, this);
			}
		}
	}
	
	protected void sendCoverDataIfNeeded() {
		if (worldObj == null || worldObj.isRemote) return;
		final int mCoverNeedUpdateLength = mCoverNeedUpdate.length;
		for (int i = 0; i < mCoverNeedUpdateLength; i++) {
			if (mCoverNeedUpdate[i]) {
				NETWORK.sendPacketToAllPlayersInRange(
						worldObj,
						new Packet_SendCoverData(i, getCoverIDAtSide(i), getComplexCoverDataAtSide(i), this),
						xCoord, zCoord
				);
				mCoverNeedUpdate[i] = false;
			}
		}
	}
	
	@Override
	public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		final NBTTagCompound tag = accessor.getNBTData();
		final int side = accessor.getSide().ordinal();
		
		final int[] coverSides = tag.getIntArray("mCoverSides");
		// Not all data is available on the client, so get it from the NBT packet
		if (coverSides != null && coverSides.length == 6 && coverSides[side] != 0) {
			final int coverId = coverSides[side];
			final SA_CoverBehaviorBase<?> behavior = API.getCoverBehaviorNew(coverId);
			if (behavior != null && behavior != API.sNoBehavior) {
				if (tag.hasKey(CoverableTileEntity.COVER_DATA_NBT_KEYS[side])) {
					final ISerializableObject dataObject = behavior.createDataObject(tag.getTag(CoverableTileEntity.COVER_DATA_NBT_KEYS[side]));
					final ItemStack coverStack = behavior.getDisplayStack(coverId, dataObject);
					if (coverStack != null) currentTip.add(String.format("Cover: %s", coverStack.getDisplayName()));
					final String behaviorDesc = behavior.getDescription(side, coverId, dataObject, null);
					if (!Objects.equals(behaviorDesc, "")) currentTip.add(behaviorDesc);
				}
			}
		}
		
		// No super implementation
		// super.getWailaBody(itemStack, currentTip, accessor, config);
	}
	
	@Override
	public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z) {
		// No super implementation
		// super.getWailaNBTData(player, tile, tag, world, x, y, z);
		
		// While we have some cover data on the client (enough to render it); we don't have all the information we want, such as
		// details on the fluid filter, so send it all here.
		writeCoverNBT(tag, false);
	}
}
