package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.typetrainer.ui.components.SummaryDonut
import com.example.typetrainer.util.Constants
import com.example.typetrainer.util.shareSheetManager
import com.example.typetrainer.viewmodels.SharedDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultsScreen(navController: NavHostController, viewModel: SharedDataViewModel) {

    //this screen shows the donut element and the number of questions the user got right that game.
    //they can also share their result here


    val correctAnswers by viewModel.correctAnswersCount.collectAsState()
    val totalQuestions = viewModel.totalQuestions

    Log.d("QuizResultsScreen", "Correct Answers: $correctAnswers")
    Log.d("QuizResultsScreen", "Total Questions: $totalQuestions")

    val message = when (correctAnswers) {
        totalQuestions -> "Perfect score!"
        0 -> "Uh oh..."
        else -> "You got $correctAnswers out of $totalQuestions correct${if (correctAnswers > totalQuestions / 2) "!" else "."} \n"
    }

    val context = LocalContext.current
    val shareSheetText = "\uD83C\uDF89 I just scored $correctAnswers out of $totalQuestions on the Type Trainer quiz! \n Can you beat my score? 🤔 \n\n Play now at: ${Constants.APP_LINK}"

    val quitQuiz by viewModel.quitQuiz.collectAsState()

    var showQuitDialog by remember { mutableStateOf(false) }




    LaunchedEffect(quitQuiz) {
        if (quitQuiz) {
            withContext(Dispatchers.Main) {
                navController.navigate("main") {
                    Log.d("QuizResultsScreen", "Quitting the quiz through dialog")
                    popUpTo("main") { inclusive = false }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Summary"
                    )
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp))
            }
            item {
                SummaryDonut(correctAnswers, totalQuestions)
            }

            item {
                Button(
                    onClick = {
                        navController.navigate("quiz_intro")
                    },
                    modifier = Modifier.fillMaxWidth(0.5f)

                ) {
                    Text("Play again")
                }
            }

            item {
                ElevatedButton(
                    onClick = { shareSheetManager(context, shareSheetText) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                        Text(text = "Share")
                    }
                }
            }

            item {
                ElevatedButton(
                    onClick = {
                        viewModel.quitQuiz()
                    },
                    Modifier.fillMaxWidth(0.5f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                        Text("Close Quiz")
                    }
                }
            }

        }
    }

    //dialog to trap back gesture
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = {
                Text("Leave quiz?")
            },
            text = {
                Text("Your progress will be lost.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    viewModel.quitQuiz()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    BackHandler {
        showQuitDialog = true
    }

}
