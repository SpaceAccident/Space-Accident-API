package space.accident.main.loading;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import space.accident.api.util.GT_LanguageManager;
import space.accident.main.render.MetaGeneratedItemRenderer;

public class Loading {
	public static void init(FMLInitializationEvent e) {
		initLang();
		initRender();
	}
	
	private static void initRender() {
		new MetaGeneratedItemRenderer();
	}
	
	private static void initLang() {
		GT_LanguageManager.writePlaceholderStrings();
	}
}