package space.accident.api.interfaces.mte

interface IConnectable {

    /**
     * Try to connect to the Block at the specified side
     * returns the connection state. Non-positive values for failed, others for succeeded.
     */
    fun connect(aSide: Byte): Int

    /**
     * Try to disconnect to the Block at the specified side
     */
    fun disconnect(aSide: Byte)
    fun isConnectedAtSide(aSide: Int): Boolean

    companion object {
        const val NO_CONNECTION = 0
        const val CONNECTED_DOWN = 1
        const val CONNECTED_UP = 2
        const val CONNECTED_NORTH = 4
        const val CONNECTED_SOUTH = 8
        const val CONNECTED_WEST = 16
        const val CONNECTED_EAST = 32
        const val CONNECTED_ALL = 63
        const val HAS_FRESHFOAM = 64
        const val HAS_HARDENEDFOAM = 128
        const val HAS_FOAM = 192
    }
}