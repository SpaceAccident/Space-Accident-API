package space.accident.api.metatileentity.implementations.hathes;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import space.accident.api.API;
import space.accident.api.gui.Container_NbyN;
import space.accident.api.gui.GUIContainer_NbyN;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tileentity.IHasInventory;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.render.TextureFactory;

import static space.accident.api.enums.Textures.BlockIcons.ITEM_OUT_SIGN;
import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;
import static space.accident.api.util.Utility.moveMultipleItemStacks;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.StringUtils.array;

public class Hatch_Output_Item extends HatchBase {
	
	public Hatch_Output_Item(int id, String aNameRegional, int tier) {
		this(id, aNameRegional, tier, getSlots(tier));
	}
	
	public Hatch_Output_Item(int id, String nameRegional, int tier, int slots) {
		super(id, "hatch.output_bus.tier." + tier, nameRegional, tier, slots, array("Item Output for Multiblocks", "Capacity: " + getSlots(tier) + " stack" + (getSlots(tier) >= 2 ? "s" : "")));
	}
	
	public Hatch_Output_Item(int id, String aNameRegional, int tier, String[] aDescription) {
		super(id, "hatch.output_bus.tier." + tier, aNameRegional, tier, getSlots(tier), aDescription);
	}
	
	public Hatch_Output_Item(int id, String aNameRegional, int tier, String[] aDescription, int inventorySize) {
		super(id, "hatch.output_bus.tier." + tier, aNameRegional, tier, inventorySize, aDescription);
	}
	
	@Deprecated
	// having too many constructors is bad, don't be so lazy, use Hatch_Output_Item(String, int, String[], ITexture[][][])
	public Hatch_Output_Item(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		this(name, aTier, getSlots(aTier), array(aDescription), aTextures);
	}
	
	public Hatch_Output_Item(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, getSlots(aTier), aDescription, aTextures);
	}
	
	public Hatch_Output_Item(String name, int tier, int slots, String[] description, ITexture[][][] textures) {
		super(name, tier, slots, description, textures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ? new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(ITEM_OUT_SIGN)} : new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ? new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(ITEM_OUT_SIGN)} : new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	@Override
	public boolean isSimpleMachine() {
		return true;
	}
	
	@Override
	public boolean isFacingValid(int face) {
		return true;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer player) {
		return true;
	}
	
	@Override
	public boolean isValidSlot(int index) {
		return true;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Output_Item(mName, mTier, mInventory.length, mDescriptionArray, mTextures);
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		if (baseTile.isClientSide()) return true;
		baseTile.openGUI(player);
		return true;
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new Container_NbyN(aPlayerInventory, baseTile, mInventory.length);
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new GUIContainer_NbyN(aPlayerInventory, baseTile, mInventory.length);
	}
	
	/**
	 * Attempt to store as many items as possible into the internal inventory of this output bus.
	 * If you need atomicity you should use {@link IHasInventory#addStackToSlot(int, ItemStack)}
	 *
	 * @param stack Assume valid.
	 *               Will be mutated.
	 *               Take over the ownership. Caller should not retain a reference to this stack if the call returns true.
	 * @return true if stack is fully accepted. false is stack is partially accepted or nothing is accepted
	 */
	public boolean storeAll(ItemStack stack) {
		markDirty();
		for (int i = 0, mInventoryLength = mInventory.length; i < mInventoryLength && stack.stackSize > 0; i++) {
			ItemStack tSlot = mInventory[i];
			if (isStackInvalid(tSlot)) {
				if (stack.stackSize <= getInventoryStackLimit()) {
					mInventory[i] = stack;
					return true;
				}
				mInventory[i] = stack.splitStack(getInventoryStackLimit());
			} else {
				int tRealStackLimit = Math.min(getInventoryStackLimit(), tSlot.getMaxStackSize());
				if (tSlot.stackSize < tRealStackLimit && tSlot.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(tSlot, stack)) {
					if (stack.stackSize + tSlot.stackSize <= tRealStackLimit) {
						mInventory[i].stackSize += stack.stackSize;
						return true;
					} else {
						// more to serve
						stack.stackSize -= tRealStackLimit - tSlot.stackSize;
						mInventory[i].stackSize = tRealStackLimit;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == baseTile.getFrontFace();
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		if (baseTile.isServerSide() && baseTile.isAllowedToWork() && (tick & 0x7) == 0) {
			IInventory tTileEntity = baseTile.getIInventoryAtSide(baseTile.getFrontFace());
			if (tTileEntity != null) {
				moveMultipleItemStacks(baseTile, tTileEntity, baseTile.getFrontFace(), baseTile.getBackFace(), null, false, 64, 1, 64, 1, mInventory.length);
				for (int i = 0; i < mInventory.length; i++)
					if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
			}
		}
	}
}
