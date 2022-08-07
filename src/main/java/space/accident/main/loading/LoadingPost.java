package space.accident.main.loading;


import appeng.api.AEApi;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraft.item.ItemStack;
import space.accident.api.API;
import space.accident.api.interfaces.IRecipeHandler;
import space.accident.api.sound.Sounds;
import space.accident.api.util.LanguageManager;
import space.accident.main.IntegrationConstants;
import space.accident.main.common.covers.SA_Cover_FacadeAE;
import space.accident.main.proxy.GuiHandler;

import static space.accident.api.enums.Values.W;
import static space.accident.main.loading.ParentIntegrations.RECIPE_HANDLERS;

public class LoadingPost {
	public static void init(FMLPostInitializationEvent e) {
		
		RECIPE_HANDLERS.forEach(IRecipeHandler::init);
		
		initAE2Integration();
		
		Sounds.registerSounds();
		GuiHandler.register();
		clearMemory();
	}
	
	private static void initAE2Integration() {
		if (IntegrationConstants.isAE2Loaded) {
//			GT_MetaTileEntity_DigitalChestBase.registerAEIntegration();
			ItemStack facade = AEApi.instance().definitions().items().facade().maybeItem()
					.transform(i -> new ItemStack(i, 1, W)).orNull();
			if (facade != null) {
				API.registerCover(facade, null, new SA_Cover_FacadeAE());
			}
		}
	}
	
	private static void clearMemory() {
		API.sBeforeGTPreload  = null;
		API.sAfterGTPreload   = null;
		API.sBeforeGTLoad     = null;
		API.sAfterGTLoad      = null;
		API.sBeforeGTPostload = null;
		API.sAfterGTPostload  = null;
	}
}