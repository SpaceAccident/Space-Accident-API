package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class Packet_Block_Event implements IPacket{
	
	private int mX, mZ, mY, mID, mValue;
	
	public Packet_Block_Event() {
	}
	
	public Packet_Block_Event(int x, int y, int z, int id, int value) {
		mX = x;
		mY = y;
		mZ = z;
		mID = id;
		mValue = value;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		aOut.writeInt(mID);
		aOut.writeInt(mValue);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_Block_Event(aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess world) {
		if (world != null) {
			TileEntity tTileEntity = world.getTileEntity(mX, mY, mZ);
			if (tTileEntity != null) tTileEntity.receiveClientEvent(mID, mValue);
		}
	}
	
	@Override
	public int getPacketId() {
		return 2;
	}
}
