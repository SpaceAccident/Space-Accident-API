package space.accident.main;

import cpw.mods.fml.common.Loader;

public class IntegrationConstants {
	
	public static final String IC2 = "IC2";
	public static boolean isIC2Loaded = Loader.isModLoaded(IC2);
	
	public static final String AE2 = "appliedenergistics2";
	public static boolean isAE2Loaded = Loader.isModLoaded(AE2);
	
	public static final String BC = "BuildCraft";
	public static boolean isBCLoaded = Loader.isModLoaded(BC);
	
	public static final String EIO = "EnderIO";
	public static boolean isEIOLoaded = Loader.isModLoaded(EIO);
	
	public static final String RC = "Railcraft";
	public static boolean isRCLoaded = Loader.isModLoaded(RC);
	
}
