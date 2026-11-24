package com.electricalc.app

import kotlin.math.sqrt

data class CableSizingInput(
    val powerKw: Double,
    val voltageV: Double,
    val powerFactor: Double,
    val efficiencyPercent: Double,
    val phases: Int,
    val tabulatedAmpacityA: Double,
    val ambientFactor: Double,
    val groupingFactor: Double,
    val installationFactor: Double,
    val otherFactor: Double,
    val lengthM: Double,
    val voltageDropCoefficientMvPerAm: Double,
    val conductorResistanceOhmPerKm: Double,
    val maximumVoltageDropPercent: Double
)

data class CableSizingResult(
    val designCurrentA: Double,
    val totalCorrectionFactor: Double,
    val correctedAmpacityA: Double,
    val ampacityPass: Boolean,
    val voltageDropV: Double,
    val voltageDropPercent: Double,
    val voltageDropPass: Boolean,
    val conductorLossW: Double
)

object CableSizingEngine {
    fun calculate(input: CableSizingInput): CableSizingResult {
        require(input.powerKw > 0.0) { "Load power must be greater than zero." }
        require(input.voltageV > 0.0) { "Supply voltage must be greater than zero." }
        require(input.powerFactor in 0.01..1.0) { "Power factor must be between 0.01 and 1.00." }
        require(input.efficiencyPercent in 1.0..100.0) { "Efficiency must be between 1 and 100%." }
        require(input.phases == 1 || input.phases == 3) { "Select single-phase or three-phase." }
        require(input.tabulatedAmpacityA > 0.0) { "Enter the cable's tabulated current capacity." }
        val factors = listOf(input.ambientFactor, input.groupingFactor, input.installationFactor, input.otherFactor)
        require(factors.all { it in 0.01..2.0 }) { "Each correction factor must be between 0.01 and 2.00." }
        require(input.lengthM > 0.0) { "Cable length must be greater than zero." }
        require(input.voltageDropCoefficientMvPerAm >= 0.0) { "Voltage-drop coefficient cannot be negative." }
        require(input.conductorResistanceOhmPerKm >= 0.0) { "Conductor resistance cannot be negative." }
        require(input.maximumVoltageDropPercent > 0.0) { "Maximum voltage drop must be greater than zero." }

        val current = if (input.phases == 1) {
            input.powerKw * 1000.0 / (input.voltageV * input.powerFactor * (input.efficiencyPercent / 100.0))
        } else {
            input.powerKw * 1000.0 / (sqrt(3.0) * input.voltageV * input.powerFactor * (input.efficiencyPercent / 100.0))
        }
        val factor = factors.fold(1.0) { acc, value -> acc * value }
        val corrected = input.tabulatedAmpacityA * factor
        val drop = input.voltageDropCoefficientMvPerAm * current * input.lengthM / 1000.0
        val dropPercent = drop / input.voltageV * 100.0
        val resistance = input.conductorResistanceOhmPerKm * input.lengthM / 1000.0
        val losses = if (input.phases == 1) 2.0 * current * current * resistance else 3.0 * current * current * resistance

        return CableSizingResult(
            designCurrentA = current,
            totalCorrectionFactor = factor,
            correctedAmpacityA = corrected,
            ampacityPass = corrected >= current,
            voltageDropV = drop,
            voltageDropPercent = dropPercent,
            voltageDropPass = dropPercent <= input.maximumVoltageDropPercent,
            conductorLossW = losses
        )
    }
}


enum class CableConductor { COPPER, ALUMINIUM }
enum class CableInsulation { PVC, XLPE }
enum class CableInstallationMethod(val label: String) {
    A1("A1 · Single-core in conduit in insulated wall"),
    A2("A2 · Multicore in conduit in insulated wall"),
    B1("B1 · Single-core in conduit on wall"),
    B2("B2 · Multicore in conduit on wall"),
    C("C · Clipped direct"),
    D1("D1 · Multicore in underground duct"),
    D2("D2 · Direct buried cable")
}

data class AutomaticCableInput(
    val powerKw: Double, val voltageV: Double, val powerFactor: Double,
    val efficiencyPercent: Double, val phases: Int, val conductor: CableConductor,
    val insulation: CableInsulation, val method: CableInstallationMethod,
    val ambientFactor: Double, val groupingFactor: Double,
    val otherFactor: Double, val lengthM: Double, val maximumVoltageDropPercent: Double
)

data class AutomaticCableResult(
    val designCurrentA: Double, val requiredTabulatedAmpacityA: Double,
    val recommendedSizeMm2: Double?, val tabulatedAmpacityA: Double?,
    val correctedAmpacityA: Double?, val voltageDropV: Double?,
    val voltageDropPercent: Double?, val conductorLossW: Double?,
    val nextSizeMm2: Double?, val message: String
)

/**
 * Reference ampacities at IEC 60364-5-52 Annex B reference conditions:
 * 30 °C air / 20 °C ground, one circuit, reference installation methods A1-D2.
 * Tables B.52.2-B.52.5: PVC two/three loaded conductors and XLPE/EPR two/three.
 * Values are tabulated as size:A1,A2,B1,B2,C,D1,D2.
 */
object AutomaticCableSizer {
    private val sizes = listOf(1.5,2.5,4.0,6.0,10.0,16.0,25.0,35.0,50.0,70.0,95.0,120.0,150.0,185.0,240.0,300.0)
    private val methods = CableInstallationMethod.values().map { it.name }

    private fun rows(data: String): Map<Double,List<Double>> = data.trimIndent().lines().filter { it.isNotBlank() }.associate { line ->
        val parts=line.trim().split(":")
        parts[0].toDouble() to parts[1].split(",").map { it.toDouble() }
    }

    private val tables = mapOf(
        "COPPER_PVC_2" to rows("""
            1.5:14.5,14,17.5,16.5,19.5,22,22
            2.5:19.5,18.5,24,23,27,29,28
            4:26,25,32,30,36,37,38
            6:34,32,41,38,46,46,48
            10:46,43,57,52,63,60,64
            16:61,57,76,69,85,78,83
            25:80,75,101,90,112,99,110
            35:99,92,125,111,138,119,132
            50:119,110,151,133,168,140,156
            70:151,139,192,168,213,173,192
            95:182,167,232,201,258,204,230
            120:210,192,269,232,299,231,261
            150:240,219,300,258,344,261,293
            185:273,248,341,294,392,292,331
            240:321,291,400,344,461,336,382
            300:367,334,458,394,530,379,427
        """),
        "COPPER_PVC_3" to rows("""
            1.5:13.5,13,15.5,15,17.5,18,19
            2.5:18,17.5,21,20,24,24,24
            4:24,23,28,27,32,30,33
            6:31,29,36,34,41,38,41
            10:42,39,50,46,57,50,54
            16:56,52,68,62,76,64,70
            25:73,68,89,80,96,82,92
            35:89,83,110,99,119,98,110
            50:108,99,134,118,144,116,130
            70:136,125,171,149,184,143,162
            95:164,150,207,179,223,169,193
            120:188,172,239,206,259,192,220
            150:216,196,262,225,299,217,246
            185:245,223,296,255,341,243,278
            240:286,261,346,297,403,280,320
            300:328,298,394,339,464,316,359
        """),
        "COPPER_XLPE_2" to rows("""
            1.5:19,18.5,23,22,24,25,27
            2.5:26,25,31,30,33,33,35
            4:35,33,42,40,45,43,46
            6:45,42,54,51,58,53,58
            10:61,57,75,69,80,71,77
            16:81,76,100,91,107,91,100
            25:106,99,133,119,138,116,129
            35:131,121,164,146,171,139,155
            50:158,145,198,175,209,164,183
            70:200,183,253,221,269,203,225
            95:241,220,306,265,328,239,270
            120:278,253,354,305,382,271,306
            150:318,290,393,334,441,306,343
            185:362,329,449,384,506,343,387
            240:424,386,528,459,599,395,448
            300:486,442,603,532,693,446,502
        """),
        "COPPER_XLPE_3" to rows("""
            1.5:17,16.5,20,19.5,22,21,23
            2.5:23,22,28,26,30,28,30
            4:31,30,37,35,40,36,39
            6:40,38,48,44,52,44,49
            10:54,51,66,60,71,58,65
            16:73,68,88,80,96,75,84
            25:95,89,117,105,119,96,107
            35:117,109,144,128,147,115,129
            50:141,130,175,154,179,135,153
            70:179,164,222,194,229,167,188
            95:216,197,269,233,278,197,226
            120:249,227,312,268,322,223,257
            150:285,259,342,300,371,251,287
            185:324,295,384,340,424,281,324
            240:380,346,450,398,500,324,375
            300:435,396,514,455,576,365,419
        """),
        "ALUMINIUM_PVC_2" to rows("""
            2.5:15,14.5,18.5,17.5,21,22,0
            4:20,19.5,25,24,28,29,0
            6:26,25,32,30,36,36,0
            10:36,33,44,41,49,47,0
            16:48,44,60,54,66,61,63
            25:63,58,79,71,83,77,82
            35:77,71,97,86,103,93,98
            50:93,86,118,104,125,109,117
            70:118,108,150,131,160,135,145
            95:142,130,181,157,195,159,173
            120:164,150,210,181,226,180,200
            150:189,172,234,201,261,204,224
            185:215,195,266,230,298,228,255
            240:252,229,312,269,352,262,298
            300:289,263,358,308,406,296,336
        """),
        "ALUMINIUM_PVC_3" to rows("""
            2.5:14,13.5,16.5,15.5,18.5,18.5,0
            4:18.5,17.5,22,21,25,24,0
            6:24,23,28,27,32,30,0
            10:32,31,39,36,44,39,0
            16:43,41,53,48,59,50,53
            25:57,53,70,62,73,64,69
            35:70,65,86,77,90,77,83
            50:84,78,104,92,110,91,99
            70:107,98,133,116,140,112,122
            95:129,118,161,139,170,132,148
            120:149,135,186,160,197,150,169
            150:170,155,204,176,227,169,189
            185:194,176,230,199,259,190,214
            240:227,207,269,232,305,218,250
            300:261,237,306,265,351,247,282
        """),
        "ALUMINIUM_XLPE_2" to rows("""
            2.5:20,19.5,25,23,26,26,0
            4:27,26,33,31,35,33,0
            6:35,33,43,40,45,42,0
            10:48,45,59,54,62,55,0
            16:64,60,79,72,84,71,76
            25:84,78,105,94,101,90,98
            35:103,96,130,115,126,108,117
            50:125,115,157,138,154,128,139
            70:158,145,200,175,198,158,170
            95:191,175,242,210,241,186,204
            120:220,201,281,242,280,211,233
            150:253,230,307,261,324,238,261
            185:288,262,351,300,371,267,296
            240:338,307,412,358,439,307,343
            300:387,352,471,415,508,346,386
        """),
        "ALUMINIUM_XLPE_3" to rows("""
            2.5:19,18,22,21,24,22,0
            4:25,24,29,28,32,28,0
            6:32,31,38,35,41,35,0
            10:44,41,52,48,57,46,0
            16:58,55,71,64,76,59,64
            25:76,71,93,84,90,75,82
            35:94,87,116,103,112,90,98
            50:113,104,140,124,136,106,117
            70:142,131,179,156,174,130,144
            95:171,157,217,188,211,154,172
            120:197,180,251,216,245,174,197
            150:226,206,267,240,283,197,220
            185:256,233,300,272,323,220,250
            240:300,273,351,318,382,253,290
            300:344,313,402,364,440,286,326
        """)
    )

    fun calculate(input: AutomaticCableInput): AutomaticCableResult {
        require(input.powerKw > 0.0 && input.voltageV > 0.0) { "Power and voltage must be greater than zero." }
        require(input.powerFactor in 0.01..1.0) { "Power factor must be between 0.01 and 1.00." }
        require(input.efficiencyPercent in 1.0..100.0) { "Efficiency must be between 1 and 100%." }
        require(input.phases == 1 || input.phases == 3) { "Select single-phase or three-phase." }
        require(listOf(input.ambientFactor,input.groupingFactor,input.otherFactor).all { it in 0.01..2.0 }) { "Correction factors must be between 0.01 and 2.00." }
        require(input.lengthM > 0.0 && input.maximumVoltageDropPercent > 0.0) { "Cable length and voltage-drop limit must be greater than zero." }
        val current = if (input.phases == 1) input.powerKw*1000.0/(input.voltageV*input.powerFactor*(input.efficiencyPercent/100.0))
            else input.powerKw*1000.0/(kotlin.math.sqrt(3.0)*input.voltageV*input.powerFactor*(input.efficiencyPercent/100.0))
        val correction = input.ambientFactor*input.groupingFactor*input.otherFactor
        val requiredTable = current/correction
        val loaded = if (input.phases == 1) 2 else 3
        val tableKey = "${input.conductor.name}_${input.insulation.name}_$loaded"
        val table = tables[tableKey] ?: error("No ampacity table is available for this combination.")
        val column = methods.indexOf(input.method.name)
        val operatingTemp = if (input.insulation == CableInsulation.PVC) 70.0 else 90.0
        val rho20 = if (input.conductor == CableConductor.COPPER) 0.017241 else 0.028264
        val alpha = if (input.conductor == CableConductor.COPPER) 0.00393 else 0.00403
        val candidates = table.entries.sortedBy { it.key }.filter { it.value[column] > 0.0 }
        var selected: Map.Entry<Double,List<Double>>? = null
        var selectedDrop = 0.0
        var selectedDropPercent = 0.0
        var selectedLoss = 0.0
        for (candidate in candidates) {
            val size = candidate.key
            val tabulated = candidate.value[column]
            if (tabulated*correction + 1e-9 < current) continue
            val resistanceOhmPerKm = rho20/size*1000.0*(1.0+alpha*(operatingTemp-20.0))
            val rPerM = resistanceOhmPerKm/1000.0
            val drop = if (input.phases == 1) 2.0*current*input.lengthM*rPerM*input.powerFactor
                else kotlin.math.sqrt(3.0)*current*input.lengthM*rPerM*input.powerFactor
            val dropPercent = drop/input.voltageV*100.0
            if (dropPercent <= input.maximumVoltageDropPercent) {
                selected = candidate
                selectedDrop = drop
                selectedDropPercent = dropPercent
                selectedLoss = (if (input.phases == 1) 2.0 else 3.0)*current*current*rPerM*input.lengthM
                break
            }
        }
        val nextSize = selected?.let { chosen -> candidates.firstOrNull { it.key > chosen.key }?.key }
        val tabulated = selected?.value?.get(column)
        return AutomaticCableResult(
            designCurrentA=current, requiredTabulatedAmpacityA=requiredTable,
            recommendedSizeMm2=selected?.key, tabulatedAmpacityA=tabulated,
            correctedAmpacityA=tabulated?.times(correction),
            voltageDropV=selected?.let { selectedDrop }, voltageDropPercent=selected?.let { selectedDropPercent },
            conductorLossW=selected?.let { selectedLoss }, nextSizeMm2=nextSize,
            message=if (selected == null) "No listed cable size satisfies both ampacity and voltage-drop limits. Review the installation design or use a larger parallel-cable design assessed separately." else "Smallest listed size passing the entered ampacity and voltage-drop checks."
        )
    }
}
