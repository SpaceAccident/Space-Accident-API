package space.accident.main.proxy;

import cpw.mods.fml.common.event.*;
import net.minecraft.entity.player.EntityPlayer;
import space.accident.api.util.SA_ClientPreference;
import space.accident.main.threads.TileEntityUpdateThread;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommonProxy implements IProxySide {
	
	protected final ConcurrentMap<UUID, SA_ClientPreference> mClientPrefernces = new ConcurrentHashMap<>();
	
	@Override
	public boolean isClientSide() {
		return false;
	}
	
	@Override
	public boolean isServerSide() {
		return true;
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return null;
	}
	
	public void onPreLoad(FMLPreInitializationEvent e) {

	}
	
	public void onLoad(FMLInitializationEvent e) {
	}
	
	public void onPostLoad(FMLPostInitializationEvent e) {
	
	}
	
	public void onServerStarted(FMLServerStartedEvent e) {
		TileEntityUpdateThread.initExecutorService();
	}
	
	public void onServerAboutToStart(FMLServerAboutToStartEvent e) {
	
	}
	
	public void onServerStarting(FMLServerStartingEvent e) {
	
	}
	
	public void onServerStopping(FMLServerStoppingEvent e) {
		TileEntityUpdateThread.shutdownExecutorService();
	}
	
	public SA_ClientPreference getClientPreference(UUID aPlayerID) {
		return null;
	}
	
	public void setClientPreference(UUID aPlayerID, SA_ClientPreference aPreference) {
	
	}
}
