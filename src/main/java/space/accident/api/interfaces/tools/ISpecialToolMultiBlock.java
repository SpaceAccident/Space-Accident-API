package space.accident.api.interfaces.tools;

import net.minecraft.item.ItemStack;
import space.accident.api.items.tools.MetaGenerated_Tool;

public interface ISpecialToolMultiBlock<TOOL extends MetaGenerated_Tool> {
	TOOL getTool();
	void doWork(ItemStack stack);
}