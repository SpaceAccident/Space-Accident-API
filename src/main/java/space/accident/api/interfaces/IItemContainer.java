package space.accident.api.interfaces;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemContainer {
	Item getItem();
	
	Block getBlock();
	
	boolean isStackEqual(ItemStack stack);
	
	boolean isStackEqual(ItemStack stack, boolean aWildcard, boolean aIgnoreNBT);
	
	ItemStack get(long amount, ItemStack... aReplacements);
	
	ItemStack getWildcard(long amount, ItemStack... aReplacements);
	
	ItemStack getUndamaged(long amount, ItemStack... aReplacements);
	
	ItemStack getAlmostBroken(long amount, ItemStack... aReplacements);
	
	ItemStack getWithDamage(long amount, long aMetaValue, ItemStack... aReplacements);
	
	IItemContainer set(Item aItem);
	
	IItemContainer set(ItemStack stack);
	
	IItemContainer registerOre(String... aOreNames);
	
	IItemContainer registerWildcardAsOre(String... aOreNames);
	
	ItemStack getWithCharge(long amount, int aEnergy, ItemStack... aReplacements);
	
	ItemStack getWithName(long amount, String aDisplayName, ItemStack... aReplacements);
	
	boolean hasBeenSet();
}