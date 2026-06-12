package com.example.typetrainer.ui.theme

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(

)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val PokemonGoColorScheme = lightColorScheme(

    primary = Color(0xFF71d59b),
    onPrimary = Color(0xFFfaffff),
    primaryContainer = Color(0xFF1b8697),
    onPrimaryContainer = Color(0xFFaefba8),
//    inversePrimary =
//    secondary =
//    onSecondary =
    secondaryContainer = Color(0xFFe9f2df),
    onSecondaryContainer = Color(0xFF48666c),
    tertiary = Color(0xFFffa345),
    onTertiary = Color(0xFFfffefb),
//    tertiaryContainer =
//    onTertiaryContainer =
    background = Color(0xFFFFFFFF),
//    onBackground =
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF256478),
//    surfaceVariant =
    onSurfaceVariant = Color(0xFF82a1a7),
    surfaceTint = Color(0xFF71d59b),
//    inverseSurface =
//    inverseOnSurface =
//    error =
//    onError =
//    errorContainer =
//    onErrorContainer =
    outline = Color(0xFF7a949a),
    outlineVariant = Color(0xFFceebe5),
//    scrim =
//    surfaceBright =
    surfaceContainer = Color(0xFFf1ffec),
//    surfaceContainerHigh =
//    surfaceContainerHighest =
//    surfaceContainerLow =
//    surfaceContainerLowest =
//    surfaceDim =

)

@Composable
fun TypeTrainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pokemonGoTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    Log.d("Theme", "dynamicColor: $dynamicColor")
    Log.d("Theme", "Build.VERSION.SDK_INT: ${Build.VERSION.SDK_INT}")
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            Log.d("Theme", "dynamicColorScheme")
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    Log.d("Theme", "colorScheme: $colorScheme")

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = 0
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    when (pokemonGoTheme) {
        true -> MaterialTheme(
            colorScheme = PokemonGoColorScheme,
            typography = PokemonGoTypography,
            content = content
        )
        else -> MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}