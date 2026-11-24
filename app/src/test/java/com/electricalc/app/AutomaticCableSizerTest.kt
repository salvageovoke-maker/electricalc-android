package com.electricalc.app

import org.junit.Assert.*
import org.junit.Test

class AutomaticCableSizerTest {
    private fun sample() = AutomaticCableInput(
        powerKw=30.0, voltageV=400.0, powerFactor=0.85, efficiencyPercent=90.0,
        phases=3, conductor=CableConductor.COPPER, insulation=CableInsulation.PVC,
        method=CableInstallationMethod.B2, ambientFactor=0.94, groupingFactor=0.80,
        otherFactor=1.0, lengthM=50.0, maximumVoltageDropPercent=5.0
    )

    @Test fun recommendsSmallestCopperPvcSizePassingCurrentAndDrop() {
        val result=AutomaticCableSizer.calculate(sample())
        assertEquals(25.0, result.recommendedSizeMm2!!, 0.0)
        assertTrue(result.correctedAmpacityA!! >= result.designCurrentA)
        assertTrue(result.voltageDropPercent!! <= 5.0)
    }

    @Test fun usesThreeLoadedConductorTableForThreePhase() {
        val result=AutomaticCableSizer.calculate(sample())
        assertEquals(80.0, result.tabulatedAmpacityA!!, 0.0)
    }

    @Test fun usesTwoLoadedConductorTableForSinglePhase() {
        val result=AutomaticCableSizer.calculate(sample().copy(phases=1,powerKw=5.0,voltageV=230.0,efficiencyPercent=100.0))
        assertEquals(6.0, result.recommendedSizeMm2!!, 0.0)
        assertTrue(result.voltageDropPercent!! <= 5.0)
    }

    @Test fun supportsCopperXlpeAmpacityTable() {
        val result=AutomaticCableSizer.calculate(sample().copy(insulation=CableInsulation.XLPE))
        assertNotNull(result.recommendedSizeMm2)
    }

    @Test fun supportsAluminiumPvcAmpacityTable() {
        val result=AutomaticCableSizer.calculate(sample().copy(conductor=CableConductor.ALUMINIUM))
        assertNotNull(result.recommendedSizeMm2)
    }

    @Test fun supportsAluminiumXlpeAmpacityTable() {
        val result=AutomaticCableSizer.calculate(sample().copy(conductor=CableConductor.ALUMINIUM,insulation=CableInsulation.XLPE))
        assertNotNull(result.recommendedSizeMm2)
    }

    @Test fun increasesRequiredTableRatingWhenCorrectionFactorsFall() {
        val normal=AutomaticCableSizer.calculate(sample())
        val derated=AutomaticCableSizer.calculate(sample().copy(groupingFactor=0.5))
        assertTrue(derated.requiredTabulatedAmpacityA > normal.requiredTabulatedAmpacityA)
        assertTrue(derated.recommendedSizeMm2!! >= normal.recommendedSizeMm2!!)
    }

    @Test fun returnsNoRecommendationWhenAvailableSizesAreInsufficient() {
        val result=AutomaticCableSizer.calculate(sample().copy(powerKw=1000.0))
        assertNull(result.recommendedSizeMm2)
        assertTrue(result.message.contains("No listed cable size"))
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsInvalidCorrectionFactor() {
        AutomaticCableSizer.calculate(sample().copy(ambientFactor=0.0))
    }

    @Test(expected=IllegalArgumentException::class)
    fun rejectsInvalidVoltageDropLimit() {
        AutomaticCableSizer.calculate(sample().copy(maximumVoltageDropPercent=0.0))
    }
}
