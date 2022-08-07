package space.accident.api.metatileentity.implementations.hathes;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import space.accident.api.API;
import space.accident.api.gui.Container_Hatch_Output_Fluid;
import space.accident.api.gui.GUIContainer_OutputHatch;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.render.TextureFactory;
import space.accident.extensions.StringUtils;

import static space.accident.api.enums.Textures.BlockIcons.FLUID_OUT_SIGN;
import static space.accident.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;
import static space.accident.api.util.Utility.getFluidDisplayStack;
import static space.accident.api.util.Utility.getFluidFromUnlocalizedName;
import static space.accident.extensions.NumberUtils.format;
import static space.accident.extensions.PlayerUtils.sendChat;
import static space.accident.extensions.StringUtils.trans;

public class Hatch_Output_Fluid extends HatchBase {
	public int mMode = 0;
	private String lockedFluidName = null;
	private EntityPlayer playerThatLockedfluid = null;
	
	public Hatch_Output_Fluid(int id, String aNameRegional, int aTier) {
		super(id, "hatch.output.tier." + aTier, aNameRegional, aTier, 4, StringUtils.array(
						"Fluid Output for Multiblocks",
						"Capacity: " + format(8000 * (1 << aTier)) + "L",
						"Right click with screwdriver to restrict output",
						"Can be restricted to put out Items and/or Steam/No Steam/1 specific Fluid",
						"Restricted Output Hatches are given priority for Multiblock Fluid output"
				)
		);
	}
	
	public Hatch_Output_Fluid(String name, int aTier, String aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 4, aDescription, aTextures);
	}
	
	public Hatch_Output_Fluid(String name, int aTier, String[] aDescription, ITexture[][][] aTextures) {
		super(name, aTier, 4, aDescription, aTextures);
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(FLUID_OUT_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		return API.mRenderIndicatorsOnHatch ?
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT), TextureFactory.of(FLUID_OUT_SIGN)} :
				new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
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
	public boolean isLiquidInput(int side) {
		return false;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Output_Fluid(mName, mTier, mDescriptionArray, mTextures);
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player) {
		if (baseTile.isClientSide()) return true;
		baseTile.openGUI(player);
		return true;
	}
	
	@Override
	public void onPostTick(ITile baseTile, long tick) {
		super.onPostTick(baseTile, tick);
		if (baseTile.isServerSide() && baseTile.isAllowedToWork() && mFluid != null) {
			IFluidHandler tTileEntity = baseTile.getITankContainerAtSide(baseTile.getFrontFace());
			if (tTileEntity != null) {
				FluidStack tDrained = baseTile.drain(ForgeDirection.getOrientation(baseTile.getFrontFace()), Math.max(1, mFluid.amount), false);
				if (tDrained != null) {
					int tFilledAmount = tTileEntity.fill(ForgeDirection.getOrientation(baseTile.getBackFace()), tDrained, false);
					if (tFilledAmount > 0) {
						tTileEntity.fill(ForgeDirection.getOrientation(baseTile.getBackFace()), baseTile.drain(ForgeDirection.getOrientation(baseTile.getFrontFace()), tFilledAmount, true), true);
					}
				}
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("mMode", mMode);
		if (lockedFluidName != null && lockedFluidName.length() != 0) nbt.setString("lockedFluidName", lockedFluidName);
		else nbt.removeTag("lockedFluidName");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		mMode           = nbt.getInteger("mMode");
		lockedFluidName = nbt.getString("lockedFluidName");
		lockedFluidName = lockedFluidName.length() == 0 ? null : lockedFluidName;
		if (getFluidFromUnlocalizedName(lockedFluidName) != null) {
			lockedFluidName = getFluidFromUnlocalizedName(lockedFluidName).getName();
		}
	}
	
	@Override
	public boolean doesFillContainers() {
		return true;
	}
	
	@Override
	public boolean doesEmptyContainers() {
		return false;
	}
	
	@Override
	public boolean canTankBeFilled() {
		return true;
	}
	
	@Override
	public boolean canTankBeEmptied() {
		return true;
	}
	
	@Override
	public boolean displaysItemStack() {
		return true;
	}
	
	@Override
	public boolean displaysStackSize() {
		return false;
	}
	
	@Override
	public void updateFluidDisplayItem() {
		super.updateFluidDisplayItem();
		if (lockedFluidName == null || mMode < 8) mInventory[3] = null;
		else {
			FluidStack tLockedFluid = FluidRegistry.getFluidStack(lockedFluidName, 1);
			// Because getStackDisplaySlot() only allow return one int, this place I only can manually set.
			if (tLockedFluid != null) {
				mInventory[3] = getFluidDisplayStack(tLockedFluid, false, true);
			} else {
				mInventory[3] = null;
			}
		}
	}
	
	@Override
	public boolean isValidSlot(int index) {
		// Because getStackDisplaySlot() only allow return one int, this place I only can manually set.
		return index != getStackDisplaySlot() && index != 3;
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new Container_Hatch_Output_Fluid(aPlayerInventory, baseTile);
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		return new GUIContainer_OutputHatch(aPlayerInventory, baseTile, getLocalName());
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == baseTile.getFrontFace() && index == 1;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return side == baseTile.getFrontFace() && index == 0;
	}
	
	@Override
	public int getCapacity() {
		return 8000 * (1 << mTier);
	}
	
	@Override
	public void onScrewdriverRightClick(int side, EntityPlayer player, float x, float y, float z) {
		if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).isGUIClickable(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), getBaseMetaTileEntity()))
			return;
		if (player.isSneaking()) {
			mMode = (mMode + 9) % 10;
		} else {
			mMode = (mMode + 1) % 10;
		}
		String inBrackets;
		switch (mMode) {
			case 0:
				sendChat(player, trans("108", "Outputs misc. Fluids, Steam and Items"));
				this.setLockedFluidName(null);
				break;
			case 1:
				sendChat(player, trans("109", "Outputs Steam and Items"));
				this.setLockedFluidName(null);
				break;
			case 2:
				sendChat(player, trans("110", "Outputs Steam and misc. Fluids"));
				this.setLockedFluidName(null);
				break;
			case 3:
				sendChat(player, trans("111", "Outputs Steam"));
				this.setLockedFluidName(null);
				break;
			case 4:
				sendChat(player, trans("112", "Outputs misc. Fluids and Items"));
				this.setLockedFluidName(null);
				break;
			case 5:
				sendChat(player, trans("113", "Outputs only Items"));
				this.setLockedFluidName(null);
				break;
			case 6:
				sendChat(player, trans("114", "Outputs only misc. Fluids"));
				this.setLockedFluidName(null);
				break;
			case 7:
				sendChat(player, trans("115", "Outputs nothing"));
				this.setLockedFluidName(null);
				break;
			case 8:
				playerThatLockedfluid = player;
				if (mFluid == null) {
					this.setLockedFluidName(null);
					inBrackets = trans("115.3", "currently none, will be locked to the next that is put in (or use fluid cell to lock)");
				} else {
					this.setLockedFluidName(this.getDrainableStack().getFluid().getName());
					inBrackets = this.getDrainableStack().getLocalizedName();
				}
				sendChat(player, String.format("%s (%s)", trans("151.1", "Outputs items and 1 specific Fluid"), inBrackets));
				break;
			case 9:
				playerThatLockedfluid = player;
				if (mFluid == null) {
					this.setLockedFluidName(null);
					inBrackets = trans("115.3", "currently none, will be locked to the next that is put in (or use fluid cell to lock)");
				} else {
					this.setLockedFluidName(this.getDrainableStack().getFluid().getName());
					inBrackets = this.getDrainableStack().getLocalizedName();
				}
				sendChat(player, String.format("%s (%s)", trans("151.2", "Outputs 1 specific Fluid"), inBrackets));
				break;
		}
	}
	
	private boolean tryToLockHatch(EntityPlayer player, int side) {
		if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(side).isGUIClickable(side, getBaseMetaTileEntity().getCoverIDAtSide(side), getBaseMetaTileEntity().getComplexCoverDataAtSide(side), getBaseMetaTileEntity()))
			return false;
		if (!isFluidLocked())
			return false;
		ItemStack tCurrentItem = player.inventory.getCurrentItem();
		if (tCurrentItem == null)
			return false;
		FluidStack tFluid = FluidContainerRegistry.getFluidForFilledItem(tCurrentItem);
		if (tFluid == null && tCurrentItem.getItem() instanceof IFluidContainerItem)
			tFluid = ((IFluidContainerItem) tCurrentItem.getItem()).getFluid(tCurrentItem);
		if (tFluid != null) {
			if (getLockedFluidName() != null && !getLockedFluidName().equals(tFluid.getFluid().getName())) {
				sendChat(player, String.format("%s %s", trans(
								"151.3",
								"Hatch is locked to a different fluid. To change the locking, empty it and made it locked to the next fluid with a screwdriver. Currently locked to"
						)
						, StatCollector.translateToLocal(getLockedFluidName())));
			} else {
				setLockedFluidName(tFluid.getFluid().getName());
				if (mMode == 8)
					sendChat(player, String.format("%s (%s)", trans("151.1", "Outputs items and 1 specific Fluid"), tFluid.getLocalizedName()));
				else
					sendChat(player, String.format("%s (%s)", trans("151.2", "Outputs 1 specific Fluid"), tFluid.getLocalizedName()));
			}
			return true;
		}
		return false;
	}
	
	public int getMode() {
		return mMode;
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player, int side, float x, float y, float z) {
		if (tryToLockHatch(player, side))
			return true;
		return super.onRightclick(baseTile, player, side, x, y, z);
	}
	
	public boolean outputsSteam() {
		return mMode < 4;
	}
	
	public boolean outputsLiquids() {
		return mMode % 2 == 0 || mMode == 9;
	}
	
	public boolean outputsItems() {
		return mMode % 4 < 2 && mMode != 9;
	}
	
	public boolean isFluidLocked() {
		return mMode == 8 || mMode == 9;
	}
	
	public String getLockedFluidName() {
		return lockedFluidName;
	}
	
	public void setLockedFluidName(String lockedFluidName) {
		this.lockedFluidName = lockedFluidName;
	}
	
	@Override
	public int getTankPressure() {
		return +100;
	}
	
	@Override
	public void onEmptyingContainerWhenEmpty() {
		if (this.lockedFluidName == null && this.mFluid != null) {
			this.setLockedFluidName(this.mFluid.getFluid().getName());
			sendChat(playerThatLockedfluid, String.format(trans("151.4", "Sucessfully locked Fluid to %s"), mFluid.getLocalizedName()));
		}
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
	@Override
	public String[] getInfoData() {
		return new String[]{
				EnumChatFormatting.BLUE + "Output Hatch" + EnumChatFormatting.RESET,
				"Stored Fluid:",
				EnumChatFormatting.GOLD + (mFluid == null ? "No Fluid" : mFluid.getLocalizedName()) + EnumChatFormatting.RESET,
				EnumChatFormatting.GREEN + format(mFluid == null ? 0 : mFluid.amount) + " L" + EnumChatFormatting.RESET + " " +
						EnumChatFormatting.YELLOW + format(getCapacity()) + " L" + EnumChatFormatting.RESET,
				(!isFluidLocked() || lockedFluidName == null) ? "Not Locked" : ("Locked to " + StatCollector.translateToLocal(FluidRegistry.getFluidStack(lockedFluidName, 1).getUnlocalizedName()))
		};
	}
}
