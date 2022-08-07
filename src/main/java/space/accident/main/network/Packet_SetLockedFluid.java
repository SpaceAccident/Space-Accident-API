package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import space.accident.api.interfaces.metatileentity.IMetaTile;
import space.accident.api.interfaces.tileentity.ITile;
import space.accident.api.metatileentity.implementations.hathes.Hatch_Output_Fluid;
import space.accident.api.util.LanguageManager;

import static space.accident.extensions.PlayerUtils.sendChat;

public class Packet_SetLockedFluid implements IPacket {
	protected int mX;
	protected int mY;
	protected int mZ;
	
	protected int mFluidID;
	
	private EntityPlayerMP mPlayer;
	
	public Packet_SetLockedFluid() {
	}
	
	public Packet_SetLockedFluid(ITile aTile, FluidStack aSource) {
		this(aTile.getX(), aTile.getY(), aTile.getZ(), aSource.getFluidID());
	}
	
	public Packet_SetLockedFluid(int x, int y, int z, int aFluidID) {
		
		this.mX = x;
		this.mY = y;
		this.mZ = z;
		
		this.mFluidID = aFluidID;
	}
	
	@Override
	public int getPacketId() {
		return 9;
	}
	
	@Override
	public void encode(ByteBuf aOut) {
		aOut.writeInt(mX);
		aOut.writeInt(mY);
		aOut.writeInt(mZ);
		aOut.writeInt(mFluidID);
	}
	
	@Override
	public void setINetHandler(INetHandler aHandler) {
		if (aHandler instanceof NetHandlerPlayServer) {
			mPlayer = ((NetHandlerPlayServer) aHandler).playerEntity;
		}
	}
	
	@Override
	public IPacket decode(ByteArrayDataInput aData) {
		return new Packet_SetLockedFluid(
				aData.readInt(),
				aData.readInt(),
				aData.readInt(),
				aData.readInt());
	}
	
	@Override
	public void process(IBlockAccess clientWorld) {
		if (mPlayer == null) return;
		World world = mPlayer.worldObj;
		TileEntity tile = world.getTileEntity(mX, mY, mZ);
		if (!(tile instanceof ITile) || ((ITile) tile).isDead()) return;
		IMetaTile mte = ((ITile) tile).getMetaTile();
		if (!(mte instanceof Hatch_Output_Fluid)) return;
		Fluid tFluid = FluidRegistry.getFluid(mFluidID);
		if (tFluid == null) return;
		Hatch_Output_Fluid hatch = (Hatch_Output_Fluid) mte;
		hatch.setLockedFluidName(tFluid.getName());
		hatch.mMode = 9;
		sendChat(mPlayer,
				String.format(
						LanguageManager.addStringLocalization(
						"Interaction_DESCRIPTION_Index_151.4", "Sucessfully locked Fluid to %s", false),
						new FluidStack(tFluid, 1).getLocalizedName())
		);
	}
}
