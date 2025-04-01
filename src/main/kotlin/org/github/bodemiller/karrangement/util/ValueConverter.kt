package org.github.bodemiller.karrangement.util

/**
 * @author Bode Miller
 */
object ValueConverter {

    /**
     * Attempts to convert a value to an Integer
     *
     * @param value - The value to attempt to get an integer from
     */
    fun convertInt(value: Any): Int? = when (value) {
        is String -> value.toIntOrNull()
        is Number -> value.toInt()
        else -> null
    }

}