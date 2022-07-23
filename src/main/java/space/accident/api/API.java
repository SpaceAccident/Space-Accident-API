package space.accident.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import space.accident.api.enums.Materials;
import space.accident.api.objects.GT_ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class API {
	
	/**
	 * These Lists are getting executed at their respective timings.
	 * Useful if you have to do things right before/after I do them, without having to control the load order.
	 * Add your "Commands" in the Constructor or in a static Code Block of your Mods Main Class.
	 * These are not Threaded, I just use a native Java Interface for their execution.
	 * Implement just the Method run() and everything should work
	 */
	public static List<Runnable>
			sBeforeGTPreload = new ArrayList<>(),
			sAfterGTPreload = new ArrayList<>(),
			sBeforeGTLoad = new ArrayList<>(),
			sAfterGTLoad = new ArrayList<>(),
			sBeforeGTPostload = new ArrayList<>(),
			sAfterGTPostload = new ArrayList<>(),
			sFirstWorldTick = new ArrayList<>(),
			sBeforeGTServerstart = new ArrayList<>(),
			sAfterGTServerstart = new ArrayList<>(),
			sBeforeGTServerstop = new ArrayList<>(),
			sAfterGTServerstop = new ArrayList<>(),
			sGTBlockIconload = new ArrayList<>(),
			sGTItemIconload = new ArrayList<>();
	
	/**
	 * Getting assigned by the Mod loading
	 */
	public static boolean
			sUnificationEntriesRegistered = false,
			sPreloadStarted = false,
			sPreloadFinished = false,
			sLoadStarted = false,
			sLoadFinished = false,
			sPostloadStarted = false,
			sPostloadFinished = false;
	
	@SideOnly(Side.CLIENT)
	public static IIconRegister
			sBlockIcons,
			sItemIcons;
	
	public static final int MAX_MATERIALS = 2000;
	public static final Materials[] sGeneratedMaterials = new Materials[MAX_MATERIALS];
	
	
	//TODO TRANSFER TO CONFIG
	public static boolean sDoShowAllItemsInCreative = false;
	
	public static final Collection<Map<GT_ItemStack, ?>> sItemStackMappings = new ArrayList<>();
	
}
