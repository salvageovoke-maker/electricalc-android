package com.electricalc.app

/**
 * SI-unit Ohm's law solver.
 * Supply any two of voltage (V), current (A), and resistance (ohm).
 */
data class OhmsLawResult(
    val voltageVolts: Double,
    val currentAmps: Double,
    val resistanceOhms: Double
)

object OhmsLaw {
    fun solve(
        voltage: Double? = null,
        current: Double? = null,
        resistance: Double? = null
    ): OhmsLawResult {
        val supplied = listOf(voltage, current, resistance).count { it != null }
        require(supplied == 2) { "Enter exactly two known values." }
        require(listOfNotNull(voltage, current, resistance).all { it.isFinite() }) {
            "Values must be finite numbers."
        }

        val v: Double
        val i: Double
        val r: Double
        when {
            voltage == null -> {
                require(resistance!! != 0.0) { "Resistance cannot be zero when solving voltage." }
                v = current!! * resistance
                i = current
                r = resistance
            }
            current == null -> {
                require(resistance!! != 0.0) { "Resistance cannot be zero when solving current." }
                v = voltage
                i = voltage / resistance
                r = resistance
            }
            else -> {
                require(current != 0.0) { "Current cannot be zero when solving resistance." }
                v = voltage
                i = current
                r = voltage / current
            }
        }
        return OhmsLawResult(v, i, r)
    }
}
