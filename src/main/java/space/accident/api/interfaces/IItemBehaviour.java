package space.accident.api.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface IItemBehaviour<ITEM extends Item> {
	
	boolean onLeftClickEntity(ITEM aItem, ItemStack stack, EntityPlayer player, Entity entity);
	
	boolean onItemUse(ITEM aItem, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ);
	
	boolean onItemUseFirst(ITEM aItem, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ);
	
	ItemStack onItemRightClick(ITEM aItem, ItemStack stack, World world, EntityPlayer player);
	
	List<String> getAdditionalToolTips(ITEM aItem, List<String> aList, ItemStack stack);
	
	void onUpdate(ITEM aItem, ItemStack stack, World world, Entity player, int aTimer, boolean aIsInHand);
	
	boolean isItemStackUsable(ITEM aItem, ItemStack stack);
}