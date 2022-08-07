package space.accident.main.crossmod.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import space.accident.api.interfaces.tileentity.ITileWailaProvider;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.BaseMetaTileEntity;

import java.util.List;

public class WailaDataProvider implements IWailaDataProvider {
	
	public static void register() {
		FMLInterModComms.sendMessage("Waila", "register", WailaDataProvider.class.getName() + ".callbackRegister" );
	}
	
	@SuppressWarnings("unused")
	public static void callbackRegister(IWailaRegistrar register){
		final IWailaDataProvider multiBlockProvider = new WailaDataProvider();
		
		register.registerBodyProvider(multiBlockProvider, BaseMetaTileEntity.class);
		register.registerBodyProvider(multiBlockProvider, BaseMetaPipeEntity.class);
		
		register.registerNBTProvider(multiBlockProvider, BaseMetaTileEntity.class);
		register.registerNBTProvider(multiBlockProvider, BaseMetaPipeEntity.class);
		
		register.registerTailProvider(multiBlockProvider, BaseMetaTileEntity.class);
		register.registerTailProvider(multiBlockProvider, BaseMetaPipeEntity.class);
	}
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}
	
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		final TileEntity tile = accessor.getTileEntity();
		if(tile instanceof ITileWailaProvider) {
			((ITileWailaProvider)tile).getWailaBody(itemStack, currentTip, accessor, config);
		}
		return currentTip;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}
	
	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity tile, final NBTTagCompound tag, final World world, int x, int y, int z) {
		if(tile instanceof ITileWailaProvider) {
			((ITileWailaProvider)tile).getWailaNBTData(player, tile, tag, world, x, y, z);
		}
		return tag;
	}
}