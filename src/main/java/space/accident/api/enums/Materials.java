package space.accident.api.enums;

import net.minecraft.enchantment.Enchantment;
import space.accident.api.interfaces.IColorModulationContainer;
import space.accident.api.interfaces.ISubTagContainer;
import space.accident.api.objects.MaterialStack;

import java.awt.*;
import java.util.List;
import java.util.*;

import static space.accident.api.API.MAX_MATERIALS;
import static space.accident.api.API.sGeneratedMaterials;
import static space.accident.api.enums.MaterialList.MATERIALS_MAP;
import static space.accident.api.enums.MaterialList.getMaterialsMap;
import static space.accident.api.enums.MaterialType.*;

public class Materials implements IColorModulationContainer, ISubTagContainer {
	
	public final int[] mRGBa;
	public String name, localName, chemicalFormula;
	public int id, durability = 16, mToolQuality = 0;
	public float mToolSpeed = 1f, mHeatDamage = 0.0F;
	public List<MaterialType> types = new ArrayList<>();
	public TextureSet icon;
	public Collection<SubTag> subTags = new LinkedHashSet<>();
	public Color color;
	
	//todo
	public List<MaterialStack> materialList = new ArrayList<>();
	
	public Element element;
	public Enchantment enchantmentTools = null, enchantmentArmors = null;
	
	public int enchantmentToolsLevel = 0, enchantmentArmorsLevel = 0;
	
	public Materials(int id, String localName, TextureSet icon, Color color, Element el, List<MaterialType> types) {
		this.name            = localName;
		this.id              = id;
		this.localName       = localName;
		this.icon            = icon;
		this.color           = color;
		this.element         = el;
		this.chemicalFormula = el.element;
		this.mRGBa           = new int[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
		this.types.addAll(types);
		MATERIALS_MAP.put(name, this);
	}
	
	public Materials(int id, String localName, Color color, Element el, List<MaterialType> types) {
		this(id, localName, new TextureSet(types), color, el, types);
	}
	
	public Materials(int id, String localName, Color color, Element el, MaterialType... types) {
		this(id, localName, color, el, Arrays.asList(types));
	}
	
	public Materials setFormula(String formula) {
		chemicalFormula = formula;
		return this;
	}
	
	public static String getLocalizedNameForItem(String aFormat, int aMaterialID) {
		if (aMaterialID >= 0 && aMaterialID < MAX_MATERIALS) {
			Materials aMaterial = sGeneratedMaterials[aMaterialID];
			if (aMaterial != null) return aMaterial.getLocalizedNameForItem(aFormat);
		}
		return aFormat;
	}
	
	public static Materials get(String aMaterialName) {
		Materials aMaterial = getMaterialsMap().get(aMaterialName);
		if (aMaterial != null) return aMaterial;
		return MaterialList._NULL;
	}
	
	public static Materials getRealMaterial(String aMaterialName) {
		return get(aMaterialName);
	}
	
	public void setDurability(int durability) {
		this.durability = durability;
	}
	
	public boolean isGeneratedType(MaterialType type) {
		return types.contains(type);
	}
	
	public String getToolTip() {
		return getToolTip(1, false);
	}
	
	public String getToolTip(boolean aShowQuestionMarks) {
		return getToolTip(1, aShowQuestionMarks);
	}
	
	public String getToolTip(long aMultiplier) {
		return getToolTip(aMultiplier, false);
	}
	
	public void createWires(MaterialType... cables) {
		types.addAll(Arrays.asList(cables));
	}
	
	public void createAllWires() {
		types.addAll(Arrays.asList(WIRE1, WIRE2, WIRE3, WIRE4, WIRE6, WIRE8, WIRE9, WIRE12, WIRE16));
	}
	
	public boolean isGeneratedType(OrePrefixes ore) {
		for (MaterialType type : types) {
			if (type.ore == ore) return true;
		}
		return false;
	}
	
	public String getToolTip(long aMultiplier, boolean aShowQuestionMarks) {
		if (!aShowQuestionMarks && chemicalFormula.equals("?")) return "";
		if (!materialList.isEmpty()) {
			return ((element != null || (materialList.size() < 2 && materialList.get(0).mAmount == 1)) ? chemicalFormula : "(" + chemicalFormula + ")") + aMultiplier;
		}
		return chemicalFormula;
	}
	
	@Override
	public int[] getRGBA() {
		return mRGBa;
	}
	
	public String getLocalizedNameForItem(String aFormat) {
		return String.format(aFormat.replace("%s", "%temp").replace("%material", "%s"), this.localName).replace("%temp", "%s");
	}
	
	public String getDefaultLocalizedNameForItem(String aFormat) {
		return String.format(aFormat.replace("%s", "%temp").replace("%material", "%s"), this.localName).replace("%temp", "%s");
	}
	
	public Materials setEnchantmentForTools(Enchantment aEnchantment, int aEnchantmentLevel) {
		enchantmentTools      = aEnchantment;
		enchantmentToolsLevel = aEnchantmentLevel;
		return this;
	}
	
	public Materials setEnchantmentForArmors(Enchantment aEnchantment, int aEnchantmentLevel) {
		enchantmentArmors      = aEnchantment;
		enchantmentArmorsLevel = aEnchantmentLevel;
		return this;
	}
	
	@Override
	public boolean contains(SubTag aTag) {
		return subTags.contains(aTag);
	}
	
	@Override
	public ISubTagContainer add(SubTag... aTags) {
		if (aTags != null) for (SubTag aTag : aTags)
			if (aTag != null && !contains(aTag)) {
				aTag.addContainerToList(this);
				subTags.add(aTag);
			}
		return this;
	}
	
	@Override
	public boolean remove(SubTag aTag) {
		return subTags.remove(aTag);
	}
}