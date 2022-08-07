package space.accident.api.interfaces.tileentity;

public interface IUpgradableMachine extends IMachineProgress {
	/**
	 * Accepts Upgrades. Some Machines have an Upgrade Limit.
	 */
	boolean isUpgradable();
	
	/**
	 * Accepts Muffler Upgrades
	 */
	boolean isMufflerUpgradable();
	
	/**
	 * Adds Muffler Upgrade
	 */
	boolean addMufflerUpgrade();
	
	/**
	 * Does this Machine have an Muffler
	 */
	boolean hasMufflerUpgrade();
}
