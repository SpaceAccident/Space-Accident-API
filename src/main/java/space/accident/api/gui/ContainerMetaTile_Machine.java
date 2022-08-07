package space.accident.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import space.accident.api.interfaces.tileentity.ITile;

public class ContainerMetaTile_Machine extends Container {
	
	public int mActive = 0,
			mMaxProgressTime = 0,
			mProgressTime = 0,
			mEnergy = 0,
			mSteam = 0,
			mSteamStorage = 0,
			mStorage = 0,
			mOutput = 0,
			mInput = 0,
			mID = 0,
			mDisplayErrorCode = 0;
	private int oActive = 0,
			oMaxProgressTime = 0,
			oProgressTime = 0,
			oEnergy = 0,
			oSteam = 0,
			oSteamStorage = 0,
			oStorage = 0,
			oOutput = 0,
			oInput = 0,
			oID = 0,
			oDisplayErrorCode = 0;
	protected int mTimer = 0;
	
	
	public ContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, ITile aTileEntity) {
		super(aInventoryPlayer, aTileEntity);
		
		mTileEntity = aTileEntity;
		
		if (mTileEntity != null && mTileEntity.getMetaTile() != null) {
			addSlots(aInventoryPlayer);
			if (doesBindPlayerInventory())
				bindPlayerInventory(aInventoryPlayer);
			detectAndSendChanges();
		} else {
			aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
		}
	}
	
	public ContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, ITile aTileEntity, boolean doesBindInventory) {
		super(aInventoryPlayer, aTileEntity);
		mTileEntity = aTileEntity;
		
		if (mTileEntity != null && mTileEntity.getMetaTile() != null) {
			addSlots(aInventoryPlayer);
			if (doesBindPlayerInventory() && doesBindInventory)
				bindPlayerInventory(aInventoryPlayer);
			detectAndSendChanges();
		} else {
			aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
		}
	}
	
	public ContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, ITile aTileEntity, int aPlayerInventoryXOffset, int aPlayerInventoryYOffset, boolean initEvertyhing) {
		super(aInventoryPlayer, aTileEntity, aPlayerInventoryXOffset, aPlayerInventoryYOffset);
		
		mTileEntity = aTileEntity;
		
		if(initEvertyhing) {
			if (mTileEntity != null && mTileEntity.getMetaTile() != null) {
				addSlots(aInventoryPlayer);
				if (doesBindPlayerInventory()) bindPlayerInventory(aInventoryPlayer);
				detectAndSendChanges();
			} else {
				aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
			}
		}
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (mTileEntity.isClientSide() || mTileEntity.getMetaTile() == null)
			return;
		mStorage = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getEUCapacity());
		mEnergy = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getStoredEU());
		mSteamStorage = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getSteamCapacity());
		mSteam = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getStoredSteam());
		mOutput = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getOutputVoltage());
		mInput = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getInputVoltage());
		mDisplayErrorCode = mTileEntity.getErrorDisplayID();
		mProgressTime = mTileEntity.getProgress();
		mMaxProgressTime = mTileEntity.getMaxProgress();
		mActive = mTileEntity.isActive() ? 1 : 0;
		mTimer++;
		
		for (Object crafter : this.crafters) {
			ICrafting player = (ICrafting) crafter;
			if (mTimer % 500 == 10 || oEnergy != mEnergy) {
				player.sendProgressBarUpdate(this, 0, mEnergy & 65535);
				player.sendProgressBarUpdate(this, 1, mEnergy >>> 16);
			}
			if (mTimer % 500 == 10 || oStorage != mStorage) {
				player.sendProgressBarUpdate(this, 2, mStorage & 65535);
				player.sendProgressBarUpdate(this, 3, mStorage >>> 16);
			}
			if (mTimer % 500 == 10 || oOutput != mOutput) {
				player.sendProgressBarUpdate(this, 4, mOutput);
			}
			if (mTimer % 500 == 10 || oInput != mInput) {
				player.sendProgressBarUpdate(this, 5, mInput);
			}
			if (mTimer % 500 == 10 || oDisplayErrorCode != mDisplayErrorCode) {
				player.sendProgressBarUpdate(this, 6, mDisplayErrorCode);
			}
			if (mTimer % 500 == 10 || oProgressTime != mProgressTime) {
				player.sendProgressBarUpdate(this, 11, mProgressTime & 65535);
				player.sendProgressBarUpdate(this, 12, mProgressTime >>> 16);
			}
			if (mTimer % 500 == 10 || oMaxProgressTime != mMaxProgressTime) {
				player.sendProgressBarUpdate(this, 13, mMaxProgressTime & 65535);
				player.sendProgressBarUpdate(this, 14, mMaxProgressTime >>> 16);
			}
			if (mTimer % 500 == 10 || oID != mID) {
				player.sendProgressBarUpdate(this, 15, mID);
			}
			if (mTimer % 500 == 10 || oActive != mActive) {
				player.sendProgressBarUpdate(this, 16, mActive);
			}
			if (mTimer % 500 == 10 || oSteam != mSteam) {
				player.sendProgressBarUpdate(this, 17, mSteam & 65535);
				player.sendProgressBarUpdate(this, 18, mSteam >>> 16);
			}
			if (mTimer % 500 == 10 || oSteamStorage != mSteamStorage) {
				player.sendProgressBarUpdate(this, 19, mSteamStorage & 65535);
				player.sendProgressBarUpdate(this, 20, mSteamStorage >>> 16);
			}
		}
		
		oID = mID;
		oSteam = mSteam;
		oInput = mInput;
		oActive = mActive;
		oOutput = mOutput;
		oEnergy = mEnergy;
		oStorage = mStorage;
		oSteamStorage = mSteamStorage;
		oProgressTime = mProgressTime;
		oMaxProgressTime = mMaxProgressTime;
		oDisplayErrorCode = mDisplayErrorCode;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		switch (id) {
			case 0:
				mEnergy = mEnergy & 0xffff0000 | value & 0x0000ffff;
				break;
			case 1:
				mEnergy = mEnergy & 0x0000ffff | value << 16;
				break;
			case 2:
				mStorage = mStorage & 0xffff0000 | value & 0x0000ffff;
				break;
			case 3:
				mStorage = mStorage & 0x0000ffff | value << 16;
				break;
			case 4:
				mOutput = value;
				break;
			case 5:
				mInput = value;
				break;
			case 6:
				mDisplayErrorCode = value;
				break;
			case 11:
				mProgressTime = mProgressTime & 0xffff0000 | value;
				break;
			case 12:
				mProgressTime = mProgressTime & 0x0000ffff | value << 16;
				break;
			case 13:
				mMaxProgressTime = mMaxProgressTime & 0xffff0000 | value & 0x0000ffff;
				break;
			case 14:
				mMaxProgressTime = mMaxProgressTime & 0x0000ffff | value << 16;
				break;
			case 15:
				mID = value;
				break;
			case 16:
				mActive = value;
				break;
			case 17:
				mSteam = mSteam & 0xffff0000 | value & 0x0000ffff;
				break;
			case 18:
				mSteam = mSteam & 0x0000ffff | value << 16;
				break;
			case 19:
				mSteamStorage = mSteamStorage & 0xffff0000 | value & 0x0000ffff;
				break;
			case 20:
				mSteamStorage = mSteamStorage & 0x0000ffff | value << 16;
				break;
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return mTileEntity.isUseableByPlayer(player);
	}
}
