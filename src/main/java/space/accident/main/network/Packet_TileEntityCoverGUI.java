package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import space.accident.api.API;
import space.accident.api.gui.GUICover;
import space.accident.api.interfaces.tileentity.ICoverable;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.util.SA_CoverBehaviorBase;
import space.accident.api.util.ISerializableObject;

/**
 * Server -> Client: Show GUI
 */
public class Packet_TileEntityCoverGUI implements IPacket {
	
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int side;
	protected int coverID, dimID, playerID;
	protected ISerializableObject coverData;
	
	protected int parentGuiId;
	
	public Packet_TileEntityCoverGUI() {
	}
	
	public Packet_TileEntityCoverGUI(int mX, short mY, int mZ, int coverSide, int coverID, int coverData, int dimID, int playerID) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = new ISerializableObject.LegacyCoverData(coverData);
		
		this.dimID = dimID;
		this.playerID = playerID;
		this.parentGuiId = -1;
	}
	
	public Packet_TileEntityCoverGUI(int mX, int mY, int mZ, int coverSide, int coverID, ISerializableObject coverData, int dimID, int playerID) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
		this.dimID = dimID;
		this.playerID = playerID;
		this.parentGuiId = -1;
	}
	
	public Packet_TileEntityCoverGUI(int mX, int mY, int mZ, int coverSide, int coverID, ISerializableObject coverData, int dimID, int playerID, int parentGuiId) {
		this.mX = mX;
		this.mY = mY;
		this.mZ = mZ;
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = coverData;
		this.dimID = dimID;
		this.playerID = playerID;
		this.parentGuiId = parentGuiId;
	}
	
	
	public Packet_TileEntityCoverGUI(int side, int coverID, int coverData, ICoverable tile, EntityPlayerMP player) {
		
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side = side;
		this.coverID = coverID;
		this.coverData = new ISerializableObject.LegacyCoverData(coverData);
		
		this.dimID = tile.getWorld().provider.dimensionId;
		this.playerID = player.getEntityId();
		this.parentGuiId = -1;
	}
	
	public Packet_TileEntityCoverGUI(int coverSide, int coverID, int coverData, ITile tile) {
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side = coverSide;
		this.coverID = coverID;
		this.coverData = new ISerializableObject.LegacyCoverData(coverData);
		
		this.dimID = tile.getWorld().provider.dimensionId;
		this.parentGuiId = -1;
	}
	
	public Packet_TileEntityCoverGUI(int side, int coverID, ISerializableObject coverData, ICoverable tile, EntityPlayerMP player) {
		this.mX = tile.getX();
		this.mY = tile.getY();
		this.mZ = tile.getZ();
		
		this.side = side;
		this.coverID = coverID;
		this.coverData = coverData.copy(); // make a copy so we don't get a race condition
		
		this.dimID = tile.getWorld().provider.dimensionId;
		this.playerID = player.getEntityId();
		this.parentGuiId = -1;
	}
	
	@Override
	public int getPacketId() {
		return 4;
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
		aOut.writeInt(playerID);
		
		aOut.writeInt(parentGuiId);
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		int coverID;
		return new Packet_TileEntityCoverGUI(
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				
				aData.readInt(),
				coverID = aData.readInt(),
				API.getCoverBehaviorNew(coverID).createDataObject().readFromPacket(aData, null),
				
				aData.readInt(),
				aData.readInt(),
				
				aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess world) {
		if (world instanceof World) {
			// Using EntityPlayer instead of EntityClientPlayerMP so both client and server can load this
			EntityPlayer thePlayer = ((EntityPlayer) ((World)world).getEntityByID(playerID));
			TileEntity tile = world.getTileEntity(mX, mY, mZ);
			if (tile instanceof ITile && !((ITile) tile).isDead()) {
				ITile gtTile = ((ITile) tile);
				gtTile.setCoverDataAtSide(side, coverData); //Set it client side to read later.
				
				GuiScreen gui = (GuiScreen) getCoverGUI(side, thePlayer, thePlayer.worldObj, gtTile);
				// If it's one of this mod's covers, tell it to exit to the GUI with the specified ID (-1 is ignored)
				if (gui instanceof GUICover) {
					((GUICover) gui).setParentGuiId(parentGuiId);
				}
				Minecraft.getMinecraft().displayGuiScreen(gui);
			}
		}
	}
	
	/**
	 * Gets the specified cover's GUI object, if one exists
	 * @param side Block side (0 through 5)
	 * @param player Current player
	 * @param world Current world
	 * @param aGtTile ITile instance
	 * @return The specified cover's GUI, if one exists
	 */
	private Object getCoverGUI(int side, EntityPlayer player, World world, ITile aGtTile) {
		SA_CoverBehaviorBase<?> cover = aGtTile.getCoverBehaviorAtSideNew(side);
		if (cover.hasCoverGUI()) {
			return cover.getClientGUI(side, aGtTile.getCoverIDAtSide(side), aGtTile.getComplexCoverDataAtSide(side), aGtTile, player, world);
		}
		return null;
	}
}
