package space.accident.api.recipe;

import space.accident.api.objects.ItemStackData;

import java.util.HashSet;


public class RecipeAPI {
	
	public static HashSet<ItemStackData> BLACKLIST_FURNACE_RECIPES = new HashSet<>();
	private static boolean isFirst = true;
	
	public static void init() {
		if (!isFirst) return;
		isFirst = false;
		RecipeInteractions.initRemoveFurnaceRecipes();
	}
}