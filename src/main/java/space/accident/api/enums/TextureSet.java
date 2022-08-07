package space.accident.api.enums;

import space.accident.api.enums.Textures.BlockIcons;
import space.accident.api.enums.Textures.ItemIcons.CustomIcon;
import space.accident.api.interfaces.IIconContainer;

import java.util.List;

public class TextureSet {
	
	/**
	 * For the Indices of OrePrefixes you need to look into the OrePrefix Enum.
	 */
	public static final short INDEX_wire = 69;
	
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
			textures[type.ore.textureId] = setItemIcon(type.name().toLowerCase());
		}
	}
	
	public TextureSet(String prefix) {
		this._prefix  = prefix.length() > 0 ? "_" + prefix.toLowerCase() : prefix;
		textures[0]   = setItemVoid();
		textures[1]   = setItemVoid();
		textures[2]   = setItemIcon("dust" + _prefix);
		textures[3]   = setItemIcon("dustImpure" + _prefix);
		textures[4]   = setItemIcon("dustPure" + _prefix);
		textures[5]   = setItemIcon("crushed" + _prefix);
		textures[6]   = setItemIcon("crushedPurified" + _prefix);
		textures[7]   = setItemIcon("crushedCentrifuged" + _prefix);
		textures[8]   = setItemIcon("gem" + _prefix);
		textures[9]   = setItemIcon("nugget" + _prefix);
		textures[10]  = setItemIcon("casingSmall" + _prefix);
		textures[11]  = setItemIcon("ingot" + _prefix);
		textures[12]  = setItemIcon("ingotHot" + _prefix);
		textures[13]  = setItemIcon("ingotDouble" + _prefix);
		textures[14]  = setItemIcon("ingotTriple" + _prefix);
		textures[15]  = setItemIcon("ingotQuadruple" + _prefix);
		textures[16]  = setItemIcon("ingotQuintuple" + _prefix);
		textures[17]  = setItemIcon("plate" + _prefix);
		textures[18]  = setItemIcon("plateDouble" + _prefix);
		textures[19]  = setItemIcon("plateTriple" + _prefix);
		textures[20]  = setItemIcon("plateQuadruple" + _prefix);
		textures[21]  = setItemIcon("plateQuintuple" + _prefix);
		textures[22]  = setItemIcon("plateDense" + _prefix);
		textures[23]  = setItemIcon("stick" + _prefix);
		textures[24]  = setItemIcon("lens" + _prefix);
		textures[25]  = setItemIcon("round" + _prefix);
		textures[26]  = setItemIcon("bolt" + _prefix);
		textures[27]  = setItemIcon("screw" + _prefix);
		textures[28]  = setItemIcon("ring" + _prefix);
		textures[29]  = setItemIcon("foil" + _prefix);
		textures[30]  = setItemIcon("cell" + _prefix);
		textures[31]  = setItemIcon("cellPlasma" + _prefix);
		textures[32]  = setItemIcon("toolHeadSword" + _prefix);
		textures[33]  = setItemIcon("toolHeadPickaxe" + _prefix);
		textures[34]  = setItemIcon("toolHeadShovel" + _prefix);
		textures[35]  = setItemIcon("toolHeadAxe" + _prefix);
		textures[36]  = setItemIcon("toolHeadHoe" + _prefix);
		textures[37]  = setItemIcon("toolHeadHammer" + _prefix);
		textures[38]  = setItemIcon("toolHeadFile" + _prefix);
		textures[39]  = setItemIcon("toolHeadSaw" + _prefix);
		textures[40]  = setItemIcon("toolHeadDrill" + _prefix);
		textures[41]  = setItemIcon("toolHeadChainsaw" + _prefix);
		textures[42]  = setItemIcon("toolHeadWrench" + _prefix);
		textures[43]  = setItemIcon("toolHeadUniversalSpade" + _prefix);
		textures[44]  = setItemIcon("toolHeadSense" + _prefix);
		textures[45]  = setItemIcon("toolHeadPlow" + _prefix);
		textures[46]  = setItemIcon("toolHeadArrow" + _prefix);
		textures[47]  = setItemIcon("toolHeadScrewdriver" + _prefix);
		textures[48]  = setItemIcon("toolHeadBuzzSaw" + _prefix);
		textures[49]  = setItemIcon("toolHeadSoldering" + _prefix);
		textures[50]  = setItemVoid();
		textures[51]  = setItemIcon("wireFine" + _prefix);
		textures[52]  = setItemIcon("gearGtSmall" + _prefix);
		textures[53]  = setItemIcon("rotor" + _prefix);
		textures[54]  = setItemIcon("stickLong" + _prefix);
		textures[55]  = setItemIcon("springSmall" + _prefix);
		textures[56]  = setItemIcon("spring" + _prefix);
		textures[57]  = setItemIcon("arrowGtWood" + _prefix);
		textures[58]  = setItemIcon("arrowGtPlastic" + _prefix);
		textures[59]  = setItemIcon("gemChipped" + _prefix);
		textures[60]  = setItemIcon("gemFlawed" + _prefix);
		textures[61]  = setItemIcon("gemFlawless" + _prefix);
		textures[62]  = setItemIcon("gemExquisite" + _prefix);
		textures[63]  = setItemIcon("gearGt" + _prefix);
		textures[64]  = setBlockVoid();
		textures[65]  = setBlockVoid();
		textures[66]  = setBlockVoid();
		textures[67]  = setBlockIcon("oreSmall" + _prefix);
		textures[68]  = setBlockIcon("ore" + _prefix);
		textures[69]  = setBlockIcon("wire" + _prefix);
		textures[70]  = setBlockIcon("foil" + _prefix);
		textures[71]  = setBlockIcon("block" + _prefix);
		textures[72]  = setBlockVoid();
		textures[73]  = setBlockVoid();
		textures[74]  = setBlockVoid();
		textures[75]  = setBlockVoid();
		textures[76]  = setBlockVoid();
		textures[77]  = setBlockIcon("pipeSide" + _prefix);
		textures[78]  = setBlockIcon("pipeTiny" + _prefix);
		textures[79]  = setBlockIcon("pipeSmall" + _prefix);
		textures[80]  = setBlockIcon("pipeMedium" + _prefix);
		textures[81]  = setBlockIcon("pipeLarge" + _prefix);
		textures[82]  = setBlockIcon("pipeHuge" + _prefix);
		textures[83]  = setBlockIcon("frameGt" + _prefix);
		textures[84]  = setBlockIcon("pipeQuadruple" + _prefix);
		textures[85]  = setBlockIcon("pipeNonuple" + _prefix);
		textures[86]  = setBlockVoid();
		textures[87]  = setBlockVoid();
		textures[88]  = setBlockVoid();
		textures[89]  = setBlockVoid();
		textures[90]  = setBlockVoid();
		textures[91]  = setBlockVoid();
		textures[92]  = setBlockVoid();
		textures[93]  = setBlockVoid();
		textures[94]  = setBlockVoid();
		textures[95]  = setBlockVoid();
		textures[96]  = setItemIcon("crateGtDust" + _prefix);
		textures[97]  = setItemIcon("crateGtIngot" + _prefix);
		textures[98]  = setItemIcon("crateGtGem" + _prefix);
		textures[99]  = setItemIcon("crateGtPlate" + _prefix);
		textures[100] = setItemIcon("turbineBlade" + _prefix);
		textures[101] = setItemVoid();
		textures[102] = setItemVoid();
		textures[103] = setItemVoid();
		textures[104] = setItemVoid();
		textures[105] = setItemVoid();
		textures[106] = setItemVoid();
		textures[107] = setItemVoid();
		textures[108] = setItemVoid();
		textures[109] = setItemVoid();
		textures[110] = setItemVoid();
		textures[111] = setItemVoid();
		textures[112] = setItemVoid();
		textures[113] = setItemVoid();
		textures[114] = setItemVoid();
		textures[115] = setItemVoid();
		textures[116] = setItemVoid();
		textures[117] = setItemVoid();
		textures[118] = setItemVoid();
		textures[119] = setItemVoid();
		textures[120] = setItemVoid();
		textures[121] = setItemVoid();
		textures[122] = setItemVoid();
		textures[123] = setItemVoid();
		textures[124] = setItemVoid();
		textures[125] = setItemVoid();
		textures[126] = setItemIcon("handleMallet" + _prefix);
		textures[127] = setItemIcon("toolHeadMallet" + _prefix);
	}
	
	private static CustomIcon setItemIcon(String path) {
		return new CustomIcon(aTextMatIconDir + path);
	}
	
	private static CustomIcon setItemVoid() {
		return setItemIcon(aTextVoidDir);
	}
	
	private static BlockIcons.CustomIcon setBlockIcon(String path) {
		return new BlockIcons.CustomIcon(aTextMatIconDir + path);
	}
	
	
	private static BlockIcons.CustomIcon setBlockVoid() {
		return setBlockIcon(aTextVoidDir);
	}
}