package com.example.typetrainer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.typetrainer.data.repositories.PreferencesRepository
import com.example.typetrainer.provider.PokemonTypeContract
import com.example.typetrainer.ui.screens.EventsScreen
import com.example.typetrainer.ui.screens.LearnScreen
import com.example.typetrainer.ui.screens.PokemonScreen
import com.example.typetrainer.ui.screens.PokemonTypeScreen
import com.example.typetrainer.ui.screens.PreferencesScreen
import com.example.typetrainer.ui.screens.QuizIntroScreen
import com.example.typetrainer.ui.screens.QuizQuestionScreen
import com.example.typetrainer.ui.screens.QuizResultsScreen
import com.example.typetrainer.ui.screens.SearchScreen
import com.example.typetrainer.ui.theme.TypeTrainerTheme
import com.example.typetrainer.viewmodels.LearnViewModel
import com.example.typetrainer.viewmodels.SharedDataViewModel
import com.example.typetrainer.viewmodels.PreferencesViewModel
import com.example.typetrainer.viewmodels.PreferencesViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


const val WEB_CLIENT_ID = "309005892877-upqvg9fb9baqjkn5r5uppfd3k63m7jjf.apps.googleusercontent.com"

class MainActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth
    private lateinit var preferencesViewModel: PreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val preferencesRepository = PreferencesRepository(this)

        // Create an instance of PreferencesViewModelFactory
        val factory = PreferencesViewModelFactory(preferencesRepository)

        // Use ViewModelProvider to get the PreferencesViewModel
        preferencesViewModel = ViewModelProvider(this, factory).get(PreferencesViewModel::class.java)


        //contentProvider test
        lifecycleScope.launch {
            Log.d("MainActivity", "Testing ContentProvider")
            queryAllPokemonTypes()
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {
            CompositionLocalProvider(LocalInspectionMode provides false) {
                val pokemonGoTheme by preferencesViewModel.pokemonGoTheme.collectAsState(initial = false)
                TypeTrainerTheme(
                    pokemonGoTheme = pokemonGoTheme,
                ) {

                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    // Initialize Credential Manager
                    val credentialManager = CredentialManager.create(context)

                    fun signInWithGoogle(scope: CoroutineScope, context: Context) {
                        Log.d("MainActivity", "signInWithGoogle called")

                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(WEB_CLIENT_ID)
                            .build()
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        scope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )
                                val credential = result.credential
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(credential.data)
                                val googleIdToken = googleIdTokenCredential.idToken

                                val firebaseCredential =
                                    GoogleAuthProvider.getCredential(googleIdToken, null)
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.e(
                                                "MainActivity",
                                                "Successfully signed in with Google"
                                            )
                                        } else {
                                            Log.e(
                                                "MainActivity",
                                                "Error signing in with Google",
                                                task.exception
                                            )
                                        }
                                    }
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error getting credential", e)
                                Toast.makeText(context, "Error ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                                e.printStackTrace()
                            }
                        }
                    }

                    fun signOut(scope: CoroutineScope, context: Context) {
                        Log.d("MainActivity", "Signing out...")
                        auth.signOut()
                        scope.launch {
                            credentialManager.clearCredentialState(
                                ClearCredentialStateRequest()
                            )
                        }
                        Log.e("MainActivity", "Signed out")
                    }

                    App(
                        preferencesViewModel,
                        auth,
                        { signOut(scope, context) },
                        { signInWithGoogle(scope, context) }
                    )
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.e("MainActivity", "User is signed in")
            preferencesViewModel.onGoogleSignInSuccess(currentUser)
        } else {
            Log.e("MainActivity", "User is not signed in")
        }
    }



    private suspend fun handleSignInResult(result: GetCredentialResponse) {
        Log.d("MainActivity", "Handling sign-in result")
        val credential = result.credential
        val authCredential =
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            } else {
                Log.d("MainActivity", "Received an invalid credential type")
                throw RuntimeException("Received an invalid credential type")
            }
        Log.d("MainActivity", "Auth credential: $authCredential")
        val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
        Log.d("MainActivity", "Auth result: $authResult")
        val firebaseUser = authResult.user
        Log.d("MainActivity", "Firebase user: $firebaseUser")
        if (firebaseUser != null) {
            Log.d("MainActivity", "Sign-in successful")
            preferencesViewModel.onGoogleSignInSuccess(firebaseUser)
        } else {
            // Handle sign-in failure
        }
    }

    private suspend fun queryAllPokemonTypes() {
        withContext(Dispatchers.IO) {
            val cursor: Cursor? = contentResolver.query(
                PokemonTypeContract.PokemonTypes.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val type = it.getString(it.getColumnIndexOrThrow(PokemonTypeContract.PokemonTypes.COLUMN_TYPE))
                    Log.d("MainActivity", "Pokemon Type: $type")
                }
            }
        }
    }

    private suspend fun queryPokemonTypeByName(name: String) {
        withContext(Dispatchers.IO) {
            val typeUri: Uri = Uri.withAppendedPath(PokemonTypeContract.PokemonTypes.CONTENT_URI, name)
            val cursor: Cursor? = contentResolver.query(
                typeUri,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val type = it.getString(it.getColumnIndexOrThrow(PokemonTypeContract.PokemonTypes.COLUMN_TYPE))
                    Log.d("MainActivity", "Specific Pokemon Type: $type")
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(preferencesViewModel: PreferencesViewModel, auth: FirebaseAuth, signOut: () -> Unit, signInWithGoogle: () -> Unit) {


    LaunchedEffect(Unit) {
        Log.d("App debug", "App was recomposed")
    }

    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)

    //debugging the awful backstack nonsense going on rn
    navController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller
            .currentBackStack.value
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog debug", "BackStack: $routes")
    }




    // remove the navigationSelectedItem variable
    // and derive selectedIndex from currentRoute
    val selectedIndex by remember(currentRoute) {
        derivedStateOf {
            val index = navItems.indexOfFirst { it.route == currentRoute }
            if (index != -1) {
                index
            } else if (currentRoute != null && (currentRoute.contains("pokemon") || currentRoute.contains(
                    "type"
                ))
            ) {
                1
            } else {
                -1
            }
        }
    }

    val transitionLength = 200
    val transitionDelay = 45


    var searchActive by remember { mutableStateOf(true) }

    val sharedDataViewModel: SharedDataViewModel = viewModel()

    //val learnViewModel: LearnViewModel = viewModel()

    val context = LocalContext.current

    var navigationStartTime by remember { mutableLongStateOf(0L) }
    var navigationEndTime by remember { mutableLongStateOf(0L) }


    Scaffold(
        bottomBar = {
//            val showNavBarRoute = currentRoute !in listOf(
//                "quiz_intro",
//                "quiz_question/{questionIndex}",
//                "quiz_results",
//                "pokemon/{id}/{form}"
//            )
            val showNavBarRoute = true
            //livedata
            val currentRelevantEvents by sharedDataViewModel.currentRelevantEvents.observeAsState(emptyList())

            AnimatedVisibility(
                visible = showNavBarRoute,
                enter = EnterTransition.None,
                exit = ExitTransition.None
            ) {
                NavigationBar {
                    //for all nav items
                    navItems.forEachIndexed { index, navItem ->
                        val isSelected = currentRoute == navItem.route ||
                                (navItem.route == "search" && currentRoute?.contains("pokemon") == true) ||
                                (navItem.route == "search" && currentRoute?.contains("type") == true)

                        val hasNewsBadge = if (navItem.route == "events") {
                            currentRelevantEvents.isNotEmpty()
                        } else {
                            navItem.hasNews
                        }

                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (navItem.badgeCount != null) {
                                            Badge {
                                                Text(text = navItem.badgeCount.toString())
                                            }
                                        } else if (hasNewsBadge) {
                                            Badge()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) navItem.activeIcon else navItem.icon,
                                        contentDescription = null
                                    )
                                }

                            },
                            label = { Text(navItem.title) },
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(navItem.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            selected = isSelected,
                        )
                    }
                }

            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "search",
            enterTransition = {
                fadeIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + slideInHorizontally(
                    initialOffsetX = { it / 16 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + slideOutHorizontally(
                    targetOffsetX = { it / -32 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        delayMillis = 100,
                        easing = FastOutSlowInEasing

                    )
                ) + slideInHorizontally(
                    initialOffsetX = { -it / 8 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    initialScale = 1.1f
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        delayMillis = 50,
                        easing = FastOutSlowInEasing,
                        durationMillis = 100
                    )
                ) + scaleOut(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    targetScale = 0.9f,
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                )
            },
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            composable("search") {
                SearchScreen(
                    navController = navController,
                    sharedDataViewModel = sharedDataViewModel,
                    onSearchBarActive = {
                        searchActive = !it
                    }
                )
            }
            //composable("learn") {
            //    LearnScreen(navController = navController, viewModel = learnViewModel)
            //}
            composable("events") {
                EventsScreen(
                    navController = navController,
                    sharedDataViewModel = sharedDataViewModel
                )
            }
            composable("preferences") {
                PreferencesScreen(
                    navController = navController,
                    sharedDataViewModel = sharedDataViewModel,
                    preferencesViewModel = preferencesViewModel,
                    currentUser = auth.currentUser,
                    signOut = signOut,
                    onGoogleSignIn = signInWithGoogle
                )
            }
            composable(
                route = "pokemon/{id}/{form}",
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("form") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val form = backStackEntry.arguments?.getString("form")
                if (id != null && form != null) {
                    PokemonScreen(
                        navController = navController,
                        id = id,
                        form = form,
                        viewModel = sharedDataViewModel
                    )
                } else {
                    navController.navigate("search") {
                        launchSingleTop = true
                    }

                    Log.d("MainActivity", "Pokemon id / form is null")
                    Log.d("MainActivity", "id: $id")
                    Log.d("MainActivity", "form: $form")
                }
            }
            composable(
                route = "type/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                if (id != null) {
                    PokemonTypeScreen(
                        navController = navController,
                        id = id,
                        viewModel = sharedDataViewModel
                    )
                } else {
                    navController.navigate("search") {
                        launchSingleTop = true
                    }
                    Log.d("MainActivity", "PokemonType id is null")
                    Log.d("MainActivity", "id: $id")
                }
            }
            //quiz nav on a separate stack
            navigation(
                startDestination = "quiz_intro",
                route = "quiz"
            ) {
                composable("quiz_intro") {
                    QuizIntroScreen(navController = navController, viewModel = sharedDataViewModel)
                }
                composable("quiz_question/{questionIndex}",
                    arguments = listOf(navArgument("questionIndex") {
                        defaultValue = 0
                    }
                    )
                ) { backStackEntry ->
                    val questionIndex = backStackEntry.arguments?.getInt("questionIndex") ?: 0
                    QuizQuestionScreen(
                        navController = navController,
                        questionIndex = questionIndex,
                        viewModel = sharedDataViewModel
                    )
                }
                composable("quiz_results") {
                    QuizResultsScreen(
                        navController = navController,
                        viewModel = sharedDataViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

