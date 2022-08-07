package space.accident.main.items.tools.behaviors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import space.accident.api.interfaces.IItemBehaviour;
import space.accident.api.items.MetaBaseItem;

import java.util.List;

public class Behaviour_None implements IItemBehaviour<MetaBaseItem> {
	
	@Override
	public boolean onLeftClickEntity(MetaBaseItem aItem, ItemStack stack, EntityPlayer player, Entity entity) {
		return false;
	}
	
	@Override
	public boolean onItemUse(MetaBaseItem aItem, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
	public boolean onItemUseFirst(MetaBaseItem aItem, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(MetaBaseItem aItem, ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}
	
	@Override
	public List<String> getAdditionalToolTips(MetaBaseItem aItem, List<String> aList, ItemStack stack) {
		return aList;
	}
	
	@Override
	public void onUpdate(MetaBaseItem aItem, ItemStack stack, World world, Entity player, int aTimer, boolean aIsInHand) {
	}
	
	@Override
	public boolean isItemStackUsable(MetaBaseItem aItem, ItemStack stack) {
		return true;
	}
}