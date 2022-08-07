package buildcraft.api.core.render;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public interface ITextureStates extends ICullable {
	ITextureStateManager getTextureState();
	
	IIcon getIcon(int var1, int var2);
	
	Block getBlock();
}
