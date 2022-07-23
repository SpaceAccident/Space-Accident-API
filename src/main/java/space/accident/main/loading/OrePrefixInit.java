package space.accident.main.loading;

import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Materials;
import space.accident.api.interfaces.EventMaterial;
import space.accident.api.interfaces.IMaterialHandler;

public class OrePrefixInit {

	public static void start() {
		MaterialsLoading.registerClass(OrePrefixInit.class);
	}
	
	public OrePrefixInit() {
	}
	
	@EventMaterial
	public void iterateMaterial(Materials material) {
		for (IMaterialHandler reg : MaterialList.MATERIAL_HANDLERS) {
			reg.onComponentIteration(material);
		}
	}
}