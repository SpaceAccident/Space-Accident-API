package space.accident.main.proxy;

import net.minecraft.entity.player.EntityPlayer;


public interface IProxySide {
	
	boolean isClientSide();
	
	boolean isServerSide();
	
	EntityPlayer getPlayer();
}
