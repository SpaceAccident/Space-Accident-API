package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import space.accident.api.metatileentity.base.BaseMetaPipeEntity;
import space.accident.api.metatileentity.base.BaseMetaTileEntity;

public class Packet_TileEntity implements IPacket {
	private int mX, mY, mZ, mC0, mC1, mC2, mC3, mC4, mC5;
	private short mID;
	private int mTexture, mTexturePage, mUpdate, mRedstone, mColor;
	
	public Packet_TileEntity() {
	}
	
	//For tiles
	public Packet_TileEntity(int x, int y, int z, short id, int aC0, int aC1, int aC2, int aC3, int aC4, int aC5, int aTexture, int aTexturePage, int aUpdate, int aRedstone, int color) {
		mX           = x;
		mY           = y;
		mZ           = z;
		mC0          = aC0;
		mC1          = aC1;
		mC2          = aC2;
		mC3          = aC3;
		mC4          = aC4;
		mC5          = aC5;
		mID          = id;
		mTexture     = aTexture;
		mTexturePage = aTexturePage;
		mUpdate      = aUpdate;
		mRedstone    = aRedstone;
		mColor       = color;
	}
	
	//For pipes
	public Packet_TileEntity(int x, int y, int z, short id, int aC0, int aC1, int aC2, int aC3, int aC4, int aC5, int aTexture, int aUpdate, int aRedstone, int color) {
		mX           = x;
		mY           = y;
		mZ           = z;
		mC0          = aC0;
		mC1          = aC1;
		mC2          = aC2;
		mC3          = aC3;
		mC4          = aC4;
		mC5          = aC5;
		mID          = id;
		mTexture     = aTexture;
		mTexturePage = 0;
		mUpdate      = aUpdate;
		mRedstone    = aRedstone;
		mColor       = color;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		aOut.writeShort(mID);
		
		aOut.writeInt(mC0);
		aOut.writeInt(mC1);
		aOut.writeInt(mC2);
		aOut.writeInt(mC3);
		aOut.writeInt(mC4);
		aOut.writeInt(mC5);
		
		aOut.writeInt(mTexture);
		aOut.writeInt(mTexturePage);
		aOut.writeInt(mUpdate);
		aOut.writeInt(mRedstone);
		aOut.writeInt(mColor);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_TileEntity(aData.readInt(), aData.readInt(), aData.readInt(), aData.readShort(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess world) {
		if (world != null) {
			TileEntity tTileEntity = world.getTileEntity(mX, mY, mZ);
			if (tTileEntity != null) {
				if (tTileEntity instanceof BaseMetaTileEntity) ((BaseMetaTileEntity) tTileEntity).receiveMetaTileEntityData(mID, mC0, mC1, mC2, mC3, mC4, mC5, mTexture, mTexturePage, mUpdate, mRedstone, mColor);
				else if (tTileEntity instanceof BaseMetaPipeEntity) ((BaseMetaPipeEntity) tTileEntity).receiveMetaTileEntityData(mID, mC0, mC1, mC2, mC3, mC4, mC5, mTexture, mUpdate, mRedstone, mColor);
			}
		}
	}
	
	@Override
	public int getPacketId() {
		return 0;
	}
}
