package space.accident.api.interfaces.tileentity;

import net.minecraftforge.fluids.FluidStack;

public interface IMultiFluidHatch {
	FluidStack[] getStoredFluid();
	boolean hasFluid(FluidStack aFluid);
}