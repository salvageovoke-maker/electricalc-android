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
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

private val Graphite=Color(0xFF252B3A)
private val CardGraphite=Color(0xFF343C4D)
private val Amber=Color(0xFFFFB02E)
private val Muted=Color(0xFFB8C0CC)
private data class HistoryEntry(
    val id:String,val calculatorId:String,val name:String,val category:String,
    val details:String,val result:String,val timestamp:Long
)

private fun loadHistory(prefs:android.content.SharedPreferences):List<HistoryEntry>{
    return try{
        val array=JSONArray(prefs.getString("calculation_history","[]") ?: "[]")
        (0 until array.length()).mapNotNull{index->
            val item=array.optJSONObject(index) ?: return@mapNotNull null
            HistoryEntry(
                item.optString("id"),item.optString("calculatorId"),item.optString("name"),
                item.optString("category"),item.optString("details"),item.optString("result"),
                item.optLong("timestamp")
            )
        }.sortedByDescending{it.timestamp}
    }catch(_:Exception){ emptyList() }
}

private fun saveHistory(prefs:android.content.SharedPreferences,entries:List<HistoryEntry>){
    val array=JSONArray()
    entries.forEach{entry->
        array.put(JSONObject().put("id",entry.id).put("calculatorId",entry.calculatorId)
            .put("name",entry.name).put("category",entry.category).put("details",entry.details)
            .put("result",entry.result).put("timestamp",entry.timestamp))
    }
    prefs.edit().putString("calculation_history",array.toString()).apply()
}

internal fun decodeFavoriteIds(raw:String?):List<String>{
    return try{
        val array=JSONArray(raw ?: "[]")
        (0 until array.length()).mapNotNull{index->
            array.optString(index).takeIf{it.isNotBlank() && it!="null"}
        }.distinct()
    }catch(_:Exception){ emptyList() }
}

internal fun encodeFavoriteIds(ids:Collection<String>):String{
    val array=JSONArray()
    ids.filter{it.isNotBlank()}.distinct().forEach{array.put(it)}
    return array.toString()
}

private fun loadFavoriteIds(prefs:android.content.SharedPreferences):List<String> =
    decodeFavoriteIds(prefs.getString("favorite_calculators","[]"))

private fun saveFavoriteIds(prefs:android.content.SharedPreferences,ids:Collection<String>){
    prefs.edit().putString("favorite_calculators",encodeFavoriteIds(ids)).apply()
}

private val LocalPrecision = compositionLocalOf { 4 }
private val LocalHaptics = compositionLocalOf { true }

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{ElectriCalcRoot()}}
}

@Composable
private fun ElectriCalcRoot(){
    val context= LocalContext.current
    val appVersion = remember { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown" }
    val prefs= remember { context.getSharedPreferences("electricalc_preferences", android.content.Context.MODE_PRIVATE) }
    var themeMode by remember { mutableStateOf(prefs.getString("theme_mode","System default") ?: "System default") }
    var precision by remember { mutableIntStateOf(prefs.getInt("decimal_precision",4)) }
    var haptics by remember { mutableStateOf(prefs.getBoolean("haptic_feedback",true)) }
    val history= remember { mutableStateListOf<HistoryEntry>().apply { addAll(loadHistory(prefs)) } }
    val favorites= remember { mutableStateListOf<String>().apply { addAll(loadFavoriteIds(prefs)) } }
    val systemDark=isSystemInDarkTheme()
    val darkMode=themeMode=="Dark" || (themeMode=="System default" && systemDark)
    val scheme=if(darkMode) darkColorScheme(
        primary=Amber,onPrimary=Graphite,secondary=Color(0xFFFFC766),
        background=Graphite,surface=CardGraphite,onBackground=Color.White,onSurface=Color.White
    ) else lightColorScheme(
        primary=Color(0xFF9A5B00),onPrimary=Color.White,secondary=Color(0xFF765000),
        background=Color(0xFFF4F5F7),surface=Color.White,onBackground=Color(0xFF202633),onSurface=Color(0xFF202633)
    )
    CompositionLocalProvider(LocalPrecision provides precision,LocalHaptics provides haptics){
        MaterialTheme(colorScheme=scheme){
            ElectriCalcApp(
                themeMode=themeMode,precision=precision,haptics=haptics,history=history,favorites=favorites,
                onFavoritesChange={saveFavoriteIds(prefs,favorites)},
                onHistoryAdd={entry->
                    history.removeAll{it.id==entry.id}
                    history.add(0,entry)
                    while(history.size>200) history.removeAt(history.lastIndex)
                    saveHistory(prefs,history)
                },
                onHistoryDelete={id->history.removeAll{it.id==id};saveHistory(prefs,history)},
                onHistoryClear={history.clear();saveHistory(prefs,history)},
                onThemeChange={themeMode=it;prefs.edit().putString("theme_mode",it).apply()},
                onPrecisionChange={precision=it;prefs.edit().putInt("decimal_precision",it).apply()},
                onHapticsChange={haptics=it;prefs.edit().putBoolean("haptic_feedback",it).apply()},
                onResetPreferences={
                    themeMode="System default";precision=4;haptics=true
                    prefs.edit().putString("theme_mode",themeMode).putInt("decimal_precision",precision).putBoolean("haptic_feedback",haptics).apply()
                }
            )
        }
    }
}
private data class Category(val name:String,val detail:String)
private val categories=listOf(
    Category("Basic Electrical","Ohm's law, power and resistance"),
    Category("AC Circuits","Reactance, impedance and power factor"),
    Category("Three-Phase Systems","Three-phase voltage, current and power"),
    Category("Transformers","Turns ratio, loading and efficiency"),
    Category("Power Distribution","Cables, voltage drop and loads"),
    Category("Cable Sizing & Ampacity","Cable current capacity, derating and voltage drop"),
    Category("Electrical Machines","Motors, generators and slip"),
    Category("Energy & Consumption","Energy use and operating cost"),
    Category("Conversions","Electrical units and quantities")
)

private fun categoryIcon(name:String):ImageVector = when(name){
    "Basic Electrical" -> Icons.Default.Memory
    "AC Circuits" -> Icons.Default.GraphicEq
    "Three-Phase Systems" -> Icons.Default.DeviceHub
    "Transformers" -> Icons.Default.Transform
    "Power Distribution" -> Icons.Default.AccountTree
    "Cable Sizing & Ampacity" -> Icons.Default.Straighten
    "Electrical Machines" -> Icons.Default.PrecisionManufacturing
    "Energy & Consumption" -> Icons.Default.Speed
    "Conversions" -> Icons.Default.SwapHoriz
    else -> Icons.Default.Calculate
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElectriCalcApp(
    themeMode:String,precision:Int,haptics:Boolean,
    history:List<HistoryEntry> = emptyList(),
    favorites:MutableList<String> = mutableListOf(),
    onFavoritesChange:()->Unit = {},
    onHistoryAdd:(HistoryEntry)->Unit = {},
    onHistoryDelete:(String)->Unit = {},
    onHistoryClear:()->Unit = {},
    onThemeChange:(String)->Unit,onPrecisionChange:(Int)->Unit,onHapticsChange:(Boolean)->Unit,onResetPreferences:()->Unit
){
    var settingsOpen by remember{mutableStateOf(false)}
    var query by remember{mutableStateOf("")}
    var tab by remember{mutableIntStateOf(0)}
    var category by remember{mutableStateOf<String?>(null)}
    var calc by remember{mutableStateOf<CalcSpec?>(null)}
    var answer by remember{mutableStateOf<CalcAnswer?>(null)}
    var error by remember{mutableStateOf<String?>(null)}
    var showAbout by remember{mutableStateOf(false)}
    val drawerState= rememberDrawerState(DrawerValue.Closed)
    val scope= rememberCoroutineScope()
    val haptic=LocalHapticFeedback.current
    val hapticsEnabled=LocalHaptics.current

    ModalNavigationDrawer(
        drawerState=drawerState,
        gesturesEnabled=calc==null,
        drawerContent={
            ModalDrawerSheet(drawerContainerColor=MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
                Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=24.dp),verticalAlignment=Alignment.CenterVertically){
                    Image(painterResource(R.drawable.electricalc_brand),contentDescription="ElectriCalc logo",modifier=Modifier.size(54.dp))
                    Column(Modifier.padding(start=14.dp)){
                        Text("ElectriCalc",fontSize=21.sp,fontWeight=FontWeight.Bold)
                        Text("Electrical Calculator",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp)
                    }
                }
                HorizontalDivider(color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f).copy(alpha=.2f))
                Text("MAIN MENU",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=11.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(start=20.dp,top=18.dp,bottom=8.dp))
                val drawerItems=listOf(
                    Triple("Home",Icons.Default.Home,0),
                    Triple("Favorites",Icons.Default.Favorite,1),
                    Triple("History",Icons.Default.History,2),
                    Triple("Library",Icons.Default.MenuBook,3)
                )
                drawerItems.forEach{item->
                    NavigationDrawerItem(
                        icon={Icon(item.second,null)},
                        label={Text(item.first)},
                        selected=tab==item.third && category==null && calc==null,
                        onClick={
                            if(hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            tab=item.third;category=null;calc=null;answer=null;error=null
                            scope.launch{drawerState.close()}
                        },
                        modifier=Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors=NavigationDrawerItemDefaults.colors(selectedContainerColor=MaterialTheme.colorScheme.background,selectedIconColor=Amber,selectedTextColor=Amber,unselectedIconColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),unselectedTextColor=MaterialTheme.colorScheme.onSurface)
                    )
                }
                NavigationDrawerItem(
                    icon={Icon(Icons.Default.Settings,null)},
                    label={Text("Settings")},
                    selected=settingsOpen,
                    onClick={settingsOpen=true;scope.launch{drawerState.close()}},
                    modifier=Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors=NavigationDrawerItemDefaults.colors(selectedContainerColor=MaterialTheme.colorScheme.background,selectedIconColor=Amber,selectedTextColor=Amber,unselectedIconColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),unselectedTextColor=MaterialTheme.colorScheme.onSurface)
                )
                HorizontalDivider(Modifier.padding(vertical=12.dp),color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f).copy(alpha=.2f))
                Text("CALCULATION CATEGORIES",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=11.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(start=20.dp,bottom=8.dp))
                categories.forEach{item->
                    NavigationDrawerItem(
                        icon={Icon(categoryIcon(item.name),null)},
                        label={Text(item.name)},
                        selected=category==item.name,
                        onClick={
                            if(hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            tab=0;category=item.name;calc=null;answer=null;error=null
                            scope.launch{drawerState.close()}
                        },
                        modifier=Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors=NavigationDrawerItemDefaults.colors(selectedContainerColor=MaterialTheme.colorScheme.background,selectedIconColor=Amber,selectedTextColor=Amber,unselectedIconColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),unselectedTextColor=MaterialTheme.colorScheme.onSurface)
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f).copy(alpha=.2f))
                NavigationDrawerItem(
                    icon={Icon(Icons.Default.Info,null)},
                    label={Text("About ElectriCalc")},
                    selected=false,
                    onClick={showAbout=true;scope.launch{drawerState.close()}},
                    modifier=Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors=NavigationDrawerItemDefaults.colors(unselectedIconColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),unselectedTextColor=MaterialTheme.colorScheme.onSurface)
                )
                Text("Version $appVersion",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=11.sp,modifier=Modifier.padding(start=22.dp,bottom=18.dp,top=4.dp))
                }
            }
        }
    ){
        Scaffold(containerColor=MaterialTheme.colorScheme.background,bottomBar={
            NavigationBar(containerColor=MaterialTheme.colorScheme.surface){
                listOf("Home" to Icons.Default.Home,"Favorites" to Icons.Default.Favorite,"History" to Icons.Default.History,"Library" to Icons.Default.MenuBook).forEachIndexed{index,item->
                    NavigationBarItem(selected=tab==index,onClick={if(hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);settingsOpen=false;tab=index;category=null;calc=null;answer=null;error=null},icon={Icon(item.second,null)},label={Text(item.first)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Amber,selectedTextColor=Amber,indicatorColor=MaterialTheme.colorScheme.background,unselectedIconColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),unselectedTextColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f)))
                }
            }
        }){padding->
            when{
                settingsOpen->SettingsScreen(
                    themeMode=themeMode,precision=precision,haptics=haptics,versionName=appVersion,
                    onThemeChange=onThemeChange,onPrecisionChange=onPrecisionChange,
                    onHapticsChange=onHapticsChange,onReset=onResetPreferences,
                    onBack={settingsOpen=false},modifier=Modifier.padding(padding)
                )
                calc!=null->CalculatorScreen(calc!!,answer,error,favorites.contains(calc!!.id),Modifier.padding(padding),
                    onHistoryAdd=onHistoryAdd,
                    onBack={calc=null;answer=null;error=null},
                    onFavorite={
                        if(favorites.contains(calc!!.id))favorites.remove(calc!!.id)else favorites.add(calc!!.id)
                        onFavoritesChange()
                    },
                    onCalculate={u,values->
                        try{
                            val solved=CalculatorCatalog.solve(calc!!.id,u,values)
                            answer=solved;error=null
                            val details=values.entries.joinToString(" • "){"${it.key} = ${it.value}"}+" • Calculate ${u}"
                            val stamp=System.currentTimeMillis()
                            onHistoryAdd(HistoryEntry(
                                id="${calc!!.id}-$stamp",calculatorId=calc!!.id,name=calc!!.name,
                                category=calc!!.category,details=details,
                                result="${String.format(Locale.US,"% .${precision}f",solved.value).trim()} ${solved.unit}".trim(),
                                timestamp=stamp
                            ))
                        }catch(e:Exception){answer=null;error=e.message?: "Check your inputs."}
                    },
                    onReset={answer=null;error=null})
                category!=null->CategoryScreen(category!!,Modifier.padding(padding),{category=null},{calc=it})
                tab==1->SavedScreen("Favorites",CalculatorCatalog.all.filter{favorites.contains(it.id)},Modifier.padding(padding)){calc=it}
                tab==2->HistoryScreen(
                    history=history,modifier=Modifier.padding(padding),
                    onOpen={entry->CalculatorCatalog.all.firstOrNull{it.id==entry.calculatorId}?.let{calc=it}},
                    onDelete=onHistoryDelete,onClear=onHistoryClear
                )
                tab==3->LibraryScreen(Modifier.padding(padding))
                else->HomeScreen(query,{query=it},Modifier.padding(padding),onCategory={category=it},onMenu={scope.launch{drawerState.open()}},onSettings={settingsOpen=true})
            }
        }
    }
    if(showAbout){
        AlertDialog(
            onDismissRequest={showAbout=false},
            confirmButton={TextButton(onClick={showAbout=false}){Text("Close",color=Amber)}},
            title={Text("About ElectriCalc")},
            text={Text("ElectriCalc is an offline-first electrical engineering calculator. Calculations run on your device and do not require an account or an internet connection.")},
            containerColor=MaterialTheme.colorScheme.surface,
            titleContentColor=MaterialTheme.colorScheme.onSurface,
            textContentColor=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f)
        )
    }
}

@Composable
private fun HomeScreen(query:String,onQuery:(String)->Unit,modifier:Modifier,onCategory:(String)->Unit,onMenu:()->Unit,onSettings:()->Unit){
    val shown=categories.filter{it.name.contains(query,true)||it.detail.contains(query,true)}
    val haptic=LocalHapticFeedback.current
    val hapticsEnabled=LocalHaptics.current
    Column(modifier.fillMaxSize().padding(horizontal=18.dp,vertical=16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onClick={if(hapticsEnabled)haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);onMenu()},modifier=Modifier.size(42.dp)){Icon(Icons.Default.Menu,null,tint=Amber,modifier=Modifier.size(28.dp))}
            Image(painterResource(R.drawable.electricalc_brand),contentDescription="ElectriCalc logo",modifier=Modifier.size(48.dp))
            Column(Modifier.padding(start=10.dp)){Text("ElectriCalc",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Electrical Calculator",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp)}
            Spacer(Modifier.weight(1f))
            IconButton(onClick={if(hapticsEnabled)haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);onSettings()}){Icon(Icons.Default.Settings,null,tint=Amber,modifier=Modifier.size(25.dp))}
        }
        Spacer(Modifier.height(24.dp));Text("What do you want to calculate?",fontSize=19.sp,fontWeight=FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(query,onQuery,Modifier.fillMaxWidth(),singleLine=true,placeholder={Text("Search calculators...",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f))},leadingIcon={Icon(Icons.Default.Search,null,tint=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f))},shape=RoundedCornerShape(14.dp))
        Spacer(Modifier.height(22.dp));Text("Calculation categories",fontSize=17.sp,fontWeight=FontWeight.SemiBold);Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.weight(1f),contentPadding=PaddingValues(bottom=12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            items(shown){item->
                Card(Modifier.fillMaxWidth().height(140.dp).clickable{if(hapticsEnabled)haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove);onCategory(item.name)},shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){
                    Column(Modifier.fillMaxSize().padding(14.dp),verticalArrangement=Arrangement.SpaceBetween){
                        Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.background,RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){
                            Icon(categoryIcon(item.name),null,tint=Amber,modifier=Modifier.size(25.dp))
                        }
                        Column{Text(item.name,fontWeight=FontWeight.SemiBold,fontSize=14.sp);Spacer(Modifier.height(4.dp));Text(item.detail,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=11.sp,lineHeight=15.sp)}
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingsScreen(
    themeMode:String,precision:Int,haptics:Boolean,versionName:String,
    onThemeChange:(String)->Unit,onPrecisionChange:(Int)->Unit,onHapticsChange:(Boolean)->Unit,
    onReset:()->Unit,onBack:()->Unit,modifier:Modifier
){
    var confirmReset by remember{mutableStateOf(false)}
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)}
            Text("Settings",fontSize=22.sp,fontWeight=FontWeight.Bold)
        }
        Text("Appearance",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(start=8.dp,top=18.dp,bottom=8.dp))
        Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp)){
                Text("Theme",fontWeight=FontWeight.SemiBold)
                Text("Choose how ElectriCalc looks.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=12.sp,modifier=Modifier.padding(top=4.dp,bottom=12.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){
                    listOf("System default","Light","Dark").forEach{mode->
                        FilterChip(selected=themeMode==mode,onClick={onThemeChange(mode)},label={Text(mode)},modifier=Modifier.weight(1f))
                    }
                }
            }
        }
        Text("Calculation preferences",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(start=8.dp,top=20.dp,bottom=8.dp))
        Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp)){
                Text("Decimal precision",fontWeight=FontWeight.SemiBold)
                Text("Choose the number of decimal places shown in calculator results.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=12.sp,modifier=Modifier.padding(top=4.dp,bottom=12.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()){
                    listOf(2,4,6).forEach{digits->
                        FilterChip(selected=precision==digits,onClick={onPrecisionChange(digits)},label={Text("$digits places")},modifier=Modifier.weight(1f))
                    }
                }
            }
        }
        Text("Interaction",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(start=8.dp,top=20.dp,bottom=8.dp))
        Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),modifier=Modifier.fillMaxWidth()){
            Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){
                    Text("Haptic feedback",fontWeight=FontWeight.SemiBold)
                    Text("Vibrate briefly when you tap app controls.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=12.sp)
                }
                Switch(checked=haptics,onCheckedChange=onHapticsChange)
            }
        }
        Text("About",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(start=8.dp,top=20.dp,bottom=8.dp))
        Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp)){
                Text("ElectriCalc",fontWeight=FontWeight.Bold,fontSize=17.sp)
                Text("Version $versionName",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),modifier=Modifier.padding(top=3.dp))
                Text("Offline-first electrical engineering calculator. Your calculations run on your device.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=12.sp,modifier=Modifier.padding(top=8.dp))
            }
        }
        OutlinedButton(onClick={confirmReset=true},modifier=Modifier.fillMaxWidth().padding(top=20.dp)){
            Icon(Icons.Default.RestartAlt,null);Spacer(Modifier.width(8.dp));Text("Reset preferences")
        }
        Spacer(Modifier.height(24.dp))
    }
    if(confirmReset){
        AlertDialog(
            onDismissRequest={confirmReset=false},
            title={Text("Reset preferences?")},
            text={Text("This restores System default theme, 4 decimal places and enabled haptic feedback.")},
            confirmButton={TextButton(onClick={onReset();confirmReset=false}){Text("Reset",color=Amber)}},
            dismissButton={TextButton(onClick={confirmReset=false}){Text("Cancel")}},
            containerColor=MaterialTheme.colorScheme.surface
        )
    }
}


@Composable
private fun CableSizingScreen(modifier:Modifier,onBack:()->Unit){
    var phases by remember { mutableIntStateOf(3) }
    var power by remember { mutableStateOf("30") }
    var voltage by remember { mutableStateOf("400") }
    var pf by remember { mutableStateOf("0.85") }
    var efficiency by remember { mutableStateOf("90") }
    var ampacity by remember { mutableStateOf("70") }
    var ambient by remember { mutableStateOf("0.94") }
    var grouping by remember { mutableStateOf("0.80") }
    var installation by remember { mutableStateOf("1.00") }
    var other by remember { mutableStateOf("1.00") }
    var length by remember { mutableStateOf("50") }
    var mv by remember { mutableStateOf("1.0") }
    var resistance by remember { mutableStateOf("0.387") }
    var maxDrop by remember { mutableStateOf("5") }
    var result by remember { mutableStateOf<CableSizingResult?>(null) }
    var automaticResult by remember { mutableStateOf<AutomaticCableResult?>(null) }
    var autoMode by remember { mutableStateOf(true) }
    var conductor by remember { mutableStateOf(CableConductor.COPPER) }
    var insulation by remember { mutableStateOf(CableInsulation.PVC) }
    var method by remember { mutableStateOf(CableInstallationMethod.B2) }
    var error by remember { mutableStateOf<String?>(null) }

    @Composable
    fun field(label:String,value:String,onValue:(String)->Unit,unit:String="",hint:String=""){
        OutlinedTextField(
            value=value,onValueChange={onValue(it);result=null;automaticResult=null;error=null},
            modifier=Modifier.fillMaxWidth().padding(top=8.dp),
            label={Text(label)},suffix={if(unit.isNotEmpty())Text(unit)},
            supportingText={if(hint.isNotEmpty())Text(hint)},
            singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal)
        )
    }
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=14.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onClick = onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)}
            Column(Modifier.weight(1f)){
                Text("Cable Sizing & Ampacity",fontSize=20.sp,fontWeight=FontWeight.Bold)
                Text("Preliminary cable design check",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f))
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth().padding(top=12.dp)) {
            FilterChip(selected=autoMode,onClick={autoMode=true;result=null;automaticResult=null;error=null},label={Text("Automatic size")})
            FilterChip(selected=!autoMode,onClick={autoMode=false;result=null;automaticResult=null;error=null},label={Text("Manual ampacity")})
        }
        Text(if(autoMode)"Automatic cable selection" else "Manual cable check",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(top=8.dp))
        if(autoMode){
            Text("The app selects the smallest listed standard size that passes both checks.",fontSize=12.sp,color=Muted)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth().padding(top=8.dp)) {
                listOf(CableConductor.COPPER,CableConductor.ALUMINIUM).forEach { item ->
                    FilterChip(selected=conductor==item,onClick={conductor=item;automaticResult=null},label={Text(if(item==CableConductor.COPPER)"Copper" else "Aluminium")})
                }
            }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
                listOf(CableInsulation.PVC,CableInsulation.XLPE).forEach { item ->
                    FilterChip(selected=insulation==item,onClick={insulation=item;automaticResult=null},label={Text(item.name)})
                }
            }
            var methodExpanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxWidth().padding(top=8.dp)) {
                OutlinedButton(onClick={methodExpanded=true},modifier=Modifier.fillMaxWidth()) { Text("Installation: ${method.name} · ${method.label.substringAfter("· ")}") }
                DropdownMenu(expanded=methodExpanded,onDismissRequest={methodExpanded=false}) {
                    CableInstallationMethod.values().forEach { item ->
                        DropdownMenuItem(text={Text(item.label)},onClick={method=item;methodExpanded=false;automaticResult=null})
                    }
                }
            }
        }
        Text("Load details",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(top=12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth().padding(top=8.dp)){
            listOf(1 to "Single-phase",3 to "Three-phase").forEach{(n,label)->
                FilterChip(selected=phases==n,onClick={phases=n;result=null;automaticResult=null},label={Text(label)})
            }
        }
        field("Load power",power,{power=it},"kW")
        field("Supply voltage",voltage,{voltage=it},"V",if(phases==1)"Single-phase line voltage" else "Three-phase line-to-line voltage")
        field("Power factor",pf,{pf=it},"")
        field("Load efficiency",efficiency,{efficiency=it},"%","Use 100% for a load where efficiency is not applicable")
        if(!autoMode){
            Text("Cable current capacity",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(top=18.dp))
            field("Tabulated cable ampacity (Iz)",ampacity,{ampacity=it},"A","Enter the reference current rating from the applicable cable table or manufacturer data")
        }
        Text("Correction factors",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=12.dp))
        field("Ambient temperature factor",ambient,{ambient=it},"k₁")
        field("Grouping factor",grouping,{grouping=it},"k₂")
        if(!autoMode) field("Installation / soil factor",installation,{installation=it},"k₃","Use the factor applicable to your installation method")
        field("Other correction factor",other,{other=it},"k₄","Use 1.00 when no additional correction applies")
        Text("Voltage drop and losses",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(top=18.dp))
        field("One-way cable length",length,{length=it},"m")
        if(!autoMode){
            field("Voltage-drop coefficient",mv,{mv=it},"mV/A/m","Use the coefficient for the selected cable, phase arrangement and power factor")
            field("Conductor resistance",resistance,{resistance=it},"Ω/km","Use operating-temperature resistance where available")
        }
        field("Maximum permitted voltage drop",maxDrop,{maxDrop=it},"%")
        Button(onClick={
            try{
                if(autoMode){
                    automaticResult=AutomaticCableSizer.calculate(AutomaticCableInput(
                        power.toDouble(),voltage.toDouble(),pf.toDouble(),efficiency.toDouble(),phases,
                        conductor,insulation,method,ambient.toDouble(),grouping.toDouble(),other.toDouble(),
                        length.toDouble(),maxDrop.toDouble()
                    ))
                    result=null
                } else {
                    result=CableSizingEngine.calculate(CableSizingInput(
                        power.toDouble(),voltage.toDouble(),pf.toDouble(),efficiency.toDouble(),phases,
                        ampacity.toDouble(),ambient.toDouble(),grouping.toDouble(),installation.toDouble(),
                        other.toDouble(),length.toDouble(),mv.toDouble(),resistance.toDouble(),maxDrop.toDouble()
                    ))
                    automaticResult=null
                }
                error=null
            }catch(e:Exception){result=null;error=e.message ?: "Check the entered values."}
        },modifier=Modifier.fillMaxWidth().padding(top=18.dp)){
            Icon(Icons.Default.Calculate,null);Spacer(Modifier.width(8.dp));Text(if(autoMode)"Recommend cable size" else "Calculate cable check")
        }
        error?.let{Text(it,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=10.dp))}
        automaticResult?.let { r ->
            Card(Modifier.fillMaxWidth().padding(top=14.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Cable size recommendation",fontWeight=FontWeight.Bold,fontSize=18.sp)
                    Text("Design current: ${"%.2f".format(Locale.US,r.designCurrentA)} A",modifier=Modifier.padding(top=10.dp))
                    Text("Required tabulated ampacity: ${"%.2f".format(Locale.US,r.requiredTabulatedAmpacityA)} A")
                    if(r.recommendedSizeMm2!=null){
                        Text("${"%.1f".format(Locale.US,r.recommendedSizeMm2)} mm²",fontSize=28.sp,fontWeight=FontWeight.Bold,color=Amber,modifier=Modifier.padding(top=8.dp))
                        Text("${conductor.name.lowercase().replaceFirstChar{it.uppercase()}} · ${insulation.name} · ${method.name} · ${if(phases==1)"2" else "3"} loaded conductors",fontSize=12.sp,color=Muted)
                        Text("Tabulated ampacity: ${"%.1f".format(Locale.US,r.tabulatedAmpacityA)} A")
                        Text("Corrected ampacity: ${"%.1f".format(Locale.US,r.correctedAmpacityA)} A")
                        HorizontalDivider(Modifier.padding(vertical=10.dp))
                        Text("Voltage drop: ${"%.2f".format(Locale.US,r.voltageDropV)} V (${"%.2f".format(Locale.US,r.voltageDropPercent)}%)")
                        Text("Estimated conductor losses: ${"%.1f".format(Locale.US,r.conductorLossW)} W")
                        r.nextSizeMm2?.let { Text("Next standard size: ${"%.1f".format(Locale.US,it)} mm²",fontSize=12.sp,color=Muted,modifier=Modifier.padding(top=6.dp)) }
                    } else {
                        Text("No suitable size found",fontWeight=FontWeight.Bold,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=8.dp))
                    }
                    Text(r.message,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.72f),modifier=Modifier.padding(top=10.dp))
                }
            }
        }
        result?.let{r->
            Card(Modifier.fillMaxWidth().padding(top=14.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){
                Column(Modifier.padding(16.dp)){
                    Text("Calculation results",fontWeight=FontWeight.Bold,fontSize=18.sp)
                    Text("Design current: ${"%.2f".format(Locale.US,r.designCurrentA)} A",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=10.dp))
                    Text("Combined correction factor: ${"%.4f".format(Locale.US,r.totalCorrectionFactor)}")
                    Text("Corrected cable ampacity: ${"%.2f".format(Locale.US,r.correctedAmpacityA)} A")
                    Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(top=6.dp)){
                        Icon(if(r.ampacityPass)Icons.Default.CheckCircle else Icons.Default.Warning,null,tint=if(r.ampacityPass)Color(0xFF43A047) else Color(0xFFE57373))
                        Text(if(r.ampacityPass)" Ampacity check passed" else " Ampacity check failed",fontWeight=FontWeight.SemiBold)
                    }
                    HorizontalDivider(Modifier.padding(vertical=10.dp))
                    Text("Voltage drop: ${"%.2f".format(Locale.US,r.voltageDropV)} V (${ "%.2f".format(Locale.US,r.voltageDropPercent)}%)")
                    Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(top=6.dp)){
                        Icon(if(r.voltageDropPass)Icons.Default.CheckCircle else Icons.Default.Warning,null,tint=if(r.voltageDropPass)Color(0xFF43A047) else Color(0xFFE57373))
                        Text(if(r.voltageDropPass)" Voltage-drop check passed" else " Voltage-drop check failed",fontWeight=FontWeight.SemiBold)
                    }
                    Text("Estimated conductor losses: ${"%.2f".format(Locale.US,r.conductorLossW)} W",modifier=Modifier.padding(top=8.dp))
                }
            }
        }
        Text("Engineering note",fontWeight=FontWeight.SemiBold,color=Amber,modifier=Modifier.padding(top=18.dp))
        Text(
            if(autoMode) "Automatic selection uses IEC 60364-5-52:2009+AMD1:2024 Annex B reference ampacity tables for methods A1 to D2, at 30 °C ambient air or 20 °C ground reference conditions. Ambient, grouping and other correction factors are applied to the tabulated rating. Voltage drop and losses use a resistive conductor model at the insulation operating temperature; cable reactance and manufacturer-specific AC resistance are not included. This is a preliminary selection only. Confirm actual cable construction, protective-device coordination, short-circuit withstand, disconnection times, terminal limits, soil conditions and local requirements before installation."
            else "Manual mode checks values that you obtain from the applicable cable tables or manufacturer data. The tabulated ampacity and correction factors must match conductor material, insulation, installation reference method, number of loaded conductors, ambient or soil conditions and grouping. Voltage-drop coefficient must match the cable and circuit arrangement. Confirm short-circuit withstand, protective-device coordination, disconnection times, terminal temperature limits and local requirements before installation.",
            color=MaterialTheme.colorScheme.onSurface.copy(alpha=.72f),fontSize=12.sp,lineHeight=17.sp,
            modifier=Modifier.padding(top=6.dp,bottom=28.dp)
        )
    }
}

@Composable
private fun CategoryScreen(category:String,modifier:Modifier,onBack:()->Unit,onSelect:(CalcSpec)->Unit){
    if(category=="Cable Sizing & Ampacity"){
        CableSizingScreen(modifier,onBack)
        return
    }
    val list=CalculatorCatalog.all.filter{it.category==category}
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)};Text(category,fontSize=21.sp,fontWeight=FontWeight.Bold)}
        if(list.isEmpty())Text("Calculators for this category will be added in a later phase.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(16.dp))
        else{
            Text("${list.size} calculators available",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(start=8.dp,bottom=12.dp))
            list.forEach{item->
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{onSelect(item)},colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){
                    Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.Calculate,null,tint=Amber)
                        Column(Modifier.weight(1f).padding(start=12.dp)){Text(item.name,fontWeight=FontWeight.SemiBold);Text(item.description,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp)}
                        Icon(Icons.Default.ChevronRight,null,tint=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorScreen(calc:CalcSpec,answer:CalcAnswer?,error:String?,favorite:Boolean,modifier:Modifier,onHistoryAdd:(HistoryEntry)->Unit,onBack:()->Unit,onFavorite:()->Unit,onCalculate:(String,Map<String,Double>)->Unit,onReset:()->Unit){
    if(calc.id.startsWith("conv")) ConversionCalculatorScreen(calc,favorite,modifier,onBack,onFavorite,onHistoryAdd)
    else StandardCalculatorScreen(calc,answer,error,favorite,modifier,onBack,onFavorite,onCalculate,onReset,onHistoryAdd,LocalPrecision.current)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversionCalculatorScreen(calc:CalcSpec,favorite:Boolean,modifier:Modifier,onBack:()->Unit,onFavorite:()->Unit,onHistoryAdd:(HistoryEntry)->Unit){
    val units=CalculatorCatalog.conversionUnits(calc.id)
    val resultPrecision=LocalPrecision.current
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
            Column(Modifier.weight(1f)){Text(calc.name,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(calc.category,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp)}
            IconButton(onFavorite){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,null,tint=Amber)}
        }
        Text(calc.description,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(start=8.dp,bottom=22.dp))
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
                else try{
                    val converted=CalculatorCatalog.convert(calc.id,fromUnit,toUnit,number)
                    result=converted;error=null
                    val stamp=System.currentTimeMillis()
                    onHistoryAdd(HistoryEntry(
                        id="${calc.id}-$stamp",calculatorId=calc.id,name=calc.name,category=calc.category,
                        details="$input $fromUnit to $toUnit",
                        result="${String.format(Locale.US,"% .${resultPrecision}f",converted.value).trim()} $toUnit",
                        timestamp=stamp
                    ))
                }catch(e:Exception){result=null;error=e.message?: "Check the entered value."}
            },modifier=Modifier.weight(1f)){Icon(Icons.Default.SwapHoriz,null);Spacer(Modifier.width(6.dp));Text("Convert")}
            OutlinedButton(onClick={input="";result=null;error=null}){Text("Reset")}
        }
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){
            Column(Modifier.padding(16.dp)){
                Text("Result",fontWeight=FontWeight.SemiBold,color=Amber)
                if(result!=null){
                    Text("${input.trim()} $fromUnit = ${String.format(Locale.US,"% .${LocalPrecision.current}f",result!!.value).trim().trimEnd('0').trimEnd('.')} $toUnit",fontSize=21.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp))
                    Text("Formula: ${result!!.formula}",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(top=8.dp))
                } else Text("Your converted value will appear here.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(top=8.dp))
                if(error!=null)Text(error!!,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandardCalculatorScreen(calc:CalcSpec,answer:CalcAnswer?,error:String?,favorite:Boolean,modifier:Modifier,onBack:()->Unit,onFavorite:()->Unit,onCalculate:(String,Map<String,Double>)->Unit,onReset:()->Unit,onHistoryAdd:(HistoryEntry)->Unit,precision:Int){
    var unknown by remember(calc.id){mutableStateOf(calc.fields.first().key)}
    val entries=remember(calc.id){mutableStateMapOf<String,String>()}
    var expanded by remember{mutableStateOf(false)}
    val known=calc.fields.filter{it.key!=unknown}
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(start=18.dp,end=18.dp,top=18.dp,bottom=110.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            IconButton(onBack){Icon(Icons.Default.ArrowBack,null,tint=Amber)}
            Column(Modifier.weight(1f)){Text(calc.name,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(calc.category,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp)}
            IconButton(onFavorite){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,null,tint=Amber)}
        }
        Text(calc.description,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),modifier=Modifier.padding(start=8.dp,bottom=16.dp))
        Text("Select the value to calculate",fontWeight=FontWeight.SemiBold)
        ExposedDropdownMenuBox(expanded,{expanded=it}){
            OutlinedTextField(value=calc.fields.first{it.key==unknown}.let{"${it.label} (${it.key})"},onValueChange={},readOnly=true,modifier=Modifier.menuAnchor().fillMaxWidth(),trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded)})
            ExposedDropdownMenu(expanded,{expanded=false}){
                calc.fields.forEach{field->DropdownMenuItem(text={Text("${field.label} (${field.key})")},onClick={unknown=field.key;onReset();expanded=false})}
            }
        }
        Spacer(Modifier.height(16.dp));Text("Enter known values",fontWeight=FontWeight.SemiBold)
        known.forEach{field->
            OutlinedTextField(value=entries[field.key]?: "",onValueChange={entries[field.key]=it},modifier=Modifier.fillMaxWidth().padding(top=9.dp),label={Text(field.label)},suffix={Text(field.unit,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f))},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
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
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){
            Column(Modifier.padding(16.dp)){
                Text("Formula",fontWeight=FontWeight.SemiBold,color=Amber);Text(calc.formula,modifier=Modifier.padding(top=6.dp))
                answer?.let{r->
                    HorizontalDivider(Modifier.padding(vertical=12.dp),color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f).copy(alpha=.3f))
                    Text("Result",fontWeight=FontWeight.SemiBold,color=Amber)
                    Text("${String.format(Locale.US,"% .${LocalPrecision.current}f",r.value).trim()} ${r.unit}".trim(),fontSize=25.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(vertical=6.dp))
                    Text("Formula used",fontWeight=FontWeight.SemiBold);Text(r.formula,modifier=Modifier.padding(top=4.dp))
                    Text("Substitution",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=12.dp));Text(r.substitution,modifier=Modifier.padding(top=4.dp))
                    Text("Working",fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=12.dp));r.steps.forEachIndexed{i,s->Text("${i+1}. $s",modifier=Modifier.padding(top=4.dp))}
                }
                if(error!=null)Text(error,color=Color(0xFFFF8A80),modifier=Modifier.padding(top=12.dp))
            }
        }
        if(calc.id=="acvalues")Text("For a sinusoidal waveform: RMS = Peak ÷ √2 and peak-to-peak = 2 × Peak.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
        if(calc.id=="pf")Text("Use compatible active and apparent power values. Leading or lagging sign is not represented.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp,modifier=Modifier.padding(top=12.dp))
    }
}

@Composable
private fun HistoryScreen(
    history:List<HistoryEntry>,modifier:Modifier,
    onOpen:(HistoryEntry)->Unit,onDelete:(String)->Unit,onClear:()->Unit
){
    var confirmClear by remember{mutableStateOf(false)}
    Column(modifier.fillMaxSize().padding(horizontal=18.dp,vertical=16.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Column(Modifier.weight(1f)){
                Text("History",fontSize=22.sp,fontWeight=FontWeight.Bold)
                Text("${history.size} saved calculation${if(history.size==1)"" else "s"}",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=12.sp)
            }
            if(history.isNotEmpty()) TextButton(onClick={confirmClear=true}){Text("Clear all",color=Amber)}
        }
        if(history.isEmpty()){
            Column(Modifier.fillMaxWidth().weight(1f),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
                Icon(Icons.Default.History,null,tint=MaterialTheme.colorScheme.onSurface.copy(alpha=.45f),modifier=Modifier.size(54.dp))
                Text("No calculations yet",fontSize=18.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.padding(top=14.dp))
                Text("Completed calculations will appear here automatically.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=13.sp,modifier=Modifier.padding(top=6.dp))
            }
        }else{
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())){
                history.forEach{entry->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{onOpen(entry)},
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)
                    ){
                        Column(Modifier.padding(14.dp)){
                            Row(verticalAlignment=Alignment.CenterVertically){
                                Column(Modifier.weight(1f)){
                                    Text(entry.name,fontWeight=FontWeight.SemiBold)
                                    Text(entry.category,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f),fontSize=11.sp)
                                }
                                IconButton(onClick={onDelete(entry.id)}){
                                    Icon(Icons.Default.DeleteOutline,null,tint=MaterialTheme.colorScheme.onSurface.copy(alpha=.65f))
                                }
                            }
                            Text(entry.details,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.75f),fontSize=12.sp,modifier=Modifier.padding(top=6.dp))
                            Text("Result: ${entry.result}",color=Amber,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))
                            Text(SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()).format(Date(entry.timestamp)),color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f),fontSize=11.sp,modifier=Modifier.padding(top=5.dp))
                            Text("Tap to open calculator",color=MaterialTheme.colorScheme.onSurface.copy(alpha=.45f),fontSize=10.sp,modifier=Modifier.padding(top=5.dp))
                        }
                    }
                }
            }
        }
    }
    if(confirmClear){
        AlertDialog(
            onDismissRequest={confirmClear=false},
            title={Text("Clear calculation history?")},
            text={Text("This permanently deletes all saved history entries from this device.")},
            confirmButton={TextButton(onClick={onClear();confirmClear=false}){Text("Clear all",color=Amber)}},
            dismissButton={TextButton(onClick={confirmClear=false}){Text("Cancel")}},
            containerColor=MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SavedScreen(title:String,list:List<CalcSpec>,modifier:Modifier,onSelect:(CalcSpec)->Unit){
    Column(modifier.fillMaxSize().padding(18.dp)){
        Text(title,fontSize=22.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(bottom=16.dp))
        if(list.isEmpty())Text(if(title=="History")"Calculation history will be added in a later update." else "No favorite calculators saved yet.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f))
        list.forEach{item->ListItem(headlineContent={Text(item.name)},supportingContent={Text(item.category)},leadingContent={Icon(Icons.Default.Calculate,null,tint=Amber)},modifier=Modifier.clickable{onSelect(item)},colors=ListItemDefaults.colors(containerColor=MaterialTheme.colorScheme.background))}
    }
}

@Composable
private fun LibraryScreen(modifier:Modifier){
    var expandedCategory by remember { mutableStateOf<String?>(null) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)){
        Text("Electrical Reference Library",fontSize=22.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(bottom=14.dp))
        categories.forEach{category->
            val formulas=CalculatorCatalog.all.filter{it.category==category.name}
            if(formulas.isNotEmpty()){
                val expanded=expandedCategory==category.name
                Card(
                    Modifier.fillMaxWidth().padding(vertical=5.dp),
                    shape=RoundedCornerShape(15.dp),
                    colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)
                ){
                    Column{
                        Row(
                            Modifier.fillMaxWidth().clickable{
                                expandedCategory=if(expanded)null else category.name
                            }.padding(horizontal=15.dp,vertical=16.dp),
                            verticalAlignment=Alignment.CenterVertically
                        ){
                            Box(
                                Modifier.size(44.dp).background(MaterialTheme.colorScheme.background,RoundedCornerShape(12.dp)),
                                contentAlignment=Alignment.Center
                            ){
                                Icon(categoryIcon(category.name),null,tint=Amber,modifier=Modifier.size(25.dp))
                            }
                            Column(Modifier.weight(1f).padding(start=13.dp)){
                                Text(category.name,fontWeight=FontWeight.SemiBold,fontSize=15.sp)
                                Text("${formulas.size} formula${if(formulas.size==1)"" else "s"}",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp,modifier=Modifier.padding(top=3.dp))
                            }
                            Icon(
                                if(expanded)Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription=if(expanded)"Collapse ${category.name}" else "Expand ${category.name}",
                                tint=Amber,modifier=Modifier.size(28.dp)
                            )
                        }
                        if(expanded){
                            HorizontalDivider(color=MaterialTheme.colorScheme.onSurface.copy(alpha=.12f))
                            Column(Modifier.padding(horizontal=12.dp,vertical=8.dp)){
                                formulas.forEachIndexed{index,item->
                                    Column(Modifier.fillMaxWidth().padding(horizontal=5.dp,vertical=9.dp)){
                                        Text(item.name,fontWeight=FontWeight.SemiBold,fontSize=14.sp)
                                        Text(item.formula,color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f),fontSize=14.sp,modifier=Modifier.padding(top=5.dp))
                                    }
                                    if(index<formulas.lastIndex) HorizontalDivider(color=MaterialTheme.colorScheme.onSurface.copy(alpha=.10f))
                                }
                            }
                        }
                    }
                }
            }
        }
        Text("Enter values using the SI units shown. Verify engineering decisions against applicable standards, equipment data and site conditions.",color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.65f),fontSize=12.sp,modifier=Modifier.padding(top=14.dp,bottom=12.dp))
    }
}
