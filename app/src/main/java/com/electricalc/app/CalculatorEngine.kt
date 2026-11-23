package com.electricalc.app

data class CalcField(val key:String,val label:String,val unit:String)
data class CalcAnswer(val value:Double,val unit:String,val formula:String,val substitution:String,val steps:List<String>)
data class CalcSpec(val id:String,val category:String,val name:String,val description:String,val fields:List<CalcField>,val formula:String)

object CalculatorCatalog {
    private fun f(k:String,n:String,u:String)=CalcField(k,n,u)
    val all=listOf(
        CalcSpec("ohms","Basic Electrical","Ohm's Law","Voltage, current and resistance",listOf(f("V","Voltage","V"),f("I","Current","A"),f("R","Resistance","Ω")),"V = I × R"),
        CalcSpec("power","Basic Electrical","Electrical Power","DC or resistive power",listOf(f("P","Power","W"),f("V","Voltage","V"),f("I","Current","A")),"P = V × I"),
        CalcSpec("resistance","Basic Electrical","Resistance","Resistance from voltage and current",listOf(f("R","Resistance","Ω"),f("V","Voltage","V"),f("I","Current","A")),"R = V ÷ I"),
        CalcSpec("series","Basic Electrical","Series Resistance","Two resistors in series",listOf(f("RT","Total resistance","Ω"),f("R1","Resistance 1","Ω"),f("R2","Resistance 2","Ω")),"RT = R1 + R2"),
        CalcSpec("parallel","Basic Electrical","Parallel Resistance","Two resistors in parallel",listOf(f("RT","Total resistance","Ω"),f("R1","Resistance 1","Ω"),f("R2","Resistance 2","Ω")),"RT = (R1 × R2) ÷ (R1 + R2)"),
        CalcSpec("divider","Basic Electrical","Voltage Divider","Output voltage across R2",listOf(f("Vout","Output voltage","V"),f("Vin","Input voltage","V"),f("R1","Series resistance","Ω"),f("R2","Output resistance","Ω")),"Vout = Vin × R2 ÷ (R1 + R2)"),
        CalcSpec("currentdivider","Basic Electrical","Current Divider","Current through branch R1",listOf(f("I1","Branch current","A"),f("IT","Total current","A"),f("R1","Branch resistance 1","Ω"),f("R2","Branch resistance 2","Ω")),"I1 = IT × R2 ÷ (R1 + R2)"),
        CalcSpec("energy","Basic Electrical","Electrical Energy","Energy from power and time",listOf(f("E","Energy","Wh"),f("P","Power","W"),f("t","Time","h")),"E = P × t"),
        CalcSpec("capacitive","AC Circuits","Capacitive Reactance","Capacitor opposition to AC",listOf(f("Xc","Capacitive reactance","Ω"),f("f","Frequency","Hz"),f("C","Capacitance","F")),"Xc = 1 ÷ (2πfC)"),
        CalcSpec("inductive","AC Circuits","Inductive Reactance","Inductor opposition to AC",listOf(f("XL","Inductive reactance","Ω"),f("f","Frequency","Hz"),f("L","Inductance","H")),"XL = 2πfL"),
        CalcSpec("impedance","AC Circuits","Impedance","Series resistance and net reactance",listOf(f("Z","Impedance","Ω"),f("R","Resistance","Ω"),f("X","Net reactance","Ω")),"Z = √(R² + X²)"),
        CalcSpec("pf","AC Circuits","Power Factor","Active power divided by apparent power",listOf(f("pf","Power factor",""),f("P","Active power","W"),f("S","Apparent power","VA")),"pf = P ÷ S"),
        CalcSpec("acpower","AC Circuits","Active, Reactive and Apparent Power","Power triangle relationship",listOf(f("S","Apparent power","VA"),f("P","Active power","W"),f("Q","Reactive power","var")),"S² = P² + Q²"),
        CalcSpec("frequency","AC Circuits","Frequency and Period","Reciprocal relationship",listOf(f("f","Frequency","Hz"),f("T","Period","s")),"f = 1 ÷ T"),
        CalcSpec("resonance","AC Circuits","Resonant Frequency","Ideal LC circuit",listOf(f("f","Resonant frequency","Hz"),f("L","Inductance","H"),f("C","Capacitance","F")),"f = 1 ÷ (2π√(LC))"),
        CalcSpec("acvalues","AC Circuits","AC Voltage and Current","Peak, RMS and peak-to-peak conversion",listOf(f("RMS","RMS value",""),f("Peak","Peak value",""),f("PP","Peak-to-peak value","")),"Peak = √2 × RMS; PP = 2 × Peak"),
        CalcSpec("threepower","Three-Phase Systems","Three-Phase Power","Balanced three-phase power using line values",listOf(f("P","Active power","W"),f("V","Line voltage","V"),f("I","Line current","A"),f("pf","Power factor","")),"P = √3 × VL × IL × pf"),
        CalcSpec("threeapparent","Three-Phase Systems","Three-Phase Apparent Power","Balanced three-phase apparent power",listOf(f("S","Apparent power","VA"),f("V","Line voltage","V"),f("I","Line current","A")),"S = √3 × VL × IL"),
        CalcSpec("threecurrent","Three-Phase Systems","Three-Phase Line Current","Line current from active power",listOf(f("I","Line current","A"),f("P","Active power","W"),f("V","Line voltage","V"),f("pf","Power factor","")),"I = P ÷ (√3 × VL × pf)"),
        CalcSpec("starvoltage","Three-Phase Systems","Star (Y) Voltage Relationship","Line voltage and phase voltage",listOf(f("VL","Line voltage","V"),f("Vph","Phase voltage","V")),"VL = √3 × Vph"),
        CalcSpec("starcurrent","Three-Phase Systems","Star (Y) Current Relationship","Line current and phase current",listOf(f("IL","Line current","A"),f("Iph","Phase current","A")),"IL = Iph"),
        CalcSpec("deltavoltage","Three-Phase Systems","Delta (Δ) Voltage Relationship","Line voltage and phase voltage",listOf(f("VL","Line voltage","V"),f("Vph","Phase voltage","V")),"VL = Vph"),
        CalcSpec("deltacurrent","Three-Phase Systems","Delta (Δ) Current Relationship","Line current and phase current",listOf(f("IL","Line current","A"),f("Iph","Phase current","A")),"IL = √3 × Iph"),
        CalcSpec("pfcorr","Three-Phase Systems","Power Factor Correction","Required capacitor bank reactive power",listOf(f("Qc","Capacitor reactive power","var"),f("P","Active power","W"),f("pf1","Initial power factor",""),f("pf2","Target power factor","")),"Qc = P × (tan(cos⁻¹ pf1) − tan(cos⁻¹ pf2))"),
        CalcSpec("turns","Transformers","Transformer Turns Ratio","Primary to secondary turns ratio",listOf(f("a","Turns ratio",""),f("Np","Primary turns","turns"),f("Ns","Secondary turns","turns")),"a = Np ÷ Ns"),
        CalcSpec("transformerv","Transformers","Primary and Secondary Voltage","Ideal transformer voltage ratio",listOf(f("Vp","Primary voltage","V"),f("Vs","Secondary voltage","V"),f("Np","Primary turns","turns"),f("Ns","Secondary turns","turns")),"Vp ÷ Vs = Np ÷ Ns"),
        CalcSpec("transformeri","Transformers","Transformer Current Ratio","Ideal transformer current ratio",listOf(f("Ip","Primary current","A"),f("Is","Secondary current","A"),f("Np","Primary turns","turns"),f("Ns","Secondary turns","turns")),"Ip ÷ Is = Ns ÷ Np"),
        CalcSpec("efficiency","Transformers","Transformer Efficiency","Output power compared with input power",listOf(f("η","Efficiency","%"),f("Pout","Output power","W"),f("Pin","Input power","W")),"η = (Pout ÷ Pin) × 100"),
        CalcSpec("loading","Transformers","Transformer Loading","Actual load as a percentage of rated capacity",listOf(f("Load","Loading","%"),f("Sload","Actual load","kVA"),f("Srated","Rated capacity","kVA")),"Loading = (Sload ÷ Srated) × 100"),
        CalcSpec("tflcurrent","Transformers","Transformer Full-Load Current","Three-phase transformer rated line current",listOf(f("I","Full-load current","A"),f("S","Rated capacity","kVA"),f("V","Line voltage","V")),"I = (S × 1000) ÷ (√3 × V)"),
        CalcSpec("losses","Transformers","Transformer Losses","Total power loss from input and output",listOf(f("Loss","Total losses","W"),f("Pin","Input power","W"),f("Pout","Output power","W")),"Loss = Pin − Pout"),
        CalcSpec("impedancepct","Transformers","Transformer Percentage Impedance","Percentage impedance from short-circuit voltage",listOf(f("%Z","Percentage impedance","%"),f("Vsc","Short-circuit voltage","V"),f("Vrated","Rated voltage","V")),"%Z = (Vsc ÷ Vrated) × 100"),
        CalcSpec("regulation","Transformers","Transformer Voltage Regulation","Voltage change from no-load to full-load",listOf(f("Reg","Voltage regulation","%"),f("Vnl","No-load voltage","V"),f("Vfl","Full-load voltage","V")),"Reg = ((Vnl − Vfl) ÷ Vfl) × 100")
    )

    fun solve(id:String,u:String,m:Map<String,Double>):CalcAnswer {
        fun v(k:String)=m[k] ?: error("Enter the required known values.")
        fun pos(x:Double,n:String){require(x>0.0 && x.isFinite()){ "$n must be greater than zero." }}
        val p=Math.PI
        val x:Double; val unit:String; val formula:String; val sub:String; val steps:List<String>
        when(id){
            "ohms" -> when(u){
                "V"->{val a=v("I");val b=v("R");x=a*b;unit="V";formula="V = I × R";sub="V = $a × $b";steps=listOf("Multiply current by resistance.")}
                "I"->{val a=v("V");val b=v("R");pos(b,"Resistance");x=a/b;unit="A";formula="I = V ÷ R";sub="I = $a ÷ $b";steps=listOf("Divide voltage by resistance.")}
                else->{val a=v("V");val b=v("I");pos(b,"Current");x=a/b;unit="Ω";formula="R = V ÷ I";sub="R = $a ÷ $b";steps=listOf("Divide voltage by current.")}
            }
            "power" -> when(u){
                "P"->{val a=v("V");val b=v("I");x=a*b;unit="W";formula="P = V × I";sub="P = $a × $b";steps=listOf("Multiply voltage by current.")}
                "V"->{val a=v("P");val b=v("I");pos(b,"Current");x=a/b;unit="V";formula="V = P ÷ I";sub="V = $a ÷ $b";steps=listOf("Divide power by current.")}
                else->{val a=v("P");val b=v("V");pos(b,"Voltage");x=a/b;unit="A";formula="I = P ÷ V";sub="I = $a ÷ $b";steps=listOf("Divide power by voltage.")}
            }
            "resistance" -> when(u){
                "R"->{val a=v("V");val b=v("I");pos(b,"Current");x=a/b;unit="Ω";formula="R = V ÷ I";sub="R = $a ÷ $b";steps=listOf("Divide voltage by current.")}
                "V"->{val a=v("I");val b=v("R");x=a*b;unit="V";formula="V = I × R";sub="V = $a × $b";steps=listOf("Multiply current by resistance.")}
                else->{val a=v("V");val b=v("R");pos(b,"Resistance");x=a/b;unit="A";formula="I = V ÷ R";sub="I = $a ÷ $b";steps=listOf("Divide voltage by resistance.")}
            }
            "series" -> when(u){
                "RT"->{val a=v("R1");val b=v("R2");x=a+b;unit="Ω";formula="RT = R1 + R2";sub="RT = $a + $b";steps=listOf("Add the resistor values.")}
                "R1"->{val a=v("RT");val b=v("R2");x=a-b;unit="Ω";formula="R1 = RT − R2";sub="R1 = $a − $b";steps=listOf("Subtract R2 from total resistance.")}
                else->{val a=v("RT");val b=v("R1");x=a-b;unit="Ω";formula="R2 = RT − R1";sub="R2 = $a − $b";steps=listOf("Subtract R1 from total resistance.")}
            }
            "parallel" -> when(u){
                "RT"->{val a=v("R1");val b=v("R2");pos(a,"R1");pos(b,"R2");x=a*b/(a+b);unit="Ω";formula="RT = (R1 × R2) ÷ (R1 + R2)";sub="RT = ($a × $b) ÷ ($a + $b)";steps=listOf("Multiply the resistances, then divide by their sum.")}
                "R1"->{val a=v("RT");val b=v("R2");pos(a,"RT");require(b>a){"R2 must exceed total resistance."};x=a*b/(b-a);unit="Ω";formula="R1 = (RT × R2) ÷ (R2 − RT)";sub="R1 = ($a × $b) ÷ ($b − $a)";steps=listOf("Rearrange the parallel resistance equation.")}
                else->{val a=v("RT");val b=v("R1");pos(a,"RT");require(b>a){"R1 must exceed total resistance."};x=a*b/(b-a);unit="Ω";formula="R2 = (RT × R1) ÷ (R1 − RT)";sub="R2 = ($a × $b) ÷ ($b − $a)";steps=listOf("Rearrange the parallel resistance equation.")}
            }
            "energy" -> when(u){
                "E"->{val a=v("P");val b=v("t");x=a*b;unit="Wh";formula="E = P × t";sub="E = $a × $b";steps=listOf("Multiply power by time.")}
                "P"->{val a=v("E");val b=v("t");pos(b,"Time");x=a/b;unit="W";formula="P = E ÷ t";sub="P = $a ÷ $b";steps=listOf("Divide energy by time.")}
                else->{val a=v("E");val b=v("P");pos(b,"Power");x=a/b;unit="h";formula="t = E ÷ P";sub="t = $a ÷ $b";steps=listOf("Divide energy by power.")}
            }
            "capacitive" -> when(u){
                "Xc"->{val a=v("f");val b=v("C");pos(a,"Frequency");pos(b,"Capacitance");x=1/(2*p*a*b);unit="Ω";formula="Xc = 1 ÷ (2πfC)";sub="Xc = 1 ÷ (2π × $a × $b)";steps=listOf("Substitute frequency and capacitance.")}
                "f"->{val a=v("Xc");val b=v("C");pos(a,"Reactance");pos(b,"Capacitance");x=1/(2*p*a*b);unit="Hz";formula="f = 1 ÷ (2πXcC)";sub="f = 1 ÷ (2π × $a × $b)";steps=listOf("Rearrange for frequency.")}
                else->{val a=v("Xc");val b=v("f");pos(a,"Reactance");pos(b,"Frequency");x=1/(2*p*a*b);unit="F";formula="C = 1 ÷ (2πfXc)";sub="C = 1 ÷ (2π × $b × $a)";steps=listOf("Rearrange for capacitance.")}
            }
            "inductive" -> when(u){
                "XL"->{val a=v("f");val b=v("L");pos(a,"Frequency");pos(b,"Inductance");x=2*p*a*b;unit="Ω";formula="XL = 2πfL";sub="XL = 2π × $a × $b";steps=listOf("Multiply 2π, frequency and inductance.")}
                "f"->{val a=v("XL");val b=v("L");pos(b,"Inductance");x=a/(2*p*b);unit="Hz";formula="f = XL ÷ (2πL)";sub="f = $a ÷ (2π × $b)";steps=listOf("Rearrange for frequency.")}
                else->{val a=v("XL");val b=v("f");pos(b,"Frequency");x=a/(2*p*b);unit="H";formula="L = XL ÷ (2πf)";sub="L = $a ÷ (2π × $b)";steps=listOf("Rearrange for inductance.")}
            }
            "impedance" -> when(u){
                "Z"->{val a=v("R");val b=v("X");x=kotlin.math.sqrt(a*a+b*b);unit="Ω";formula="Z = √(R² + X²)";sub="Z = √($a² + $b²)";steps=listOf("Square R and X, add, then take the square root.")}
                "R"->{val a=v("Z");val b=v("X");require(a*a>=b*b){"Z must be at least |X|."};x=kotlin.math.sqrt(a*a-b*b);unit="Ω";formula="R = √(Z² − X²)";sub="R = √($a² − $b²)";steps=listOf("Rearrange the impedance triangle.")}
                else->{val a=v("Z");val b=v("R");require(a*a>=b*b){"Z must be at least R."};x=kotlin.math.sqrt(a*a-b*b);unit="Ω";formula="X = √(Z² − R²)";sub="X = √($a² − $b²)";steps=listOf("Rearrange the impedance triangle.")}
            }
            "pf" -> when(u){
                "pf"->{val a=v("P");val b=v("S");pos(b,"Apparent power");x=a/b;unit="";formula="pf = P ÷ S";sub="pf = $a ÷ $b";steps=listOf("Divide active power by apparent power.")}
                "P"->{val a=v("pf");val b=v("S");x=a*b;unit="W";formula="P = pf × S";sub="P = $a × $b";steps=listOf("Multiply power factor by apparent power.")}
                else->{val a=v("P");val b=v("pf");pos(b,"Power factor");x=a/b;unit="VA";formula="S = P ÷ pf";sub="S = $a ÷ $b";steps=listOf("Divide active power by power factor.")}
            }
            "acpower" -> when(u){
                "S"->{val a=v("P");val b=v("Q");x=kotlin.math.sqrt(a*a+b*b);unit="VA";formula="S = √(P² + Q²)";sub="S = √($a² + $b²)";steps=listOf("Square P and Q, add, then take the square root.")}
                "P"->{val a=v("S");val b=v("Q");require(a*a>=b*b){"S must be at least |Q|."};x=kotlin.math.sqrt(a*a-b*b);unit="W";formula="P = √(S² − Q²)";sub="P = √($a² − $b²)";steps=listOf("Rearrange the power triangle.")}
                else->{val a=v("S");val b=v("P");require(a*a>=b*b){"S must be at least |P|."};x=kotlin.math.sqrt(a*a-b*b);unit="var";formula="Q = √(S² − P²)";sub="Q = √($a² − $b²)";steps=listOf("Rearrange the power triangle.")}
            }
            "frequency"->{val a=v(if(u=="f")"T" else "f");pos(a,"Input");x=1/a;unit=if(u=="f")"Hz" else "s";formula=if(u=="f")"f = 1 ÷ T" else "T = 1 ÷ f";sub="${if(u=="f")"f" else "T"} = 1 ÷ $a";steps=listOf("Take the reciprocal of the known value.")}
            "resonance" -> when(u){
                "f"->{val a=v("L");val b=v("C");pos(a,"Inductance");pos(b,"Capacitance");x=1/(2*p*kotlin.math.sqrt(a*b));unit="Hz";formula="f = 1 ÷ (2π√(LC))";sub="f = 1 ÷ (2π√($a × $b))";steps=listOf("Multiply L and C, take the square root, then apply the formula.")}
                "L"->{val a=v("f");val b=v("C");pos(a,"Frequency");pos(b,"Capacitance");x=1/(4*p*p*a*a*b);unit="H";formula="L = 1 ÷ (4π²f²C)";sub="L = 1 ÷ (4π² × $a² × $b)";steps=listOf("Rearrange the resonance equation for inductance.")}
                else->{val a=v("f");val b=v("L");pos(a,"Frequency");pos(b,"Inductance");x=1/(4*p*p*a*a*b);unit="F";formula="C = 1 ÷ (4π²f²L)";sub="C = 1 ÷ (4π² × $a² × $b)";steps=listOf("Rearrange the resonance equation for capacitance.")}
            }
            "acvalues" -> when(u){
                "RMS"->{val a=v("Peak");x=a/kotlin.math.sqrt(2.0);unit="RMS";formula="RMS = Peak ÷ √2";sub="RMS = $a ÷ √2";steps=listOf("Divide peak value by √2.")}
                "Peak"->{val a=v("RMS");x=a*kotlin.math.sqrt(2.0);unit="Peak";formula="Peak = RMS × √2";sub="Peak = $a × √2";steps=listOf("Multiply RMS value by √2.")}
                else->{val a=v("Peak");x=2*a;unit="Peak-to-peak";formula="PP = 2 × Peak";sub="PP = 2 × $a";steps=listOf("Multiply peak value by two.")}
            }
            "divider" -> when(u){
                "Vout"->{val a=v("Vin");val b=v("R1");val c=v("R2");pos(b+c,"Resistance sum");x=a*c/(b+c);unit="V";formula="Vout = Vin × R2 ÷ (R1 + R2)";sub="Vout = $a × $c ÷ ($b + $c)";steps=listOf("Apply the voltage divider equation.")}
                "Vin"->{val a=v("Vout");val b=v("R1");val c=v("R2");pos(c,"R2");x=a*(b+c)/c;unit="V";formula="Vin = Vout × (R1 + R2) ÷ R2";sub="Vin = $a × ($b + $c) ÷ $c";steps=listOf("Rearrange for input voltage.")}
                "R1"->{val a=v("Vout");val b=v("Vin");val c=v("R2");pos(a,"Vout");x=c*(b-a)/a;unit="Ω";formula="R1 = R2 × (Vin − Vout) ÷ Vout";sub="R1 = $c × ($b − $a) ÷ $a";steps=listOf("Rearrange for R1.")}
                else->{val a=v("Vout");val b=v("Vin");val c=v("R1");pos(b-a,"Vin minus Vout");x=a*c/(b-a);unit="Ω";formula="R2 = Vout × R1 ÷ (Vin − Vout)";sub="R2 = $a × $c ÷ ($b − $a)";steps=listOf("Rearrange for R2.")}
            }
            "currentdivider" -> when(u){
                "I1"->{val a=v("IT");val b=v("R1");val c=v("R2");pos(b+c,"Resistance sum");x=a*c/(b+c);unit="A";formula="I1 = IT × R2 ÷ (R1 + R2)";sub="I1 = $a × $c ÷ ($b + $c)";steps=listOf("Apply the current divider rule.")}
                "IT"->{val a=v("I1");val b=v("R1");val c=v("R2");pos(c,"R2");x=a*(b+c)/c;unit="A";formula="IT = I1 × (R1 + R2) ÷ R2";sub="IT = $a × ($b + $c) ÷ $c";steps=listOf("Rearrange for total current.")}
                "R1"->{val a=v("I1");val b=v("IT");val c=v("R2");pos(a,"Branch current");x=c*(b-a)/a;unit="Ω";formula="R1 = R2 × (IT − I1) ÷ I1";sub="R1 = $c × ($b − $a) ÷ $a";steps=listOf("Rearrange for R1.")}
                else->{val a=v("I1");val b=v("IT");val c=v("R1");pos(b-a,"IT minus I1");x=a*c/(b-a);unit="Ω";formula="R2 = I1 × R1 ÷ (IT − I1)";sub="R2 = $a × $c ÷ ($b − $a)";steps=listOf("Rearrange for R2.")}
            }

            "threepower" -> when(u){
                "P"->{val a=v("V");val b=v("I");val c=v("pf");pos(a,"Line voltage");pos(b,"Line current");require(c in 0.0..1.0){"Power factor must be between 0 and 1."};x=kotlin.math.sqrt(3.0)*a*b*c;unit="W";formula="P = √3 × VL × IL × pf";sub="P = √3 × $a × $b × $c";steps=listOf("Multiply line voltage, line current and power factor.","Multiply by √3 for a balanced three-phase system.")}
                "V"->{val a=v("P");val b=v("I");val c=v("pf");pos(b,"Line current");pos(c,"Power factor");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="V";formula="VL = P ÷ (√3 × IL × pf)";sub="VL = $a ÷ (√3 × $b × $c)";steps=listOf("Divide active power by √3, line current and power factor.")}
                "I"->{val a=v("P");val b=v("V");val c=v("pf");pos(b,"Line voltage");pos(c,"Power factor");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="A";formula="IL = P ÷ (√3 × VL × pf)";sub="IL = $a ÷ (√3 × $b × $c)";steps=listOf("Divide active power by √3, line voltage and power factor.")}
                else->{val a=v("P");val b=v("V");val c=v("I");pos(b,"Line voltage");pos(c,"Line current");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="";formula="pf = P ÷ (√3 × VL × IL)";sub="pf = $a ÷ (√3 × $b × $c)";steps=listOf("Divide active power by three-phase apparent power.");require(x in 0.0..1.0){"Calculated power factor is outside 0 to 1."}}
            }
            "threeapparent" -> when(u){
                "S"->{val a=v("V");val b=v("I");pos(a,"Line voltage");pos(b,"Line current");x=kotlin.math.sqrt(3.0)*a*b;unit="VA";formula="S = √3 × VL × IL";sub="S = √3 × $a × $b";steps=listOf("Multiply line voltage and line current by √3.")}
                "V"->{val a=v("S");val b=v("I");pos(b,"Line current");x=a/(kotlin.math.sqrt(3.0)*b);unit="V";formula="VL = S ÷ (√3 × IL)";sub="VL = $a ÷ (√3 × $b)";steps=listOf("Divide apparent power by √3 times line current.")}
                else->{val a=v("S");val b=v("V");pos(b,"Line voltage");x=a/(kotlin.math.sqrt(3.0)*b);unit="A";formula="IL = S ÷ (√3 × VL)";sub="IL = $a ÷ (√3 × $b)";steps=listOf("Divide apparent power by √3 times line voltage.")}
            }
            "threecurrent" -> when(u){
                "I"->{val a=v("P");val b=v("V");val c=v("pf");pos(b,"Line voltage");pos(c,"Power factor");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="A";formula="I = P ÷ (√3 × VL × pf)";sub="I = $a ÷ (√3 × $b × $c)";steps=listOf("Divide active power by √3, line voltage and power factor.")}
                "P"->{val a=v("I");val b=v("V");val c=v("pf");x=kotlin.math.sqrt(3.0)*a*b*c;unit="W";formula="P = √3 × VL × I × pf";sub="P = √3 × $b × $a × $c";steps=listOf("Calculate balanced three-phase active power.")}
                "V"->{val a=v("P");val b=v("I");val c=v("pf");pos(b,"Current");pos(c,"Power factor");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="V";formula="VL = P ÷ (√3 × I × pf)";sub="VL = $a ÷ (√3 × $b × $c)";steps=listOf("Rearrange the three-phase power equation.")}
                else->{val a=v("P");val b=v("V");val c=v("I");pos(b,"Voltage");pos(c,"Current");x=a/(kotlin.math.sqrt(3.0)*b*c);unit="";formula="pf = P ÷ (√3 × VL × I)";sub="pf = $a ÷ (√3 × $b × $c)";steps=listOf("Divide active power by apparent power.")}
            }
            "starvoltage" -> when(u){
                "VL"->{val a=v("Vph");x=kotlin.math.sqrt(3.0)*a;unit="V";formula="VL = √3 × Vph";sub="VL = √3 × $a";steps=listOf("Multiply phase voltage by √3.")}
                else->{val a=v("VL");x=a/kotlin.math.sqrt(3.0);unit="V";formula="Vph = VL ÷ √3";sub="Vph = $a ÷ √3";steps=listOf("Divide line voltage by √3.")}
            }
            "starcurrent" -> when(u){
                "IL"->{x=v("Iph");unit="A";formula="IL = Iph";sub="IL = $x";steps=listOf("In a star connection, line current equals phase current.")}
                else->{x=v("IL");unit="A";formula="Iph = IL";sub="Iph = $x";steps=listOf("In a star connection, phase current equals line current.")}
            }
            "deltavoltage" -> when(u){
                "VL"->{x=v("Vph");unit="V";formula="VL = Vph";sub="VL = $x";steps=listOf("In a delta connection, line voltage equals phase voltage.")}
                else->{x=v("VL");unit="V";formula="Vph = VL";sub="Vph = $x";steps=listOf("In a delta connection, phase voltage equals line voltage.")}
            }
            "deltacurrent" -> when(u){
                "IL"->{val a=v("Iph");x=kotlin.math.sqrt(3.0)*a;unit="A";formula="IL = √3 × Iph";sub="IL = √3 × $a";steps=listOf("Multiply phase current by √3.")}
                else->{val a=v("IL");x=a/kotlin.math.sqrt(3.0);unit="A";formula="Iph = IL ÷ √3";sub="Iph = $a ÷ √3";steps=listOf("Divide line current by √3.")}
            }
            "pfcorr" -> when(u){
                "Qc"->{val a=v("P");val b=v("pf1");val c=v("pf2");require(b in 0.0..1.0 && c in 0.0..1.0 && b>0 && c>0){"Power factors must be greater than 0 and no greater than 1."};x=a*(kotlin.math.tan(kotlin.math.acos(b))-kotlin.math.tan(kotlin.math.acos(c)));unit="var";formula="Qc = P × (tan(cos⁻¹ pf1) − tan(cos⁻¹ pf2))";sub="Qc = $a × (tan(cos⁻¹ $b) − tan(cos⁻¹ $c))";steps=listOf("Convert both power factors to phase angles.","Find the difference between their tangent values.","Multiply by active power.")}
                "P"->{val a=v("Qc");val b=v("pf1");val c=v("pf2");require(b in 0.0..1.0 && c in 0.0..1.0 && b>0 && c>0){"Power factors must be greater than 0 and no greater than 1."};val d=kotlin.math.tan(kotlin.math.acos(b))-kotlin.math.tan(kotlin.math.acos(c));pos(kotlin.math.abs(d),"Power factor angle difference");x=a/d;unit="W";formula="P = Qc ÷ (tan(cos⁻¹ pf1) − tan(cos⁻¹ pf2))";sub="P = $a ÷ ($d)";steps=listOf("Calculate the tangent difference, then divide capacitor reactive power by it.")}
                "pf1"->{val a=v("Qc");val b=v("P");val c=v("pf2");pos(b,"Active power");require(c in 0.0..1.0 && c>0){"Target power factor must be greater than 0 and no greater than 1."};val t=a/b+kotlin.math.tan(kotlin.math.acos(c));x=kotlin.math.cos(kotlin.math.atan(t));unit="";formula="pf1 = cos(tan⁻¹(Qc ÷ P + tan(cos⁻¹ pf2)))";sub="pf1 = cos(tan⁻¹($a ÷ $b + tan(cos⁻¹ $c)))";steps=listOf("Calculate the target phase angle tangent.","Add Qc divided by active power.","Convert the resulting angle back to power factor.")}
                else->{val a=v("Qc");val b=v("P");val c=v("pf1");pos(b,"Active power");require(c in 0.0..1.0 && c>0){"Initial power factor must be greater than 0 and no greater than 1."};val t=kotlin.math.tan(kotlin.math.acos(c))-a/b;require(t>=0){"Target power factor cannot be leading for this calculation."};x=kotlin.math.cos(kotlin.math.atan(t));unit="";formula="pf2 = cos(tan⁻¹(tan(cos⁻¹ pf1) − Qc ÷ P))";sub="pf2 = cos(tan⁻¹(tan(cos⁻¹ $c) − $a ÷ $b))";steps=listOf("Calculate the initial phase angle tangent.","Subtract Qc divided by active power.","Convert the resulting angle back to target power factor.")}
            }
            "turns" -> when(u){
                "a"->{val a=v("Np");val b=v("Ns");pos(b,"Secondary turns");x=a/b;unit="";formula="a = Np ÷ Ns";sub="a = $a ÷ $b";steps=listOf("Divide primary turns by secondary turns.")}
                "Np"->{val a=v("a");val b=v("Ns");x=a*b;unit="turns";formula="Np = a × Ns";sub="Np = $a × $b";steps=listOf("Multiply turns ratio by secondary turns.")}
                else->{val a=v("Np");val b=v("a");pos(b,"Turns ratio");x=a/b;unit="turns";formula="Ns = Np ÷ a";sub="Ns = $a ÷ $b";steps=listOf("Divide primary turns by turns ratio.")}
            }
            "transformerv" -> when(u){
                "Vp"->{val a=v("Vs");val b=v("Np");val c=v("Ns");pos(c,"Secondary turns");x=a*b/c;unit="V";formula="Vp = Vs × Np ÷ Ns";sub="Vp = $a × $b ÷ $c";steps=listOf("Multiply secondary voltage by the turns ratio.")}
                "Vs"->{val a=v("Vp");val b=v("Ns");val c=v("Np");pos(c,"Primary turns");x=a*b/c;unit="V";formula="Vs = Vp × Ns ÷ Np";sub="Vs = $a × $b ÷ $c";steps=listOf("Multiply primary voltage by secondary-to-primary turns ratio.")}
                "Np"->{val a=v("Vp");val b=v("Vs");val c=v("Ns");pos(b,"Secondary voltage");x=a*c/b;unit="turns";formula="Np = Vp × Ns ÷ Vs";sub="Np = $a × $c ÷ $b";steps=listOf("Rearrange the ideal transformer voltage ratio.")}
                else->{val a=v("Vs");val b=v("Vp");val c=v("Np");pos(b,"Primary voltage");x=a*c/b;unit="turns";formula="Ns = Vs × Np ÷ Vp";sub="Ns = $a × $c ÷ $b";steps=listOf("Rearrange the ideal transformer voltage ratio.")}
            }
            "transformeri" -> when(u){
                "Ip"->{val a=v("Is");val b=v("Ns");val c=v("Np");pos(c,"Primary turns");x=a*b/c;unit="A";formula="Ip = Is × Ns ÷ Np";sub="Ip = $a × $b ÷ $c";steps=listOf("Use the inverse current-to-turns relationship.")}
                "Is"->{val a=v("Ip");val b=v("Np");val c=v("Ns");pos(c,"Secondary turns");x=a*c/b;unit="A";formula="Is = Ip × Np ÷ Ns";sub="Is = $a × $b ÷ $c";steps=listOf("Multiply primary current by primary-to-secondary turns ratio.")}
                "Np"->{val a=v("Is");val b=v("Ns");val c=v("Ip");pos(c,"Primary current");x=a*b/c;unit="turns";formula="Np = Is × Ns ÷ Ip";sub="Np = $a × $b ÷ $c";steps=listOf("Rearrange the ideal transformer current ratio.")}
                else->{val a=v("Ip");val b=v("Np");val c=v("Is");pos(c,"Secondary current");x=a*b/c;unit="turns";formula="Ns = Ip × Np ÷ Is";sub="Ns = $a × $b ÷ $c";steps=listOf("Rearrange the ideal transformer current ratio.")}
            }
            "efficiency" -> when(u){
                "η"->{val a=v("Pout");val b=v("Pin");pos(b,"Input power");x=a/b*100;unit="%";formula="η = (Pout ÷ Pin) × 100";sub="η = ($a ÷ $b) × 100";steps=listOf("Divide output power by input power.","Multiply by 100 to express as a percentage.")}
                "Pout"->{val a=v("η");val b=v("Pin");x=a/100*b;unit="W";formula="Pout = (η ÷ 100) × Pin";sub="Pout = ($a ÷ 100) × $b";steps=listOf("Convert efficiency to a decimal and multiply by input power.")}
                else->{val a=v("Pout");val b=v("η");pos(b,"Efficiency");x=a/(b/100);unit="W";formula="Pin = Pout ÷ (η ÷ 100)";sub="Pin = $a ÷ ($b ÷ 100)";steps=listOf("Divide output power by efficiency as a decimal.")}
            }
            "loading" -> when(u){
                "Load"->{val a=v("Sload");val b=v("Srated");pos(b,"Rated capacity");x=a/b*100;unit="%";formula="Loading = (Sload ÷ Srated) × 100";sub="Loading = ($a ÷ $b) × 100";steps=listOf("Divide actual load by rated capacity.","Multiply by 100.")}
                "Sload"->{val a=v("Load");val b=v("Srated");x=a/100*b;unit="kVA";formula="Sload = (Loading ÷ 100) × Srated";sub="Sload = ($a ÷ 100) × $b";steps=listOf("Convert loading percentage to a decimal and multiply by rated capacity.")}
                else->{val a=v("Sload");val b=v("Load");pos(b,"Loading percentage");x=a/(b/100);unit="kVA";formula="Srated = Sload ÷ (Loading ÷ 100)";sub="Srated = $a ÷ ($b ÷ 100)";steps=listOf("Divide actual load by loading as a decimal.")}
            }
            "tflcurrent" -> when(u){
                "I"->{val a=v("S");val b=v("V");pos(b,"Line voltage");x=a*1000/(kotlin.math.sqrt(3.0)*b);unit="A";formula="I = (S × 1000) ÷ (√3 × V)";sub="I = ($a × 1000) ÷ (√3 × $b)";steps=listOf("Convert rated kVA to VA.","Divide by √3 times rated line voltage.")}
                "S"->{val a=v("I");val b=v("V");x=kotlin.math.sqrt(3.0)*a*b/1000;unit="kVA";formula="S = √3 × I × V ÷ 1000";sub="S = √3 × $a × $b ÷ 1000";steps=listOf("Multiply line current and voltage by √3, then convert VA to kVA.")}
                else->{val a=v("S");val b=v("I");pos(b,"Full-load current");x=a*1000/(kotlin.math.sqrt(3.0)*b);unit="V";formula="V = (S × 1000) ÷ (√3 × I)";sub="V = ($a × 1000) ÷ (√3 × $b)";steps=listOf("Rearrange the three-phase rated current equation.")}
            }
            "losses" -> when(u){
                "Loss"->{val a=v("Pin");val b=v("Pout");x=a-b;unit="W";formula="Loss = Pin − Pout";sub="Loss = $a − $b";steps=listOf("Subtract output power from input power.")}
                "Pin"->{val a=v("Loss");val b=v("Pout");x=a+b;unit="W";formula="Pin = Loss + Pout";sub="Pin = $a + $b";steps=listOf("Add total losses to output power.")}
                else->{val a=v("Pin");val b=v("Loss");x=a-b;unit="W";formula="Pout = Pin − Loss";sub="Pout = $a − $b";steps=listOf("Subtract total losses from input power.")}
            }
            "impedancepct" -> when(u){
                "%Z"->{val a=v("Vsc");val b=v("Vrated");pos(b,"Rated voltage");x=a/b*100;unit="%";formula="%Z = (Vsc ÷ Vrated) × 100";sub="%Z = ($a ÷ $b) × 100";steps=listOf("Divide short-circuit voltage by rated voltage.","Multiply by 100.")}
                "Vsc"->{val a=v("%Z");val b=v("Vrated");x=a/100*b;unit="V";formula="Vsc = (%Z ÷ 100) × Vrated";sub="Vsc = ($a ÷ 100) × $b";steps=listOf("Convert percentage impedance to a decimal and multiply by rated voltage.")}
                else->{val a=v("Vsc");val b=v("%Z");pos(b,"% impedance");x=a/(b/100);unit="V";formula="Vrated = Vsc ÷ (%Z ÷ 100)";sub="Vrated = $a ÷ ($b ÷ 100)";steps=listOf("Divide short-circuit voltage by percentage impedance as a decimal.")}
            }
            "regulation" -> when(u){
                "Reg"->{val a=v("Vnl");val b=v("Vfl");pos(b,"Full-load voltage");x=(a-b)/b*100;unit="%";formula="Reg = ((Vnl − Vfl) ÷ Vfl) × 100";sub="Reg = (($a − $b) ÷ $b) × 100";steps=listOf("Subtract full-load voltage from no-load voltage.","Divide by full-load voltage and multiply by 100.")}
                "Vnl"->{val a=v("Reg");val b=v("Vfl");x=(a/100+1)*b;unit="V";formula="Vnl = Vfl × (1 + Reg ÷ 100)";sub="Vnl = $b × (1 + $a ÷ 100)";steps=listOf("Convert regulation to a decimal, add one and multiply by full-load voltage.")}
                else->{val a=v("Vnl");val b=v("Reg");pos(1+b/100,"Regulation factor");x=a/(1+b/100);unit="V";formula="Vfl = Vnl ÷ (1 + Reg ÷ 100)";sub="Vfl = $a ÷ (1 + $b ÷ 100)";steps=listOf("Divide no-load voltage by one plus regulation as a decimal.")}
            }

            else -> error("Calculator not available.")
        }
        require(x.isFinite()) { "Result is not finite. Check the entered values." }
        return CalcAnswer(x,unit,formula,sub,steps)
    }
}
