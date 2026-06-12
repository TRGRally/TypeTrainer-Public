package com.example.typetrainer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun QuestionBars(totalQuestions: Int = 5, currentQuestion: Int = 3) {
    //these show the current question the user is in through progress elements in a row

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp, 4.dp)
    ) {
        for (i in 1..totalQuestions) {
            val color = if (i == currentQuestion) {
                //current question
                MaterialTheme.colorScheme.primary
            } else if (i < currentQuestion) {
                //completed question
                MaterialTheme.colorScheme.primaryContainer

            } else {
                //upcoming question
                Color.Gray
            }

            LinearProgressIndicator(
                progress = if (i <= currentQuestion) 1f else 0f,
                strokeCap = StrokeCap.Round,
                color = color,
                modifier = Modifier
                    .height(5.dp)
                    .weight(1f)
            )

        }
    }
}