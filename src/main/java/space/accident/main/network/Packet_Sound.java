package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.world.IBlockAccess;
import space.accident.api.util.SpaceLog;
import space.accident.api.util.Utility;

import java.io.DataOutput;
import java.io.IOException;

public class Packet_Sound implements IPacket {
	private int mX, mZ, mY;
	private String mSoundName;
	private float mSoundStrength, mSoundPitch;
	
	public Packet_Sound() {
	}
	
	public Packet_Sound(String aSoundName, float aSoundStrength, float aSoundPitch, int x, int y, int z) {
		mX = x;
		mY = y;
		mZ = z;
		mSoundName = aSoundName;
		mSoundStrength = aSoundStrength;
		mSoundPitch = aSoundPitch;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		DataOutput tOut = new ByteBufOutputStream(aOut);
		try {
			tOut.writeUTF(mSoundName);
			tOut.writeFloat(mSoundStrength);
			tOut.writeFloat(mSoundPitch);
			tOut.writeInt(mX);
			tOut.writeInt(mY);
			tOut.writeInt(mZ);
		} catch (IOException e) {
			e.printStackTrace(SpaceLog.err);
		}
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_Sound(aData.readUTF(), aData.readFloat(), aData.readFloat(), aData.readInt(), aData.readInt(), aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess world) {
		Utility.doSoundAtClient(mSoundName, 1, mSoundStrength, mSoundPitch, mX, mY, mZ);
	}
	
	@Override
	public int getPacketId() {
		return 1;
	}
}
