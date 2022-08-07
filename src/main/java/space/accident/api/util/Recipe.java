package space.accident.api.util;

import codechicken.nei.PositionedStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.objects.FluidStackData;

import java.util.ArrayList;
import java.util.Arrays;

import static space.accident.api.enums.Values.W;
import static space.accident.api.util.Utility.*;

public class Recipe implements Comparable<Recipe> {
	
	
	/**
	 * If you want to change the Output, feel free to modify or even replace the whole ItemStack Array, for Inputs, please add a new Recipe, because of the HashMaps.
	 */
	public ItemStack[] mInputs, mOutputs;
	/**
	 * If you want to change the Output, feel free to modify or even replace the whole ItemStack Array, for Inputs, please add a new Recipe, because of the HashMaps.
	 */
	public FluidStack[] mFluidInputs, mFluidOutputs;
	/**
	 * If you changed the amount of Array-Items inside the Output Array then the length of this Array must be larger or equal to the Output Array. A chance of 10000 equals 100%
	 */
	public int[] mChances;
	/**
	 * An Item that needs to be inside the Special Slot, like for example the Copy Slot inside the Printer. This is only useful for Fake Recipes in NEI, since findRecipe() and containsInput() don't give a shit about this Field. Lists are also possible.
	 */
	public Object mSpecialItems;
	public int mDuration, mEUt, mSpecialValue;
	/**
	 * Use this to just disable a specific Recipe, but the Configuration enables that already for every single Recipe.
	 */
	public boolean mEnabled = true;
	/**
	 * If this Recipe is hidden from NEI
	 */
	public boolean mHidden = false;
	/**
	 * If this Recipe is Fake and therefore doesn't get found by the findRecipe Function (It is still in the HashMaps, so that containsInput does return T on those fake Inputs)
	 */
	public boolean mFakeRecipe = false;
	/**
	 * If this Recipe can be stored inside a Machine in order to make Recipe searching more Efficient by trying the previously used Recipe first. In case you have a Recipe Map overriding things and returning one time use Recipes, you have to set this to F.
	 */
	public boolean mCanBeBuffered = true;
	/**
	 * If this Recipe needs the Output Slots to be completely empty. Needed in case you have randomised Outputs
	 */
	public boolean mNeedsEmptyOutput = false;
	/**
	 * Used for describing recipes that do not fit the default recipe pattern (for example Large Boiler Fuels)
	 */
	private String[] neiDesc = null;
	
	private Recipe(Recipe aRecipe) {
		mInputs           = copyStackArray(aRecipe.mInputs);
		mOutputs          = copyStackArray(aRecipe.mOutputs);
		mSpecialItems     = aRecipe.mSpecialItems;
		mChances          = aRecipe.mChances;
		mFluidInputs      = copyFluidArray(aRecipe.mFluidInputs);
		mFluidOutputs     = copyFluidArray(aRecipe.mFluidOutputs);
		mDuration         = aRecipe.mDuration;
		mSpecialValue     = aRecipe.mSpecialValue;
		mEUt              = aRecipe.mEUt;
		mNeedsEmptyOutput = aRecipe.mNeedsEmptyOutput;
		mCanBeBuffered    = aRecipe.mCanBeBuffered;
		mFakeRecipe       = aRecipe.mFakeRecipe;
		mEnabled          = aRecipe.mEnabled;
		mHidden           = aRecipe.mHidden;
	}
	
	public Recipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecialItems, int[] aChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		if (aInputs == null) aInputs = new ItemStack[0];
		if (aOutputs == null) aOutputs = new ItemStack[0];
		if (aFluidInputs == null) aFluidInputs = new FluidStack[0];
		if (aFluidOutputs == null) aFluidOutputs = new FluidStack[0];
		if (aChances == null) aChances = new int[aOutputs.length];
		if (aChances.length < aOutputs.length) aChances = Arrays.copyOf(aChances, aOutputs.length);
		
		aInputs       = withoutTrailingNulls(aInputs, ItemStack[]::new);
		aOutputs      = withoutTrailingNulls(aOutputs, ItemStack[]::new);
		aFluidInputs  = withoutNulls(aFluidInputs, FluidStack[]::new);
		aFluidOutputs = withoutNulls(aFluidOutputs, FluidStack[]::new);
		
		OreDictUnifier.setStackArray(true, aInputs);
		OreDictUnifier.setStackArray(true, aOutputs);
		
		for (ItemStack tStack : aOutputs)
			updateItemStack(tStack);
		
		for (int i = 0; i < aChances.length; i++)
			if (aChances[i] <= 0) aChances[i] = 10000;
		for (int i = 0; i < aFluidInputs.length; i++)
			 aFluidInputs[i] = new FluidStackData(aFluidInputs[i]);
		for (int i = 0; i < aFluidOutputs.length; i++)
			 aFluidOutputs[i] = new FluidStackData(aFluidOutputs[i]);
		
		for (ItemStack aInput : aInputs)
			if (aInput != null && Items.feather.getDamage(aInput) != W) for (int j = 0; j < aOutputs.length; j++) {
				if (areStacksEqual(aInput, aOutputs[j]) && aChances[j] >= 10000) {
					if (aInput.stackSize >= aOutputs[j].stackSize) {
						aInput.stackSize -= aOutputs[j].stackSize;
						aOutputs[j] = null;
					} else {
						aOutputs[j].stackSize -= aInput.stackSize;
					}
				}
			}
		
		if (aOptimize && aDuration >= 32) {
			ArrayList<ItemStack> tList = new ArrayList<>();
			tList.addAll(Arrays.asList(aInputs));
			tList.addAll(Arrays.asList(aOutputs));
			for (int i = 0; i < tList.size(); i++) if (tList.get(i) == null) tList.remove(i--);
			
			for (int i = Math.min(64, aDuration / 16); i > 1; i--)
				if (aDuration / i >= 16) {
					boolean temp = true;
					for (ItemStack stack : tList)
						if (stack.stackSize % i != 0) {
							temp = false;
							break;
						}
					if (temp) for (FluidStack aFluidInput : aFluidInputs)
						if (aFluidInput.amount % i != 0) {
							temp = false;
							break;
						}
					if (temp) for (FluidStack aFluidOutput : aFluidOutputs)
						if (aFluidOutput.amount % i != 0) {
							temp = false;
							break;
						}
					if (temp) {
						for (ItemStack itemStack : tList)
							itemStack.stackSize /= i;
						for (FluidStack aFluidInput : aFluidInputs)
							aFluidInput.amount /= i;
						for (FluidStack aFluidOutput : aFluidOutputs)
							aFluidOutput.amount /= i;
						aDuration /= i;
					}
				}
		}
		
		mInputs       = aInputs;
		mOutputs      = aOutputs;
		mSpecialItems = aSpecialItems;
		mChances      = aChances;
		mFluidInputs  = aFluidInputs;
		mFluidOutputs = aFluidOutputs;
		mDuration     = aDuration;
		mSpecialValue = aSpecialValue;
		mEUt          = aEUt;
//		checkCellBalance();
	}
	
	
	@Override
	public int compareTo(Recipe recipe) {
		// first lowest tier recipes
		// then fastest
		// then with lowest special value
		// then dry recipes
		// then with fewer inputs
		if (this.mEUt != recipe.mEUt) {
			return this.mEUt - recipe.mEUt;
		} else if (this.mDuration != recipe.mDuration) {
			return this.mDuration - recipe.mDuration;
		} else if (this.mSpecialValue != recipe.mSpecialValue) {
			return this.mSpecialValue - recipe.mSpecialValue;
		} else if (this.mFluidInputs.length != recipe.mFluidInputs.length) {
			return this.mFluidInputs.length - recipe.mFluidInputs.length;
		} else if (this.mInputs.length != recipe.mInputs.length) {
			return this.mInputs.length - recipe.mInputs.length;
		}
		return 0;
	}
	
	public ItemStack getRepresentativeInput(int index) {
		if (index < 0 || index >= mInputs.length) return null;
		return copyOrNull(mInputs[index]);
	}
	
	public ItemStack getOutput(int index) {
		if (index < 0 || index >= mOutputs.length) return null;
		return copyOrNull(mOutputs[index]);
	}
	
	public int getOutputChance(int index) {
		if (index < 0 || index >= mChances.length) return 10000;
		return mChances[index];
	}
	
	public FluidStack getRepresentativeFluidInput(int index) {
		if (index < 0 || index >= mFluidInputs.length || mFluidInputs[index] == null) return null;
		return mFluidInputs[index].copy();
	}
	
	public FluidStack getFluidOutput(int index) {
		if (index < 0 || index >= mFluidOutputs.length || mFluidOutputs[index] == null) return null;
		return mFluidOutputs[index].copy();
	}
	
	public Recipe copy() {
		return new Recipe(this);
	}
	
	public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, FluidStack[] aFluidInputs, ItemStack... aInputs) {
		return isRecipeInputEqual(aDecreaseStacksizeBySuccess, false, aFluidInputs, aInputs);
	}
	
	/**
	 * WARNING: Do not call this method with both {@code aDecreaseStacksizeBySuccess} and {@code aDontCheckStackSizes} set to {@code true}!
	 * You'll get weird behavior.
	 */
	public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, boolean aDontCheckStackSizes, FluidStack[] aFluidInputs, ItemStack... aInputs) {
		if (mInputs.length > 0 && aInputs == null) return false;
		if (mFluidInputs.length > 0 && aFluidInputs == null) return false;
		
		// We need to handle 0-size recipe inputs. These are for inputs that don't get consumed.
		boolean inputFound;
		int remainingCost;
		
		// Array tracking modified fluid amounts. For efficiency, we will lazily initialize this array.
		// We use Integer so that we can have null as the default value, meaning unchanged.
		Integer[] newFluidAmounts = null;
		if (aFluidInputs != null) {
			newFluidAmounts = new Integer[aFluidInputs.length];
			
			for (FluidStack recipeFluidCost : mFluidInputs) {
				if (recipeFluidCost != null) {
					inputFound    = false;
					remainingCost = recipeFluidCost.amount;
					
					for (int i = 0; i < aFluidInputs.length; i++) {
						FluidStack providedFluid = aFluidInputs[i];
						if (providedFluid != null && providedFluid.isFluidEqual(recipeFluidCost)) {
							inputFound = true;
							if (newFluidAmounts[i] == null) {
								newFluidAmounts[i] = providedFluid.amount;
							}
							
							if (aDontCheckStackSizes || newFluidAmounts[i] >= remainingCost) {
								newFluidAmounts[i] -= remainingCost;
													  remainingCost = 0;
								break;
							} else {
								remainingCost -= newFluidAmounts[i];
								newFluidAmounts[i] = 0;
							}
						}
					}
					
					if (remainingCost > 0 || !inputFound) {
						// Cost not satisfied, or for non-consumed inputs, input not found.
						return false;
					}
				}
			}
		}
		
		// Array tracking modified item stack sizes. For efficiency, we will lazily initialize this array.
		// We use Integer so that we can have null as the default value, meaning unchanged.
		Integer[] newItemAmounts = null;
		if (aInputs != null) {
			newItemAmounts = new Integer[aInputs.length];
			
			for (ItemStack recipeItemCost : mInputs) {
				ItemStack unifiedItemCost = OreDictUnifier.get_nocopy(true, recipeItemCost);
				if (unifiedItemCost != null) {
					inputFound    = false;
					remainingCost = recipeItemCost.stackSize;
					
					for (int i = 0; i < aInputs.length; i++) {
						ItemStack providedItem = aInputs[i];
						if (OreDictUnifier.isInputStackEqual(providedItem, unifiedItemCost)) {
							
							inputFound = true;
							if (newItemAmounts[i] == null) {
								newItemAmounts[i] = providedItem.stackSize;
							}
							
							if (aDontCheckStackSizes || newItemAmounts[i] >= remainingCost) {
								newItemAmounts[i] -= remainingCost;
													 remainingCost = 0;
								break;
							} else {
								remainingCost -= newItemAmounts[i];
								newItemAmounts[i] = 0;
							}
						}
					}
					
					if (remainingCost > 0 || !inputFound) {
						// Cost not satisfied, or for non-consumed inputs, input not found.
						return false;
					}
				}
			}
		}
		
		if (aDecreaseStacksizeBySuccess) {
			// Copy modified amounts into the input stacks.
			if (aFluidInputs != null) {
				for (int i = 0; i < aFluidInputs.length; i++) {
					if (newFluidAmounts[i] != null) {
						aFluidInputs[i].amount = newFluidAmounts[i];
					}
				}
			}
			
			if (aInputs != null) {
				for (int i = 0; i < aInputs.length; i++) {
					if (newItemAmounts[i] != null) {
						aInputs[i].stackSize = newItemAmounts[i];
					}
				}
			}
		}
		
		return true;
	}
	
	public String[] getNeiDesc() {
		return neiDesc;
	}
	
	protected void setNeiDesc(String... neiDesc) {
		this.neiDesc = neiDesc;
	}
	
	/**
	 * Overriding this method and getOutputPositionedStacks allows for custom NEI stack placement
	 *
	 * @return A list of input stacks
	 */
	public ArrayList<PositionedStack> getInputPositionedStacks() {
		return null;
	}
	
	/**
	 * Overriding this method and getInputPositionedStacks allows for custom NEI stack placement
	 *
	 * @return A list of output stacks
	 */
	public ArrayList<PositionedStack> getOutputPositionedStacks() {
		return null;
	}
}
