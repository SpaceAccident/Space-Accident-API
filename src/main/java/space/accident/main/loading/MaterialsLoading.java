package space.accident.main.loading;

import space.accident.api.enums.Materials;
import space.accident.api.interfaces.EventMaterial;
import space.accident.api.util.SpaceLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MaterialsLoading {
	private static final List<Class<?>> MATERIALS_EVENT_CLASSES = new ArrayList<>();
	
	public static void registerClass(Class<?> clazz) {
		MATERIALS_EVENT_CLASSES.add(clazz);
		SpaceLog.out.println("Added class by Material Event" + clazz.getName());
	}
	
	public static void iteratePreMaterials(Materials material) {
		for (Class<?> materialsEventClass : MATERIALS_EVENT_CLASSES) {
			try {
				for (Method method : materialsEventClass.getDeclaredMethods()) {
					for (Annotation annotation : method.getAnnotations()) {
						if (annotation.annotationType().equals(EventMaterial.class)) {
							if (method.getParameterTypes().length == 1) {
								method.invoke(materialsEventClass.newInstance(), material);
							} else {
								throw new IllegalArgumentException("Method " + method + " has @EventPreMaterial annotation, but requires " + method.getParameterTypes().length +
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
}