package space.accident.api.interfaces;

import net.minecraft.block.Block;

public interface IBlockContainer {
	Block getBlock();
	int getMeta();
}