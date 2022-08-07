package space.accident.main.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public interface IPacketWorld {
	void sendToPlayer(IPacket packet, EntityPlayerMP player);
	void sendToAllAround(IPacket packet, NetworkRegistry.TargetPoint pos);
	void sendToServer(IPacket packet);
	void sendPacketToAllPlayersInRange(World world, IPacket packet, int x, int z);
}