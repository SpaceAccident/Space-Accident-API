package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import space.accident.main.common.blocks.TileEntity_MetaBlocks;

public class Packet_MetaBlocks implements IPacket {
	private int mX;
	private int mZ;
	private int mY;
	private int mMetaData;
	
	public Packet_MetaBlocks() {
	}
	
	public Packet_MetaBlocks(int x, int y, int z, int meta) {
		this.mX = x;
		this.mY = y;
		this.mZ = z;
		this.mMetaData = meta;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(this.mX);
		aOut.writeInt(this.mY);
		aOut.writeInt(this.mZ);
		aOut.writeInt(this.mMetaData);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_MetaBlocks(aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess clientWorld) {
		if (clientWorld != null) {
			TileEntity tTileEntity = clientWorld.getTileEntity(this.mX, this.mY, this.mZ);
			if ((tTileEntity instanceof TileEntity_MetaBlocks)) {
				((TileEntity_MetaBlocks) tTileEntity).mMetaData = (short) this.mMetaData;
			}
			if (((clientWorld instanceof World)) && (((World) clientWorld).isRemote)) {
				((World) clientWorld).markBlockForUpdate(this.mX, this.mY, this.mZ);
			}
		}
	}
	
	@Override
	public int getPacketId() {
		return 8;
	}
}
