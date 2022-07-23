package space.accident.main.loading;


import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import space.accident.api.API;
import space.accident.api.util.GT_LanguageManager;

public class PostLoading {
	public static void init(FMLPostInitializationEvent e) {
		langPostInit();
		clearMemory();
	}
	
	private static void clearMemory() {
		API.sBeforeGTPreload  = null;
		API.sAfterGTPreload   = null;
		API.sBeforeGTLoad     = null;
		API.sAfterGTLoad      = null;
		API.sBeforeGTPostload = null;
		API.sAfterGTPostload  = null;
	}
	
	private static void langPostInit() {
		GT_LanguageManager.propagateLocalizationServerSide();
		GT_LanguageManager.sEnglishFile.save();
	}
}