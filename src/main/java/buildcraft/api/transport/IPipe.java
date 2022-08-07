package buildcraft.api.transport;

import buildcraft.api.gates.IGate;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPipe {
	IPipeTile getTile();
	
	IGate getGate(ForgeDirection var1);
	
	boolean hasGate(ForgeDirection var1);
	
	boolean isWired(PipeWire var1);
	
	boolean isWireActive(PipeWire var1);
}