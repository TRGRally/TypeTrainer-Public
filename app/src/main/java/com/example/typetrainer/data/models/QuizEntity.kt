package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_table")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long
)
