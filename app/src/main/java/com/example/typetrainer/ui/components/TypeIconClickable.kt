package com.example.typetrainer.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.typetrainer.util.PokemonTypeDrawable

@Composable
fun TypeIconClickable(
    type: String = "Unknown",
    hasText: Boolean = false,
    hasIcon: Boolean = true,
    hasContainer: Boolean = false,
    isLarge: Boolean = false,
    iconWidth: Dp? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    textOverride: String? = null,
    isDoubleEffective: Boolean = false
) {
    val elevation = if (hasContainer) {
        if (isDoubleEffective) 0.dp else 8.dp
    } else 0.dp
    val padding = if (hasContainer) PaddingValues(6.dp, 8.dp, 6.dp, 6.dp) else PaddingValues(0.dp)
    val color = if (hasContainer) {
        if (isDoubleEffective) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
    } else Color.Transparent

    val textStyle = if (isLarge) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium

    val border = if (isDoubleEffective) BorderStroke(1.5.dp, Color.hsl(8f, 0.8f, 0.55f)) else null

    //converts to percentage if override passed in
    var text = ""
    if (textOverride != null) {
//        val textPercent = textOverride * 100f
//        text = textPercent.toString().substringBefore(".") + "%"
        text = textOverride
    } else {
        text = type
    }


    Surface(
        border = border,
        shape = MaterialTheme.shapes.small,
        tonalElevation = elevation,
        color = color,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(padding)

        ) {
            if (hasIcon) {
                if (iconWidth != null) {
                    Image(
                        painter = painterResource(id = PokemonTypeDrawable(type)),
                        contentDescription = "Type icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(iconWidth)
                            .aspectRatio(1f)

                    )
                } else {
                    Image(
                        painter = painterResource(id = PokemonTypeDrawable(type)),
                        contentDescription = "Type icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .aspectRatio(1f)
                    )
                }
            }

            if (hasIcon && hasText) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (hasText) {
                Text(
                    text = text,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}