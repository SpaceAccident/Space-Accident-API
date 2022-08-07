package buildcraft.api.transport.pluggable;

import buildcraft.api.core.render.ITextureStates;
import buildcraft.api.transport.IPipe;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPipePluggableRenderer {
	void renderPluggable(RenderBlocks var1, IPipe var2, ForgeDirection var3, PipePluggable var4, ITextureStates var5, int var6, int var7, int var8, int var9);
}