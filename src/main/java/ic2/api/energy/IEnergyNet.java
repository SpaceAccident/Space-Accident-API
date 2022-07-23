package ic2.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyNet {
	TileEntity getTileEntity(World var1, int var2, int var3, int var4);
	
	TileEntity getNeighbor(TileEntity var1, ForgeDirection var2);
	
	/** @deprecated */
	@Deprecated
	double getTotalEnergyEmitted(TileEntity var1);
	
	/** @deprecated */
	@Deprecated
	double getTotalEnergySunken(TileEntity var1);
	
	NodeStats getNodeStats(TileEntity var1);
	
	double getPowerFromTier(int var1);
	
	int getTierFromPower(double var1);
}
