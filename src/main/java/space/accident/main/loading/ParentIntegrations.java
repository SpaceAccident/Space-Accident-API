package space.accident.main.loading;

import codechicken.core.ClassDiscoverer;
import space.accident.api.enums.Materials;
import space.accident.api.interfaces.IMaterialHandler;
import space.accident.api.interfaces.IRecipeHandler;
import space.accident.api.interfaces.MaterialsEvent;
import space.accident.api.interfaces.MaterialsEvent.IMaterialEvent;
import space.accident.api.recipe.RecipeInteractions;
import space.accident.api.util.SpaceLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Integrations for the parent mod (Space Accident API)
 */
public class ParentIntegrations {
	
	public static List<Class<?>> MATERIAL_EVENT_CLASSES = new ArrayList<>();
	public static final List<IMaterialHandler> MATERIAL_HANDLERS = new ArrayList<>();
	public static final List<IRecipeHandler> RECIPE_HANDLERS = new ArrayList<>();
	
	/**
	 * Initialisation
	 */
	public static void init() {
		initMaterialHandlers();
		initRecipeHandlers();
		initMaterialEvents();
		RecipeInteractions.initOreDictProcessing();
	}
	
	/**
	 * Init registered handlers to start your materials in a third-party mod
	 */
	private static void initMaterialHandlers() {
		CompletableFuture.supplyAsync(() -> {
			ClassDiscoverer classDiscoverer = new ClassDiscoverer(test -> test.startsWith("Material") && test.endsWith("Handler.class"), IMaterialHandler.class);
			classDiscoverer.findClasses();
			return classDiscoverer.classes;
		}).thenAccept(classes -> {
			for (Class<?> clazz : classes) {
				try {
					IMaterialHandler handler = (IMaterialHandler) clazz.newInstance();
					registerMaterialHandler(handler);
					SpaceLog.FML_LOGGER.info("Loaded Material Handler " + clazz.getName());
				} catch (Exception e) {
					SpaceLog.FML_LOGGER.error("Failed to Load Material Handler " + clazz.getName(), e);
				}
			}
			SpaceLog.FML_LOGGER.info("Loaded " + MATERIAL_HANDLERS.size() + " Material Handlers");
		});
	}
	
	/**
	 * Init registered handlers to start your recipes in a third-party mod.
	 * Initializing recipes in the POST LOAD PHASE
	 */
	private static void initRecipeHandlers() {
		CompletableFuture.supplyAsync(() -> {
			ClassDiscoverer classDiscoverer = new ClassDiscoverer(test -> test.startsWith("Recipe") && test.endsWith("Handler.class"), IRecipeHandler.class);
			classDiscoverer.findClasses();
			return classDiscoverer.classes;
		}).thenAccept(classes -> {
			for (Class<?> clazz : classes) {
				try {
					IRecipeHandler handler = (IRecipeHandler) clazz.newInstance();
					registerRecipeHandler(handler);
					SpaceLog.FML_LOGGER.info("Loaded Recipe Handler " + clazz.getName());
				} catch (Exception e) {
					SpaceLog.FML_LOGGER.error("Failed to Load Recipe Handler" + clazz.getName(), e);
				}
			}
			SpaceLog.FML_LOGGER.info("Loaded " + MATERIAL_HANDLERS.size() + " Recipe Handlers");
		});
	}
	
	/**
	 * Init registered events to iterate materials in a third-party mod.
	 * Iterate materials start in the LOAD PHASE
	 */
	private static void initMaterialEvents() {
		CompletableFuture.supplyAsync(() -> {
			ClassDiscoverer classDiscoverer = new ClassDiscoverer(test -> test.startsWith("Material") && test.endsWith("Event.class"), IMaterialEvent.class);
			classDiscoverer.findClasses();
			return classDiscoverer.classes;
		}).thenAccept(classes -> {
			for (Class<?> clazz : classes) {
				try {
					registerMaterialEvent(clazz);
					SpaceLog.FML_LOGGER.info("Loaded Recipe Handler " + clazz.getName());
				} catch (Exception e) {
					SpaceLog.FML_LOGGER.error("Failed to Load Recipe Handler" + clazz.getName(), e);
				}
			}
			SpaceLog.FML_LOGGER.info("Loaded " + MATERIAL_HANDLERS.size() + " Recipe Handlers");
		});
	}
	
	/**
	 * Iterate materials in LOAD PHASE
	 * @param material Material
	 */
	public static void iterateMaterials(Materials material) {
		for (Class<?> materialsEventClass : MATERIAL_EVENT_CLASSES) {
			try {
				for (Method method : materialsEventClass.getDeclaredMethods()) {
					for (Annotation annotation : method.getAnnotations()) {
						if (annotation.annotationType().equals(MaterialsEvent.MaterialEvent.class)) {
							if (method.getParameterTypes().length == 1) {
								method.invoke(materialsEventClass.newInstance(), material);
							} else {
								throw new IllegalArgumentException("Method " + method + " has @MaterialEvent annotation, but requires " + method.getParameterTypes().length +
										" arguments.  Event handler methods must require a single argument."
								);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace(SpaceLog.err);
			}
		}
	}
	
	/**
	 * Register class with material event
	 * @param reg class instanceof IMaterialEvent
	 */
	private static void registerMaterialEvent(Class<?> reg) {
		if (reg == null) return;
		MATERIAL_EVENT_CLASSES.add(reg);
	}
	
	/**
	 * Register class with material handler
	 * @param reg instance of IMaterialHandler
	 */
	private static void registerMaterialHandler(IMaterialHandler reg) {
		if (reg == null) return;
		MATERIAL_HANDLERS.add(reg);
	}
	
	/**
	 * Register class with recipe handler
	 * @param reg instance of IRecipeHandler
	 */
	private static void registerRecipeHandler(IRecipeHandler reg) {
		if (reg == null) return;
		RECIPE_HANDLERS.add(reg);
	}
}
