package space.accident.api.interfaces.tileentity;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public interface ITileWailaProvider {
	default void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {}
	
	default void getWailaNBTData(final EntityPlayerMP player, final TileEntity tile, final NBTTagCompound tag, final World world, int x, int y, int z) {}
}