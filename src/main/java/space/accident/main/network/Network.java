package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import space.accident.api.util.SpaceLog;

import java.util.EnumMap;
import java.util.List;

import static space.accident.main.SpaceAccidentApi.proxy;

@ChannelHandler.Sharable
public class Network extends MessageToMessageCodec<FMLProxyPacket, IPacket> implements IPacketWorld {
	private final EnumMap<Side, FMLEmbeddedChannel> mChannel;
	private final IPacket[] mSubChannels;
	
	public Network() {
		this(
				"SpaceAccidentAPI",
				new Packet_TileEntity(),
				new Packet_Sound(), 					 //1
				new Packet_Block_Event(), 			 //2
				new Packet_TileEntityCover(), 		 //3
				new Packet_TileEntityCoverGUI(), 	 //4
				new Packet_TileEntityGuiRequest(),  //5
				new Packet_SendCoverData(),			 //6
				new Packet_RequestCoverData(),		 //7
				new Packet_MetaBlocks(),		         //8
				new Packet_SetLockedFluid()		     //9
		);
	}
	
	public Network(String channelName, IPacket... packetTypes) {
		this.mChannel     = NetworkRegistry.INSTANCE.newChannel(channelName, this, new HandlerShared());
		this.mSubChannels = new IPacket[packetTypes.length];
		for (IPacket packetType : packetTypes) {
			final int pId = packetType.getPacketId();
			if (this.mSubChannels[pId] == null)
				this.mSubChannels[pId] = packetType;
			else
				throw new IllegalArgumentException("Duplicate Packet ID! " + pId);
		}
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, IPacket msg, List<Object> out) {
		ByteBuf tBuf = Unpooled.buffer().writeByte(msg.getPacketId());
		msg.encode(tBuf);
		out.add(new FMLProxyPacket(tBuf, ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get()));
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
		ByteArrayDataInput data = ByteStreams.newDataInput(msg.payload().array());
		IPacket packet = this.mSubChannels[data.readByte()].decode(data);
		packet.setINetHandler(msg.handler());
		out.add(packet);
	}
	
	@Override
	public void sendToPlayer(IPacket packet, EntityPlayerMP player) {
		if (packet == null) {
			SpaceLog.FML_LOGGER.info("packet null");
			return;
		}
		if (player == null) {
			SpaceLog.FML_LOGGER.info("player null");
			return;
		}
		this.mChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		this.mChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		this.mChannel.get(Side.SERVER).writeAndFlush(packet);
	}
	
	@Override
	public void sendToAllAround(IPacket packet, NetworkRegistry.TargetPoint pos) {
		this.mChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		this.mChannel.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(pos);
		this.mChannel.get(Side.SERVER).writeAndFlush(packet);
	}
	
	@Override
	public void sendToServer(IPacket packet) {
		this.mChannel.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		this.mChannel.get(Side.CLIENT).writeAndFlush(packet);
	}
	
	@Override
	public void sendPacketToAllPlayersInRange(World world, IPacket packet, int x, int z) {
		if (!world.isRemote) {
			for (Object tObject : world.playerEntities) {
				if (!(tObject instanceof EntityPlayerMP)) {
					break;
				}
				EntityPlayerMP tPlayer = (EntityPlayerMP) tObject;
				Chunk tChunk = world.getChunkFromBlockCoords(x, z);
				if (tPlayer.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(tPlayer, tChunk.xPosition, tChunk.zPosition)) {
					sendToPlayer(packet, tPlayer);
				}
			}
		}
	}
	
	@ChannelHandler.Sharable
	static final class HandlerShared extends SimpleChannelInboundHandler<IPacket> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, IPacket aPacket) {
			final EntityPlayer player = proxy.getPlayer();
			aPacket.process(player == null ? null : player.worldObj);
		}
	}
}
