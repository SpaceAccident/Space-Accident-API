package space.accident.api.enums;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import space.accident.api.API;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;
import space.accident.api.render.TextureFactory;

import static space.accident.api.enums.Values.RES_PATH_BLOCK;
import static space.accident.api.enums.Values.RES_PATH_ITEM;

public class Textures {
	private static final int TEXTURE_PAGES = 256;
	private static final int AMOUNT_TEXTURES_IN_PAGE = 128;
	public static ITexture[][] MACHINE_CASINGS = new ITexture[16][17];
	
	public static void addTexturePage(int page) {
		casingTexturePages[page] = new ITexture[AMOUNT_TEXTURES_IN_PAGE];
	}
	
	public static ITexture getTextureByCasingId(int casingPage, int id) {
		return casingTexturePages[casingPage][id];
	}
	
	/**
	 * by Default pages are null
	 */
	public static ITexture[][] casingTexturePages = new ITexture[TEXTURE_PAGES][];
	
	@SuppressWarnings("ALL")
	public static void register() {
		Textures.BlockIcons.NONE.name();
		Textures.ItemIcons.NONE.name();
	}
	
	public enum BlockIcons implements IIconContainer, Runnable {
		
		RENDERING_ERROR, HIDDEN_FACE,
		
		INSULATION_FULL, INSULATION_TINY, INSULATION_SMALL, INSULATION_MEDIUM, INSULATION_MEDIUM_PLUS, INSULATION_LARGE, INSULATION_HUGE,
		
		PIPE_RESTRICTOR,
		PIPE_RESTRICTOR_UP, PIPE_RESTRICTOR_DOWN, PIPE_RESTRICTOR_LEFT, PIPE_RESTRICTOR_RIGHT, PIPE_RESTRICTOR_NU, PIPE_RESTRICTOR_ND, PIPE_RESTRICTOR_NL, PIPE_RESTRICTOR_NR,
		PIPE_RESTRICTOR_UD, PIPE_RESTRICTOR_UL, PIPE_RESTRICTOR_UR, PIPE_RESTRICTOR_DL, PIPE_RESTRICTOR_DR, PIPE_RESTRICTOR_LR,
		
		CFOAM_FRESH, CFOAM_HARDENED,
		
		
		MACHINE_ULV_SIDE, MACHINE_LV_SIDE, MACHINE_MV_SIDE, MACHINE_HV_SIDE, MACHINE_EV_SIDE,
		MACHINE_IV_SIDE, MACHINE_LuV_SIDE, MACHINE_ZPM_SIDE, MACHINE_UV_SIDE, MACHINE_MAX_SIDE,
		MACHINE_UEV_SIDE, MACHINE_UIV_SIDE, MACHINE_UMV_SIDE, MACHINE_UXV_SIDE, MACHINE_OPV_SIDE,
		MACHINE_MAXV_SIDE,
		
		
		MACHINE_ULV_TOP, MACHINE_LV_TOP, MACHINE_MV_TOP, MACHINE_HV_TOP, MACHINE_EV_TOP,
		MACHINE_IV_TOP, MACHINE_LuV_TOP, MACHINE_ZPM_TOP, MACHINE_UV_TOP, MACHINE_MAX_TOP,
		MACHINE_UEV_TOP, MACHINE_UIV_TOP, MACHINE_UMV_TOP, MACHINE_UXV_TOP, MACHINE_OPV_TOP,
		MACHINE_MAXV_TOP,
		
		
		MACHINE_ULV_BOTTOM, MACHINE_LV_BOTTOM, MACHINE_MV_BOTTOM, MACHINE_HV_BOTTOM, MACHINE_EV_BOTTOM,
		MACHINE_IV_BOTTOM, MACHINE_LuV_BOTTOM, MACHINE_ZPM_BOTTOM, MACHINE_UV_BOTTOM, MACHINE_MAX_BOTTOM,
		MACHINE_UEV_BOTTOM, MACHINE_UIV_BOTTOM, MACHINE_UMV_BOTTOM, MACHINE_UXV_BOTTOM, MACHINE_OPV_BOTTOM,
		MACHINE_MAXV_BOTTOM,
		
		OVERLAY_PIPE, OVERLAY_PIPE_IN, OVERLAY_PIPE_OUT,
		OVERLAY_INPUT_HATCH_2x2,
		FLUID_OUT_SIGN, FLUID_IN_SIGN,
		ITEM_IN_SIGN, ITEM_OUT_SIGN,
		OVERLAY_MUFFLER,
		
		OVERLAY_ENERGY_IN, OVERLAY_ENERGY_OUT,
		OVERLAY_ENERGY_IN_MULTI, OVERLAY_ENERGY_OUT_MULTI,
		OVERLAY_ENERGY_IN_POWER, OVERLAY_ENERGY_OUT_POWER,
		
		OVERLAY_AUTOMAINTENANCE, OVERLAY_AUTOMAINTENANCE_GLOW, OVERLAY_AUTOMAINTENANCE_IDLE, OVERLAY_AUTOMAINTENANCE_IDLE_GLOW,
		OVERLAY_DUCTTAPE, OVERLAY_MAINTENANCE,
		
		
		NONE;
		
		public static final ITexture[] ERROR_RENDERING = {
				TextureFactory.of(RENDERING_ERROR)
		};
		
		public static final ITexture[] HIDDEN_TEXTURE = {
				TextureFactory.builder().addIcon(HIDDEN_FACE).stdOrient().build()
		};
		
		public static final ITexture[] FRESHFOAM = {TextureFactory.of(CFOAM_FRESH)};
		
		/**
		 * Icons for Hardened CFoam
		 * 0 = No Color
		 * 1 - 16 = Colors
		 */
		public static final ITexture[][] HARDENEDFOAMS = {
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.CONSTRUCTION_FOAM.mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[0].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[1].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[2].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[3].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[4].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[5].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[6].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[7].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[8].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[9].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[10].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[11].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[12].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[13].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[14].mRGBa)},
				new ITexture[]{TextureFactory.of(CFOAM_HARDENED, Colors.VALUES[15].mRGBa)}
		};
		
		public static final ITexture[] OVERLAYS_ENERGY_IN = {
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN, new int[]{40, 40, 245, 0}),
		};
		public static final ITexture[] OVERLAYS_ENERGY_OUT = {
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT, new int[]{40, 40, 245, 0}),
		};
		public static final ITexture[] OVERLAYS_ENERGY_IN_MULTI = {
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_MULTI, new int[]{40, 40, 245, 0}),
		};
		public static final ITexture[] OVERLAYS_ENERGY_OUT_MULTI = {
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_MULTI, new int[]{40, 40, 245, 0}),
		};
		public static final ITexture[] OVERLAYS_ENERGY_IN_POWER = {
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_IN_POWER, new int[]{40, 40, 245, 0}),
		};
		public static final ITexture[] OVERLAYS_ENERGY_OUT_POWER = {
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{180, 180, 180, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{220, 220, 220, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{255, 100, 0, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{255, 255, 30, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{128, 128, 128, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{240, 240, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{220, 220, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{200, 200, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{180, 180, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{160, 160, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{140, 140, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{120, 120, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{100, 100, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{80, 80, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{60, 60, 245, 0}),
				TextureFactory.of(OVERLAY_ENERGY_OUT_POWER, new int[]{40, 40, 245, 0}),
		};
		/**
		 * Machine Casings by Tier
		 * 0 = ULV, 1 = LV, 2 = MV, 3 = HV, 4 = EV, 5 = IV, 6 = IV, 7 = IV, 8 = IV, 9 = IV
		 */
		public static final IIconContainer[] MACHINECASINGS_SIDE = {
				MACHINE_ULV_SIDE,
				MACHINE_LV_SIDE,
				MACHINE_MV_SIDE,
				MACHINE_HV_SIDE,
				MACHINE_EV_SIDE,
				MACHINE_IV_SIDE,
				MACHINE_LuV_SIDE,
				MACHINE_ZPM_SIDE,
				MACHINE_UV_SIDE,
				MACHINE_MAX_SIDE,
				MACHINE_UEV_SIDE,
				MACHINE_UIV_SIDE,
				MACHINE_UMV_SIDE,
				MACHINE_UXV_SIDE,
				MACHINE_OPV_SIDE,
				MACHINE_MAXV_SIDE,
		};
		public static final IIconContainer[] MACHINECASINGS_TOP = {
				MACHINE_ULV_TOP,
				MACHINE_LV_TOP,
				MACHINE_MV_TOP,
				MACHINE_HV_TOP,
				MACHINE_EV_TOP,
				MACHINE_IV_TOP,
				MACHINE_LuV_TOP,
				MACHINE_ZPM_TOP,
				MACHINE_UV_TOP,
				MACHINE_MAX_TOP,
				MACHINE_UEV_TOP,
				MACHINE_UIV_TOP,
				MACHINE_UMV_TOP,
				MACHINE_UXV_TOP,
				MACHINE_OPV_TOP,
				MACHINE_MAXV_TOP,
		};
		public static final IIconContainer[] MACHINECASINGS_BOTTOM = {
				MACHINE_ULV_BOTTOM,
				MACHINE_LV_BOTTOM,
				MACHINE_MV_BOTTOM,
				MACHINE_HV_BOTTOM,
				MACHINE_EV_BOTTOM,
				MACHINE_IV_BOTTOM,
				MACHINE_LuV_BOTTOM,
				MACHINE_ZPM_BOTTOM,
				MACHINE_UV_BOTTOM,
				MACHINE_MAX_BOTTOM,
				MACHINE_UEV_BOTTOM,
				MACHINE_UIV_BOTTOM,
				MACHINE_UMV_BOTTOM,
				MACHINE_UXV_BOTTOM,
				MACHINE_OPV_BOTTOM,
				MACHINE_MAXV_BOTTOM,
		};
		
		private IIcon icon;
		
		BlockIcons() {
			API.sGTBlockIconload.add(this);
		}
		
		static {
			
			for (int i = 0; i < MACHINE_CASINGS.length; i++) {
				for (int j = 0; j < MACHINE_CASINGS[i].length; j++) {
					MACHINE_CASINGS[i][j] = TextureFactory.of(
							MACHINECASINGS_BOTTOM[i], MACHINECASINGS_TOP[i], MACHINECASINGS_SIDE[i],
							Colors.getModulation(j - 1, Colors.MACHINE_METAL.mRGBa)
					);
				}
			}
			
			addTexturePage(0);
		}
		
		@Override
		public IIcon getIcon() {
			return icon;
		}
		
		@Override
		public IIcon getOverlayIcon() {
			return null;
		}
		
		@Override
		public ResourceLocation getTextureFile() {
			return TextureMap.locationBlocksTexture;
		}
		
		@Override
		public void run() {
			icon = API.sBlockIcons.registerIcon(RES_PATH_BLOCK + "iconsets/" + this);
		}
		
		public static class CustomIcon implements IIconContainer, Runnable {
			protected IIcon mIcon;
			protected String mIconName;
			
			public CustomIcon(String aIconName) {
				mIconName = aIconName;
				API.sGTBlockIconload.add(this);
			}
			
			@Override
			public IIcon getIcon() {
				return mIcon;
			}
			
			@Override
			public IIcon getOverlayIcon() {
				return null;
			}
			
			@Override
			public ResourceLocation getTextureFile() {
				return TextureMap.locationBlocksTexture;
			}
			
			@Override
			public void run() {
				mIcon = API.sBlockIcons.registerIcon(RES_PATH_BLOCK + mIconName);
			}
		}
	}
	
	public enum ItemIcons implements IIconContainer, Runnable {
		NONE,
		WRENCH,
		
		
		
		DURABILITY_BAR_01, DURABILITY_BAR_02, DURABILITY_BAR_03, DURABILITY_BAR_04, DURABILITY_BAR_05, DURABILITY_BAR_06, DURABILITY_BAR_07, DURABILITY_BAR_08, DURABILITY_BAR_09,
		ENERGY_BAR_01, ENERGY_BAR_02, ENERGY_BAR_03, ENERGY_BAR_04, ENERGY_BAR_05, ENERGY_BAR_06, ENERGY_BAR_07, ENERGY_BAR_08, ENERGY_BAR_09,
		
		RENDERING_ERROR;
		
		public static final IIconContainer[]
				DURABILITY_BAR = {
				DURABILITY_BAR_01,
				DURABILITY_BAR_02,
				DURABILITY_BAR_03,
				DURABILITY_BAR_04,
				DURABILITY_BAR_05,
				DURABILITY_BAR_06,
				DURABILITY_BAR_07,
				DURABILITY_BAR_08,
				DURABILITY_BAR_09,
		},
				ENERGY_BAR = {
						ENERGY_BAR_01,
						ENERGY_BAR_02,
						ENERGY_BAR_03,
						ENERGY_BAR_04,
						ENERGY_BAR_05,
						ENERGY_BAR_06,
						ENERGY_BAR_07,
						ENERGY_BAR_08,
						ENERGY_BAR_09,
				};
		
		private IIcon icon, overlay;
		
		ItemIcons() {
			API.sGTItemIconload.add(this);
		}
		
		@Override
		public IIcon getIcon() {
			return icon;
		}
		
		@Override
		public IIcon getOverlayIcon() {
			return overlay;
		}
		
		@Override
		public ResourceLocation getTextureFile() {
			return TextureMap.locationItemsTexture;
		}
		
		@Override
		public void run() {
			icon = API.sItemIcons.registerIcon(RES_PATH_ITEM + "iconsets/" + this);
			overlay = API.sItemIcons.registerIcon(RES_PATH_ITEM + "iconsets/" + this + "_OVERLAY");
		}
		
		public static class CustomIcon implements IIconContainer, Runnable {
			protected IIcon mIcon, mOverlay;
			protected String mIconName;
			
			public CustomIcon(String aIconName) {
				mIconName = aIconName;
				API.sGTItemIconload.add(this);
			}
			
			@Override
			public IIcon getIcon() {
				return mIcon;
			}
			
			@Override
			public IIcon getOverlayIcon() {
				return mOverlay;
			}
			
			@Override
			public ResourceLocation getTextureFile() {
				return TextureMap.locationItemsTexture;
			}
			
			@Override
			public void run() {
				mIcon = API.sItemIcons.registerIcon(RES_PATH_ITEM + mIconName);
				mOverlay = API.sItemIcons.registerIcon(RES_PATH_ITEM + mIconName + "_OVERLAY");
			}
		}
	}
}
