package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_answer_table",
    foreignKeys = [
        ForeignKey(
            entity = QuizQuestionJoinEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["quizQuestionId"])]
)
data class UserAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizQuestionId: Long,
    val timeLeftOnSubmit: Int,
    val userAnswer: String?,
    val isCorrect: Boolean  // Calculated when the answer is stored
)