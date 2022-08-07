package space.accident.main.loading;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import space.accident.api.enums.MaterialList;
import space.accident.api.util.LanguageManager;
import space.accident.main.crossmod.waila.WailaDataProvider;
import space.accident.main.events.ClientEvents;
import space.accident.main.events.ServerEvents;
import space.accident.main.render.FluidDisplayStackRenderer;
import space.accident.main.render.MetaGeneratedItemRenderer;
import space.accident.main.render.MetaGeneratedToolRenderer;

public class Loading {
	public static void init(FMLInitializationEvent e) {
		MaterialList.load();
		
		initRender();
		initEvents();
		crossModInit();
	}
	
	private static void initEvents() {
		ClientEvents.register();
		ServerEvents.register();
	}
	
	private static void initRender() {
		new MetaGeneratedItemRenderer();
		new MetaGeneratedToolRenderer();
		new FluidDisplayStackRenderer();
	}
	
	private static void crossModInit() {
		WailaDataProvider.register();
	}
}