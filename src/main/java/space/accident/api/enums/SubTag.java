package space.accident.api.enums;

import space.accident.api.interfaces.ICondition;
import space.accident.api.interfaces.ISubTagContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class SubTag implements ICondition<ISubTagContainer> {
	
	/**
	 * If this Material is invisible
	 */
	public static final SubTag INVISIBLE = getNewSubTag("INVISIBLE");
	
	/**
	 * This Material cannot be unificated
	 */
	public static final SubTag NO_UNIFICATION = getNewSubTag("NO_UNIFICATION");
	
	private static long sSubtagID = 0;
	public final long mSubtagID;
	public final String mName;
	
	private SubTag(String aName) {
		mSubtagID = sSubtagID++;
		mName = aName;
		sSubTags.put(aName, this);
	}
	
	public final Collection<ISubTagContainer> mRelevantTaggedItems = new HashSet<>(1);
	public static final HashMap<String, SubTag> sSubTags = new HashMap<>();
	
	public static SubTag getNewSubTag(String aName) {
		for (SubTag tSubTag : sSubTags.values()) {
			if (tSubTag.mName.equals(aName)) {
				return tSubTag;
			}
		}
		return new SubTag(aName);
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
	public SubTag addContainerToList(ISubTagContainer... aContainers) {
		if (aContainers != null)
			for (ISubTagContainer aContainer : aContainers)
				if (aContainer != null && !mRelevantTaggedItems.contains(aContainer))
					mRelevantTaggedItems.add(aContainer);
		return this;
	}
	
	public SubTag addTo(ISubTagContainer... aContainers) {
		if (aContainers != null)
			for (ISubTagContainer aContainer : aContainers)
				if (aContainer != null)
					aContainer.add(this);
		return this;
	}
	
	@Override
	public boolean isTrue(ISubTagContainer aObject) {
		return aObject.contains(this);
	}
}
