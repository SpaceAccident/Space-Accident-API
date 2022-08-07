package space.accident.main.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLModIdMappingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import space.accident.api.API;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.objects.ItemStackData;
import space.accident.api.recipe.RecipeMaps;
import space.accident.api.util.SpaceLog;
import space.accident.api.util.Utility;

import java.io.File;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static space.accident.api.API.METATILEENTITIES;

public class ServerEvents {
	
	public static ServerEvents INSTANCE;
	
	public static ReentrantLock TICK_LOCK = new ReentrantLock();
	
	public static void register() {
		ServerEvents events = new ServerEvents();
		INSTANCE = events;
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
	}
	
	private boolean isFirstServerWorldTick = true;
	private boolean isFirstWorldTick = true;
	
	public int mTicksUntilNextCraftSound = 0;
	
	@SubscribeEvent
	public void onServerTickEvent(TickEvent.ServerTickEvent aEvent) {
		if (aEvent.side.isServer()) {
			if (aEvent.phase == TickEvent.Phase.START) {
				TICK_LOCK.lock();
			} else {
				TICK_LOCK.unlock();
			}
		}
		
	}
	
	@SubscribeEvent
	public void onWorldTickEvent(TickEvent.WorldTickEvent e) {
		if(e.world.provider.dimensionId == 0) {
			mTicksUntilNextCraftSound--;
		}
		if (e.side.isServer()) {
			if (this.isFirstServerWorldTick) {
				File tSaveDiretory = e.world.getSaveHandler().getWorldDirectory();
				if (tSaveDiretory != null) {
					this.isFirstServerWorldTick = false;
					try {
						for (IMetaTile tMetaTileEntity : METATILEENTITIES) {
							if (tMetaTileEntity != null) {
								tMetaTileEntity.onWorldLoad(tSaveDiretory);
							}
						}
					} catch (Throwable t) {
						t.printStackTrace(SpaceLog.err);
					}
				}
			}
		}
	}
	
	@Mod.EventHandler
	public void onIDChangingEvent(FMLModIdMappingEvent aEvent) {
		Utility.reInit();
		RecipeMaps.reInit();
		try {
			for (Map<ItemStackData, ?> gt_itemStackMap : API.sItemStackMappings) {
				Utility.reMap((Map) gt_itemStackMap);
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
	}
}
