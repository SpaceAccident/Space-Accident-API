package space.accident.api.enums;

import space.accident.api.interfaces.IColorModulationContainer;
import space.accident.api.interfaces.ISubTagContainer;
import space.accident.api.objects.MaterialStack;

import java.awt.*;
import java.util.List;
import java.util.*;

import static space.accident.api.enums.MaterialList.MATERIALS_MAP;

public class Materials implements IColorModulationContainer, ISubTagContainer {
	
	public String
			name = "ERROR NAME",
			localName = "ERROR NAME",
			chemicalFormula = "";
	public int id;
	public TextureSet icon = TextureSet.NONE;
	public List<MaterialType> types = new ArrayList<>();
	
	//todo
	public List<MaterialStack> mMaterialList = new ArrayList<>();
	public Collection<SubTag> subTags = new LinkedHashSet<>();
	public Color color;
	
	public Element element = null;
	
	public Materials(int id, String localName, TextureSet icon, Color color, Element el, List<MaterialType> types) {
		this.name      = localName;
		this.id        = id;
		this.localName = localName;
		this.icon      = icon;
		this.types.addAll(types);
		this.color = color;
		this.element = el;
		this.chemicalFormula = el.element;
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
	
	public String getToolTip() {
		return getToolTip(1, false);
	}
	
	public String getToolTip(boolean aShowQuestionMarks) {
		return getToolTip(1, aShowQuestionMarks);
	}
	
	public String getToolTip(long aMultiplier) {
		return getToolTip(aMultiplier, false);
	}
	
	public String getToolTip(long aMultiplier, boolean aShowQuestionMarks) {
		if (!aShowQuestionMarks && chemicalFormula.equals("?"))
			return "";
		if (!mMaterialList.isEmpty()) {
			return ((element != null || (mMaterialList.size() < 2 && mMaterialList.get(0).mAmount == 1)) ? chemicalFormula : "(" + chemicalFormula + ")") + aMultiplier;
		}
		return chemicalFormula;
	}
	
	@Override
	public int[] getRGBA() {
		return new int[]{
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				color.getAlpha(),
		};
	}
	
	public String getLocalizedNameForItem(String aFormat) {
		return String.format(aFormat.replace("%s", "%temp")
				.replace("%material", "%s"), this.localName).replace("%temp", "%s");
	}
	
	public String getDefaultLocalizedNameForItem(String aFormat) {
		return String.format(aFormat.replace("%s", "%temp")
				.replace("%material", "%s"), this.localName).replace("%temp", "%s");
	}
	
	@Override
	public boolean contains(SubTag aTag) {
		return subTags.contains(aTag);
	}
	
	@Override
	public ISubTagContainer add(SubTag... aTags) {
		if (aTags != null)
			for (SubTag aTag : aTags)
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