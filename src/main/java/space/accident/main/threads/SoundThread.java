package space.accident.main.threads;

import net.minecraft.world.World;
import space.accident.api.util.PlayedSound;
import space.accident.api.util.Utility;

public class SoundThread implements Runnable {
	private final int mX, mY, mZ, mTimeUntilNextSound;
	private final World mWorld;
	private final String mSoundName;
	private final float mSoundStrength, mSoundModulation;
	
	public SoundThread(World world, int x, int y, int z, int aTimeUntilNextSound, String aSoundName, float aSoundStrength, float aSoundModulation) {
		mWorld              = world;
		mX                  = x;
		mY                  = y;
		mZ                  = z;
		mTimeUntilNextSound = aTimeUntilNextSound;
		mSoundName          = aSoundName;
		mSoundStrength      = aSoundStrength;
		mSoundModulation    = aSoundModulation;
	}
	
	@Override
	public void run() {
		try {
			PlayedSound tSound;
			if (Utility.sPlayedSoundMap.containsKey(tSound = new PlayedSound(mSoundName, mX, mY, mZ))) return;
			mWorld.playSound(mX, mY, mZ, mSoundName, mSoundStrength, mSoundModulation, false);
			Utility.sPlayedSoundMap.put(tSound, mTimeUntilNextSound);
		} catch (Throwable ignored) {
		}
	}
}