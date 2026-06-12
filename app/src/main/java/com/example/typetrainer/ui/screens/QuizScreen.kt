package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.typetrainer.ui.components.Countdown
import com.example.typetrainer.ui.components.Question
import com.example.typetrainer.ui.components.QuestionBars
import com.example.typetrainer.viewmodels.SharedDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionScreen(
    navController: NavController,
    questionIndex: Int,
    viewModel: SharedDataViewModel
) {
    val question = viewModel.getCurrentQuestion()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()

    val remainingTime by viewModel.remainingTime.collectAsState()

    val quitQuiz by viewModel.quitQuiz.collectAsState()

    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()

    val totalQuestions = viewModel.totalQuestions

    var showQuitDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentQuestionIndex) {
        viewModel.startTimer(question.timeLimit)
    }

    LaunchedEffect(isQuizFinished) {
        Log.e("QuizQuestionScreen", "QuizEntity finished: $isQuizFinished")
        if (isQuizFinished) {
            viewModel.submitQuizResult()
            //sometimes there is an error when navigating, caused by doing so not on the main thread. this may fix.
            withContext(Dispatchers.Main) {
                Log.d("QuizQuestionScreen", "Navigating to quiz_results as quiz is finished")
                navController.navigate("quiz_results")
            }
        }
    }

    LaunchedEffect(quitQuiz) {
        Log.e("QuizQuestionScreen", "Quit quiz: $quitQuiz")
        if (quitQuiz) {
            //same here
            withContext(Dispatchers.Main) {
                navController.navigate("main") {
                    Log.d("QuizQuestionScreen", "Quitting the quiz through dialog")
                    popUpTo("main") { inclusive = false }
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    if (!isQuizFinished) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                        )
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    showQuitDialog = true
                                }
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                QuestionBars(
                    currentQuestion = currentQuestionIndex + 1,
                    totalQuestions = totalQuestions,
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 16.dp, top = 8.dp, bottom = 0.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Time remaining",
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Countdown(remainingTime, question.timeLimit)
                }
                Question(
                    text = question.questionText,
                    options = question.options,
                    types = question.types,
                    onOptionSelected = { selectedOption ->
                        viewModel.selectOption(selectedOption)
                    },
                    viewModel = viewModel
                )
                Button(
                    onClick = {
                        viewModel.submitAnswer()
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (remainingTime > 0) {
                            if (selectedOption != null) {
                                Icon(
                                    imageVector = Icons.Default.MoreTime,
                                    contentDescription = "Submit early"
                                )
                                Text("Submit early")
                            } else {
                                Text("Select an option")
                            }
                        } else {
                            Text("Time's up!")
                        }
                    }
                }
            }
        }

        //dialog to trap the back gesture
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
}






