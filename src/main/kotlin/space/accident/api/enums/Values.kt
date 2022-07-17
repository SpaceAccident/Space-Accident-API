package space.accident.api.enums

object Values {

    val V = longArrayOf(
        8L,
        32L,
        128L,
        512L,
        2048L,
        8192L,
        32768L,
        131072L,
        524288L,
        2097152L,
        8388608L,
        33554432L,
        134217728L,
        536870912L,
        1073741824L,
        (Int.MAX_VALUE - 7).toLong()
    )

    /**
     * An Array containing all Sides which follow the Condition, in order to iterate over them for example.
     */
    val ALL_SIDES = byteArrayOf(0, 1, 2, 3, 4, 5, 6)

    /**
     * An Array containing all Sides which follow the Condition, in order to iterate over them for example.
     */
    val ALL_VALID_SIDES = byteArrayOf(0, 1, 2, 3, 4, 5)
}