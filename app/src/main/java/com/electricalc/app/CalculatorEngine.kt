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
        CalcSpec("acvalues","AC Circuits","AC Voltage and Current","Peak, RMS and peak-to-peak conversion",listOf(f("RMS","RMS value",""),f("Peak","Peak value",""),f("PP","Peak-to-peak value","")),"Peak = √2 × RMS; PP = 2 × Peak")
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
            else -> error("Calculator not available.")
        }
        require(x.isFinite()) { "Result is not finite. Check the entered values." }
        return CalcAnswer(x,unit,formula,sub,steps)
    }
}
