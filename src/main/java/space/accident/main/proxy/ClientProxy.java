package space.accident.main.proxy;

public class ClientProxy extends CommonProxy {
	
	@Override
	public boolean isClientSide() {
		return true;
	}
	
	@Override
	public boolean isServerSide() {
		return false;
	}
}