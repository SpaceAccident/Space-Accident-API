package space.accident.api.util;

public class PlayedSound {
	public final String mSoundName;
	public final int mX, mY, mZ;
	
	public PlayedSound(String aSoundName, double x, double y, double z) {
		mSoundName = aSoundName == null ? "" : aSoundName;
		mX = (int) x;
		mY = (int) y;
		mZ = (int) z;
	}
	
	@Override
	public boolean equals(Object aObject) {
		if (aObject instanceof PlayedSound) {
			return ((PlayedSound) aObject).mX == mX && ((PlayedSound) aObject).mY == mY && ((PlayedSound) aObject).mZ == mZ && ((PlayedSound) aObject).mSoundName.equals(mSoundName);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return mX + mY + mZ + mSoundName.hashCode();
	}
}
