package space.accident.api.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import space.accident.api.objects.ItemStackData;

import java.util.HashSet;

@SuppressWarnings("ALL")
public class FurnaceRemover implements IRecipeFurnaceRemove {
	
	@Override
	public void remove(HashSet<ItemStackData> out) {
		out.add(new ItemStackData(new ItemStack(Items.cooked_chicken)));
		out.add(new ItemStackData(new ItemStack(Items.iron_ingot)));
	}
}