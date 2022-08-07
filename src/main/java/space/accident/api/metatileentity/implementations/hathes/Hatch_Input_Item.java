package space.accident.api.metatileentity.implementations.hathes;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import space.accident.api.API;
import space.accident.api.gui.Container_NbyN;
import space.accident.api.gui.GUIContainer_NbyN;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.render.TextureFactory;
import space.accident.api.util.OreDictUnifier;
import space.accident.api.util.SA_ClientPreference;
import space.accident.api.util.RecipeMap;
import space.accident.api.util.Utility.ItemId;
import space.accident.extensions.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static space.accident.api.enums.Textures.BlockIcons.ITEM_IN_SIGN;
import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_PIPE_IN;
import static space.accident.api.util.Utility.areStacksEqual;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.main.SpaceAccidentApi.proxy;

public class Hatch_Input_Item extends HatchBase {
	public RecipeMap mRecipeMap = null;
	public boolean disableSort;
	public boolean disableFilter = false;
	public boolean disableLimited = true;
	
	public Hatch_Input_Item(int id, String nameRegional, int tier) {
		this(id, nameRegional, tier, getSlots(tier));
	}
	
	public Hatch_Input_Item(int id, String nameRegional, int tier, int slots) {
		super(id, "hatch.input_bus.tier." + tier, nameRegional, tier, slots, StringUtils.array(
						"Item Input for Multiblocks",
						"Shift + right click with screwdriver to turn Sort mode on/off",
						"Capacity: " + slots + " stack" + (slots >= 2 ? "s" : "")
				)
		);
	}
	
	@Deprecated
	// having too many constructors is bad, don't be so lazy, use Hatch_Input_Item(String, int, String[], ITexture[][][])
	public Hatch_Input_Item(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		this(name, aTier, new String[]{aDescription}, aTextures);
	}
	
	public Hatch_Input_Item(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		this(name, aTier, getSlots(aTier), aDescription, aTextures);
	}
	
	public Hatch_Input_Item(String name, int aTier, int aSlots, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, aSlots, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN), TextureFactory.of(ITEM_IN_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN), TextureFactory.of(ITEM_IN_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_IN)};
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
		return new Hatch_Input_Item(mName, mTier, mInventory.length, mDescriptionArray, mTextures);
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
	public void initDefaultModes(NBTTagCompound nbt) {
		if (!getBaseMetaTileEntity().getWorld().isRemote) {
			SA_ClientPreference tPreference = proxy.getClientPreference(getBaseMetaTileEntity().getOwnerUuid());
			if (tPreference != null)
				disableFilter = !tPreference.isInputBusInitialFilterEnabled();
		}
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new GUIContainer_NbyN(aPlayerInventory, baseTile, mInventory.length);
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		if (baseTile.isServerSide() && baseTile.hasInventoryBeenModified()) {
			updateSlots();
		}
	}
	
	public void updateSlots() {
		for (int i = 0; i < mInventory.length; i++)
			if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
		if (!disableSort)
			fillStacksIntoFirstSlots();
	}
	
	protected void fillStacksIntoFirstSlots() {
		HashMap<ItemId, Integer> slots = new HashMap<>(mInventory.length);
		HashMap<ItemId, ItemStack> stacks = new HashMap<>(mInventory.length);
		List<ItemId> order = new ArrayList<>(mInventory.length);
		List<Integer> validSlots = new ArrayList<>(mInventory.length);
		for (int i = 0; i < mInventory.length; i++) {
			if (!isValidSlot(i))
				continue;
			validSlots.add(i);
			ItemStack s = mInventory[i];
			if (s == null)
				continue;
			ItemId sID = ItemId.createNoCopy(s);
			slots.merge(sID, s.stackSize, Integer::sum);
			if (!stacks.containsKey(sID))
				stacks.put(sID, s);
			order.add(sID);
			mInventory[i] = null;
		}
		int slotindex = 0;
		for (ItemId sID : order) {
			int toSet = slots.get(sID);
			if (toSet == 0)
				continue;
			int slot = validSlots.get(slotindex);
			slotindex++;
			mInventory[slot]           = stacks.get(sID).copy();
			toSet                      = Math.min(toSet, mInventory[slot].getMaxStackSize());
			mInventory[slot].stackSize = toSet;
			slots.merge(sID, toSet, (a, b) -> a - b);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("disableSort", disableSort);
		nbt.setBoolean("disableFilter", disableFilter);
		nbt.setBoolean("disableLimited", disableLimited);
		if (mRecipeMap != null)
			nbt.setString("recipeMap", mRecipeMap.mUniqueIdentifier);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		disableSort   = nbt.getBoolean("disableSort");
		disableFilter = nbt.getBoolean("disableFilter");
		if (nbt.hasKey("disableLimited"))
			disableLimited = nbt.getBoolean("disableLimited");
		mRecipeMap = RecipeMap.sIndexedMappings.getOrDefault(nbt.getString("recipeMap"), null);
	}
	
	@Override
	public void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z) {
		if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).isGUIClickable(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), getBaseMetaTileEntity()))
			return;
		if (player.isSneaking()) {
			if (disableSort) {
				disableSort = false;
			} else {
				if (disableLimited) {
					disableLimited = false;
				} else {
					disableSort    = true;
					disableLimited = true;
				}
			}
			sendChat(player, StatCollector.translateToLocal("hatch.disableSort." + disableSort) + "   " +
					StatCollector.translateToLocal("hatch.disableLimited." + disableLimited));
		} else {
			disableFilter = !disableFilter;
			sendChat(player, StatCollector.translateToLocal("hatch.disableFilter." + disableFilter));
		}
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == getBaseMetaTileEntity().getFrontFace();
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == getBaseMetaTileEntity().getFrontFace()
				&& (mRecipeMap == null || disableFilter || mRecipeMap.containsInput(stack))
				&& (disableLimited || limitedAllowPutStack(index, stack));
	}
	
	protected boolean limitedAllowPutStack(int index, ItemStack stack) {
		for (int i = 0; i < getSizeInventory(); i++)
			if (areStacksEqual(OreDictUnifier.get_nocopy(stack), mInventory[i]))
				return i == index;
		return mInventory[index] == null;
	}
}
