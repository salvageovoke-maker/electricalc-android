package com.electricalc.app

data class OhmsLawResult(val voltageVolts:Double,val currentAmps:Double,val resistanceOhms:Double)
object OhmsLaw {
    fun solve(voltage:Double?=null,current:Double?=null,resistance:Double?=null):OhmsLawResult {
        require(listOf(voltage,current,resistance).count{it!=null}==2){"Enter exactly two known values."}
        require(listOfNotNull(voltage,current,resistance).all{it.isFinite()}){"Values must be finite numbers."}
        val v:Double;val i:Double;val r:Double
        when {
            voltage==null->{val a=requireNotNull(current);val b=requireNotNull(resistance);require(b!=0.0){"Resistance cannot be zero when solving voltage."};v=a*b;i=a;r=b}
            current==null->{val b=requireNotNull(resistance);require(b!=0.0){"Resistance cannot be zero when solving current."};v=voltage;i=voltage/b;r=b}
            else->{require(current!=0.0){"Current cannot be zero when solving resistance."};v=voltage;i=current;r=voltage/current}
        }
        return OhmsLawResult(v,i,r)
    }
}
