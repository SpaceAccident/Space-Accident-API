package space.accident.main.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Materials;
import space.accident.api.enums.OrePrefixes;
import space.accident.api.util.SpaceLog;

import static space.accident.api.util.Utility.copyAmount;

public class OreDictionaryEvent {
	
	public OreDictionaryEvent() {
		try {
			for (String tOreName : OreDictionary.getOreNames()) {
				for (ItemStack stack : OreDictionary.getOres(tOreName)) {
					registerOre(new OreDictionary.OreRegisterEvent(tOreName, stack));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace(SpaceLog.err);
		}
	}
	
	public static void register() {
		OreDictionaryEvent events = new OreDictionaryEvent();
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
	}
	
	public static void registerRecipes(OreDictEventContainer ore) {
		if ((ore.mEvent.Ore == null) || (ore.mEvent.Ore.getItem() == null)) {
			return;
		}
		if (ore.mEvent.Ore.stackSize != 1) {
			ore.mEvent.Ore.stackSize = 1;
		}
		if (ore.mPrefix != null) {
			
			ore.mPrefix.processOre(ore.mMaterial == null ? MaterialList._NULL : ore.mMaterial, ore.mEvent.Name, ore.mModID, copyAmount(1L, ore.mEvent.Ore));
		}
	}
	
	@SubscribeEvent
	public void registerOre(OreDictionary.OreRegisterEvent e) {
		
		ModContainer tContainer = Loader.instance().activeModContainer();
		String aMod = tContainer == null ? "UNKNOWN" : tContainer.getModId();
		
		OrePrefixes ore = OrePrefixes.getOrePrefix(e.Name);
		
		if (ore == null) return;
		
		String name = e.Name.replaceFirst(ore.toString(), "");
		Materials aMaterial = Materials.get(name);
		
		OreDictEventContainer tOre = new OreDictEventContainer(e, ore, aMaterial, aMod);
		registerRecipes(tOre);
	}
	
	public static class OreDictEventContainer {
		public final OreDictionary.OreRegisterEvent mEvent;
		public final OrePrefixes mPrefix;
		public final Materials mMaterial;
		public final String mModID;
		
		public OreDictEventContainer(OreDictionary.OreRegisterEvent aEvent, OrePrefixes aPrefix, Materials aMaterial, String aModID) {
			this.mEvent    = aEvent;
			this.mPrefix   = aPrefix;
			this.mMaterial = aMaterial;
			this.mModID    = ((aModID == null) || (aModID.equals("UNKNOWN")) ? null : aModID);
		}
	}
}
