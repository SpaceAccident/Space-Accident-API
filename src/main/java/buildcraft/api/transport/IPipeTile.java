package buildcraft.api.transport;


import buildcraft.api.core.EnumColor;
import buildcraft.api.transport.pluggable.PipePluggable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPipeTile extends IInjectable {
	PipeType getPipeType();
	
	World getWorld();
	
	int x();
	
	int y();
	
	int z();
	
	boolean isPipeConnected(ForgeDirection var1);
	
	Block getNeighborBlock(ForgeDirection var1);
	
	TileEntity getNeighborTile(ForgeDirection var1);
	
	IPipe getNeighborPipe(ForgeDirection var1);
	
	IPipe getPipe();
	
	int getPipeColor();
	
	PipePluggable getPipePluggable(ForgeDirection var1);
	
	boolean hasPipePluggable(ForgeDirection var1);
	
	boolean hasBlockingPluggable(ForgeDirection var1);
	
	void scheduleNeighborChange();
	
	void scheduleRenderUpdate();
	
	int injectItem(ItemStack var1, boolean var2, ForgeDirection var3, EnumColor var4);
	
	/** @deprecated */
	@Deprecated
	int injectItem(ItemStack var1, boolean var2, ForgeDirection var3);
	
	public static enum PipeType {
		ITEM,
		FLUID,
		POWER,
		STRUCTURE;
		
		private PipeType() {
		}
	}
}