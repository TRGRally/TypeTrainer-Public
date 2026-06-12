package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_table")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionText: String,
    val questionType: String,  // e.g., "super effective", "resistant to"
    val timeAllowed: Int  // in seconds
)