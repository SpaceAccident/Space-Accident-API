package space.accident.api.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface IItemBehaviour<ITEM extends Item> {
	
	boolean onLeftClickEntity(ITEM aItem, ItemStack aStack, EntityPlayer aPlayer, Entity aEntity);
	
	boolean onItemUse(ITEM aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float hitX, float hitY, float hitZ);
	
	boolean onItemUseFirst(ITEM aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float hitX, float hitY, float hitZ);
	
	ItemStack onItemRightClick(ITEM aItem, ItemStack aStack, World aWorld, EntityPlayer aPlayer);
	
	List<String> getAdditionalToolTips(ITEM aItem, List<String> aList, ItemStack aStack);
	
	void onUpdate(ITEM aItem, ItemStack aStack, World aWorld, Entity aPlayer, int aTimer, boolean aIsInHand);
	
	boolean isItemStackUsable(ITEM aItem, ItemStack aStack);
}