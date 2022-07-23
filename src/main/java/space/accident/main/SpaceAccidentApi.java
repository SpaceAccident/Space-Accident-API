package space.accident.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import space.accident.api.API;
import space.accident.api.util.SpaceLog;
import space.accident.main.loading.PostLoading;
import space.accident.main.loading.PreLoading;
import space.accident.main.proxy.CommonProxy;
import space.accident.main.loading.Loading;

import static space.accident.main.Tags.*;

@Mod(modid = MODID, name = MODNAME, version = VERSION, dependencies = "required-after:IC2;")
public class SpaceAccidentApi {
	
	@Instance(MODID)
	public static SpaceAccidentApi INSTANCE;
	
	@SidedProxy(modId = MODID, clientSide = "space.accident.main.proxy.ClientProxy", serverSide = "space.accident.main.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void onPreLoad(FMLPreInitializationEvent e) {
		if (API.sPreloadStarted) return;
		SpaceLog.out.println(MODNAME + ": Pre Loading Started");
		PreLoading.init(e);
		proxy.onPreLoad(e);
		API.sPreloadFinished = true;
		SpaceLog.out.println(MODNAME + ": Pre Loading Finished");
	}
	
	@EventHandler
	public void onLoad(FMLInitializationEvent e) {
		if (API.sLoadStarted) return;
		SpaceLog.out.println(MODNAME + ": Loading Started");
		Loading.init(e);
		proxy.onLoad(e);
		SpaceLog.out.println(MODNAME + ": Loading Finished");
		API.sLoadFinished = true;
	}
	
	@EventHandler
	public void onPostLoad(FMLPostInitializationEvent e) {
		if (API.sPostloadStarted) return;
		SpaceLog.out.println(MODNAME + ": Post Loading Started");
		PostLoading.init(e);
		proxy.onPostLoad(e);
		SpaceLog.out.println(MODNAME + ": Post Loading Finished");
		API.sPostloadFinished = true;
	}
	
	@EventHandler
	public void onServerStarted(FMLServerStartedEvent e) {
		proxy.onServerStarted(e);
	}
	
	@EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent e) {
		proxy.onServerAboutToStart(e);
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		proxy.onServerStarting(e);
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent e) {
		proxy.onServerStopping(e);
	}
}