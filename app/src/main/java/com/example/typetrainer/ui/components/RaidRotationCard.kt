package com.example.typetrainer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.RemoveModerator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.typetrainer.util.Constants

@Composable
fun RaidRotationCard(
    url: String,
    name: String,
    types: List<String>,
    counters: Map<String, Double>,
    recommendedTrainersMin: Int,
    recommendedTrainersMax: Int,
    onClick: () -> Unit
) {
    // Identify x2 counters based on the counters map
    val x2Counters = counters.filterValues { it >= 2.0 }.keys.toList()

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = url,
                        contentDescription = "Event image",
                        modifier = Modifier
                            .padding(10.dp)
                            .width(80.dp)
                            .height(80.dp),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Image(
                                painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                                contentDescription = "Loading Image",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(72.dp)
                                    .height(72.dp),
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
                                    .width(72.dp)
                                    .height(72.dp),
                                contentScale = ContentScale.Fit,
                                alpha = 0.2f
                            )
                        }
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small.copy(
                            topEnd = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = MaterialTheme.shapes.small.bottomEnd
                        ),
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        tonalElevation = 0.dp,
                    ) {
                        RecommendedTrainersIcon(
                            recommendedTrainersMin = recommendedTrainersMin,
                            recommendedTrainersMax = recommendedTrainersMax,
                        )
                    }
                }
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
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    types.forEach { type ->
                        TypeIcon(
                            type = type,
                            iconWidth = 28.dp,
                            hasContainer = false,
                            hasText = false,
                        )
                    }
                    if (x2Counters.isNotEmpty()) {
                        X2CounterIcon()
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Forward Arrow",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .height(24.dp)
                    .width(24.dp)
            )
        }
    }
}

@Composable
fun X2CounterIcon() {
    Icon(
        imageVector = Icons.Outlined.RemoveModerator,
        contentDescription = "x2 Counter",
        tint = Color.hsl(8f, 0.8f, 0.55f),
        modifier = Modifier
            .height(24.dp)
            .width(24.dp)
    )
}
