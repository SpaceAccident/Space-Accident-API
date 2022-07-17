package space.accident.api.interfaces.te

interface IGearEnergyTileEntity {
    /**
     * If Rotation Energy can be accepted on this Side.
     * This means that the Gear/Axle will connect to this Side, and can cause the Gear/Axle to stop if the Energy isn't accepted.
     */
    fun acceptsRotationalEnergy(aSide: Byte): Boolean

    /**
     * Inject Energy Call for Rotational Energy.
     * Rotation Energy can't be stored, this is just for things like internal Dynamos, which convert it into Energy, or into Progress.
     *
     * @param aSpeed Positive = Clockwise, Negative = Counterclockwise
     */
    fun injectRotationalEnergy(aSide: Byte, aSpeed: Long, aEnergy: Long): Boolean
}