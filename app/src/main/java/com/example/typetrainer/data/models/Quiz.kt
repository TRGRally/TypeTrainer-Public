package com.example.typetrainer.data.models

data class Quiz(
    val id: Long,
    val timestamp: Long,
    val questions: List<QuizQuestion>,
)
