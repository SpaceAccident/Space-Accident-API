package space.accident.api.interfaces.te

/**
 * Interface for internal Code, which is mainly used for independent Energy conversion.
 */
interface IBasicEnergyContainer : IEnergyConnected {

    /**
     * Gets if that Amount of Energy is stored inside the Machine.
     * It is used for checking the contained Energy before consuming it.
     * If this returns false, it will also give a Message inside the Scanner, that this Machine doesn't have enough Energy.
     */
    fun isUniversalEnergyStored(aEnergyAmount: Long): Boolean

    /**
     * Gets the stored electric, kinetic or steam Energy (with EU as reference Value)
     * Always returns the largest one.
     */
    fun getUniversalEnergyStored(): Long

    /**
     * Gets the largest electric, kinetic or steam Energy Capacity (with EU as reference Value)
     */
    fun getUniversalEnergyCapacity(): Long

    /**
     * Gets the amount of Energy Packets per tick.
     */
    fun getOutputAmperage(): Long

    /**
     * Gets the Output in EU/p.
     */
    fun getOutputVoltage(): Long

    /**
     * Gets the amount of Energy Packets per tick.
     */
    fun getInputAmperage(): Long

    /**
     * Gets the maximum Input in EU/p.
     */
    fun getInputVoltage(): Long

    /**
     * Decreases the Amount of stored universal Energy. If ignoring too less Energy, then it just sets the Energy to 0 and returns false.
     */
    fun decreaseStoredEnergyUnits(aEnergy: Long, aIgnoreTooLessEnergy: Boolean): Boolean

    /**
     * Increases the Amount of stored electric Energy. If ignoring too much Energy, then the Energy Limit is just being ignored.
     */
    fun increaseStoredEnergyUnits(aEnergy: Long, aIgnoreTooMuchEnergy: Boolean): Boolean

    /**
     * Drain Energy Call for Electricity.
     */
    fun drainEnergyUnits(aSide: Byte, aVoltage: Long, aAmperage: Long): Boolean

    /**
     * returns the amount of Electricity, accepted by this Block the last 5 ticks as Average.
     */
    fun getAverageElectricInput(): Long

    /**
     * returns the amount of Electricity, outputted by this Block the last 5 ticks as Average.
     */
    fun getAverageElectricOutput(): Long

    /**
     * returns the amount of electricity contained in this Block, in EU units!
     */
    fun getStoredEU(): Long

    /**
     * returns the amount of electricity containable in this Block, in EU units!
     */
    fun getEUCapacity(): Long

    /**
     * returns the amount of Steam contained in this Block, in EU units!
     */
    fun getStoredSteam(): Long {
        return 0
    }

    /**
     * returns the amount of Steam containable in this Block, in EU units!
     */
    fun getSteamCapacity(): Long {
        return 0
    }

    /**
     * Increases stored Energy. Energy Base Value is in EU, even though it's Steam!
     *
     * @param aEnergy              The Energy to add to the Machine.
     * @param aIgnoreTooMuchEnergy if it shall ignore if it has too much Energy.
     * @return if it was successful
     *
     *
     * And yes, you can't directly decrease the Steam of a Machine. That is done by decreaseStoredEnergyUnits
     */
    fun increaseStoredSteam(aEnergy: Long, aIgnoreTooMuchEnergy: Boolean): Boolean {
        return false
    }
}