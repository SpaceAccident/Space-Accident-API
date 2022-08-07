package buildcraft.api.core;

import io.netty.buffer.ByteBuf;

public interface ISerializable {
	void writeData(ByteBuf var1);
	
	void readData(ByteBuf var1);
}