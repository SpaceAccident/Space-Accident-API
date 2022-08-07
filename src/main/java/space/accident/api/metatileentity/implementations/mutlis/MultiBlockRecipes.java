package space.accident.api.metatileentity.implementations.mutlis;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.metatileentity.base.MultiBlockBase;
import space.accident.api.util.Recipe;

import static space.accident.api.enums.Values.V;
import static space.accident.extensions.NumberUtils.getTier;

public class MultiBlockRecipes {
	
	public static boolean defaultRecipe(MultiBlockBase base) {
		ItemStack[] tInputList = base.getCompactedInputs();
		FluidStack[] tFluidList = base.getCompactedFluids();
		
		long voltage = base.getMaxInputVoltage();
		int tier = Math.max(1, getTier(voltage));
		
		Recipe recipe = base.getRecipeMap().findRecipe(base.getBaseMetaTileEntity(), false, V[tier], tFluidList, tInputList);
		if (recipe != null) {
			if (recipe.isRecipeInputEqual(true, tFluidList, tInputList)) {
				base.efficiency         = (10000 - (base.getIdealStatus() - base.getRepairStatus()) * 1000);
				base.efficiencyIncrease = 10000;
				base.calculateOverclockedNessMulti(recipe.mEUt, recipe.mDuration, 1, voltage);
				
				if (base.maxProgressTime == Integer.MAX_VALUE - 1 && base.eUt == Integer.MAX_VALUE - 1) return false;
				if (base.eUt > 0) {
					base.eUt = (-base.eUt);
				}
				base.maxProgressTime = Math.max(1, base.maxProgressTime);
				base.mOutputItems    = new ItemStack[]{recipe.getOutput(0)};
				base.mOutputFluids   = new FluidStack[]{recipe.getFluidOutput(0)};
				base.updateSlots();
				return true;
			}
		}
		return false;
	}
	
}
