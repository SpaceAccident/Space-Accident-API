package space.accident.api.recipe;

import space.accident.api.util.RecipeMap;
import space.accident.api.util.SpaceLog;

public class RecipeMaps {
	
	public static void reInit() {
		SpaceLog.FML_LOGGER.info("Re-Unificating Recipes.");
		for (RecipeMap tMapEntry : RecipeMap.sMappings)
			tMapEntry.reInit();
	}
}
