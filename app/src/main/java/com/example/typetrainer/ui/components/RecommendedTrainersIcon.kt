package com.example.typetrainer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendedTrainersIcon(
    recommendedTrainersMin: Int,
    recommendedTrainersMax: Int,
    modifier: Modifier = Modifier
) {
    val text = if (recommendedTrainersMin == recommendedTrainersMax) {
        "SOLO"
    } else {
        "$recommendedTrainersMin-$recommendedTrainersMax"
    }
    val icon = if (recommendedTrainersMin == recommendedTrainersMax) {
        Icons.Filled.SelfImprovement
    } else {
        Icons.Filled.Person
    }
    val style = if (recommendedTrainersMin == recommendedTrainersMax) {
        FontStyle.Normal
    } else {
        FontStyle.Normal
    }


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(
            PaddingValues(
                start = 4.dp,
                end = 4.dp,
                top = 2.dp,
                bottom = 2.dp
            )
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = style,
//            modifier = Modifier.alpha(0.75f)
        )
        Icon(
            imageVector = icon,
            contentDescription = "Recommended Trainers",
            modifier = Modifier
                .height(14.dp)
                .width(14.dp)
//                .alpha(0.75f)
        )
    }


}