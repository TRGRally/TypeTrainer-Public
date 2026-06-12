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
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.typetrainer.data.models.Event
import com.example.typetrainer.util.Constants

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    val singleDayEvent = event.startDate == event.endDate
    val dateText = if (singleDayEvent) {
        event.startDate
    } else {
        "${event.startDate} until ${event.endDate}"
    }
    val dateIcon = if(singleDayEvent) Icons.Default.Event else Icons.Default.DateRange

    val timeText = if (singleDayEvent) {
        "${event.startTime} -> ${event.endTime}"
    } else {
        "${event.startTime} - ${event.endTime}"
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small
        ) {
            SubcomposeAsyncImage(
                model = event.image,
                contentDescription = "Event image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth(),
                loading = {
                    Image(
                        painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
                        contentDescription = "Loading Image",
                        modifier = Modifier
                            .padding(8.dp)
                            .width(72.dp)
                            .height(72.dp),
                        contentScale = ContentScale.Crop,
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
                        contentScale = ContentScale.Crop,
                        alpha = 0.2f
                    )
                }
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            if (singleDayEvent) {

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.startTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Text(
                        text = event.startDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
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
