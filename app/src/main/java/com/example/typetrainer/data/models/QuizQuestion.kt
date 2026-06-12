package com.example.typetrainer.data.models

import androidx.compose.ui.text.AnnotatedString

data class QuizQuestion(
    val types: List<String> = listOf("Normal"),
    val questionText: AnnotatedString,
    val options: List<String> = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
    val timeLimit: Int,
    val correctAnswer: String = "Option 1",
    //answer params
    val selectedOption: String? = null,
    val timeLeftOnSubmit: Int = 0
)