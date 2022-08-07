package space.accident.main.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.accident.api.objects.ItemStackData;
import space.accident.api.recipe.RecipeAPI;

import static space.accident.api.recipe.RecipeAPI.*;

@Mixin(FurnaceRecipes.class)
public abstract class VanilaDisabledFurnaceRecipes_Mixin {
	
	@Inject(method = "func_151394_a", at = @At("HEAD"), remap = false, cancellable = true)
	private void addSmelting(ItemStack in, ItemStack out, float value, CallbackInfo ci) {
		RecipeAPI.init();
		if (BLACKLIST_FURNACE_RECIPES.contains(new ItemStackData(out))) {
			ci.cancel();
		}
	}
}
