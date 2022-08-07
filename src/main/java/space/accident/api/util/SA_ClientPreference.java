package space.accident.api.util;

public class SA_ClientPreference {
	private final boolean mSingleBlockInitialFilter;
	private final boolean mSingleBlockInitialMultiStack;
	private final boolean mInputBusInitialFilter;
	
	public SA_ClientPreference(boolean mSingleBlockInitialFilter, boolean mSingleBlockInitialMultiStack, boolean mInputBusInitialFilter) {
		this.mSingleBlockInitialFilter = mSingleBlockInitialFilter;
		this.mSingleBlockInitialMultiStack = mSingleBlockInitialMultiStack;
		this.mInputBusInitialFilter = mInputBusInitialFilter;
	}
	
//	public SA_ClientPreference(Config aClientDataFile) {
//		this.mSingleBlockInitialFilter = aClientDataFile.get("preference", "mSingleBlockInitialFilter", false);
//		this.mSingleBlockInitialMultiStack = aClientDataFile.get("preference", "mSingleBlockInitialAllowMultiStack", false);
//		this.mInputBusInitialFilter = aClientDataFile.get("preference", "mInputBusInitialFilter", true);
//	}
	
	public boolean isSingleBlockInitialFilterEnabled() {
		return mSingleBlockInitialFilter;
	}
	
	public boolean isSingleBlockInitialMultiStackEnabled() {
		return mSingleBlockInitialMultiStack;
	}
	
	public boolean isInputBusInitialFilterEnabled() {
		return mInputBusInitialFilter;
	}
}
