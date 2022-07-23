package ic2.api.energy.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyEmitter extends IEnergyTile {
	boolean emitsEnergyTo(TileEntity var1, ForgeDirection var2);
}