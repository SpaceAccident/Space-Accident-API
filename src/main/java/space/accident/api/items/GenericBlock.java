package space.accident.api.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import space.accident.api.util.LanguageManager;

import static space.accident.api.enums.Values.W;

public class GenericBlock extends Block {
	protected final String mUnlocalizedName;
	
	protected GenericBlock(Class<? extends ItemBlock> aItemClass, String name, Material aMaterial) {
		super(aMaterial);
		setBlockName(mUnlocalizedName = name);
		GameRegistry.registerBlock(this, aItemClass, getUnlocalizedName());
		LanguageManager.addStringLocalization(getUnlocalizedName() + "." + W + ".name", "Any Sub Block of this one");
	}
}
