package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_table")
data class TypeEntity(
    @PrimaryKey val type: String
)