package space.accident.main.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import space.accident.api.util.SA_ClientPreference;
import space.accident.main.render.Renderer_Block;

import java.util.UUID;

public class ClientProxy extends CommonProxy {
	
	@Override
	public boolean isClientSide() {
		return true;
	}
	
	@Override
	public boolean isServerSide() {
		return false;
	}
	
	@Override
	public void onLoad(FMLInitializationEvent e) {
		Renderer_Block.register();
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public SA_ClientPreference getClientPreference(UUID aPlayerID) {
		return mClientPrefernces.get(aPlayerID);
	}
	
	public void setClientPreference(UUID aPlayerID, SA_ClientPreference aPreference) {
		mClientPrefernces.put(aPlayerID, aPreference);
	}
}