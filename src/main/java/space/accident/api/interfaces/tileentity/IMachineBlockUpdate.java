package space.accident.api.interfaces.tileentity;

public interface IMachineBlockUpdate {
	void onMachineBlockUpdate();
	default boolean isMachineBlockUpdateRecursive(){
		return true;
	}
}
