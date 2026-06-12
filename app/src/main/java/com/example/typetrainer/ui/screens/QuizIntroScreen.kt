package com.example.typetrainer.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.typetrainer.R
import com.example.typetrainer.viewmodels.SharedDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun QuizIntroScreen(
    navController: NavHostController,
    viewModel: SharedDataViewModel
) {

    //this is the short animation screen that gets the user prepared for the quiz, navigates straight to question 1.

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.resetQuiz()
        visible = true
        delay(2500)
        visible = false
        delay(500) // 3 seconds delay total
        //yet another main thread navigation
        withContext(Dispatchers.Main) {
            Log.d("QuizIntroScreen", "Navigating to quiz_question/0")
            navController.navigate("quiz_question/0")
        }
    }

    Scaffold(
        content = {paddingValues ->
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Image(
                        painter = painterResource(id = R.drawable.tt_foreground),
                        contentDescription = "Type Icon",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(250.dp)
                            .padding(16.dp)
                    )
                    Text(
                        text = "Are you ready?!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}