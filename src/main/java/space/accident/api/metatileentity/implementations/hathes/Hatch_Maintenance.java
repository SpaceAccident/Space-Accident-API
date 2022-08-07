package space.accident.api.metatileentity.implementations.hathes;

import ic2.core.item.ItemToolbox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import space.accident.api.gui.Container_MaintenanceHatch;
import space.accident.api.gui.GUIContainer_MaintenanceHatch;
import space.accident.api.interfaces.ITexture;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.interfaces.tools.IMaintenanceBox;
import space.accident.api.metatileentity.base.MetaTileEntity;
import space.accident.api.metatileentity.base.HatchBase;
import space.accident.api.render.TextureFactory;

import static space.accident.api.enums.Textures.BlockIcons.*;

public class Hatch_Maintenance extends HatchBase {
	public boolean isNeedMaintenance, noMaintenance;
	
	
	public Hatch_Maintenance(int id, String aNameRegional, boolean aAuto) {
		super(id, "hatch.maintenance." +( aAuto ? 1 : 5), aNameRegional, aAuto ? 1 : 5, 4, "For automatically maintaining Multiblocks");
		noMaintenance = aAuto;
	}
	
	public Hatch_Maintenance(String name, int aTier, String[] aDescription, ITexture[][][] aTextures, boolean aAuto) {
		super(name, aTier, 1, aDescription, aTextures);
		noMaintenance = aAuto;
	}
	
	@Override
	public String[] getDescription() {
		String[] desc;
		if (noMaintenance) {
			desc = new String[mDescriptionArray.length + 3];
			System.arraycopy(mDescriptionArray, 0, desc, 0, mDescriptionArray.length);
			desc[mDescriptionArray.length] = "4 Ducttape, 2 Lubricant Cells";
			desc[mDescriptionArray.length + 1] = "4 Steel Screws, 2 HV Circuits";
			desc[mDescriptionArray.length + 2] = "For each autorepair";
		} else {
			desc = new String[mDescriptionArray.length + 1];
			System.arraycopy(mDescriptionArray, 0, desc, 0, mDescriptionArray.length);
			desc[mDescriptionArray.length] = "Cannot be shared between Multiblocks!";
		}
		return desc;
	}
	
	@Override
	public ITexture[] getTexturesActive(ITexture aBaseTexture) {
		if (noMaintenance) return new ITexture[]{
				aBaseTexture,
				TextureFactory.of(OVERLAY_AUTOMAINTENANCE_IDLE),
				TextureFactory.builder().addIcon(OVERLAY_AUTOMAINTENANCE_IDLE_GLOW).glow().build()};
		return new ITexture[]{
				aBaseTexture,
				TextureFactory.of(OVERLAY_MAINTENANCE)};
	}
	
	@Override
	public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
		if (noMaintenance) return new ITexture[]{
				aBaseTexture,
				TextureFactory.of(OVERLAY_AUTOMAINTENANCE),
				TextureFactory.builder().addIcon(OVERLAY_AUTOMAINTENANCE_GLOW).glow().build()};
		return new ITexture[]{
				aBaseTexture,
				TextureFactory.of(OVERLAY_MAINTENANCE),
				TextureFactory.of(OVERLAY_DUCTTAPE)};
	}
	
	@Override
	public void initDefaultModes(NBTTagCompound nbt) {
		getBaseMetaTileEntity().setActive(true);
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
		return noMaintenance;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(ITile aTileEntity) {
		return new Hatch_Maintenance(mName, mTier, mDescriptionArray, mTextures, noMaintenance);
	}
	
	@Override
	public boolean onRightclick(ITile baseTile, EntityPlayer player, int side, float x, float y, float z) {
		if (baseTile.isClientSide()) return true;
		if (side == baseTile.getFrontFace()) {
			if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemToolbox) {
				onToolClick(player.getCurrentEquippedItem(), player);
			}
		}
		return true;
	}
	
	@Override
	public Container getServerGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		if (noMaintenance) return null;
		return new Container_MaintenanceHatch(aPlayerInventory, baseTile);
	}
	
	@Override
	public GuiContainer getClientGUI(int id, InventoryPlayer aPlayerInventory, ITile baseTile) {
		if (noMaintenance) return null;
		return new GUIContainer_MaintenanceHatch(aPlayerInventory, baseTile);
	}
	
	public void onToolClick(ItemStack stack, EntityLivingBase player) {
		if (stack == null || player == null) return;
		
		if (stack.getItem() instanceof IMaintenanceBox) {
			((IMaintenanceBox) stack.getItem()).hasMaintenance(stack);
		}
	}
	
	@Override
	public boolean allowPullStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean allowPutStack(ITile baseTile, int index, int side, ItemStack stack) {
		return false;
	}
}