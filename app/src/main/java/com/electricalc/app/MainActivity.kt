package com.electricalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Graphite=Color(0xFF252B3A)
private val CardGraphite=Color(0xFF343C4D)
private val Amber=Color(0xFFFFB02E)
private val Muted=Color(0xFFB8C0CC)
private val AppScheme=darkColorScheme(primary=Amber,onPrimary=Graphite,secondary=Color(0xFFFFC766),background=Graphite,surface=CardGraphite,onBackground=Color.White,onSurface=Color.White)

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MaterialTheme(colorScheme=AppScheme){ElectriCalcApp()}}}
}
private data class Category(val name:String,val detail:String)
private val categories=listOf(
    Category("Basic Electrical","Ohm's law, power and resistance"),
    Category("AC Circuits","Reactance, impedance and power factor"),
    Category("Three-Phase Systems","Three-phase voltage, current and power"),
    Category("Transformers","Turns ratio, loading and efficiency"),
    Category("Power Distribution","Cables, voltage drop and loads"),
    Category("Electrical Machines","Motors, generators and slip"),
    Category("Energy & Consumption","Energy use and operating cost"),
    Category("Conversions","Electrical units and quantities")
)

@Composable
private fun ElectriCalcApp(){
    var query by remember{mutableStateOf("")}
    var tab by remember{mutableIntStateOf(0)}
    var category by remember{mutableStateOf<String?>(null)}
    var calc by remember{mutableStateOf<CalcSpec?>(null)}
    var answer by remember{mutableStateOf<CalcAnswer?>(null)}
    var error by remember{mutableStateOf<String?>(null)}
    val favorites=remember{mutableStateListOf<String>()}
    Scaffold(containerColor=Graphite,bottomBar={
        NavigationBar(containerColor=CardGraphite){
            listOf("Home" to Icons.Default.Home,"Favorites" to Icons.Default.Favorite,"History" to Icons.Default.History,"Library" to Icons.Default.MenuBook).forEachIndexed{index,item->
                NavigationBarItem(selected=tab==index,onClick={tab=index;category=null;calc=null},icon={Icon(item.second,null)},label={Text(item.first)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Amber,selectedTextColor=Amber,indicatorColor=Graphite,unselectedIconColor=Muted,unselectedTextColor=Muted))
            }
        }
    }){padding->
        when{
            calc!=null->CalculatorScreen(calc!!,answer,error,favorites.contains(calc!!.id),Modifier.padding(padding),
                onBack={calc=null;answer=null;error=null},
                onFavorite={if(favorites.contains(calc!!.id))favorites.remove(calc!!.id)else favorites.add(calc!!.id)},
                onCalculate={u,values->try{answer=CalculatorCatalog.solve(calc!!.id,u,values);error=null}catch(e:Exception){answer=null;error=e.message?: "Check your inputs."}},
                onReset={answer=null;error=null})
            category!=null->CategoryScreen(category!!,Modifier.padding(padding),{category=null},{calc=it})
            tab==1->SavedScreen("Favorites",CalculatorCatalog.all.filter{favorites.contains(it.id)},Modifier.padding(padding)){calc=it}
            tab==2->SavedScreen("History",emptyList(),Modifier.padding(padding)){calc=it}
            tab==3->LibraryScreen(Modifier.padding(padding))
            else->HomeScreen(query,{query=it},Modifier.padding(padding)){category=it}
        }
    }
}

@Composable
private fun HomeScreen(query:String,onQuery:(String)->Unit,modifier:Modifier,onCategory:(String)->Unit){
    val shown=categories.filter{it.name.contains(query,true)||it.detail.contains(query,true)}
    Column(modifier.fillMaxSize().padding(horizontal=18.dp,vertical=16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(46.dp).background(Amber,RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Icon(Icons.Default.Bolt,null,tint=Graphite,modifier=Modifier.size(30.dp))}
            Column(Modifier.padding(start=11.dp)){Text("ElectriCalc",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Electrical Calculator",color=Muted,fontSize=12.sp)}
            Spacer(Modifier.weight(1f));Icon(Icons.Default.Settings,null,tint=Amber,modifier=Modifier.size(26.dp))
        }
        Spacer(Modifier.height(24.dp));Text("What do you want to calculate?",fontSize=19.sp,fontWeight=FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(query,onQuery,Modifier.fillMaxWidth(),singleLine=true,placeholder={Text("Search calculators...",color=Muted)},leadingIcon={Icon(Icons.Default.Search,null,tint=Muted)},shape=RoundedCornerShape(14.dp))
        Spacer(Modifier.height(22.dp));Text("Calculation categories",fontSize=17.sp,fontWeight=FontWeight.SemiBold);Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f),contentPadding=PaddingValues(bottom=12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            items(shown){item->
                Card(Modifier.fillMaxWidth().height(140.dp).clickable{onCategory(item.name)},shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=CardGraphite)){
                    Column(Modifier.fillMaxSize().padding(14.dp),verticalArrangement=Arrangement.SpaceBetween){
                        Icon(Icons.Default.Bolt,null,tint=Amber,modifier=Modifier.size(27.dp))
                        Column{Text(item.name,fontWeight=FontWeight.SemiBold,fontSize=14.sp);Spacer(Modifier.height(4.dp));Text(item.detail,color=Muted,fontSize=11.sp,lineHeight=15.sp)}
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryScreen(category:String,modifier:Modifier,onBack:()->Unit,onSelect:(CalcSpec)->Unit){
    val list=CalculatorCatalog.all.filter{it.category==category}
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)};Text(category,fontSize=21.sp,fontWeight=FontWeight.Bold)}
        if(list.isEmpty())Text("Calculators for this category will be added in a later phase.",color=Muted,modifier=Modifier.padding(16.dp))
        else{
            Text("${list.size} calculators available",color=Muted,modifier=Modifier.padding(start=8.dp,bottom=12.dp))
            list.forEach{item->
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{onSelect(item)},colors=CardDefaults.cardColors(containerColor=CardGraphite)){
                    Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.Calculate,null,tint=Amber)
                        Column(Modifier.weight(1f).padding(start=12.dp)){Text(item.name,fontWeight=FontWeight.SemiBold);Text(item.description,color=Muted,fontSize=12.sp)}
                        Icon(Icons.Default.ChevronRight,null,tint=Muted)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorScreen(calc:CalcSpec,answer:CalcAnswer?,error:String?,favorite:Boolean,modifier:Modifier,onBack:()->Unit,onFavorite:()->Unit,onCalculate:(String,Map<String,Double>)->Unit,onReset:()->Unit){
    if(calc.id.startsWith("conv")) ConversionCalculatorScreen(calc,favorite,modifier,onBack,onFavorite)
    else StandardCalculatorScreen(calc,answer,error,favorite,modifier,onBack,onFavorite,onCalculate,onReset)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversionCalculatorScreen(calc:CalcSpec,favorite:Boolean,modifier:Modifier,onBack:()->Unit,onFavorite:()->Unit){
    val units=CalculatorCatalog.conversionUnits(calc.id)
    var fromUnit by remember(calc.id){mutableStateOf(units.first())}
    var toUnit by remember(calc.id){mutableStateOf(units.getOrElse(1){units.first()})}
    var input by remember(calc.id){mutableStateOf("")}
    var result by remember(calc.id){mutableStateOf<CalcAnswer?>(null)}
    var error by remember(calc.id){mutableStateOf<String?>(null)}
    var fromExpanded by remember{mutableStateOf(false)}
    var toExpanded by remember{mutableStateOf(false)}
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(start=18.dp,end=18.dp,top=18.dp,bottom=110.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)}
            Column(Modifier.weight(1f)){Text(calc.name,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(calc.category,color=Muted,fontSize=12.sp)}
            IconButton(onFavorite){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,null,tint=Amber)}
        }
        Text(calc.description,color=Muted,modifier=Modifier.padding(start=8.dp,bottom=22.dp))
        Text("Convert from",fontWeight=FontWeight.SemiBold)
        ExposedDropdownMenuBox(expanded=fromExpanded,onExpandedChange={fromExpanded=it}){
            OutlinedTextField(value=fromUnit,onValueChange={},readOnly=true,modifier=Modifier.menuAnchor().fillMaxWidth(),trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(fromExpanded)})
            ExposedDropdownMenu(expanded=fromExpanded,onDismissRequest={fromExpanded=false}){
                units.forEach{unit->DropdownMenuItem(text={Text(unit)},onClick={fromUnit=unit;result=null;error=null;fromExpanded=false})}
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value=input,onValueChange={input=it;result=null;error=null},modifier=Modifier.fillMaxWidth(),label={Text("Enter value")},placeholder={Text("e.g. 2.5")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
        Spacer(Modifier.height(18.dp))
        Text("Convert to",fontWeight=FontWeight.SemiBold)
        ExposedDropdownMenuBox(expanded=toExpanded,onExpandedChange={toExpanded=it}){
            OutlinedTextField(value=toUnit,onValueChange={},readOnly=true,modifier=Modifier.menuAnchor().fillMaxWidth(),trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(toExpanded)})
            ExposedDropdownMenu(expanded=toExpanded,onDismissRequest={toExpanded=false}){
                units.forEach{unit->DropdownMenuItem(text={Text(unit)},onClick={toUnit=unit;result=null;error=null;toExpanded=false})}
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
            Button(onClick={
                val number=input.trim().toDoubleOrNull()
                if(number==null){result=null;error="Enter a valid number."}
                else try{result=CalculatorCatalog.convert(calc.id,fromUnit,toUnit,number);error=null}
                catch(e:Exception){result=null;error=e.message?: "Check the entered value."}
            },modifier=Modifier.weight(1f)){Icon(Icons.Default.SwapHoriz,null);Spacer(Modifier.width(6.dp));Text("Convert")}
            OutlinedButton(onClick={input="";result=null;error=null}){Text("Reset")}
        }
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=CardGraphite)){
            Column(Modifier.padding(16.dp)){
                Text("Result",fontWeight=FontWeight.SemiBold,color=Amber)
                if(result!=null){
                    Text("${input.trim()} $fromUnit = ${String.format(Locale.US,"%.6f",result!!.value).trimEnd('0').trimEnd('.')} $toUnit",fontSize=21.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp))
                    Text("Formula: ${result!!.formula}",color=Muted,modifier=Modifier.padding(top=8.dp))
                } else Text("Your converted value will appear here.",color=Muted,modifier=Modifier.padding(top=8.dp))
                if(error!=null)Text(error!!,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=10.dp))
            }
        }
    }
}

@Composable
private fun StandardCalculatorScreen(calc:CalcSpec,answer:CalcAnswer?,error:String?,favorite:Boolean,modifier:Modifier,onBack:()->Unit,onFavorite:()->Unit,onCalculate:(String,Map<String,Double>)->Unit,onReset:()->Unit){
    var unknown by remember(calc.id){mutableStateOf(calc.fields.first().key)}
    val entries=remember(calc.id){mutableStateMapOf<String,String>()}
    var expanded by remember{mutableStateOf(false)}
    val known=calc.fields.filter{it.key!=unknown}
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(start=18.dp,end=18.dp,top=18.dp,bottom=110.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)}
            Column(Modifier.weight(1f)){Text(calc.name,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(calc.category,color=Muted,fontSize=12.sp)}
            IconButton(onFavorite){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,null,tint=Amber)}
        }
        Text(calc.description,color=Muted,modifier=Modifier.padding(start=8.dp,bottom=16.dp))
        Text("Select the value to calculate",fontWeight=FontWeight.SemiBold)
        ExposedDropdownMenuBox(expanded,{expanded=it}){
            OutlinedTextField(value=calc.fields.first{it.key==unknown}.let{"${it.label} (${it.key})"},onValueChange={},readOnly=true,modifier=Modifier.menuAnchor().fillMaxWidth(),trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded)})
            ExposedDropdownMenu(expanded,{expanded=false}){
                calc.fields.forEach{field->DropdownMenuItem(text={Text("${field.label} (${field.key})")},onClick={unknown=field.key;onReset();expanded=false})}
            }
        }
        Spacer(Modifier.height(16.dp));Text("Enter known values",fontWeight=FontWeight.SemiBold)
        known.forEach{field->
            OutlinedTextField(value=entries[field.key]?: "",onValueChange={entries[field.key]=it},modifier=Modifier.fillMaxWidth().padding(top=9.dp),label={Text(field.label)},suffix={Text(field.unit,color=Muted)},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
            Button(onClick={
                val parsed=known.mapNotNull{field->(entries[field.key]?: "").toDoubleOrNull()?.let{field.key to it}}.toMap()
                if(parsed.size!=known.size)onCalculate(unknown,emptyMap())else onCalculate(unknown,parsed)
            },modifier=Modifier.weight(1f)){Icon(Icons.Default.Calculate,null);Spacer(Modifier.width(6.dp));Text("Calculate")}
            OutlinedButton(onClick={entries.clear();onReset()}){Text("Reset")}
        }
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=CardGraphite)){
            Column(Modifier.padding(16.dp)){
                Text("Formula",fontWeight=FontWeight.SemiBold,color=Amber);Text(calc.formula,modifier=Modifier.padding(top=6.dp))
                answer?.let{r->
                    HorizontalDivider(Modifier.padding(vertical=12.dp),color=Muted.copy(alpha=.3f))
                    Text("Result",fontWeight=FontWeight.SemiBold,color=Amber)
                    Text("${String.format(Locale.US,"%.4f",r.value)} ${r.unit}".trim(),fontSize=25.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(vertical=6.dp))
                    Text("Formula used",fontWeight=FontWeight.SemiBold);Text(r.formula,modifier=Modifier.padding(top=4.dp))
                    Text("Substitution",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=12.dp));Text(r.substitution,modifier=Modifier.padding(top=4.dp))
                    Text("Working",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=12.dp));r.steps.forEachIndexed{i,s->Text("${i+1}. $s",modifier=Modifier.padding(top=4.dp))}
                }
                if(error!=null)Text(error,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=12.dp))
            }
        }
        if(calc.id=="acvalues")Text("For a sinusoidal waveform: RMS = Peak ÷ √2 and peak-to-peak = 2 × Peak.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
        if(calc.id=="pf")Text("Use compatible active and apparent power values. Leading or lagging sign is not represented.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
    }
}

@Composable
private fun SavedScreen(title:String,list:List<CalcSpec>,modifier:Modifier,onSelect:(CalcSpec)->Unit){
    Column(modifier.fillMaxSize().padding(18.dp)){
        Text(title,fontSize=22.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(bottom=16.dp))
        if(list.isEmpty())Text(if(title=="History")"Calculation history will be added in a later update." else "No favorite calculators saved yet.",color=Muted)
        list.forEach{item->ListItem(headlineContent={Text(item.name)},supportingContent={Text(item.category)},leadingContent={Icon(Icons.Default.Calculate,null,tint=Amber)},modifier=Modifier.clickable{onSelect(item)},colors=ListItemDefaults.colors(containerColor=Graphite))}
    }
}

@Composable
private fun LibraryScreen(modifier:Modifier){
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
        Text("Electrical Reference Library",fontSize=22.sp,fontWeight=FontWeight.Bold)
        Text("Phase 1 formulas",color=Amber,fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=14.dp,bottom=8.dp))
        CalculatorCatalog.all.forEach{item->
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp),colors=CardDefaults.cardColors(containerColor=CardGraphite)){
                Column(Modifier.padding(12.dp)){Text(item.name,fontWeight=FontWeight.SemiBold);Text(item.formula,color=Muted,modifier=Modifier.padding(top=4.dp))}
            }
        }
        Text("Enter values using the SI units shown. Verify engineering decisions against applicable standards, equipment data and site conditions.",color=Muted,fontSize=12.sp,modifier=Modifier.padding(top=14.dp))
    }
}
