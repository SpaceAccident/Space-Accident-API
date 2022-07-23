package space.accident.main.proxy;

import cpw.mods.fml.common.event.*;

public class CommonProxy implements IProxySide {
	
	@Override
	public boolean isClientSide() {
		return false;
	}
	
	@Override
	public boolean isServerSide() {
		return true;
	}
	
	public void onPreLoad(FMLPreInitializationEvent e) {

	}
	
	public void onLoad(FMLInitializationEvent e) {
	}
	
	public void onPostLoad(FMLPostInitializationEvent e) {
	
	}
	
	public void onServerStarted(FMLServerStartedEvent e) {
	}
	
	public void onServerAboutToStart(FMLServerAboutToStartEvent e) {
	
	}
	
	public void onServerStarting(FMLServerStartingEvent e) {
	
	}
	
	public void onServerStopping(FMLServerStoppingEvent e) {
	
	}
}
