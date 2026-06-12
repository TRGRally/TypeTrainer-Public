package com.example.typetrainer.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.typetrainer.viewmodels.SharedDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Question(
    text: AnnotatedString,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    types: List<String>?,
    viewModel: SharedDataViewModel
) {
    Log.d("QuestionEntity", "Options: $options")
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp)
    ) {
        val selectedOption by viewModel.selectedOption.collectAsState()
        TypeIcon(
            type= types?.get(0) ?: "unknown",
            hasText = false,
            hasContainer = false,
            iconWidth = 80.dp,
            modifier = Modifier.padding(32.dp)
        )
        Text(
            text = types?.joinToString(", ") ?: "Unknown",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 0.dp, bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .width(5.dp)
                    .height(64.dp)
            ) {
                Spacer(modifier = Modifier.padding(4.dp))
            }

            Text(
                text = text,
                fontWeight = FontWeight.Normal,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            options.forEach { option ->
                val colors = if (selectedOption == option) {
                    CardDefaults.cardColors() // changes to default card look for elevation
                } else {
                    CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
                val border = if (selectedOption == option) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    CardDefaults.outlinedCardBorder()
                }
                OutlinedCard(
                    onClick = {
                        onOptionSelected(option)
                    },
                    //conditional colours depending on if selected
                    colors = colors,
                    border = border,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, top = 4.dp)
                        .weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        TypeIcon(
                            type = option,
                            hasText = false,
                            hasContainer = false,
                            iconWidth = 50.dp
                        )
                        Text(
                            text = option,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}