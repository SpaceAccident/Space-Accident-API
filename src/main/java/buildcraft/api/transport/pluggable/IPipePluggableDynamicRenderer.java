package buildcraft.api.transport.pluggable;

import buildcraft.api.transport.IPipe;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPipePluggableDynamicRenderer {
	void renderPluggable(IPipe var1, ForgeDirection var2, PipePluggable var3, double var4, double var6, double var8);
}