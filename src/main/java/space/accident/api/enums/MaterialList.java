package space.accident.api.enums;

import space.accident.api.API;
import space.accident.api.interfaces.IMaterialHandler;
import space.accident.main.loading.MaterialsLoading;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static space.accident.api.enums.Element.*;
import static space.accident.api.enums.MaterialType.*;

public class MaterialList {
	public static final List<IMaterialHandler> MATERIAL_HANDLERS = new ArrayList<>();
	static final Map<String, Materials> MATERIALS_MAP = new LinkedHashMap<>();
	public static Materials _NULL = new Materials(-1, "null", TextureSet.NONE, Color.BLACK, Element._NULL, new ArrayList<>());
	
	public static Materials IRON = new Materials(0, "Iron", new Color(200, 200, 200), Fe, INGOT_METAL, PLATE_DEFAULT, DUST_DEFAULT);
	public static Materials GOLD = new Materials(1, "Gold", new Color(255, 255, 30), Au, INGOT_DEFAULT, PLATE_METAL, DUST_METAL);
	public static Materials COPPER = new Materials(2, "Copper", new Color(201, 120, 20), Cu, INGOT_DEFAULT, PLATE_METAL);
	public static Materials COBALT = new Materials(3, "Cobalt", new Color(26, 59, 206), Co, INGOT_METAL, PLATE_METAL);
	private static Materials[] MATERIALS_ARRAY;
	
	public static void preLoad() {
		MATERIAL_HANDLERS.forEach(IMaterialHandler::onMaterialsInit);
		MATERIAL_HANDLERS.forEach(IMaterialHandler::onComponentInit);
		
		MATERIALS_ARRAY = MATERIALS_MAP.values().toArray(new Materials[0]);
		for (Materials material : MATERIALS_ARRAY) {
			if (material.id >= 0) {
				if (API.sGeneratedMaterials[material.id] == null) {
					API.sGeneratedMaterials[material.id] = material;
				}
				MaterialsLoading.iteratePreMaterials(material);
			}
		}
	}
	
	public static Materials[] values() {
		return MATERIALS_ARRAY;
	}
	
	public static Map<String, Materials> getMaterialsMap() {
		return MATERIALS_MAP;
	}
	
	public static boolean registerMaterialHandler(IMaterialHandler reg) {
		if (reg == null) return false;
		return MATERIAL_HANDLERS.add(reg);
	}
}
