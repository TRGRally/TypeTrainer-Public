package com.example.typetrainer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
fun TypeIcon(
    type: String = "Unknown",
    hasText: Boolean = false,
    hasIcon: Boolean = true,
    hasContainer: Boolean = false,
    isLarge: Boolean = false,
    iconWidth: Dp? = null,
    modifier: Modifier = Modifier,
) {
    val elevation = if (hasContainer) 8.dp else 0.dp
    val padding = if (hasContainer) 6.dp else 0.dp
    val color = if (hasContainer) MaterialTheme.colorScheme.surface else Color.Transparent
    val textStyle = if (isLarge) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium



    Surface(
        border = null,
        shape = MaterialTheme.shapes.small,
        tonalElevation = elevation,
        color = color,
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
                    text = type,
                    style = textStyle
                )
            }
        }
    }
}