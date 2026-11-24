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


## Phase 3
Adds 18 calculators across Power Distribution and Electrical Machines.

Power Distribution includes single-phase and balanced three-phase cable voltage drop, voltage-drop percentage, conductor resistance, three-phase copper loss, single-phase load current, maximum demand, load factor, diversity factor, demand factor and distribution efficiency.

Electrical Machines includes synchronous speed, induction motor slip, rotor speed, shaft torque, motor input power, motor output power, motor efficiency and three-phase motor current.

Engineering assumptions:
- Cable voltage-drop calculations use the stated conductor resistance and reactance in Ω/km, with cable length in metres. The single-phase equation uses a two-conductor loop. The three-phase equation assumes a balanced load.
- Cable conductor resistance uses the resistivity, conductor length and cross-sectional area entered by the user. Temperature correction, skin effect and AC resistance are not included.
- Cable copper loss is three-phase and uses per-phase resistance.
- Load current is single-phase. Three-phase motor current assumes a balanced motor and uses line voltage, output power, power factor and efficiency.
- Cable ampacity is not estimated from a generic formula. Select a cable using the applicable IEC/installation tables, installation method, ambient temperature, grouping and permissible voltage drop.
- Verify all cable selections and protection settings against applicable standards, manufacturer data and site conditions.
