package com.example.typetrainer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SummaryDonut(
    correctAnswers: Int,
    totalAnswers: Int,
    size: Dp = 200.dp,
    showAsPercentage: Boolean = false,
    padding: PaddingValues = PaddingValues(16.dp)
) {

    val targetProgress = if (totalAnswers != 0) {
        correctAnswers.toFloat() / totalAnswers
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    val text = if (showAsPercentage) {
        "${(animatedProgress * 100).toInt()}%"
    } else {
        "$correctAnswers/$totalAnswers"
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(padding)
        ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(size),
            strokeWidth = 14.dp,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}