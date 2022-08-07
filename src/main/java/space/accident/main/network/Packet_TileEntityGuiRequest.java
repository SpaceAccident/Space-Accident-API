package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import space.accident.api.interfaces.tileentity.ITile;

import static space.accident.main.SpaceAccidentApi.NETWORK;
import static space.accident.main.proxy.GuiHandler.GUI_ID_COVER_SIDE_BASE;

public class Packet_TileEntityGuiRequest implements IPacket {
	
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int guiId;
	protected int dimId, playerId;
	
	protected int parentGuiId;
	
	public Packet_TileEntityGuiRequest() {
	}
	
	public Packet_TileEntityGuiRequest(int mX, int mY, int mZ, int guiId, int dimID, int playerID, int parentGuiId) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.guiId = guiId;
		
		this.dimId = dimID;
		this.playerId = playerID;
		
		this.parentGuiId = parentGuiId;
	}
	
	public Packet_TileEntityGuiRequest(int mX, int mY, int mZ, int guiId, int dimID, int playerID) {
		this(mX, mY, mZ, guiId, dimID, playerID, -1);
	}
	
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		
		aOut.writeInt(guiId);
		
		aOut.writeInt(dimId);
		aOut.writeInt(playerId);
		
		aOut.writeInt(parentGuiId);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_TileEntityGuiRequest(
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				
				aData.readInt(),
				
				aData.readInt(),
				aData.readInt(),
				
				aData.readInt()
		);
	}
	
	@Override
	public void process(IBlockAccess w) {
		World world = DimensionManager.getWorld(this.dimId);
		if (world == null) return;
		TileEntity tile = world.getTileEntity(this.mX, this.mY, this.mZ);
		if (!(tile instanceof ITile) || ((ITile) tile).isDead()) return;
		
		ITile gtTile = ((ITile) tile);
		EntityPlayerMP player = (EntityPlayerMP) world.getEntityByID(playerId);
		// If the requested Gui ID corresponds to a cover, send the cover data  to the client so they can open it.
		if (GUI_ID_COVER_SIDE_BASE <= guiId && guiId < GUI_ID_COVER_SIDE_BASE + 6) {
			int coverSide = (guiId - GUI_ID_COVER_SIDE_BASE);
			Packet_TileEntityCoverGUI packet = new Packet_TileEntityCoverGUI(
					this.mX, this.mY, this.mZ,
					coverSide,
					gtTile.getCoverIDAtSide(coverSide),
					gtTile.getComplexCoverDataAtSide(coverSide),
					this.dimId,
					this.playerId,
					parentGuiId
			);
			NETWORK.sendToPlayer(packet, player);
		} else if (guiId == 0) {
			gtTile.openGUI(player);
		}
	}
	
	@Override
	public int getPacketId() {
		return 5;
	}
}
