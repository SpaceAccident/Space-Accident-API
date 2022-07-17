package space.accident.api.interfaces.te


/**
 * For Machines which have Progress
 */
interface IMachineProgress : IHasWorldObjectAndCoords {
    /**
     * returns the Progress this Machine has made. Warning, this can also be negative!
     */
    fun getProgress(): Int

    /**
     * returns the Progress the Machine needs to complete its task.
     */
    fun getMaxProgress(): Int

    /**
     * increases the Progress of the Machine
     */
    fun increaseProgress(aProgressAmountInTicks: Int): Boolean

    /**
     * returns if the Machine currently does something.
     */
    fun hasThingsToDo(): Boolean

    /**
     * returns if the Machine just got enableWorking called after being disabled.
     * Used for Translocators, which need to check if they need to transfer immediately.
     */
    fun hasWorkJustBeenEnabled(): Boolean

    /**
     * allows Machine to work
     */
    fun enableWorking()

    /**
     * disallows Machine to work
     */
    fun disableWorking()

    /**
     * if the Machine is allowed to Work
     */
    fun isAllowedToWork(): Boolean

    /**
     * used to control Machines via Redstone Signal Strength by special Covers
     * In case of 0 the Machine is very likely doing nothing, or is just not being controlled at all.
     */
    fun getWorkDataValue(): Byte {
        return 0
    }

    /**
     * used to control Machines via Redstone Signal Strength by special Covers
     * only Values between 0 and 15!
     */
    fun setWorkDataValue(aValue: Byte) {}

    /**
     * gives you the Active Status of the Machine
     */
    fun isActive(): Boolean

    /**
     * sets the visible Active Status of the Machine
     */
    fun setActive(aActive: Boolean)

    /**
     * Indicates if the object in question was forced to shut down (i.e. loss of power)
     */
    fun wasShutdown(): Boolean {
        return false
    }
}
