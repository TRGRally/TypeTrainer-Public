package com.example.typetrainer.data.models

data class EventRecommendation(
    val id: String = "",
    val form: String = "",
    val name: String = "",
    val image: String = "",
    val level: String = "",
    val types: List<String> = emptyList(),
    val counters: Map<String, Double> = emptyMap(),
    val recommendedTrainersMin: Int = 0,
    val recommendedTrainersMax: Int = 0,
)
