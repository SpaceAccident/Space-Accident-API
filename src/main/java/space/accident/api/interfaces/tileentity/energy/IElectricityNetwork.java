package space.accident.api.interfaces.tileentity.energy;

public interface IElectricityNetwork {
	
	/**
	 * @return true if this Device consumes Energy at all
	 */
	default boolean isEnergyInput() {
		return false;
	}
	
	/**
	 * @return true if this Device emits Energy at all
	 */
	default boolean isEnergyOutput() {
		return false;
	}
}