package space.accident.api.recipe;

import net.minecraft.item.ItemStack;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;

@SuppressWarnings("ALL")
public class OreMainProcessing implements IOreProcessing {
	
	@Override
	public void init() {
		for (OrePrefixes ore : OrePrefixes.values()) {
			ore.add(this);
		}
	}
	
	@Override
	public void registerOre(OrePrefixes ore, Materials mat, String oreName, String mod, ItemStack stack) {
		if (ore.stackSize < stack.getItem().getItemStackLimit(stack)) {
			stack.getItem().setMaxStackSize(ore.stackSize);
		}
	}
}
