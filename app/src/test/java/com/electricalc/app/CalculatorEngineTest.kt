package com.electricalc.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {
    @Test fun phaseOneCatalogHasSixteenCalculators() { assertEquals(16, CalculatorCatalog.all.size) }
    @Test fun solvesOhmsLawVoltage() {
        val r=CalculatorCatalog.solve("ohms","V",mapOf("I" to 2.0,"R" to 10.0))
        assertEquals(20.0,r.value,1e-9)
    }
    @Test fun solvesParallelResistance() {
        val r=CalculatorCatalog.solve("parallel","RT",mapOf("R1" to 6.0,"R2" to 3.0))
        assertEquals(2.0,r.value,1e-9)
    }
    @Test fun solvesInductiveReactance() {
        val r=CalculatorCatalog.solve("inductive","XL",mapOf("f" to 50.0,"L" to 0.1))
        assertEquals(31.4159265,r.value,1e-6)
    }
    @Test fun solvesResonantFrequency() {
        val r=CalculatorCatalog.solve("resonance","f",mapOf("L" to 0.1,"C" to 0.0001))
        assertTrue(r.value>0.0)
    }
    @Test(expected=IllegalArgumentException::class) fun rejectsZeroResistance() {
        CalculatorCatalog.solve("ohms","I",mapOf("V" to 12.0,"R" to 0.0))
    }
}
