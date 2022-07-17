package space.accident.api.interfaces.te

import space.accident.api.enums.Values.ALL_VALID_SIDES


/**
 * Implemented by all my Machines. However without any security checks, if the Players are even allowed to rotate it.
 */
interface ITurnable {
    /**
     * Get the block's facing.
     *
     * @return front Block facing
     */
    fun getFrontFacing(): Byte

    /**
     * Set the block's facing
     *
     * @param aSide facing to set the block to
     */
    fun setFrontFacing(aSide: Byte)

    /**
     * Get the block's back facing.
     *
     * @return opposite Block facing
     */
    fun getBackFacing(): Byte

    /**
     * Determine if the wrench can be used to set the block's facing.
     */
    fun isValidFacing(aSide: Byte): Boolean

    /**
     * Get the list of valid facings
     */
    fun validFacings(): BooleanArray {
        val validFacings = BooleanArray(6)
        for (facing in ALL_VALID_SIDES) {
            validFacings[facing.toInt()] = isValidFacing(facing)
        }
        return validFacings
    }
}
