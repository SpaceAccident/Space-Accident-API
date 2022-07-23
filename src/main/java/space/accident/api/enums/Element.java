package space.accident.api.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public enum Element {
	
	_NULL(0, 0, "", "", false),
	H(1, 0, "H\u2082", "Hydrogen", false),
	Fe(26, 30, "Fe", "Iron", false),
	Co(27, 32, "Co", "Cobalt", false),
	Au(79, 117, "Au", "Gold", false),
	Cu(29, 34, "Cu", "Copper", false),
	
	;
	
	public final long mProtons, mNeutrons;
	public final String mName, element;
	public final boolean mIsIsotope;
	
	/**
	 * Links to every pure Material containing just this Element.
	 */
	public ArrayList<Materials> mLinkedMaterials = new ArrayList<>();
	
	Element(long aProtons, long aNeutrons, String el, String aName, boolean aIsIsotope) {
		mProtons   = aProtons;
		mNeutrons  = aNeutrons;
		mName      = aName;
		mIsIsotope = aIsIsotope;
		element    = el;
		Companion.VALUES.put(name(), this);
	}
	
	public static Element get(String aMaterialName) {
		return Companion.VALUES.getOrDefault(aMaterialName, _NULL);
	}
	
	public long getProtons() {
		return mProtons;
	}
	
	public long getNeutrons() {
		return mNeutrons;
	}
	
	public long getMass() {
		return mProtons + mNeutrons;
	}
	
	private static final class Companion {
		/**
		 * Why is this a separate map and populated by enum constructor instead of a Map prepoluated with values()?
		 * Because apparently there are people hacking into this enum via EnumHelper.
		 */
		private static final Map<String, Element> VALUES = new HashMap<>();
	}
}
