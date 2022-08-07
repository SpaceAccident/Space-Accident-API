package space.accident.api.util;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import space.accident.api.API;
import space.accident.api.enums.MaterialList;
import space.accident.extensions.ItemStackUtils;
import space.accident.extensions.StringUtils;
import space.accident.main.SpaceAccidentApi;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class LanguageManager {
	
	public static String
			FACE_ANY = "lang.face.any",
			FACE_BOTTOM = "lang.face.bottom",
			FACE_TOP = "lang.face.top",
			FACE_LEFT = "lang.face.left",
			FACE_FRONT = "lang.face.front",
			FACE_RIGHT = "lang.face.right",
			FACE_BACK = "lang.face.back",
			FACE_NONE = "lang.face.none";
	
	public static String[] FACES = {FACE_BOTTOM, FACE_TOP, FACE_LEFT, FACE_FRONT, FACE_RIGHT, FACE_BACK, FACE_NONE};
	
	
	public static String addStringLocalization(String aKey, String aEnglish) {
		return addStringLocalization(aKey, aEnglish, true);
	}
	
	public static synchronized String addStringLocalization(String aKey, String aEnglish, boolean aWriteIntoLangFile) {
		if (aKey == null) return "";
		String translate = LanguageRegistry.instance().getStringLocalization(aKey);
		if(!translate.isEmpty()) return translate;
		LanguageRegistry.instance().addStringLocalization(aKey, "en_US", aEnglish);
		return aEnglish;
	}
	
	public static String getTranslation(String aKey) {
		if (aKey == null) return "";
		String tTrimmedKey = aKey.trim(), rTranslation = LanguageRegistry.instance().getStringLocalization(tTrimmedKey);
		if (StringUtils.isStringInvalid(rTranslation)) {
			rTranslation = StatCollector.translateToLocal(tTrimmedKey);
			if (StringUtils.isStringInvalid(rTranslation) || tTrimmedKey.equals(rTranslation)) {
				if (aKey.endsWith(".name")) {
					String substring = tTrimmedKey.substring(0, tTrimmedKey.length() - 5);
					rTranslation = StatCollector.translateToLocal(substring);
					if (StringUtils.isStringInvalid(rTranslation) || substring.equals(rTranslation)) {
						return aKey;
					}
				} else {
					rTranslation = StatCollector.translateToLocal(tTrimmedKey + ".name");
					if (StringUtils.isStringInvalid(rTranslation) || (tTrimmedKey + ".name").equals(rTranslation)) {
						return aKey;
					}
				}
			}
		}
		return rTranslation;
	}
	
	public static String getTranslation(String aKey, String aSeperator) {
		if (aKey == null) return "";
		StringBuilder rTranslationSB = new StringBuilder();
		for (String tString : aKey.split(aSeperator)) {
			rTranslationSB.append(getTranslation(tString));
		}
		return rTranslationSB.toString();
	}
	
	public static String getTranslateItemStackName(ItemStack stack) {
		if (ItemStackUtils.isStackInvalid(stack)) return "null";
		NBTTagCompound tNBT = stack.getTagCompound();
		if (tNBT != null && tNBT.hasKey("display")) {
			String tName = tNBT.getCompoundTag("display").getString("Name");
			if (StringUtils.isStringValid(tName)) {
				return tName;
			}
		}
		return stack.getUnlocalizedName() + ".name";
	}
	
	public static String addOverlayLocalization(String key, String english) {
		return addStringLocalization("OVERLAY_" + key, english);
	}
	
	public static void initLocalization(File mc) {
		MaterialList.getMaterialsMap().values()
				.parallelStream()
				.filter(Objects::nonNull)
				.forEach(aMaterial -> aMaterial.localName = LanguageManager.addStringLocalization("Material." + aMaterial.name.toLowerCase(), aMaterial.localName));
	}
}
