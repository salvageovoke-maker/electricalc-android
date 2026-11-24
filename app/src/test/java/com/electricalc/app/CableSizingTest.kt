package com.electricalc.app

import org.junit.Assert.*
import org.junit.Test

class CableSizingTest {
    private fun sample() = CableSizingInput(
        powerKw = 30.0, voltageV = 400.0, powerFactor = 0.85,
        efficiencyPercent = 90.0, phases = 3, tabulatedAmpacityA = 70.0,
        ambientFactor = 0.94, groupingFactor = 0.8, installationFactor = 1.0,
        otherFactor = 1.0, lengthM = 50.0, voltageDropCoefficientMvPerAm = 1.0,
        conductorResistanceOhmPerKm = 0.387, maximumVoltageDropPercent = 5.0
    )

    @Test fun calculatesBalancedThreePhaseDesignCurrent() {
        val result = CableSizingEngine.calculate(sample())
        assertEquals(56.603, result.designCurrentA, 0.01)
    }

    @Test fun appliesAllCorrectionFactorsToTabulatedAmpacity() {
        val result = CableSizingEngine.calculate(sample())
        assertEquals(52.64, result.correctedAmpacityA, 0.02)
        assertFalse(result.ampacityPass)
    }

    @Test fun calculatesVoltageDropAndPercentage() {
        val result = CableSizingEngine.calculate(sample())
        assertEquals(2.8301, result.voltageDropV, 0.01)
        assertEquals(0.7075, result.voltageDropPercent, 0.01)
        assertTrue(result.voltageDropPass)
    }

    @Test fun calculatesThreePhaseConductorLoss() {
        val result = CableSizingEngine.calculate(sample())
        assertEquals(185.99, result.conductorLossW, 0.5)
    }

    @Test fun calculatesSinglePhaseCurrent() {
        val input = sample().copy(powerKw=5.0, voltageV=230.0, phases=1)
        val result = CableSizingEngine.calculate(input)
        assertEquals(28.417, result.designCurrentA, 0.01)
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsInvalidPowerFactor() {
        CableSizingEngine.calculate(sample().copy(powerFactor=0.0))
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsZeroLength() {
        CableSizingEngine.calculate(sample().copy(lengthM=0.0))
    }

    @Test fun reportsVoltageDropFailureWhenLimitIsExceeded() {
        val result = CableSizingEngine.calculate(sample().copy(
            lengthM=500.0, maximumVoltageDropPercent=1.0
        ))
        assertFalse(result.voltageDropPass)
    }
}
