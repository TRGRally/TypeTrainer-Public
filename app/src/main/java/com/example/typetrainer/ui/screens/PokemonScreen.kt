package com.example.typetrainer.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.RemoveModerator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.typetrainer.data.models.PokemonEntity
import com.example.typetrainer.ui.components.TypeIcon
import com.example.typetrainer.ui.components.TypeIconClickable
import com.example.typetrainer.util.Constants
import coil.compose.SubcomposeAsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonScreen(
    navController: NavController,
    id: String,
    form: String,
    viewModel: SharedDataViewModel
) {

    //this screen shows the type effectiveness breakdown for an individual pokemon. its creature types, move types and effectiveness are shown.

    // Observe the back trigger state
    val backTrigger by viewModel.pokemonBackTrigger.collectAsState()

    // Lifecycle-aware back navigation using LaunchedEffect
    LaunchedEffect(backTrigger) {
        if (backTrigger) {
            navController.popBackStack()
            viewModel.resetBackTrigger()
        }
    }

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
            viewModel.resetNavigationTrigger() // Reset the trigger after navigation
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    //viewmodel.currentPokemon
    val pokemonEntity by viewModel.currentPokemon.collectAsState()

    val pokemonName = pokemonEntity?.name ?: "Pokemon not found"

    //cool lambda magic
    val onTypeClicked: (String) -> Unit = { type ->
        Log.d("SearchScreen", "Navigating to type screen: $type")
        viewModel.navigateToType(type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pokemonName,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.triggerBackNavigation()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (pokemonEntity?.image != null) {
                        IconButton(
                            onClick = {
                                pokemonEntity?.let {
                                    val url = viewModel.pokemonToWikiLink(it)
                                    Log.d("PokemonScreen", "Opening URL: $url")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                    navController.context.startActivity(intent)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open in browser"
                            )
                        }
                    } else {
                        Log.d("PokemonScreen", "Pokemon has no image, not showing the open in browser icon as its probably not in the game yet")
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
                .padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
        ) {
            pokemonEntity?.let {
                PokemonDetails(
                    pokemon = it,
                    viewModel = viewModel,
                    url = it.image,
                    onTypeClicked
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonDetails(
    pokemon: PokemonEntity,
    viewModel: SharedDataViewModel,
    url: String? = null,
    onTypeClicked: (String) -> Unit = {},
) {
    Log.d("PokemonDetails debug", "Pokemon: $pokemon")
    val effectivenessMap by viewModel.currentEffectiveness.collectAsState()

    val moveMap by viewModel.moveMap.observeAsState(emptyMap())

    val fastMoves = remember(pokemon.quickMoveIds, moveMap) {
        pokemon.quickMoveIds.mapNotNull { moveMap[it] }
    }
    val chargeMoves = remember(pokemon.cinematicMoveIds, moveMap) {
        pokemon.cinematicMoveIds.mapNotNull { moveMap[it] }
    }

    when {
        effectivenessMap == null -> {
            //loading state
        }

        effectivenessMap?.isEmpty() == true -> {
            //no data available, only happens if something went very wrong
            Text(
                text = "Effectiveness data is unavailable.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        else -> {

            //effectivenessMap now has content and is not null
            effectivenessMap?.let { map ->

                //PokemonDetails will recompose as effectivenessMap observes livedata
                //skips recomposition if effectivenessMap is the same as last time
                val isWeakTo = remember(effectivenessMap) {
                    map.filterValues { it > 1.0 && it <= 2.0 }
                }
                val isDoubleWeakTo = remember(effectivenessMap) {
                    map.filterValues { it > 2.0 }
                }
                val isResistantTo = remember(effectivenessMap) {
                    map.filterValues { it >= 0.5 && it < 1.0 }
                }
                val isDoubleResistantTo = remember(effectivenessMap) {
                    map.filterValues { it < 0.5 }
                }


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ElevatedCard {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("#${pokemon.dexNr}")
                                            }
                                            append("  ")
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                                                append(pokemon.name)
                                            }
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    if (isDoubleWeakTo.isNotEmpty()) {
                                        X2CounterWarningLarge()
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(100.dp)
                                ) {
                                    Surface(
                                        tonalElevation = 8.dp,
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            SubcomposeAsyncImage(
                                                model = url,
                                                contentDescription = "Pokemon image",
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxSize(),
                                                contentScale = ContentScale.Fit,
                                                loading = {
                                                    Image(
                                                        painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                                                        contentDescription = "Loading Image",
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .fillMaxSize(),
                                                        contentScale = ContentScale.Fit,
                                                        alpha = 0.2f
                                                    )
                                                },
                                                error = {
                                                    Image(
                                                        painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                                                        contentDescription = "Error Image",
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .fillMaxSize(),
                                                        contentScale = ContentScale.Fit,
                                                        alpha = 0.2f
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f),
                                    ) {
                                        TypeIconClickable(
                                            type = pokemon.primaryType,
                                            hasText = true,
                                            hasContainer = true,
                                            isLarge = true,
                                            iconWidth = 40.dp,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f),
                                            onClick = {
                                                onTypeClicked(pokemon.primaryType)
                                            },
                                        )

                                        pokemon.secondaryType?.let {
                                            TypeIconClickable(
                                                type = it,
                                                hasText = true,
                                                hasContainer = true,
                                                isLarge = true,
                                                iconWidth = 40.dp,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .weight(1f),
                                                onClick = {
                                                    onTypeClicked(it)
                                                },
                                            )
                                        }
                                    }


                                }

                                Text(
                                    text = "Weak to",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    isDoubleWeakTo.forEach {
                                        TypeIconClickable(
                                            type = it.key,
                                            hasText = true,
                                            hasContainer = true,
                                            iconWidth = 40.dp,
                                            modifier = Modifier.widthIn(min = 56.dp),
                                            onClick = { onTypeClicked(it.key) },
                                            isDoubleEffective = true
                                        )
                                    }
                                    isWeakTo.forEach {
                                        TypeIconClickable(
                                            type = it.key,
                                            hasText = true,
                                            hasContainer = true,
                                            iconWidth = 40.dp,
                                            modifier = Modifier.widthIn(min = 56.dp),
                                            onClick = { onTypeClicked(it.key) },
                                            isDoubleEffective = false
                                        )
                                    }
                                }

                                Text(
                                    text = "Resistant to",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    isDoubleResistantTo.forEach {
                                        TypeIconClickable(
                                            type = it.key,
                                            hasText = true,
                                            hasContainer = true,
                                            iconWidth = 40.dp,
                                            modifier = Modifier.widthIn(min = 56.dp),
                                            onClick = { onTypeClicked(it.key) },
                                            isDoubleEffective = true
                                        )
                                    }
                                    isResistantTo.forEach {
                                        TypeIconClickable(
                                            type = it.key,
                                            hasText = true,
                                            hasContainer = true,
                                            iconWidth = 40.dp,
                                            modifier = Modifier.widthIn(min = 56.dp),
                                            onClick = { onTypeClicked(it.key) },
                                            isDoubleEffective = false
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ElevatedCard(
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 12.dp, 16.dp, 16.dp)
                                ) {
                                    Text(
                                        text = "Fast moves",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    fastMoves.forEach {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            TypeIcon(
                                                type = it.type,
                                                iconWidth = 26.dp
                                            )
                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                        }
                                    }
                                }
                            }

                            ElevatedCard(
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 12.dp, 16.dp, 16.dp)
                                ) {
                                    Text(
                                        text = "Charge moves",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    chargeMoves.forEach {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            TypeIcon(
                                                type = it.type,
                                                iconWidth = 26.dp
                                            )
                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
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
    }
}

@Composable
fun X2CounterWarningLarge() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Color.hsl(8f, 0.8f, 0.55f)),
        color = MaterialTheme.colorScheme.errorContainer.copy(
            alpha = 0.25f
        ),
        contentColor = Color.hsl(8f, 0.8f, 0.55f),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp, 3.dp, 8.dp, 3.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.RemoveModerator,
                contentDescription = "Warning",
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
            )
            Text(
                text = "DOUBLE",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

        }
    }
}



