package space.accident.api.ext

import space.accident.api.enums.Values.V

fun Number.safeInt(margin: Int): Int {
    return when {
        this.toLong() > Int.MAX_VALUE -> Int.MAX_VALUE - margin
        else -> this.toInt()
    }
}

fun Number.safeInt(): Int {
    return when {
        this.toLong() > V[V.size - 1] -> V[V.size - 1].safeInt(1)
        this.toLong() < Int.MIN_VALUE -> Int.MIN_VALUE
        else -> this.toInt()
    }
}