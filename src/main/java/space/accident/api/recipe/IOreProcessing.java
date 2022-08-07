package space.accident.api.recipe;

import net.minecraft.item.ItemStack;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;

public interface IOreProcessing {
	
	void init();
	
	/**
	 * Contains a Code Fragment, used in the OrePrefix to register Recipes. Better than using a switch/case, like I did before.
	 *
	 * @param aPrefix   always != null
	 * @param aMaterial always != null, and can be == _NULL if the Prefix is Self Referencing or not Material based!
	 * @param stack    always != null
	 */
	void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack stack);
}
