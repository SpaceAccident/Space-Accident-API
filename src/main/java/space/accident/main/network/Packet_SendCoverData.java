package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import space.accident.api.API;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.metatileentity.base.CoverableTileEntity;
import space.accident.api.util.ISerializableObject;

/**
 * Server -> Client : Update cover data
 */
public class Packet_SendCoverData implements IPacket {
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int side;
	protected int coverID;
	protected ISerializableObject coverData;
	
	public Packet_SendCoverData() {
	}
	
	public Packet_SendCoverData(int mX, int mY, int mZ, int coverSide, int coverID, ISerializableObject coverData) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
	}
	public Packet_SendCoverData(int coverSide, int coverID, ISerializableObject coverData, ICoverable tile) {
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
	}
	
	@Override
	public int getPacketId() {
		return 6;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		aOut.writeInt(side);
		aOut.writeInt(coverID);
		coverData.writeToByteBuf(aOut);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		int coverId;
		return new Packet_SendCoverData(
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				coverId = aData.readInt(),
				API.getCoverBehaviorNew(coverId).createDataObject().readFromPacket(aData, null)
		);
	}
	
	@Override
	public void process(IBlockAccess clientWorld) {
		if (clientWorld != null) {
			TileEntity tile = clientWorld.getTileEntity(mX, mY, mZ);
			if (tile instanceof CoverableTileEntity && !((CoverableTileEntity) tile).isDead()) {
				((CoverableTileEntity) tile).receiveCoverData(side, coverID, coverData, null);
			}
		}
	}
}

