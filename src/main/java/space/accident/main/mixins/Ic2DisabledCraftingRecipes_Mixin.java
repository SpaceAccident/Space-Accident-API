package space.accident.main.mixins;

import ic2.core.AdvRecipe;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(AdvRecipe.class)
public abstract class Ic2DisabledCraftingRecipes_Mixin {
	
	@Inject(method = "expand", at = @At("HEAD"), remap = false, cancellable = true)
	private static void expand(Object o, CallbackInfoReturnable<List<ItemStack>> call) {
		call.setReturnValue(new ArrayList<>());
	}
}
