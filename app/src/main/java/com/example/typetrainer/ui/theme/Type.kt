package com.example.typetrainer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.typetrainer.R

val interFontFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold)
)

val pokemonGoFontFamily = FontFamily(
    Font(R.font.lato_light, FontWeight.Thin),
    Font(R.font.lato_regular, FontWeight.Light),
    Font(R.font.lato_bold, FontWeight.Normal),
    Font(R.font.lato_black, FontWeight.Bold)
)

val DefaultTypography = Typography()

val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = interFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = interFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = interFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = interFontFamily,),

    //for the main LargeTopAppBar on the search screen, this lets the text be styled and also still work with the TopAppBar composable
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontFamily = interFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
    ),

    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = interFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = interFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = interFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = interFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = interFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = interFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = interFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = interFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = interFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = interFontFamily)



)

val PokemonGoTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = pokemonGoFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = pokemonGoFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = pokemonGoFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = pokemonGoFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = pokemonGoFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = pokemonGoFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = pokemonGoFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = pokemonGoFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = pokemonGoFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = pokemonGoFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = pokemonGoFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = pokemonGoFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = pokemonGoFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = pokemonGoFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = pokemonGoFontFamily)
)
