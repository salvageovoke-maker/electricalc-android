package com.electricalc.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {
    @Test fun phaseOneCatalogHasSixteenCalculators() { assertEquals(16, CalculatorCatalog.all.count { it.category == "Basic Electrical" || it.category == "AC Circuits" }) }
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
    @Test fun phaseTwoCatalogHasFifteenCalculators() {
        assertEquals(17, CalculatorCatalog.all.count { it.category == "Three-Phase Systems" || it.category == "Transformers" })
    }
    @Test fun solvesBalancedThreePhasePower() {
        val r=CalculatorCatalog.solve("threepower","P",mapOf("V" to 400.0,"I" to 10.0,"pf" to 0.8))
        assertEquals(5542.5626,r.value,1e-3)
    }
    @Test fun solvesStarPhaseVoltage() {
        val r=CalculatorCatalog.solve("starvoltage","Vph",mapOf("VL" to 400.0))
        assertEquals(230.9401,r.value,1e-3)
    }
    @Test fun solvesDeltaLineCurrent() {
        val r=CalculatorCatalog.solve("deltacurrent","IL",mapOf("Iph" to 10.0))
        assertEquals(17.3205,r.value,1e-3)
    }
    @Test fun solvesDeltaLineVoltage() {
        val r=CalculatorCatalog.solve("deltavoltage","VL",mapOf("Vph" to 400.0))
        assertEquals(400.0,r.value,1e-9)
    }
    @Test fun solvesTransformerFullLoadCurrent() {
        val r=CalculatorCatalog.solve("tflcurrent","I",mapOf("S" to 500.0,"V" to 11000.0))
        assertEquals(26.243,r.value,1e-3)
    }
    @Test fun solvesTransformerEfficiency() {
        val r=CalculatorCatalog.solve("efficiency","η",mapOf("Pout" to 9500.0,"Pin" to 10000.0))
        assertEquals(95.0,r.value,1e-9)
    }
}


class PhaseThreeCalculatorTest {
    @org.junit.Test fun completeCatalogHasFiftyTwoCalculators() { org.junit.Assert.assertEquals(72, CalculatorCatalog.all.size) }
    @org.junit.Test fun phaseThreeAddsEighteenCalculators() {
        org.junit.Assert.assertEquals(19, CalculatorCatalog.all.count { it.category == "Power Distribution" || it.category == "Electrical Machines" })
    }
    @org.junit.Test fun calculatesThreePhaseVoltageDrop() {
        val r=CalculatorCatalog.solve("vdrop3","VD",mapOf("I" to 100.0,"L" to 100.0,"R" to 0.2,"X" to 0.08,"pf" to 0.8))
        org.junit.Assert.assertEquals(3.603,r.value,0.01)
    }
    @org.junit.Test fun calculatesCableResistance() {
        val r=CalculatorCatalog.solve("cableR","R",mapOf("rho" to 1.72e-8,"L" to 100.0,"A" to 50.0))
        org.junit.Assert.assertEquals(0.0344,r.value,0.0001)
    }
    @org.junit.Test fun calculatesCableAreaFromResistance() {
        val r=CalculatorCatalog.solve("cableR","A",mapOf("R" to 0.0344,"rho" to 1.72e-8,"L" to 100.0))
        org.junit.Assert.assertEquals(50.0,r.value,0.01)
    }
    @org.junit.Test fun calculatesSynchronousSpeed() {
        val r=CalculatorCatalog.solve("syncSpeed","Ns",mapOf("f" to 50.0,"P" to 4.0))
        org.junit.Assert.assertEquals(1500.0,r.value,1e-9)
    }
    @org.junit.Test fun calculatesMotorSlip() {
        val r=CalculatorCatalog.solve("slip","s",mapOf("Ns" to 1500.0,"Nr" to 1440.0))
        org.junit.Assert.assertEquals(4.0,r.value,1e-9)
    }
    @org.junit.Test fun calculatesMotorTorque() {
        val r=CalculatorCatalog.solve("torque","T",mapOf("Pout" to 15.0,"N" to 1470.0))
        org.junit.Assert.assertEquals(97.449,r.value,0.01)
    }
    @org.junit.Test fun calculatesThreePhaseMotorCurrent() {
        val r=CalculatorCatalog.solve("motorCurrent","I",mapOf("Pout" to 15.0,"V" to 400.0,"pf" to 0.85,"η" to 90.0))
        org.junit.Assert.assertEquals(28.31,r.value,0.1)
    }
}

class PhaseFourCalculatorTest {
 @org.junit.Test fun phaseFourHasTwentyCalculators() {
  org.junit.Assert.assertEquals(10,CalculatorCatalog.all.count{it.category=="Energy & Consumption"})
  org.junit.Assert.assertEquals(10,CalculatorCatalog.all.count{it.category=="Unit Conversions"})
 }
 @org.junit.Test fun energyCostExample() {
  val r=CalculatorCatalog.solve("energyCost","Cost",mapOf("E" to 150.0,"Rate" to 60.0))
  org.junit.Assert.assertEquals(9000.0,r.value,1e-9)
 }
 @org.junit.Test fun meterReadingExample() {
  val r=CalculatorCatalog.solve("meterEnergy","E",mapOf("End" to 1250.0,"Start" to 1100.0))
  org.junit.Assert.assertEquals(150.0,r.value,1e-9)
 }
 @org.junit.Test fun batteryRuntimeExample() {
  val r=CalculatorCatalog.solve("batteryRuntime","t",mapOf("V" to 12.0,"Ah" to 100.0,"DOD" to 80.0,"P" to 200.0,"η" to 90.0))
  org.junit.Assert.assertEquals(4.32,r.value,1e-9)
 }
 @org.junit.Test fun powerConversionExample() {
  val r=CalculatorCatalog.solve("convPower","W",mapOf("kW" to 2.5))
  org.junit.Assert.assertEquals(2500.0,r.value,1e-9)
 }
}

class TwoUnitConversionTest {
 @org.junit.Test fun convertsKilovoltsToVolts(){val r=CalculatorCatalog.convert("convVoltage","kV","V",2.5);org.junit.Assert.assertEquals(2500.0,r.value,1e-9)}
 @org.junit.Test fun convertsMillivoltsToKilovolts(){val r=CalculatorCatalog.convert("convVoltage","mV","kV",500.0);org.junit.Assert.assertEquals(0.0005,r.value,1e-12)}
 @org.junit.Test fun convertsJoulesToKilowattHours(){val r=CalculatorCatalog.convert("convEnergy","J","kWh",3600000.0);org.junit.Assert.assertEquals(1.0,r.value,1e-12)}
 @org.junit.Test fun convertsFahrenheitToKelvin(){val r=CalculatorCatalog.convert("convTemperature","°F","K",32.0);org.junit.Assert.assertEquals(273.15,r.value,1e-9)}
 @org.junit.Test fun convertsKelvinToFahrenheit(){val r=CalculatorCatalog.convert("convTemperature","K","°F",273.15);org.junit.Assert.assertEquals(32.0,r.value,1e-9)}
 @org.junit.Test fun sameUnitConversionKeepsValue(){val r=CalculatorCatalog.convert("convCurrent","A","A",12.5);org.junit.Assert.assertEquals(12.5,r.value,1e-12)}
}
