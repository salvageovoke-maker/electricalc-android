package com.electricalc.app
import org.junit.Assert.assertEquals
import org.junit.Test
class OhmsLawTest {
    @Test fun solvesVoltage(){assertEquals(20.0,OhmsLaw.solve(current=2.0,resistance=10.0).voltageVolts,1e-9)}
    @Test fun solvesCurrent(){assertEquals(2.0,OhmsLaw.solve(voltage=24.0,resistance=12.0).currentAmps,1e-9)}
    @Test fun solvesResistance(){assertEquals(8.0,OhmsLaw.solve(voltage=24.0,current=3.0).resistanceOhms,1e-9)}
    @Test(expected=IllegalArgumentException::class) fun rejectsThreeKnownValues(){OhmsLaw.solve(voltage=12.0,current=2.0,resistance=6.0)}
}
