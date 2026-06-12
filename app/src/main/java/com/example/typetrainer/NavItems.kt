package com.example.typetrainer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)
//used to iterate through the navbar items and show them
val navItems = listOf(
    //NavItem("Learn", "learn", Icons.Outlined.SportsEsports, Icons.Default.SportsEsports),
    NavItem("Events", "events", Icons.Outlined.Campaign, Icons.Default.Campaign, false),
    NavItem("Search", "search", Icons.Default.CatchingPokemon, Icons.Default.CatchingPokemon, false),
    NavItem("Developer", "preferences", Icons.Outlined.BugReport, Icons.Default.BugReport, false),
)