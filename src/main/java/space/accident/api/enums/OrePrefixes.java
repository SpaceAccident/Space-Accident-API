package space.accident.api.enums;

import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.ICondition;
import space.accident.api.interfaces.ISubTagContainer;
import space.accident.api.objects.ItemData;
import space.accident.api.recipe.IOreProcessing;
import space.accident.api.recipe.RecipeInteractions;

import java.util.*;

import static space.accident.api.util.Utility.copyAmount;
import static space.accident.extensions.ItemStackUtils.isStackInvalid;
import static space.accident.extensions.ItemStackUtils.isStackValid;

public enum OrePrefixes {
	
	ingot("Ingots", "Ingot", 11, 64),
	dust("Dusts", "Dust", 2, 64),
	plate("Plates", "Plate", 17, 64),
	block("Blocks", "Block", 71, 64),
	
	wireGt1("1x Wires", "1x ", " Wire", 64),
	wireGt2("2x Wires", "2x ", " Wire", 64),
	wireGt3("3x Wires", "3x ", " Wire", 64),
	wireGt4("4x Wires", "4x ", " Wire", 64),
	wireGt6("6x Wires", "6x ", " Wire", 64),
	wireGt8("8x Wires", "8x ", " Wire", 64),
	wireGt9("9x Wires", "9x ", " Wire", 64),
	wireGt12("12x Wires", "12x ", " Wire", 64),
	wireGt16("16x Wires", "16x ", " Wire", 64),
	
	cableGt1("1x Cables", "1x ", " Cable", 64),
	cableGt2("2x Cables", "2x ", " Cable", 64),
	cableGt3("3x Cables", "3x ", " Cable", 64),
	cableGt4("4x Cables", "4x ", " Cable", 64),
	cableGt6("6x Cables", "6x ", " Cable", 64),
	cableGt8("8x Cables", "8x ", " Cable", 64),
	cableGt9("9x Cables", "9x ", " Cable", 64),
	cableGt12("12x Cables", "12x ", " Cable", 64),
	cableGt16("16x Cables", "16x ", " Cable", 64),
	
	pipe("Pipes", " Pipe", 77,64),
	pipeTiny("Tiny Pipes", "Tiny ", " Pipe", 78,64),
	pipeSmall("Small Pipes", "Small ", " Pipe", 79,64),
	pipeMedium("Medium Pipes", "Medium ", " Pipe", 80,64),
	pipeLarge("Large pipes", "Large ", " Pipe", 81,64),
	pipeHuge("Huge Pipes", "Huge ", " Pipe", 82,64),
	pipeQuadruple("Quadruple Pipes", "Quadruple ", " Pipe", 84,64),
	pipeNonuple("Nonuple Pipes", "Nonuple ", " Pipe", 85,64),
	
	;
	
	
	public static final List<OrePrefixes> mPreventableComponents = new LinkedList<>();
	public final String localName, prefix, postfix;
	public final int textureId, stackSize;
	public final Collection<Materials>
			mDisabledItems = new HashSet<Materials>(),
			mNotGeneratedItems = new HashSet<Materials>(),
			mIgnoredMaterials = new HashSet<Materials>(),
			mGeneratedItems = new HashSet<Materials>();
	public ItemStack mContainerItem = null;
	public ICondition<ISubTagContainer> mCondition = null;
	private final HashSet<IOreProcessing> oreProcessing = new HashSet<>();
	
	OrePrefixes(String localName, String prefix, String postfix, int textureId, int stackSize) {
		this.localName = localName;
		this.prefix    = prefix.length() == 0 ? "" : prefix + " ";
		this.postfix   = postfix.length() == 0 ? "" : " " + postfix;
		this.textureId = textureId;
		this.stackSize = stackSize;
	}
	
	OrePrefixes(String localName, String prefix, String postfix, int stackSize) {
		this.localName = localName;
		this.prefix    = prefix.length() == 0 ? "" : postfix + " ";
		this.postfix   = postfix.length() == 0 ? "" : " " + postfix;
		this.textureId = -1;
		this.stackSize = stackSize;
	}
	
	OrePrefixes(String localName, String postfix, int textureId, int stackSize) {
		this(localName, "", postfix, textureId, stackSize);
	}
	
	public boolean add(IOreProcessing reg) {
		if (reg == null) return false;
		return oreProcessing.add(reg);
	}
	
	public String get(Materials aMaterial) {
		if (aMaterial != null) return new ItemData(this, aMaterial).toString();
		return name();
	}
	
	public boolean doGenerateItem(Materials aMaterial) {
		return aMaterial != null
				&& aMaterial != MaterialList._NULL
				&& (aMaterial.types.stream().anyMatch(materialType -> materialType.ore == this) || mGeneratedItems.contains(aMaterial))
				&& !mNotGeneratedItems.contains(aMaterial)
				&& !mDisabledItems.contains(aMaterial)
				&& (mCondition == null || mCondition.isTrue(aMaterial));
	}
	
	public String getDefaultLocalNameForItem(Materials aMaterial) {
		return aMaterial.getDefaultLocalizedNameForItem(getDefaultLocalNameFormatForItem(aMaterial));
	}
	
	public String getDefaultLocalNameFormatForItem(Materials aMaterial) {
		// Use Standard Localization
		return prefix + "%material" + postfix;
	}
	
	public void processOre(Materials aMaterial, String aOreDictName, String aModName, ItemStack stack) {
		if (aMaterial != null && (aMaterial != MaterialList._NULL) && isStackValid(stack)) {
			for (IOreProcessing reg : RecipeInteractions.ORE_PROCESSING_LIST) {
				reg.registerOre(this, aMaterial, aOreDictName, aModName, copyAmount(1, stack));
			}
		}
	}
	
	public static OrePrefixes getOrePrefix(String aOre) {
		for (OrePrefixes tPrefix : values()) {
			if (aOre.startsWith(tPrefix.toString())) {
				return tPrefix;
			}
		}
		return null;
	}
}