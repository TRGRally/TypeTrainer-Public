package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.typetrainer.ui.components.SummaryDonut
import com.example.typetrainer.ui.components.TypeIconClickable
import com.example.typetrainer.util.Constants
import com.example.typetrainer.util.shareSheetManager
import com.example.typetrainer.viewmodels.LearnViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LearnScreen(
    navController: NavController,
    viewModel: LearnViewModel,
) {

    //this is the screen that shows the users quiz accuracy, and they can navigate into a quiz from here.

    val allQuizzes by viewModel.allQuizzes.observeAsState(initial = emptyList())
    //quiz id with highest timestamp
    val latestQuiz = allQuizzes.maxByOrNull { it.timestamp }

    Log.d("LearnScreen", "Quiz Results: $allQuizzes")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val typeAccuracyList by viewModel.typeAccuracy.observeAsState(initial = emptyList())

    val totalQuestionsAsked = typeAccuracyList.sumOf { it.total }
    val totalQuestionsCorrect = typeAccuracyList.sumOf { it.correct }


    val numberAnswered by viewModel.numberAttempted.observeAsState(initial = 0)
    val numberCorrect by viewModel.numberCorrect.observeAsState(initial = 0)

    //emptyState is true if numberAnswered is 0
    val emptyState = numberAnswered == 0

    val averageText = if (emptyState) {
        "no stats yet bro"
    } else {
        "Your average"
    }

    val accuracyPercent = if (numberAnswered > 0) {
        (numberCorrect.toFloat() / numberAnswered * 100).toInt()
    } else {
        0
    }

    val onTypeClicked: (String) -> Unit = { type ->
        Log.d("LearnScreen", "Type clicked: $type")
        navController.navigate("type/$type") {
            launchSingleTop = true
        }
    }

    //sharing
    val bestType = viewModel.bestType.observeAsState(initial = "Unknown").value
    val shareEmoji = viewModel.getEmojiFromAccuracy(numberAnswered, numberCorrect)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Learn Types",
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            shareSheetManager(
                                context = navController.context,
                                message = "I've been playing Type Trainer!\n$bestType is my best type - with ${accuracyPercent}% accuracy $shareEmoji \nCan you beat my score? 🤔 \n\uD83C\uDFAE Play now at: ${Constants.APP_LINK}"
                            )
                            Log.d("LearnScreen", "Share button clicked")
                        },
                        enabled = !emptyState
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                scrollBehavior = scrollBehavior
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

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
            ) {
                item {

                    Text(
                        text = averageText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp, 4.dp, 12.dp, 4.dp)
                    )

                    SummaryDonut(
                        correctAnswers = numberCorrect,
                        totalAnswers = numberAnswered,
                        size = 150.dp,
                        showAsPercentage = true,
                        padding = PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)
                    )

                }

                item {
                    Spacer(modifier = Modifier.padding(4.dp))
                }



                item {
                    Button(
                        onClick = {
                            navController.navigate("quiz_intro") {
                                launchSingleTop = true
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("Quiz me")
                    }
                }


                item {
                    if (emptyState) {
                        Text("Play a quiz to track your stats!")
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
                                ) {
                                    Text(
                                        text = "Your accuracy",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {

                                        if (typeAccuracyList.isEmpty()) {
                                            Text(
                                                text = "You've never got a question right... \uD83D\uDE2D",
                                                style = MaterialTheme.typography.bodyMedium,

                                                modifier = Modifier.padding(8.dp)
                                            )
                                        } else {
                                            typeAccuracyList.forEach { type ->
                                                Card {
                                                    TypeIconClickable(
                                                        type = type.type,
                                                        iconWidth = 36.dp,
                                                        hasContainer = true,
                                                        hasText = true,
                                                        modifier = Modifier
                                                            .widthIn(min = 56.dp)
                                                            .heightIn(min = 76.dp),
                                                        onClick = { onTypeClicked(type.type) },
                                                        textOverride = "${type.correct}/${type.total}"
                                                    )

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Text(
                        text = "Correct: $numberCorrect / $numberAnswered",
                    )
                    Text(
                        text = "Latest quiz: $latestQuiz",
                    )
                }
            }
        }
    }
}