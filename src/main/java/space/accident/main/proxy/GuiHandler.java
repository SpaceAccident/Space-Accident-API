package space.accident.main.proxy;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.util.SA_CoverBehaviorBase;
import space.accident.main.SpaceAccidentApi;

public class GuiHandler implements IGuiHandler {
	
	public static final int GUI_ID_COVER_SIDE_BASE = 10; // Takes GUI ID 10 - 15
	
	private GuiHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(SpaceAccidentApi.INSTANCE, this);
	}
	
	public static void register() {
		new GuiHandler();
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof ITile) {
			ITile gte = (ITile) te;
			if (GUI_ID_COVER_SIDE_BASE <= ID && ID < GUI_ID_COVER_SIDE_BASE + 6) {
				return null;
			}
			IMetaTile mte = gte.getMetaTile();
			if (mte != null) {
				return mte.getServerGUI(ID, player.inventory, gte);
			}
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof ITile) {
			ITile gte = (ITile) te;
			if (GUI_ID_COVER_SIDE_BASE <= ID && ID < GUI_ID_COVER_SIDE_BASE + 6) {
				int side = (ID - GUI_ID_COVER_SIDE_BASE);
				SA_CoverBehaviorBase<?> cover = gte.getCoverBehaviorAtSideNew(side);
				if (cover.hasCoverGUI()) {
					return cover.getClientGUI(side, gte.getCoverIDAtSide(side), gte.getComplexCoverDataAtSide(side), gte, player, world);
				}
				return null;
			}
			IMetaTile mte = gte.getMetaTile();
			if (mte != null) {
				return mte.getClientGUI(ID, player.inventory, gte);
			}
		}
		return null;
	}
}
