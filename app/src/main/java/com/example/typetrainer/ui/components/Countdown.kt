package com.example.typetrainer.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun Countdown(remainingTime: Int, timeLimit: Int = 10) {
    val sanitisedTime = if (remainingTime < 0) 0 else remainingTime

    val targetProgress = sanitisedTime.toFloat() / timeLimit

    //this is technically state but its only a visual transition and not used for calculation
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(48.dp),
            strokeWidth = 6.dp,
            color = MaterialTheme.colorScheme.primary,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = "$sanitisedTime",
            fontWeight = FontWeight.Bold,
        )
    }
}