package space.accident.api.recipe;

import codechicken.core.ClassDiscoverer;
import space.accident.api.util.SpaceLog;

import java.util.HashSet;

public class RecipeInteractions {
	
	public static final HashSet<IOreProcessing> ORE_PROCESSING_LIST = new HashSet<>();
	
	public static void initRemoveFurnaceRecipes() {
		int counter = 0;
		ClassDiscoverer classDiscoverer = new ClassDiscoverer(test -> test.startsWith("Furnace") && test.endsWith("Remover.class"), IRecipeFurnaceRemove.class);
		for (Class<?> clazz : classDiscoverer.findClasses()) {
			try {
				IRecipeFurnaceRemove furnaceRemover = (IRecipeFurnaceRemove) clazz.newInstance();
				furnaceRemover.remove(RecipeAPI.BLACKLIST_FURNACE_RECIPES);
				SpaceLog.FML_LOGGER.info("Loaded Recipe Furnace Remover " + clazz.getName());
				counter++;
			} catch (Exception e) {
				SpaceLog.FML_LOGGER.error("Failed to Load Recipe Furnace Remover " + clazz.getName(), e);
			}
		}
		SpaceLog.FML_LOGGER.info("Loaded " + counter + " Recipe Furnace Removers");
	}
	
	public static void initOreDictProcessing() {
		ORE_PROCESSING_LIST.clear();
		int counter = 0;
		ClassDiscoverer classDiscoverer = new ClassDiscoverer(test -> test.startsWith("Ore") && test.endsWith("Processing.class"), IOreProcessing.class);
		for (Class<?> clazz : classDiscoverer.findClasses()) {
			try {
				IOreProcessing processing = (IOreProcessing) clazz.newInstance();
				ORE_PROCESSING_LIST.add(processing);
				SpaceLog.FML_LOGGER.info("Loaded Ore Processing " + clazz.getName());
				counter++;
			} catch (Exception e) {
				SpaceLog.FML_LOGGER.error("Failed to Load Ore Processing " + clazz.getName(), e);
			}
		}
		SpaceLog.FML_LOGGER.info("Loaded " + counter + " Ore Processing");
	}
}