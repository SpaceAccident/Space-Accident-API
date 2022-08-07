package space.accident.api.interfaces.tileentity.energy;

public interface IEnergyTransferTile {
	/**
	 * If Rotation Energy can be accepted on this Side.
	 * This means that the Gear/Axle will connect to this Side, and can cause the Gear/Axle to stop if the Energy isn't accepted.
	 */
	boolean acceptsRotationalEnergy(int side);
	
	/**
	 * Inject Energy Call for Rotational Energy.
	 * Rotation Energy can't be stored, this is just for things like internal Dynamos, which convert it into Energy, or into Progress.
	 *
	 * @param aSpeed Positive = Clockwise, Negative = Counterclockwise
	 */
	boolean injectRotationalEnergy(int side, long aSpeed, long aEnergy);
}
