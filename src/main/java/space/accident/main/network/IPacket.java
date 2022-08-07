package space.accident.main.network;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import net.minecraft.world.IBlockAccess;

public interface IPacket {
	void encode(ByteBuf aOut);
	IPacket decode(ByteArrayDataInput aData);
	void process(IBlockAccess world);
	int getPacketId();
	default void setINetHandler(INetHandler aHandler) {}
}
