package com.electricalc.app

import org.junit.Assert.assertEquals
import org.junit.Test

class OhmsLawTest {
    @Test fun solvesVoltage() {
        val result = OhmsLaw.solve(current = 2.0, resistance = 10.0)
        assertEquals(20.0, result.voltageVolts, 1e-9)
    }

    @Test fun solvesCurrent() {
        val result = OhmsLaw.solve(voltage = 24.0, resistance = 12.0)
        assertEquals(2.0, result.currentAmps, 1e-9)
    }

    @Test fun solvesResistance() {
        val result = OhmsLaw.solve(voltage = 24.0, current = 3.0)
        assertEquals(8.0, result.resistanceOhms, 1e-9)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsThreeKnownValues() {
        OhmsLaw.solve(voltage = 12.0, current = 2.0, resistance = 6.0)
    }
}
