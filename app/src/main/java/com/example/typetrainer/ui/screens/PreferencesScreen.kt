package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.typetrainer.util.Constants
import com.example.typetrainer.viewmodels.SharedDataViewModel
import com.example.typetrainer.viewmodels.PreferencesViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    navController: NavController,
    sharedDataViewModel: SharedDataViewModel = viewModel(), // this is so the user can refresh the pokemon list from the preferences screen
    preferencesViewModel: PreferencesViewModel,
    currentUser: FirebaseUser?,
    signOut: () -> Unit,
    onGoogleSignIn: () -> Unit
) {

    val context = LocalContext.current as ComponentActivity
    val scope = rememberCoroutineScope()

    //this screen was originally meant to be for firebase integration, but that was a stretch goal that was not implemented.
    //now it is used to refresh the data from the API (as there is not a Service to do so automatically)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by sharedDataViewModel.isLoading.collectAsState()

    val loadComplete by sharedDataViewModel.loadComplete.observeAsState(false)

    var isDialogOpen by remember { mutableStateOf(false) }

    val webHashes by sharedDataViewModel.webHashes.collectAsState()

    val allLocalHashes = sharedDataViewModel.allLocalHashes
    Log.d("PreferencesScreen", "All Local Hashes: ${allLocalHashes.value}")
    //converting this to a map
    val localHashes = allLocalHashes.value?.associateBy { it.filename }?.mapValues { it.value.hash }
    Log.d("PreferencesScreen", "Local Hashes: $localHashes")

    LaunchedEffect(loadComplete) {
        if (loadComplete) {
            snackbarHostState.showSnackbar(
                message = "Pokemon data loaded successfully.",
                withDismissAction = true
            )
            sharedDataViewModel.resetLoadComplete()
        }
    }

    val pokemonGoTheme by preferencesViewModel.pokemonGoTheme.collectAsState(initial=false)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Developer tools",
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            isDialogOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { preferencesViewModel.openBugReport(context) },
                icon = { Icon(Icons.Outlined.BugReport, "Bug report") },
                text = { Text(text = "Report a bug") },
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .animateContentSize(
                animationSpec = spring()
            )
    ) { paddingValues ->

        when {
            isDialogOpen -> {
                AlertDialog(
                    onDismissRequest = {
                        isDialogOpen = false
                    },
                    confirmButton = {
                        TextButton(onClick = { isDialogOpen = false }) {
                            Text("Cool beans")
                        }
                    },
                    title = {
                        Text("About")
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                "This screen is meant to be for firebase integration, but right now it's just where I dump all the developer tools.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Stuff on this page might not work - and will change often!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Let me know of any bugs you encounter by clicking the bug report button :)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 64.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()


        ) {
            item {
                ElevatedCard(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            //profile picture from currentUser
                            if (currentUser?.photoUrl != null) {
                                AsyncImage(
                                    model = currentUser.photoUrl!!,
                                    contentDescription = "Profile Picture",
                                    imageLoader = ImageLoader(context),
                                    modifier = Modifier
                                        .height(64.dp)
                                        .width(64.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = Constants.PROFILE_PLACEHOLDER),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .height(64.dp)
                                        .width(64.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                Text(
                                    text = currentUser?.displayName ?: "Local account",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = currentUser?.email ?: "Sign in to back up your data",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = currentUser?.metadata?.creationTimestamp?.let {
                                    "Created ${DateFormat.getDateInstance().format(Date(it))}"
                                } ?: "Not signed in",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )

                            if (currentUser != null) {
                                TextButton(
                                    onClick = {
                                        signOut()
                                    }) {
                                    Text(text = "Sign out")
                                }
                            } else {
                                GoogleSignInButton {
                                    onGoogleSignIn()
                                }
                            }
                        }
                    }
                }
            }

            item {
                //a toggle that turns the pokemon go theme on or off
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = "Pokemon Go Theme",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Switch(
                        checked = pokemonGoTheme,

                        onCheckedChange = { isChecked ->
                            scope.launch {
                                preferencesViewModel.setPokemonGoTheme(isChecked)
                            }
                        }
                    )
                }
            }

            item {
                HorizontalDivider()
            }

            item {
                TextButton(
                    onClick = {
                        sharedDataViewModel.loadPokemonFromJson(manual = true)
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "Refreshing..." else "Refresh Pokemon Data from Internet"
                    )
                }
            }
            item {
                TextButton(
                    onClick = {
                        sharedDataViewModel.loadTypeDataFromJson()
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "Refreshing..." else "Refresh Type Data"
                    )
                }
            }
            item {
                TextButton(
                    onClick = {
                        sharedDataViewModel.deleteQuizData(manual = false)
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "Refreshing..." else "Delete Quiz Data"
                    )
                }
            }
            item {
                TextButton(
                    onClick = {
                        sharedDataViewModel.checkWebHashes()
                    },
                    enabled = !isLoading
                ) {
                    Text(
                        if (isLoading) "Fetching..." else "Check route hash"
                    )
                }
            }



            for (hash in webHashes) {
                item {
                    ElevatedCard(
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "${hash.key} SHA512",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = "API",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = hash.value,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Local",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = localHashes?.get(hash.key) ?: "none found",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                if (localHashes?.get(hash.key) == hash.value) {
                                    Text(
                                        text = "Match",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Match",
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(20.dp)
                                            .padding(start = 6.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else if (localHashes?.get(hash.key) == null) {
                                    Text(
                                        text = "Hash missing",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CodeOff,
                                        contentDescription = "Missing",
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(20.dp)
                                            .padding(start = 6.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "Outdated",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = "Warning",
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(20.dp)
                                            .padding(start = 6.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            //sources
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = "Sources",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            sharedDataViewModel.copySourcesToClipboard()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            item {
                Column() {
                    Text(
                        text = "Pokemon and raid data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )

                    Text(
                        text = "https://github.com/pokemon-go-api/pokemon-go-api",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 4.dp)
                    )
                }

            }

            item {
                Column() {
                    Text(
                        text = "Events data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "https://github.com/bigfoott/ScrapedDuck",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 4.dp)
                    )
                    Text(
                        text = "(scrapes https://leekduck.com)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 4.dp)
                    )
                }
            }

            item {
                Column() {
                    Text(
                        text = "Type icons recoloured from",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "https://github.com/partywhale/pokemon-type-icons",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 4.dp)
                    )
                }
            }

            item {
                Column() {
                    Text(
                        text = "App icon svg (fighting icon)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "https://github.com/duiker101/pokemon-type-svg-icons",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp, 4.dp)
                    )
                }
            }

        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    TextButton(onClick = {
        Log.d("SignInButton", "GoogleSignInButton clicked")
        onClick()
    }) {
        Text(text = "Sign in with Google")
    }
}