package space.accident.api.enums;

import net.minecraftforge.oredict.OreDictionary;
import space.accident.api.util.SA_BlockMap;

import java.util.HashSet;
import java.util.Set;

import static space.accident.main.Tags.ASSETS;

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
	
	/**
	 * The first 32 Bits
	 */
	public static final int[] B = new int[]{
			1 << 0, 1 << 1, 1 << 2,
			1 << 3, 1 << 4, 1 << 5,
			1 << 6, 1 << 7, 1 << 8,
			1 << 9, 1 << 10, 1 << 11,
			1 << 12, 1 << 13, 1 << 14,
			1 << 15, 1 << 16, 1 << 17,
			1 << 18, 1 << 19, 1 << 20,
			1 << 21, 1 << 22, 1 << 23,
			1 << 24, 1 << 25, 1 << 26,
			1 << 27, 1 << 28, 1 << 29,
			1 << 30, 1 << 31};
	
	public static final int UNCOLORED = 0x00ffffff;
	
	/**
	 * Sides
	 */
	public static final int
			SIDE_BOTTOM    = 0, SIDE_DOWN      = 0,
			SIDE_TOP       = 1, SIDE_UP        = 1,
			SIDE_NORTH     = 2, // Also a Side with a stupidly mirrored Texture
			SIDE_SOUTH     = 3,
			SIDE_WEST      = 4,
			SIDE_EAST      = 5, // Also a Side with a stupidly mirrored Texture
			SIDE_ANY    = 6, SIDE_UNKNOWN   = 6, SIDE_INVALID = 6, SIDE_INSIDE = 6, SIDE_UNDEFINED = 6;
	
	/** Compass alike Array for the proper ordering of North, East, South and West. */
	public static final int[] COMPASS_DIRECTIONS = {SIDE_NORTH, SIDE_EAST, SIDE_SOUTH, SIDE_WEST};
	
	/**
	 * The short Names for the Voltages
	 */
	public static final String[] VN =
			new String[]{"ULV", "LV", "MV",
					"HV", "EV", "IV",
					"LuV", "ZPM", "UV",
					"UHV", "UEV", "UIV",
					"UMV", "UXV", "OpV",
					"MAX"};
	
	
	/**
	 * An Array containing all Sides which follow the Condition, in order to iterate over them for example.
	 */
	public static final int[]
			ALL_SIDES                    =  {0,1,2,3,4,5,6},
			ALL_VALID_SIDES              =  {0,1,2,3,4,5  };
	
	public static final int[] emptyIntArray = new int[0];
	
	public static final Set<String> mCTMEnabledBlock = new HashSet<>();
	public static final Set<String> mCTMDisabledBlock = new HashSet<>();
	
	public static final SA_BlockMap<Boolean> mCTMBlockCache = new SA_BlockMap<>();
}