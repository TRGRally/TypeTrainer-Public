package com.example.typetrainer.ui.components

import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MultipleStop
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.typetrainer.data.models.Event
import com.example.typetrainer.util.Constants

@Composable
fun EventCardSmall(
    event: Event,
    onClick: () -> Unit
) {
    val singleDayEvent = event.startDate == event.endDate

    val timeText = if (singleDayEvent) {
        "${event.startTime} -> ${event.endTime}"
    } else {
        "${event.startTime} - ${event.endTime}"
    }

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                SubcomposeAsyncImage(
                    model = event.image,
                    contentDescription = "Event image",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp),
                    loading = {
                        Image(
                            painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                            contentDescription = "Loading Image",
                            modifier = Modifier
                                .padding(8.dp),
                            contentScale = ContentScale.Crop,
                            alpha = 0.2f
                        )
                    },
                    error = {
                        Image(
                            painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                            contentDescription = "Error Image",
                            modifier = Modifier
                                .padding(8.dp),
                            contentScale = ContentScale.Crop,
                            alpha = 0.2f
                        )
                    }
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(0.dp, 8.dp, 16.dp, 8.dp)
                    .fillMaxHeight()

            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                if (singleDayEvent) {

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                } else {

                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(
                            text = "Until ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Text(
                            text = event.endTime,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        Text(
                            text = event.endDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }


                }

            }
        }
    }
}
