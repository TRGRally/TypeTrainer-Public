package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_hash_table")
data class LocalHashEntity (
    @PrimaryKey(autoGenerate = false) val filename: String = "",
    val hash: String = "",
    val timestamp: Long = 0
)