package space.accident.api.objects;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.util.SpaceLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static space.accident.api.API.mServerStarted;
import static space.accident.api.API.sFluidMappings;
import static space.accident.api.util.Utility.reMap;

/**
 * Because Forge fucked this one up royally.
 */
public class FluidStackData extends FluidStack {
	private static final Collection<FluidStackData> sAllFluidStacks = new ArrayList<FluidStackData>(5000);
	private static volatile boolean lock = false;
	private final Fluid mFluid;
	
	public FluidStackData(Fluid aFluid, int amount) {
		super(aFluid, amount);
		mFluid = aFluid;
		if (!mServerStarted) {
			sAllFluidStacks.add(this);
		}
	}
	
	public FluidStackData(FluidStack aFluid) {
		this(aFluid.getFluid(), aFluid.amount);
	}
	
	public static final synchronized void fixAllThoseFuckingFluidIDs() {
		if (ForgeVersion.getBuildVersion() < 1355 && ForgeVersion.getRevisionVersion() < 4) {
			try {
				while (lock) {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
			lock = true;
			for (FluidStackData tFluid : sAllFluidStacks) tFluid.fixFluidIDForFucksSake();
			try {
				for (Map<Fluid, ?> tMap : sFluidMappings)
					reMap(tMap);
			} catch (Throwable e) {
				e.printStackTrace(SpaceLog.err);
			}
			lock = false;
		}
	}
	
	public final void fixFluidIDForFucksSake() {
		if (ForgeVersion.getBuildVersion() < 1355 && ForgeVersion.getRevisionVersion() < 4) {
			int fluidID;
			try {
				fluidID = this.getFluid().getID();
			} catch (Throwable e) {
				System.err.println(e);
			}
			try {
				fluidID = mFluid.getID();
			} catch (Throwable e) {
				fluidID = -1;
			}
		}
	}
	
	@Override
	public FluidStack copy() {
		if (ForgeVersion.getBuildVersion() < 1355 && ForgeVersion.getRevisionVersion() < 4) {
			fixFluidIDForFucksSake();
		}
		return new FluidStackData(this);
	}
	
	@Override
	public String toString() {
		return String.format("FluidStackData: %s x %s, ID:%s", this.amount, this.getFluid().getName(), this.getFluidID());
	}
}