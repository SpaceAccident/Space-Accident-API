package space.accident.api.enums;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import space.accident.api.API;
import space.accident.api.interfaces.IIconContainer;
import space.accident.api.interfaces.ITexture;

import static space.accident.api.enums.Values.RES_PATH_BLOCK;
import static space.accident.api.enums.Values.RES_PATH_ITEM;

public class Textures {
	private static final int TEXTURE_PAGES = 256;
	private static final int AMOUNT_TEXTURES_IN_PAGE = 16;
	
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
	
	public enum BlockIcons implements IIconContainer, Runnable {
		NONE;
		
		private IIcon icon;
		
		BlockIcons() {
			API.sGTBlockIconload.add(this);
		}
		
		static {
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
		NONE, RENDERING_ERROR;
		
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
