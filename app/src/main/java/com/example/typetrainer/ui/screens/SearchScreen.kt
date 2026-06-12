package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.typetrainer.R
import com.example.typetrainer.data.models.PokemonEntity
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.ui.components.PokemonSearchResult
import com.example.typetrainer.ui.components.RaidRotationCard
import com.example.typetrainer.ui.components.TypeSearchResult
import com.example.typetrainer.viewmodels.SharedDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    sharedDataViewModel: SharedDataViewModel,
    onSearchBarActive: (Boolean) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    //pokemon nav
    val pokemonNavigationTrigger by sharedDataViewModel.pokemonNavigationTrigger.collectAsState()

    //type nav
    val typeNavigationTrigger by sharedDataViewModel.typeNavigationTrigger.collectAsState()

    // Handle navigation in a lifecycle-aware way
    LaunchedEffect(pokemonNavigationTrigger) {
        pokemonNavigationTrigger?.let { route ->
            navController.navigate(route)
            sharedDataViewModel.resetNavigationTrigger() // Reset the trigger after navigation
        }
    }

    LaunchedEffect(typeNavigationTrigger) {
        typeNavigationTrigger?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
            sharedDataViewModel.resetNavigationTrigger() // Reset the trigger after navigation
        }
    }

    val queryText by sharedDataViewModel.queryText.collectAsState()

    val isSearchBarActive by sharedDataViewModel.isSearchBarActive.collectAsState()

    LaunchedEffect(isSearchBarActive) {
        Log.d("SearchScreen", "Search bar active: $isSearchBarActive")
        onSearchBarActive(isSearchBarActive)
    }


    val searchBarPaddingStart by animateDpAsState(
        targetValue = if (!isSearchBarActive) 16.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "Search bar padding start"
    )
    val searchBarPaddingEnd by animateDpAsState(
        targetValue = if (!isSearchBarActive) 16.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "Search bar padding end"
    )
    val searchBarPaddingBottom by animateDpAsState(
        targetValue = if (!isSearchBarActive) 24.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "Search bar padding bottom"
    )
    val searchBarPaddingTop by animateDpAsState(
        targetValue = if (!isSearchBarActive) 8.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "Search bar padding top"
    )

    val searchResults by sharedDataViewModel.searchResults.collectAsState()

    val recommendations by sharedDataViewModel.raidRecommendations.observeAsState(emptyList())

    val megaRaids by sharedDataViewModel.megaRaids.observeAsState(emptyList())
    val legendaryMegaRaids by sharedDataViewModel.legendaryMegaRaids.observeAsState(emptyList())
    val exRaids by sharedDataViewModel.exRaids.observeAsState(emptyList())
    val ultraBeastRaids by sharedDataViewModel.ultraBeastRaids.observeAsState(emptyList())
    val lvl5Raids by sharedDataViewModel.lvl5Raids.observeAsState(emptyList())
    val lvl3Raids by sharedDataViewModel.lvl3Raids.observeAsState(emptyList())
    val lvl1Raids by sharedDataViewModel.lvl1Raids.observeAsState(emptyList())


    val isTopAppBarCollapsed by remember {
        derivedStateOf { scrollBehavior.state.collapsedFraction  == 1f }
    }


    val searchBarTonalElevation by animateDpAsState(
        targetValue = if (isSearchBarActive) 2.dp else 4.dp,
        animationSpec = tween(durationMillis = 150),
        label = "Search bar tonal elevation"
    )

    val searchBarShadowElevation by animateDpAsState(
        targetValue = if (isSearchBarActive) 0.dp else 2.dp,
        animationSpec = tween(durationMillis = 150),
        label = "Search bar shadow elevation"
    )


    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !isSearchBarActive, //hides top app bar when search bar is active
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Type Trainer",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchBar(
                tonalElevation = searchBarTonalElevation,
                shadowElevation = searchBarShadowElevation,
                query = queryText,
                onQueryChange = {
                    sharedDataViewModel.updateQueryText(it)
                    sharedDataViewModel.search()
                },
                onSearch = {
                    sharedDataViewModel.search()
                },
                active = isSearchBarActive,
                onActiveChange = {
                    sharedDataViewModel.setSearchBarActive(it)
                },
                placeholder = {
                    Text("Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            sharedDataViewModel.setSearchBarActive(false)
                            sharedDataViewModel.updateQueryText("")
                        },
                        enabled = isSearchBarActive || queryText.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear icon"
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = searchBarPaddingStart,
                        top = searchBarPaddingTop,
                        end = searchBarPaddingEnd,
                        bottom = searchBarPaddingBottom
                    )


            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (queryText.isNotEmpty()) { // >= 1 for search performance
                        Log.d("SearchScreen", "Search results: $searchResults")
                        if(searchResults.isEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)

                                ) {
                                    //empty state
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                                        contentDescription = "No results",
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                    Text(
                                        text ="No results found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                            }
                        }
                        items(searchResults) { result ->
                            when (result) {
                                is PokemonEntity -> {
                                    PokemonSearchResult(
                                        url = result.image,
                                        name = result.name,
                                        types = listOfNotNull(result.primaryType, result.secondaryType),
                                        onClick = {
                                            Log.d("SearchScreen", "Navigating to Pokemon screen: ${result.id}/${result.formId}")
                                            sharedDataViewModel.navigateToPokemon(result.id, result.formId)
                                        }
                                    )
                                }

                                is TypeEntity -> {
                                    TypeSearchResult(
                                        type = result.type,
                                        onClick = {
                                            Log.d("SearchScreen", "Navigating to Type screen: ${result.type}")
                                            sharedDataViewModel.navigateToType(result.type)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        //not searched yet state
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = "Search Pokemon or Types",
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    text ="Search for Pokemon or Types",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }


            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 72.dp, 16.dp, 8.dp)
            ) {


                if (megaRaids.isNotEmpty() || legendaryMegaRaids.isNotEmpty() || exRaids.isNotEmpty() || ultraBeastRaids.isNotEmpty()){
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp, 16.dp, 0.dp, 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CrisisAlert,
                                contentDescription = "Group",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Crazy raids",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                items(
                    count = megaRaids.size,
                    key = { index -> megaRaids[index].image }
                ) { index ->
                    val megaRaid = megaRaids[index]
                    RaidRotationCard(
                        url = megaRaid.image,
                        name = megaRaid.name,
                        types = megaRaid.types,
                        counters = megaRaid.counters,
                        recommendedTrainersMin = megaRaid.recommendedTrainersMin,
                        recommendedTrainersMax = megaRaid.recommendedTrainersMax,
                        onClick = {
                            Log.d(
                                "SearchScreen",
                                "Navigate to Pokemon screen: ${megaRaid.id}/${megaRaid.form}"
                            )
                            sharedDataViewModel.navigateToPokemon(megaRaid.id, megaRaid.form)
                        }
                    )
                }

                items(
                    count = legendaryMegaRaids.size,
                    key = { index -> legendaryMegaRaids[index].image }
                ) { index ->
                    val legendaryMegaRaid = legendaryMegaRaids[index]
                    RaidRotationCard(
                        url = legendaryMegaRaid.image,
                        name = legendaryMegaRaid.name,
                        types = legendaryMegaRaid.types,
                        counters = legendaryMegaRaid.counters,
                        recommendedTrainersMin = legendaryMegaRaid.recommendedTrainersMin,
                        recommendedTrainersMax = legendaryMegaRaid.recommendedTrainersMax,
                        onClick = {
                            Log.d(
                                "SearchScreen",
                                "Navigate to Pokemon screen: ${legendaryMegaRaid.id}/${legendaryMegaRaid.form}"
                            )
                            sharedDataViewModel.navigateToPokemon(legendaryMegaRaid.id, legendaryMegaRaid.form)
                        }
                    )
                }

                items(
                    count = exRaids.size,
                    key = { index -> exRaids[index].image }
                ) { index ->
                    val exRaid = exRaids[index]
                    RaidRotationCard(
                        url = exRaid.image,
                        name = exRaid.name,
                        types = exRaid.types,
                        counters = exRaid.counters,
                        recommendedTrainersMin = exRaid.recommendedTrainersMin,
                        recommendedTrainersMax = exRaid.recommendedTrainersMax,
                        onClick = {
                            Log.d(
                                "SearchScreen",
                                "Navigate to Pokemon screen: ${exRaid.id}/${exRaid.form}"
                            )
                            sharedDataViewModel.navigateToPokemon(exRaid.id, exRaid.form)
                        }
                    )
                }

                items(
                    count = ultraBeastRaids.size,
                    key = { index -> ultraBeastRaids[index].image }
                ) { index ->
                    val ultraBeastRaid = ultraBeastRaids[index]
                    RaidRotationCard(
                        url = ultraBeastRaid.image,
                        name = ultraBeastRaid.name,
                        types = ultraBeastRaid.types,
                        counters = ultraBeastRaid.counters,
                        recommendedTrainersMin = ultraBeastRaid.recommendedTrainersMin,
                        recommendedTrainersMax = ultraBeastRaid.recommendedTrainersMax,
                        onClick = {
                            Log.d(
                                "SearchScreen",
                                "Navigate to Pokemon screen: ${ultraBeastRaid.id}/${ultraBeastRaid.form}"
                            )
                            sharedDataViewModel.navigateToPokemon(ultraBeastRaid.id, ultraBeastRaid.form)
                        }
                    )
                }

                items(
                    count = lvl5Raids.size,
                    key = { index -> lvl5Raids[index].image }
                ) { index ->
                    val lvl5Raid = lvl5Raids[index]
                    RaidRotationCard(
                        url = lvl5Raid.image,
                        name = lvl5Raid.name,
                        types = lvl5Raid.types,
                        counters = lvl5Raid.counters,
                        recommendedTrainersMin = lvl5Raid.recommendedTrainersMin,
                        recommendedTrainersMax = lvl5Raid.recommendedTrainersMax,
                        onClick = {
                            Log.d(
                                "SearchScreen",
                                "Navigate to Pokemon screen: ${lvl5Raid.id}/${lvl5Raid.form}"
                            )
                            sharedDataViewModel.navigateToPokemon(lvl5Raid.id, lvl5Raid.form)
                        }
                    )
                }

                if (lvl3Raids.isNotEmpty() || lvl1Raids.isNotEmpty()) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp, 4.dp, 0.dp, 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Easy",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Unserious raids",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                items(
                    count = lvl3Raids.size,
                    key = { index -> lvl3Raids[index].image }
                ) { index ->
                    val lvl3Raid = lvl3Raids[index]
                    RaidRotationCard(
                        url = lvl3Raid.image,
                        name = lvl3Raid.name,
                        types = lvl3Raid.types,
                        counters = lvl3Raid.counters,
                        recommendedTrainersMin = lvl3Raid.recommendedTrainersMin,
                        recommendedTrainersMax = lvl3Raid.recommendedTrainersMax,
                        onClick = {
                            Log.d("SearchScreen", "Navigate to Pokemon screen: ${lvl3Raid.id}/${lvl3Raid.form}")
                            sharedDataViewModel.navigateToPokemon(lvl3Raid.id, lvl3Raid.form)
                        }
                    )
                }
                items(
                    count = lvl1Raids.size,
                    key = { index -> lvl1Raids[index].image }
                ) { index ->
                    val lvl1Raid = lvl1Raids[index]
                    RaidRotationCard(
                        url = lvl1Raid.image,
                        name = lvl1Raid.name,
                        types = lvl1Raid.types,
                        counters = lvl1Raid.counters,
                        recommendedTrainersMin = lvl1Raid.recommendedTrainersMin,
                        recommendedTrainersMax = lvl1Raid.recommendedTrainersMax,
                        onClick = {
                            Log.d("SearchScreen", "Navigate to Pokemon screen: ${lvl1Raid.id}/${lvl1Raid.form}")
                            sharedDataViewModel.navigateToPokemon(lvl1Raid.id, lvl1Raid.form)
                        }
                    )
                }

            }
        }

    }
}
