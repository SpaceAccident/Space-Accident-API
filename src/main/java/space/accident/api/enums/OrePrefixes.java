package space.accident.api.enums;

import net.minecraft.item.ItemStack;
import space.accident.api.interfaces.ICondition;
import space.accident.api.interfaces.ISubTagContainer;
import space.accident.api.objects.ItemData;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static space.accident.api.enums.MaterialType.*;

public enum OrePrefixes {
	
	ingot("Ingots", "Ingot", 11, INGOT_DEFAULT, 64),
	dust("Dusts", "Dust", 2, DUST_DEFAULT, 64),
	plate("Plates", "Plate", 17, PLATE_DEFAULT, 64),
	;
	
	
	public static List<OrePrefixes> mPreventableComponents = new LinkedList<>();
	public final String localName, prefix, postfix;
	public final int textureId, stackSize;
	public final MaterialType type;
	public final Collection<Materials>
			mDisabledItems = new HashSet<Materials>(),
			mNotGeneratedItems = new HashSet<Materials>(),
			mIgnoredMaterials = new HashSet<Materials>(),
			mGeneratedItems = new HashSet<Materials>();
	public ItemStack mContainerItem = null;
	public ICondition<ISubTagContainer> mCondition = null;
	
	OrePrefixes(String localName, String prefix, String postfix, int textureId, MaterialType type, int stackSize) {
		this.localName = localName;
		this.prefix    = prefix.length() == 0 ? "" : postfix + " ";
		this.postfix   = postfix.length() == 0 ? "" : " " + postfix;
		this.textureId = textureId;
		this.type      = type;
		this.stackSize = stackSize;
	}
	
	OrePrefixes(String localName, String postfix, int textureId, MaterialType type, int stackSize) {
		this(localName, "", postfix, textureId, type, stackSize);
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
	
	@SuppressWarnings("incomplete-switch")
	public String getDefaultLocalNameFormatForItem(Materials aMaterial) {
		// Use Standard Localization
		return prefix + "%material" + postfix;
	}
}