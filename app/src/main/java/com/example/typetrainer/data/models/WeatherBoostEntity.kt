package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_boost_table",
    foreignKeys = [
        ForeignKey(
            entity = TypeEntity::class,
            parentColumns = ["type"],
            childColumns = ["type"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["type"])]
)
data class WeatherBoostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val weatherBoost: String
)