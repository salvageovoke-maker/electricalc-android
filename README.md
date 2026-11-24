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
Adds 19 calculators across Power Distribution and Electrical Machines. The complete catalog contains 52 calculators.

Power Distribution includes single-phase and balanced three-phase cable voltage drop, voltage-drop percentage, conductor resistance, three-phase copper loss, single-phase load current, maximum demand, load factor, diversity factor, demand factor, distribution efficiency and the additional distribution calculations included in the catalog.

Electrical Machines includes synchronous speed, induction motor slip, rotor speed, shaft torque, motor input power, motor output power, motor efficiency and three-phase motor current.

Engineering assumptions:
- Cable voltage-drop calculations use the stated conductor resistance and reactance in Ω/km, with cable length in metres. The single-phase equation uses a two-conductor loop. The three-phase equation assumes a balanced load.
- Cable conductor resistance uses the resistivity, conductor length and cross-sectional area entered by the user. Temperature correction, skin effect and AC resistance are not included.
- Cable copper loss is three-phase and uses per-phase resistance.
- Load current is single-phase. Three-phase motor current assumes a balanced motor and uses line voltage, output power, power factor and efficiency.
- Cable ampacity is not estimated from a generic formula. Select a cable using the applicable IEC/installation tables, installation method, ambient temperature, grouping and permissible voltage drop.
- Verify all cable selections and protection settings against applicable standards, manufacturer data and site conditions.


## Phase 4
Adds 20 calculators across Energy & Consumption and Unit Conversions, bringing the catalog to 72 calculators. Battery runtime and capacity are estimates based on nominal voltage, rated Ah, user-entered depth of discharge, load and inverter efficiency. Actual battery performance varies with age, temperature, chemistry and discharge rate. Energy cost is energy multiplied by the entered tariff rate and excludes fixed charges, taxes, demand charges and tariff bands.


### Conversion interface update (v1.5.0)
All 10 conversion tools use a source unit, one input value, and a target unit. Temperature supports every direction among Celsius, Fahrenheit and Kelvin.


## Version 1.6.0 interface update

- Added a premium Graphite and Amber ElectriCalc brand icon for the Android launcher and in-app header.
- Added distinct icons for all eight calculator categories.
- Added a functional navigation drawer with Home, Favorites, History, Library, category shortcuts, and About ElectriCalc.
- Preserved the existing bottom navigation and calculator screens.


## Version 1.7.0 settings

- Added a functional Settings screen from the home gear and navigation drawer.
- Added persistent System default, Light, and Dark appearance preferences.
- Added persistent result precision choices: 2, 4, or 6 decimal places.
- Added a persistent haptic feedback toggle, applied to navigation and home controls.
- Added a reset preferences action.
- Settings are stored locally using Android SharedPreferences.


## Version 1.8.0 calculation history

- Added automatic history entries after successful standard calculator calculations and unit conversions.
- Stores calculator name, category, entered values or conversion details, result, and date/time.
- Persists the latest 200 calculations locally on the device.
- Added history count, open-calculator shortcut, individual delete, and clear-all confirmation.
- History is stored separately from appearance and interaction preferences.


## Version 1.9.0 reference library

- Removed the "Phase 1 formulas" heading.
- Grouped reference formulas under the same eight categories used on the calculator home screen.
- Added collapsible accordion sections with category icons and formula counts.
- Only one category remains expanded at a time.
- Formula names and expressions are displayed when a category is expanded.
- Formula definitions and calculator registrations remain unchanged.


## Version 1.9.1 build correction

- Added safe default values for the optional history state and history callbacks in ElectriCalcApp.
- This keeps lightweight preview or alternate entry-point calls source-compatible while the production root continues to pass the persistent history state and handlers.
- No calculator logic, history storage, category grouping, accordion behavior, or formulas were changed.


## Version 1.9.2 Settings screen build correction

- Removed accidentally inherited calculation-history parameters from the SettingsScreen function signature.
- SettingsScreen now accepts only the settings values and callbacks used by its caller.
- Persistent history remains owned by ElectriCalcApp and continues to flow into HistoryScreen and calculator screens.
- No calculator formulas, category accordion behavior, or saved-history storage were changed.


## Version 1.9.3 persistent favorites

- Favorites are loaded from local SharedPreferences when the app starts.
- Adding or removing a calculator from Favorites immediately saves the updated list.
- Favorite IDs are stored as JSON, deduplicated, and invalid stored data safely falls back to an empty list.
- Existing favorite IDs remain compatible because the stored values are the existing calculator IDs.
- Added unit tests for favorite serialization, duplicate removal, blank IDs, and malformed stored data.


## Version 1.9.4
- Fixed the Favorites build error by declaring the hoisted favorites collection as `MutableList<String>`, matching the add/remove operations used by the calculator screen. Persistent local storage is retained.
