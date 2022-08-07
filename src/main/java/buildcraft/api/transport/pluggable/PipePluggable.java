package buildcraft.api.transport.pluggable;


import buildcraft.api.core.INBTStoreable;
import buildcraft.api.core.ISerializable;
import buildcraft.api.transport.IPipeTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class PipePluggable implements INBTStoreable, ISerializable {
	public PipePluggable() {
	}
	
	public abstract ItemStack[] getDropItems(IPipeTile var1);
	
	public void update(IPipeTile pipe, ForgeDirection direction) {
	}
	
	public void onAttachedPipe(IPipeTile pipe, ForgeDirection direction) {
		this.validate(pipe, direction);
	}
	
	public void onDetachedPipe(IPipeTile pipe, ForgeDirection direction) {
		this.invalidate();
	}
	
	public abstract boolean isBlocking(IPipeTile var1, ForgeDirection var2);
	
	public void invalidate() {
	}
	
	public void validate(IPipeTile pipe, ForgeDirection direction) {
	}
	
	public boolean isSolidOnSide(IPipeTile pipe, ForgeDirection direction) {
		return false;
	}
	
	public abstract AxisAlignedBB getBoundingBox(ForgeDirection var1);
	
	@SideOnly(Side.CLIENT)
	public abstract IPipePluggableRenderer getRenderer();
	
	@SideOnly(Side.CLIENT)
	public IPipePluggableDynamicRenderer getDynamicRenderer() {
		return null;
	}
	
	public boolean requiresRenderUpdate(PipePluggable old) {
		return true;
	}
}