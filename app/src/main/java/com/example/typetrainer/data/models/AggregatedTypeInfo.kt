package com.example.typetrainer.data.models

data class AggregatedTypeInfo(
    val type: String,
    val doubleDamageFrom: List<String>,
    val halfDamageFrom: List<String>,
    val noDamageFrom: List<String>
)