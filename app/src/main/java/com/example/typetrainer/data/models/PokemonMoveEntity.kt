package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moves_table")
data class MoveEntity(
    @PrimaryKey val id: String,
    val name: String,
    val power: Double,
    val energy: Double,
    val durationMs: Double,
    val type: String,
    val combat: Combat?
)