package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.typetrainer.viewmodels.SharedDataViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.ui.components.TypeIcon
import com.example.typetrainer.ui.components.TypeIconClickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonTypeScreen(
    navController: NavController,
    id: String,
    viewModel: SharedDataViewModel
) {

    //this shows the type effectiveness breakdown of a single pokemon type. the types can be clicked on to navigate from screen to screen.

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val typeMap by viewModel.typesMap.observeAsState(emptyMap())

    val typeEntity = typeMap[id]

    Log.d("TypeScreen", "TypeEntity: $typeEntity")

    //pokemon nav
    val pokemonNavigationTrigger by viewModel.pokemonNavigationTrigger.collectAsState()

    //type nav
    val typeNavigationTrigger by viewModel.typeNavigationTrigger.collectAsState()

    LaunchedEffect(pokemonNavigationTrigger) {
        pokemonNavigationTrigger?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
            viewModel.resetNavigationTrigger() // Reset the trigger after navigation
        }
    }

    LaunchedEffect(typeNavigationTrigger) {
        typeNavigationTrigger?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
            viewModel.resetTypeNavigationTrigger() // Reset the trigger after navigation
        }
    }

    val typeName = typeEntity?.type ?: "Type not found"

    val onTypeClicked: (String) -> Unit = { type ->
        Log.d("SearchScreen", "Navigating to type screen: $type")
        viewModel.navigateToType(type)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = typeName,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .animateContentSize(
                animationSpec = spring()
            )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            typeEntity?.let {
                TypeDetails(type = it, viewModel = viewModel, onTypeClicked)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypeDetails(
    type: TypeEntity,
    viewModel: SharedDataViewModel,
    onTypeClicked: (String) -> Unit = {},
) {
    val aggregatedTypeInfo by viewModel.aggregatedTypeInfo.observeAsState(emptyList())
    val currentTypeInfo = aggregatedTypeInfo.find { it.type == type.type }

    val isWeakTo = currentTypeInfo?.doubleDamageFrom ?: emptyList()
    val isResistantTo = currentTypeInfo?.halfDamageFrom ?: emptyList()
    val noDamageFrom = currentTypeInfo?.noDamageFrom ?: emptyList()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)
            ) {
                TypeIcon(
                    type = type.type,
                    iconWidth = 72.dp,
                    hasContainer = false,
                    hasText = false,
                    isLarge = true,
                )
                Text(
                    text = type.type,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ElevatedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)
                    ) {
                        Text(
                            text = "Resistant to",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            noDamageFrom.forEach {
                                TypeIconClickable(
                                    type = it,
                                    hasText = true,
                                    hasContainer = true,
                                    iconWidth = 40.dp,
                                    modifier = Modifier.widthIn(min = 56.dp),
                                    onClick = { onTypeClicked(it) },
                                    isDoubleEffective = true,
                                )
                            }
                            if (isResistantTo.isEmpty() && noDamageFrom.isEmpty()) {
                                Text(
                                    text = "No resistances \uD83D\uDE10",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                isResistantTo.forEach {
                                    TypeIconClickable(
                                        type = it,
                                        hasText = true,
                                        hasContainer = true,
                                        iconWidth = 40.dp,
                                        modifier = Modifier.widthIn(min = 56.dp),
                                        onClick = { onTypeClicked(it) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ElevatedCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)
                    ) {
                        Text(
                            text = "Weak to",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (isWeakTo.isEmpty()) {
                                Text(
                                    text = "No weaknesses?! \uD83D\uDE32",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                isWeakTo.forEach {
                                    TypeIconClickable(
                                        type = it,
                                        hasText = true,
                                        hasContainer = true,
                                        iconWidth = 40.dp,
                                        modifier = Modifier.widthIn(min = 56.dp),
                                        onClick = { onTypeClicked(it) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



