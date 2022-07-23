package space.accident.api.interfaces;

import space.accident.api.enums.Materials;

public interface IMaterialHandler {
	void onMaterialsInit();
	void onComponentInit();
	void onComponentIteration(Materials aMaterial);
}