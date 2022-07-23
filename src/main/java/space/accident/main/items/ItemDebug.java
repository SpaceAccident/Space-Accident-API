package space.accident.main.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import space.accident.api.API;
import space.accident.api.items.GenericItem;
import space.accident.api.util.SpaceLog;

import java.util.List;

public class ItemDebug extends GenericItem {
	
	public ItemDebug() {
		super("item_debug", "Debug Item", "");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item aItem, CreativeTabs aCreativeTab, List aList) {
		aList.add(new ItemStack(this, 1, 0));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister aIconRegister) {
		super.registerIcons(aIconRegister);
		
		if (API.sPostloadFinished) {
			SpaceLog.out.println("GT_Mod: Starting Item Icon Load Phase");
			API.sItemIcons = aIconRegister;
			try {
				API.sGTItemIconload.forEach(Runnable::run);
			} catch (Throwable e) {
				e.printStackTrace(SpaceLog.err);
			}
			SpaceLog.out.println("GT_Mod: Finished Item Icon Load Phase");
		}
	}
	
}
