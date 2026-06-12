package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "pokemon_table", primaryKeys = ["id", "formId"])
data class PokemonEntity(
    val id: String,
    val formId: String,
    val dexNr: Int,
    val generation: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?,
    val image: String?,
    val quickMoveIds: List<String> = emptyList(),
    val cinematicMoveIds: List<String> = emptyList(),
    val eliteQuickMoveIds: List<String> = emptyList(),
    val eliteCinematicMoveIds: List<String> = emptyList()
)
