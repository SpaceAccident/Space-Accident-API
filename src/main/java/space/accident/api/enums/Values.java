package space.accident.api.enums;

import net.minecraftforge.oredict.OreDictionary;

import static space.accident.main.Tags.ASSETS;
import static space.accident.main.Tags.MODID;

public class Values {
	
	public static final String
			TEX_DIR = "textures/",
			TEX_DIR_GUI = TEX_DIR + "gui/",
			TEX_DIR_ITEM = TEX_DIR + "items/",
			TEX_DIR_BLOCK = TEX_DIR + "blocks/",
			TEX_DIR_ENTITY = TEX_DIR + "entity/",
			RES_PATH = ASSETS + ":" + TEX_DIR,
			RES_PATH_GUI = ASSETS + ":" + TEX_DIR_GUI,
			RES_PATH_ITEM = ASSETS + ":",
			RES_PATH_BLOCK = ASSETS + ":",
			RES_PATH_ENTITY = ASSETS + ":" + TEX_DIR_ENTITY,
			RES_PATH_MODEL = ASSETS + ":" + TEX_DIR + "models/";
	
	/**
	 * The Item WildCard Tag. Even shorter than the "-1" of the past
	 */
	public static final short W = OreDictionary.WILDCARD_VALUE;
	
	/**
	 * The Voltage Tiers. Use this Array instead of the old named Voltage Variables
	 */
	public static final long[] V =
			new long[]{
					8L, 32L, 128L,
					512L, 2048L, 8192L,
					32768L, 131072L, 524288L,
					2097152L, 8388608L, 33554432L,
					134217728L, 536870912L, 1073741824L,
					Integer.MAX_VALUE - 7};
}