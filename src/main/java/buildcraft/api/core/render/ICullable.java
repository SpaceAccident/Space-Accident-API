package buildcraft.api.core.render;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICullable {
	void setRenderSide(ForgeDirection var1, boolean var2);
	
	void setRenderAllSides();
	
	boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5);
	
	void setRenderMask(int var1);
}
