package space.accident.api.enums;

import space.accident.api.interfaces.IColorModulationContainer;

import java.awt.*;

public enum Colors implements IColorModulationContainer {
	
	/**
	 * The valid Colors, see VALUES Array below
	 */
	dyeBlack(0, new Color(32, 32, 32), "Black"), dyeRed(1, new Color(255, 0, 0), "Red"), dyeGreen(2, new Color(0, 255, 0), "Green"), dyeBrown(3, new Color(96, 64, 0), "Brown"), dyeBlue(4, new Color(0, 32, 255), "Blue"), dyePurple(5, new Color(128, 0, 128), "Purple"), dyeCyan(6, new Color(0, 255, 255), "Cyan"), dyeLightGray(7, new Color(192, 192, 192), "Light Gray"), dyeGray(8, new Color(128, 128, 128), "Gray"), dyePink(9, new Color(255, 192, 192), "Pink"), dyeLime(10, new Color(128, 255, 128), "Lime"), dyeYellow(11, new Color(255, 255, 0), "Yellow"), dyeLightBlue(12, new Color(96, 128, 255), "Light Blue"), dyeMagenta(13, new Color(255, 0, 255), "Magenta"), dyeOrange(14, new Color(255, 128, 0), "Orange"), dyeWhite(15, new Color(255, 255, 255), "White"),
	
	/**
	 * The NULL Color
	 */
	_NULL(-1, new Color(255, 255, 255), "INVALID COLOR"),
	
	/**
	 * Additional Colors only used for direct Color referencing
	 */
	CABLE_INSULATION(-1, new Color(64, 64, 64), "Cable Insulation"), CONSTRUCTION_FOAM(-1, new Color(64, 64, 64), "Construction Foam"), MACHINE_METAL(-1, new Color(210, 220, 255), "Machine Metal");
	
	public static final Colors[] VALUES = {dyeBlack, dyeRed, dyeGreen, dyeBrown, dyeBlue, dyePurple, dyeCyan, dyeLightGray, dyeGray, dyePink, dyeLime, dyeYellow, dyeLightBlue, dyeMagenta, dyeOrange, dyeWhite};
	
	public final int mIndex;
	public final String mName;
	public final int[] mRGBa;
	public final int[] mOriginalRGBa;
	
	Colors(int index, Color color, String name) {
		mIndex        = index;
		mName         = name;
		mRGBa         = new int[]{color.getRed(), color.getGreen(), color.getBlue(), 0};
		mOriginalRGBa = mRGBa.clone();
	}
	
	public static Colors get(int color) {
		if (color >= 0 && color < 16) {
			return VALUES[color];
		}
		return _NULL;
	}
	
	public static int[] getModulation(int color, int[] aDefaultModulation) {
		if (color >= 0 && color < 16) return VALUES[color].mRGBa;
		return aDefaultModulation;
	}
	
	@Override
	public int[] getRGBA() {
		return mRGBa;
	}
}
