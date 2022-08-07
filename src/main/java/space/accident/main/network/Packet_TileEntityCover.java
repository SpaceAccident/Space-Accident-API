package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import space.accident.api.API;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.util.ISerializableObject;

/**
 * Client -> Server: Update cover data
 */
public class Packet_TileEntityCover implements IPacket {
	
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int side;
	protected ISerializableObject coverData;
	protected int coverID, dimID;
	
	protected EntityPlayerMP mPlayer;
	
	public Packet_TileEntityCover() {
	}
	
	public Packet_TileEntityCover(int mX, int mY, int mZ, int coverSide, int coverID, ISerializableObject coverData, int dimID) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
		
		this.dimID = dimID;
	}
	public Packet_TileEntityCover(int coverSide, int coverID, ISerializableObject coverData, ICoverable tile) {
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
		
		this.dimID = tile.getWorld().provider.dimensionId;
	}
	
	@Override
	public void setINetHandler(INetHandler aHandler) {
		if (aHandler instanceof NetHandlerPlayServer) {
			mPlayer = ((NetHandlerPlayServer) aHandler).playerEntity;
		}
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		
		aOut.writeInt(side);
		aOut.writeInt(coverID);
		coverData.writeToByteBuf(aOut);
		
		aOut.writeInt(dimID);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		int coverId;
		return new Packet_TileEntityCover(
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				
				aData.readInt(),
				coverId = aData.readInt(),
				API.getCoverBehaviorNew(coverId).createDataObject().readFromPacket(aData, mPlayer),
				
				aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess w) {
		if (mPlayer == null) // impossible, but who knows
			return;
		World world = DimensionManager.getWorld(dimID);
		if (world != null) {
			TileEntity tile = world.getTileEntity(mX, mY, mZ);
			if (tile instanceof ITile && !((ITile) tile).isDead()) {
				((ITile) tile).receiveCoverData(side, coverID, coverData, mPlayer);
			}
		}
	}
	
	@Override
	public int getPacketId() {
		return 3;
	}
}
