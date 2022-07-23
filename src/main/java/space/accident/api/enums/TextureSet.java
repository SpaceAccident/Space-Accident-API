package space.accident.api.enums;

import space.accident.api.enums.Textures.ItemIcons.CustomIcon;
import space.accident.api.interfaces.IIconContainer;

import java.util.List;

public class TextureSet {
	
	public static final TextureSet NONE = new TextureSet();
	
	private static final String aTextMatIconDir = "materialicons/";
	private static final String aTextVoidDir = "/void";
	
	public final IIconContainer[] textures = new IIconContainer[128];
	
	public final String _prefix;
	
	public TextureSet() {
		this("");
	}
	
	public TextureSet(List<MaterialType> types) {
		this("");
		for (MaterialType type : types) {
			textures[type.ore.textureId] = setIcon(type.name().toLowerCase());
		}
	}
	
	public TextureSet(String prefix) {
		this._prefix  = prefix.length() > 0 ? "_" + prefix.toLowerCase() : prefix;
		textures[0]   = setVoid();
		textures[1]   = setVoid();
		textures[2]   = setIcon("dust" + _prefix);
		textures[3]   = setIcon("dustImpure" + _prefix);
		textures[4]   = setIcon("dustPure" + _prefix);
		textures[5]   = setIcon("crushed" + _prefix);
		textures[6]   = setIcon("crushedPurified" + _prefix);
		textures[7]   = setIcon("crushedCentrifuged" + _prefix);
		textures[8]   = setIcon("gem" + _prefix);
		textures[9]   = setIcon("nugget" + _prefix);
		textures[10]  = setIcon("casingSmall" + _prefix);
		textures[11]  = setIcon("ingot" + _prefix);
		textures[12]  = setIcon("ingotHot" + _prefix);
		textures[13]  = setIcon("ingotDouble" + _prefix);
		textures[14]  = setIcon("ingotTriple" + _prefix);
		textures[15]  = setIcon("ingotQuadruple" + _prefix);
		textures[16]  = setIcon("ingotQuintuple" + _prefix);
		textures[17]  = setIcon("plate" + _prefix);
		textures[18]  = setIcon("plateDouble" + _prefix);
		textures[19]  = setIcon("plateTriple" + _prefix);
		textures[20]  = setIcon("plateQuadruple" + _prefix);
		textures[21]  = setIcon("plateQuintuple" + _prefix);
		textures[22]  = setIcon("plateDense" + _prefix);
		textures[23]  = setIcon("stick" + _prefix);
		textures[24]  = setIcon("lens" + _prefix);
		textures[25]  = setIcon("round" + _prefix);
		textures[26]  = setIcon("bolt" + _prefix);
		textures[27]  = setIcon("screw" + _prefix);
		textures[28]  = setIcon("ring" + _prefix);
		textures[29]  = setIcon("foil" + _prefix);
		textures[30]  = setIcon("cell" + _prefix);
		textures[31]  = setIcon("cellPlasma" + _prefix);
		textures[32]  = setIcon("toolHeadSword" + _prefix);
		textures[33]  = setIcon("toolHeadPickaxe" + _prefix);
		textures[34]  = setIcon("toolHeadShovel" + _prefix);
		textures[35]  = setIcon("toolHeadAxe" + _prefix);
		textures[36]  = setIcon("toolHeadHoe" + _prefix);
		textures[37]  = setIcon("toolHeadHammer" + _prefix);
		textures[38]  = setIcon("toolHeadFile" + _prefix);
		textures[39]  = setIcon("toolHeadSaw" + _prefix);
		textures[40]  = setIcon("toolHeadDrill" + _prefix);
		textures[41]  = setIcon("toolHeadChainsaw" + _prefix);
		textures[42]  = setIcon("toolHeadWrench" + _prefix);
		textures[43]  = setIcon("toolHeadUniversalSpade" + _prefix);
		textures[44]  = setIcon("toolHeadSense" + _prefix);
		textures[45]  = setIcon("toolHeadPlow" + _prefix);
		textures[46]  = setIcon("toolHeadArrow" + _prefix);
		textures[47]  = setIcon("toolHeadScrewdriver" + _prefix);
		textures[48]  = setIcon("toolHeadBuzzSaw" + _prefix);
		textures[49]  = setIcon("toolHeadSoldering" + _prefix);
		textures[50]  = setVoid();
		textures[51]  = setIcon("wireFine" + _prefix);
		textures[52]  = setIcon("gearGtSmall" + _prefix);
		textures[53]  = setIcon("rotor" + _prefix);
		textures[54]  = setIcon("stickLong" + _prefix);
		textures[55]  = setIcon("springSmall" + _prefix);
		textures[56]  = setIcon("spring" + _prefix);
		textures[57]  = setIcon("arrowGtWood" + _prefix);
		textures[58]  = setIcon("arrowGtPlastic" + _prefix);
		textures[59]  = setIcon("gemChipped" + _prefix);
		textures[60]  = setIcon("gemFlawed" + _prefix);
		textures[61]  = setIcon("gemFlawless" + _prefix);
		textures[62]  = setIcon("gemExquisite" + _prefix);
		textures[63]  = setIcon("gearGt" + _prefix);
		textures[64]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[65]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[66]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[67]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "oreSmall" + _prefix);
		textures[68]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "ore" + _prefix);
		textures[69]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "wire" + _prefix);
		textures[70]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "foil" + _prefix);
		textures[71]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block1" + _prefix);
		textures[72]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block2" + _prefix);
		textures[73]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block3" + _prefix);
		textures[74]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block4" + _prefix);
		textures[75]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block5" + _prefix);
		textures[76]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "block6" + _prefix);
		textures[77]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeSide" + _prefix);
		textures[78]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeTiny" + _prefix);
		textures[79]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeSmall" + _prefix);
		textures[80]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeMedium" + _prefix);
		textures[81]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeLarge" + _prefix);
		textures[82]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeHuge" + _prefix);
		textures[83]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "frameGt" + _prefix);
		textures[84]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeQuadruple" + _prefix);
		textures[85]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + "pipeNonuple" + _prefix);
		textures[86]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[87]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[88]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[89]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[90]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[91]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[92]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[93]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[94]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[95]  = new Textures.BlockIcons.CustomIcon(aTextMatIconDir + aTextVoidDir + _prefix);
		textures[96]  = setIcon("crateGtDust" + _prefix);
		textures[97]  = setIcon("crateGtIngot" + _prefix);
		textures[98]  = setIcon("crateGtGem" + _prefix);
		textures[99]  = setIcon("crateGtPlate" + _prefix);
		textures[100] = setIcon("turbineBlade" + _prefix);
		textures[101] = setVoid();
		textures[102] = setVoid();
		textures[103] = setVoid();
		textures[104] = setVoid();
		textures[105] = setVoid();
		textures[106] = setVoid();
		textures[107] = setVoid();
		textures[108] = setVoid();
		textures[109] = setVoid();
		textures[110] = setVoid();
		textures[111] = setVoid();
		textures[112] = setVoid();
		textures[113] = setVoid();
		textures[114] = setVoid();
		textures[115] = setVoid();
		textures[116] = setVoid();
		textures[117] = setVoid();
		textures[118] = setVoid();
		textures[119] = setVoid();
		textures[120] = setVoid();
		textures[121] = setVoid();
		textures[122] = setVoid();
		textures[123] = setVoid();
		textures[124] = setVoid();
		textures[125] = setVoid();
		textures[126] = setIcon("handleMallet" + _prefix);
		textures[127] = setIcon("toolHeadMallet" + _prefix);
	}
	
	private static CustomIcon setIcon(String path) {
		return new CustomIcon(aTextMatIconDir + path);
	}
	
	private static CustomIcon setVoid() {
		return setIcon(aTextVoidDir);
	}
}