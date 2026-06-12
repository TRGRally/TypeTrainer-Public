package com.example.typetrainer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.typetrainer.util.Constants

@Composable
fun PokemonSearchResult(
    url: String?,
    name: String,
    types: List<String>,
    onClick: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Surface(
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.small
            ) {
                SubcomposeAsyncImage(
                    model = url,
                    contentDescription = "Pokemon image",
                    modifier = Modifier
                        .padding(4.dp, 4.dp, 8.dp, 4.dp)
                        .width(72.dp)
                        .height(72.dp),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Image(
                            painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                            contentDescription = "Loading Image",
                            contentScale = ContentScale.Fit
                        )
                    },
                    error = {
                        Image(
                            painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                            contentDescription = "Error Image",
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(100f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    types.forEach { type ->
                        TypeIcon(type = type, iconWidth = 32.dp, hasContainer = false)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Forward Arrow",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .height(24.dp)
                    .width(24.dp)
            )
        }
    }
}
