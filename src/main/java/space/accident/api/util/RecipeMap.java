package space.accident.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.tileentity.IWorldInteraction;
import space.accident.api.objects.ItemStackData;

import java.util.*;

import static space.accident.api.API.*;

public class RecipeMap {
	
	/**
	 * Contains all Recipe Maps
	 */
	public static final Collection<RecipeMap> sMappings = new ArrayList<>();
	
	/**
	 * All recipe maps indexed by their {@link #mUniqueIdentifier}.
	 */
	public static final Map<String, RecipeMap> sIndexedMappings = new HashMap<>();
	
	
	/**
	 * HashMap of Recipes based on their Items
	 */
	public final Map<ItemStackData, Collection<Recipe>> mRecipeItemMap = new /*Concurrent*/HashMap<>();
	/**
	 * HashMap of Recipes based on their Fluids
	 */
	public final Map<Fluid, Collection<Recipe>> mRecipeFluidMap = new /*Concurrent*/HashMap<>();
	public final HashSet<String> mRecipeFluidNameMap = new HashSet<>();
	/**
	 * The List of all Recipes
	 */
	public final Collection<Recipe> mRecipeList;
	/**
	 * String used as an unlocalised Name.
	 */
	public final String mUnlocalizedName;
	/**
	 * String used in NEI for the Recipe Lists. If null it will use the unlocalised Name instead
	 */
	public final String mNEIName;
	/**
	 * GUI used for NEI Display. Usually the GUI of the Machine itself
	 */
	public final String mNEIGUIPath;
	public final String mNEISpecialValuePre, mNEISpecialValuePost;
	public final int mUsualInputCount, mUsualOutputCount, mNEISpecialValueMultiplier, mMinimalInputItems, mMinimalInputFluids, mAmperage;
	public final boolean mNEIAllowed, mShowVoltageAmperageInNEI, mNEIUnificateOutput;
	
	/**
	 * Unique identifier for this recipe map. Generated from aUnlocalizedName and a few other parameters.
	 * See constructor for details.
	 */
	public final String mUniqueIdentifier;
	
	/**
	 * Whether this recipe map contains any fluid outputs.
	 */
	private boolean mHasFluidOutputs = false;
	
	/**
	 * Whether this recipe map contains special slot inputs.
	 */
	private boolean mUsesSpecialSlot = false;
	
	/**
	 * Initialises a new type of Recipe Handler.
	 *
	 * @param aRecipeList                a List you specify as Recipe List. Usually just an ArrayList with a pre-initialised Size.
	 * @param aUnlocalizedName           the unlocalised Name of this Recipe Handler, used mainly for NEI.
	 * @param aLocalName                 the displayed Name inside the NEI Recipe GUI.
	 * @param aNEIGUIPath                the displayed GUI Texture, usually just a Machine GUI. Auto-Attaches ".png" if forgotten.
	 * @param aUsualInputCount           the usual amount of Input Slots this Recipe Class has.
	 * @param aUsualOutputCount          the usual amount of Output Slots this Recipe Class has.
	 * @param aNEISpecialValuePre        the String in front of the Special Value in NEI.
	 * @param aNEISpecialValueMultiplier the Value the Special Value is getting Multiplied with before displaying
	 * @param aNEISpecialValuePost       the String after the Special Value. Usually for a Unit or something.
	 * @param aNEIAllowed                if NEI is allowed to display this Recipe Handler in general.
	 * @param aNEIUnificateOutput        if NEI generate oredict equivalents
	 */
	public RecipeMap(Collection<Recipe> aRecipeList, String aUnlocalizedName, String aLocalName, String aNEIName, String aNEIGUIPath, int aUsualInputCount, int aUsualOutputCount, int aMinimalInputItems, int aMinimalInputFluids, int aAmperage, String aNEISpecialValuePre, int aNEISpecialValueMultiplier, String aNEISpecialValuePost, boolean aShowVoltageAmperageInNEI, boolean aNEIAllowed, boolean aNEIUnificateOutput) {
		sMappings.add(this);
		mNEIAllowed = aNEIAllowed;
		mShowVoltageAmperageInNEI = aShowVoltageAmperageInNEI;
		mNEIUnificateOutput = aNEIUnificateOutput;
		mRecipeList = aRecipeList;
		mNEIName = aNEIName == null ? aUnlocalizedName : aNEIName;
		mNEIGUIPath = aNEIGUIPath.endsWith(".png") ? aNEIGUIPath : aNEIGUIPath + ".png";
		mNEISpecialValuePre = aNEISpecialValuePre;
		mNEISpecialValueMultiplier = aNEISpecialValueMultiplier;
		mNEISpecialValuePost = aNEISpecialValuePost;
		mAmperage = aAmperage;
		mUsualInputCount = aUsualInputCount;
		mUsualOutputCount = aUsualOutputCount;
		mMinimalInputItems = aMinimalInputItems;
		mMinimalInputFluids = aMinimalInputFluids;
		sFluidMappings.add(mRecipeFluidMap);
		sItemStackMappings.add(mRecipeItemMap);
		LanguageManager.addStringLocalization(mUnlocalizedName = aUnlocalizedName, aLocalName);
		mUniqueIdentifier = String.format("%s_%d_%d_%d_%d_%d", aUnlocalizedName, aAmperage, aUsualInputCount, aUsualOutputCount, aMinimalInputFluids, aMinimalInputItems);
		if (sIndexedMappings.put(mUniqueIdentifier, this) != null)
			throw new IllegalArgumentException("Duplicate recipe map registered: " + mUniqueIdentifier);
	}
	
	public RecipeMap(Collection<Recipe> aRecipeList, String aUnlocalizedName, String aLocalName, String aNEIName, String aNEIGUIPath, int aUsualInputCount, int aUsualOutputCount, int aMinimalInputItems, int aMinimalInputFluids, int aAmperage, String aNEISpecialValuePre, int aNEISpecialValueMultiplier, String aNEISpecialValuePost, boolean aShowVoltageAmperageInNEI, boolean aNEIAllowed) {
		this(aRecipeList, aUnlocalizedName, aLocalName, aNEIName, aNEIGUIPath, aUsualInputCount, aUsualOutputCount, aMinimalInputItems, aMinimalInputFluids, aAmperage, aNEISpecialValuePre, aNEISpecialValueMultiplier, aNEISpecialValuePost, aShowVoltageAmperageInNEI, aNEIAllowed, true);
	}
	
	public Recipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		return addRecipe(new Recipe(aOptimize, aInputs, aOutputs, aSpecial, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
	}
	
	public Recipe addRecipe(int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		return addRecipe(new Recipe(false, null, null, null, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue), false, false, false);
	}
	
	public Recipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		return addRecipe(new Recipe(aOptimize, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
	}
	
	public Recipe addRecipe(Recipe aRecipe) {
		return addRecipe(aRecipe, true, false, false);
	}
	
	protected Recipe addRecipe(Recipe aRecipe, boolean aCheckForCollisions, boolean aFakeRecipe, boolean aHidden) {
		aRecipe.mHidden = aHidden;
		aRecipe.mFakeRecipe = aFakeRecipe;
		if (aRecipe.mFluidInputs.length < mMinimalInputFluids && aRecipe.mInputs.length < mMinimalInputItems)
			return null;
		if (aCheckForCollisions && findRecipe(null, false, true, Long.MAX_VALUE, aRecipe.mFluidInputs, aRecipe.mInputs) != null)
			return null;
		return add(aRecipe);
	}
	
	/**
	 * Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes
	 */
	public Recipe addFakeRecipe(boolean aCheckForCollisions, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		return addFakeRecipe(aCheckForCollisions, new Recipe(false, aInputs, aOutputs, aSpecial, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
	}
	
	/**
	 * Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes
	 */
	public Recipe addFakeRecipe(boolean aCheckForCollisions, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
		return addFakeRecipe(aCheckForCollisions, new Recipe(false, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue));
	}
	
	public Recipe addFakeRecipe(boolean aCheckForCollisions, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue, boolean hidden) {
		return addFakeRecipe(aCheckForCollisions, new Recipe(false, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue),hidden);
	}
	
	/**
	 * Only used for fake Recipe Handlers to show something in NEI, do not use this for adding actual Recipes! findRecipe wont find fake Recipes, containsInput WILL find fake Recipes
	 */
	public Recipe addFakeRecipe(boolean aCheckForCollisions, Recipe aRecipe) {
		return addRecipe(aRecipe, aCheckForCollisions, true, false);
	}
	public Recipe addFakeRecipe(boolean aCheckForCollisions, Recipe aRecipe, boolean hidden) {
		return addRecipe(aRecipe, aCheckForCollisions, true, hidden);
	}
	
	public Recipe add(Recipe aRecipe) {
		mRecipeList.add(aRecipe);
		for (FluidStack aFluid : aRecipe.mFluidInputs) {
			if (aFluid != null) {
				Collection<Recipe> tList = mRecipeFluidMap.computeIfAbsent(aFluid.getFluid(), k -> new HashSet<>(1));
				tList.add(aRecipe);
				mRecipeFluidNameMap.add(aFluid.getFluid().getName());
			}
		}
		if (aRecipe.mFluidOutputs.length != 0) {
			this.mHasFluidOutputs = true;
		}
		if (aRecipe.mSpecialItems != null) {
			this.mUsesSpecialSlot = true;
		}
		return addToItemMap(aRecipe);
	}
	
	public void reInit() {
		mRecipeItemMap.clear();
		for (Recipe tRecipe : mRecipeList) {
			OreDictUnifier.setStackArray(true, tRecipe.mInputs);
			OreDictUnifier.setStackArray(true, tRecipe.mOutputs);
			addToItemMap(tRecipe);
		}
	}
	
	/**
	 * @return if this Item is a valid Input for any for the Recipes
	 */
	public boolean containsInput(ItemStack stack) {
		return stack != null && (mRecipeItemMap.containsKey(new ItemStackData(stack)) || mRecipeItemMap.containsKey(new ItemStackData(stack, true)));
	}
	
	/**
	 * @return if this Fluid is a valid Input for any for the Recipes
	 */
	public boolean containsInput(FluidStack aFluid) {
		return aFluid != null && containsInput(aFluid.getFluid());
	}
	
	/**
	 * @return if this Fluid is a valid Input for any for the Recipes
	 */
	public boolean containsInput(Fluid aFluid) {
		return aFluid != null && mRecipeFluidNameMap.contains(aFluid.getName());
	}
	
	public Recipe findRecipe(IWorldInteraction aTileEntity, boolean aNotUnificated, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
		return findRecipe(aTileEntity, null, aNotUnificated, aVoltage, aFluids, null, aInputs);
	}
	
	public Recipe findRecipe(IWorldInteraction aTileEntity, boolean aNotUnificated, boolean aDontCheckStackSizes, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
		return findRecipe(aTileEntity, null, aNotUnificated, aDontCheckStackSizes, aVoltage, aFluids, null, aInputs);
	}
	
	public Recipe findRecipe(IWorldInteraction aTileEntity, Recipe aRecipe, boolean aNotUnificated, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
		return findRecipe(aTileEntity, aRecipe, aNotUnificated, aVoltage, aFluids, null, aInputs);
	}
	
	public Recipe findRecipe(IWorldInteraction aTileEntity, Recipe aRecipe, boolean aNotUnificated, boolean aDontCheckStackSizes, long aVoltage, FluidStack[] aFluids, ItemStack... aInputs) {
		return findRecipe(aTileEntity, aRecipe, aNotUnificated, aDontCheckStackSizes, aVoltage, aFluids, null, aInputs);
	}
	
	public Recipe findRecipe(IWorldInteraction aTileEntity, Recipe aRecipe, boolean aNotUnificated, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
		return findRecipe(aTileEntity, aRecipe, aNotUnificated, false, aVoltage, aFluids, aSpecialSlot, aInputs);
	}
	/**
	 * finds a Recipe matching the aFluid and ItemStack Inputs.
	 *
	 * @param aTileEntity    an Object representing the current coordinates of the executing Block/Entity/Whatever. This may be null, especially during Startup.
	 * @param aRecipe        in case this is != null it will try to use this Recipe first when looking things up.
	 * @param aNotUnificated if this is T the Recipe searcher will unificate the ItemStack Inputs
	 * @param aDontCheckStackSizes if set to false will only return recipes that can be executed at least once with the provided input
	 * @param aVoltage       Voltage of the Machine or Long.MAX_VALUE if it has no Voltage
	 * @param aFluids        the Fluid Inputs
	 * @param aSpecialSlot   the content of the Special Slot, the regular Manager doesn't do anything with this, but some custom ones do.
	 * @param aInputs        the Item Inputs
	 * @return the Recipe it has found or null for no matching Recipe
	 */
	public Recipe findRecipe(IWorldInteraction aTileEntity, Recipe aRecipe, boolean aNotUnificated, boolean aDontCheckStackSizes, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
		// No Recipes? Well, nothing to be found then.
		if (mRecipeList.isEmpty()) return null;
		
		// Some Recipe Classes require a certain amount of Inputs of certain kinds. Like "at least 1 Fluid + 1 Stack" or "at least 2 Stacks" before they start searching for Recipes.
		// This improves Performance massively, especially if people leave things like Circuits, Molds or Shapes in their Machines to select Sub Recipes.
		if (sPostloadFinished) {
			if (mMinimalInputFluids > 0) {
				if (aFluids == null) return null;
				int tAmount = 0;
				for (FluidStack aFluid : aFluids) if (aFluid != null) tAmount++;
				if (tAmount < mMinimalInputFluids) return null;
			}
			if (mMinimalInputItems > 0) {
				if (aInputs == null) return null;
				int tAmount = 0;
				for (ItemStack aInput : aInputs) if (aInput != null) tAmount++;
				if (tAmount < mMinimalInputItems) return null;
			}
		}
		
		// Unification happens here in case the Input isn't already unificated.
		if (aNotUnificated) aInputs = OreDictUnifier.getStackArray(true, aInputs);
		
		// Check the Recipe which has been used last time in order to not have to search for it again, if possible.
		if (aRecipe != null)
			if (!aRecipe.mFakeRecipe && aRecipe.mCanBeBuffered && aRecipe.isRecipeInputEqual(false, aDontCheckStackSizes, aFluids, aInputs))
				return aRecipe.mEnabled && aVoltage * mAmperage >= aRecipe.mEUt ? aRecipe : null;
		
		// Now look for the Recipes inside the Item HashMaps, but only when the Recipes usually have Items.
		if (mUsualInputCount > 0 && aInputs != null) for (ItemStack tStack : aInputs)
			if (tStack != null) {
				Collection<Recipe>
						tRecipes = mRecipeItemMap.get(new ItemStackData(tStack));
				if (tRecipes != null) for (Recipe tRecipe : tRecipes)
					if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aDontCheckStackSizes, aFluids, aInputs))
						return tRecipe.mEnabled && aVoltage * mAmperage >= tRecipe.mEUt ? tRecipe : null;
				tRecipes = mRecipeItemMap.get(new ItemStackData(tStack, true));
				if (tRecipes != null) for (Recipe tRecipe : tRecipes)
					if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aDontCheckStackSizes, aFluids, aInputs))
						return tRecipe.mEnabled && aVoltage * mAmperage >= tRecipe.mEUt ? tRecipe : null;
			}
		
		// If the minimal Amount of Items for the Recipe is 0, then it could be a Fluid-Only Recipe, so check that Map too.
		if (mMinimalInputItems == 0 && aFluids != null) for (FluidStack aFluid : aFluids)
			if (aFluid != null) {
				Collection<Recipe>
						tRecipes = mRecipeFluidMap.get(aFluid.getFluid());
				if (tRecipes != null) for (Recipe tRecipe : tRecipes)
					if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aDontCheckStackSizes, aFluids, aInputs))
						return tRecipe.mEnabled && aVoltage * mAmperage >= tRecipe.mEUt ? tRecipe : null;
			}
		
		// And nothing has been found.
		return null;
	}
	
	protected Recipe addToItemMap(Recipe aRecipe) {
		for (ItemStack stack : aRecipe.mInputs)
			if (stack != null) {
				ItemStackData tStack = new ItemStackData(stack);
				Collection<Recipe> tList = mRecipeItemMap.computeIfAbsent(tStack, k -> new HashSet<>(1));
				tList.add(aRecipe);
			}
		return aRecipe;
	}
	
	/**
	 * Whether this recipe map contains any fluid outputs.
	 */
	public boolean hasFluidOutputs() {
		return mHasFluidOutputs;
	}
	
	/**
	 * Whether this recipe map contains any fluid inputs.
	 */
	public boolean hasFluidInputs() {
		return mRecipeFluidNameMap.size() != 0;
	}
	
	/**
	 * Whether this recipe map contains special slot inputs.
	 */
	public boolean usesSpecialSlot() {
		return mUsesSpecialSlot;
	}
	
	public void addRecipe(Object o, FluidStack[] fluidInputArray, FluidStack[] fluidOutputArray) {
	}
}
