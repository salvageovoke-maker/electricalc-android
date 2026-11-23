# ElectriCalc Android

Native Kotlin and Jetpack Compose electrical calculator.

## Phase 1
Adds 16 calculators in Basic Electrical and AC Circuits: Ohm's Law, Electrical Power, Resistance, Series Resistance, Parallel Resistance, Voltage Divider, Current Divider, Electrical Energy, Capacitive Reactance, Inductive Reactance, Impedance, Power Factor, Active/Reactive/Apparent Power, Frequency and Period, Resonant Frequency, and AC Voltage and Current.

Select the unknown value, enter the known values in the displayed SI units, and calculate. Results include the formula, substitution and working. Favorites are session-only in this phase. History persistence and settings are planned for later updates.

GitHub Actions runs unit tests and creates a debug APK.


## Phase 2
Adds 17 calculators across Three-Phase Systems and Transformers:
- Three-phase power, apparent power and line current
- Star (Y) voltage/current and Delta (Δ) voltage/current relationships
- Power factor correction
- Transformer turns ratio, primary/secondary voltage and current ratios
- Efficiency, loading, full-load current, losses, percentage impedance and voltage regulation

Three-phase power equations assume a balanced system and use line quantities. Transformer current and full-load current calculators use ideal transformer ratios and a balanced three-phase transformer respectively. Power-factor correction returns the required total reactive power in var; capacitor connection and per-phase capacitance sizing require the applicable system voltage, frequency and connection details and are not inferred by this calculator.
