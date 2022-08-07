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
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.metatileentity.base.CoverableTileEntity;

/**
 * Client -> Server : ask for cover data
 */
public class Packet_RequestCoverData implements IPacket {
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int side;
	protected int coverID;
	
	protected EntityPlayerMP mPlayer;
	
	public Packet_RequestCoverData() {
	}
	
	public Packet_RequestCoverData(int mX, int mY, int mZ, int coverSide, int coverID) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side    = coverSide;
		this.coverID = coverID;
	}
	
	public Packet_RequestCoverData(int coverSide, int coverID, ICoverable tile) {
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side    = coverSide;
		this.coverID = coverID;
	}
	
	@Override
	public int getPacketId() {
		return 7;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		aOut.writeInt(side);
		aOut.writeInt(coverID);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_RequestCoverData(aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt());
	}
	
	@Override
	public void setINetHandler(INetHandler aHandler) {
		if (aHandler instanceof NetHandlerPlayServer) {
			mPlayer = ((NetHandlerPlayServer) aHandler).playerEntity;
		}
	}
	
	@Override
	public void process(IBlockAccess clientWorld) {
		if (mPlayer == null) // impossible, but who knows
			return;
		World world = DimensionManager.getWorld(mPlayer.dimension);
		if (world != null) {
			final TileEntity tile = world.getTileEntity(mX, mY, mZ);
			if (tile instanceof CoverableTileEntity) {
				final CoverableTileEntity te = (CoverableTileEntity) tile;
				if (!te.isDead() && te.getCoverIDAtSide(side) == coverID) {
					te.issueCoverUpdate(side);
				}
			}
		}
	}
}

