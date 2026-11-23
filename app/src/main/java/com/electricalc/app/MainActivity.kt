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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Graphite = Color(0xFF252B3A)
private val CardGraphite = Color(0xFF343C4D)
private val Amber = Color(0xFFFFB02E)
private val Muted = Color(0xFFB8C0CC)

private val ElectriCalcScheme = darkColorScheme(
    primary = Amber, onPrimary = Graphite, secondary = Color(0xFFFFC766),
    background = Graphite, surface = CardGraphite,
    onBackground = Color.White, onSurface = Color.White
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = ElectriCalcScheme) {
                ElectriCalcApp()
            }
        }
    }
}

private data class Category(val name: String, val detail: String)

private val categories = listOf(
    Category("Basic Electrical", "Ohm's law, power and resistance"),
    Category("AC Circuits", "Reactance, impedance and power factor"),
    Category("Three-Phase Systems", "Three-phase voltage, current and power"),
    Category("Transformers", "Turns ratio, loading and efficiency"),
    Category("Power Distribution", "Cables, voltage drop and loads"),
    Category("Electrical Machines", "Motors, generators and slip"),
    Category("Energy & Consumption", "Energy use and operating cost"),
    Category("Conversions", "Electrical units and quantities")
)

@Composable
private fun ElectriCalcApp() {
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val filtered = categories.filter {
        it.name.contains(query, true) || it.detail.contains(query, true)
    }

    Scaffold(
        containerColor = Graphite,
        bottomBar = {
            NavigationBar(containerColor = CardGraphite) {
                val items = listOf(
                    "Home" to Icons.Default.Home,
                    "Favorites" to Icons.Default.Favorite,
                    "History" to Icons.Default.History,
                    "Library" to Icons.Default.MenuBook
                )
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(item.second, contentDescription = item.first) },
                        label = { Text(item.first) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Amber,
                            selectedTextColor = Amber,
                            indicatorColor = Graphite,
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(46.dp).background(Amber, RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = "ElectriCalc mark",
                        tint = Graphite, modifier = Modifier.size(30.dp))
                }
                Column(Modifier.padding(start = 11.dp)) {
                    Text("ElectriCalc", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Electrical Calculator", color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Amber)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("What do you want to calculate?", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search calculators...", color = Muted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Muted) },
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(22.dp))
            Text("Calculation categories", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardGraphite)
                    ) {
                        Column(
                            Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Amber,
                                modifier = Modifier.size(27.dp))
                            Column {
                                Text(category.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(category.detail, color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
