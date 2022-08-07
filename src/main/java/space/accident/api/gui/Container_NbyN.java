package space.accident.api.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import space.accident.api.interfaces.tileentity.ITile;

/**
 * Max n = 8, min n = 1
 */
public class Container_NbyN extends ContainerMetaTile_Machine {
    
    int countSlots;
    
    public Container_NbyN(InventoryPlayer aInventoryPlayer, ITile aTileEntity, int n) {
        super(aInventoryPlayer, aTileEntity, 9, n == 64 ? 72 : n == 49 ? 54 : n == 36 ? 36 : n == 25 ? 18 : 0, false);
        this.countSlots = n == 64 ? 8 : n == 49 ? 7 : n == 36 ? 6 : n == 25 ? 5 : n == 16 ? 4 : n == 9 ? 3 : n == 4 ? 2 : 1;
        
        if (mTileEntity != null && mTileEntity.getMetaTile() != null) {
            addSlots(aInventoryPlayer);
            if (doesBindPlayerInventory()) bindPlayerInventory(aInventoryPlayer);
            detectAndSendChanges();
        } else {
            aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
        }
    }
    
    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        int xOffset = 0;
        int yOffset = 0;
        switch (countSlots) {
            case 1:
                xOffset = 89;
                yOffset = 35;
                break;
            case 2:
                xOffset = 80;
                yOffset = 26;
                break;
            case 3:
                xOffset = 71;
                yOffset = 17;
                break;
            case 4:
                xOffset = 62;
                yOffset = 8;
                break;
            case 5:
                xOffset = 53;
                yOffset = 8;
                break;
            case 6:
                xOffset = 44;
                yOffset = 8;
                break;
            case 7:
                xOffset = 35;
                yOffset = 8;
                break;
            case 8:
                xOffset = 26;
                yOffset = 8;
                break;
        }
        for (int x = 0; x < countSlots; x++) {
            for (int y = 0; y < countSlots; y++) {
                int xx = xOffset + x * 18;
                int yy = yOffset + y * 18;
                addSlotToContainer(new Slot(mTileEntity, countSlots * x + y, xx, yy));
            }
        }
    }
    
    @Override
    public int getSlotCount() {
        return countSlots * countSlots;
    }
    
    @Override
    public int getShiftClickSlotCount() {
        return countSlots * countSlots;
    }
}
