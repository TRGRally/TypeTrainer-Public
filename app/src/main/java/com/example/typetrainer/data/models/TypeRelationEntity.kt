package com.example.typetrainer.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "type_relation_table",
    foreignKeys = [
        ForeignKey(
            entity = TypeEntity::class,
            parentColumns = ["type"],
            childColumns = ["type"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TypeEntity::class,
            parentColumns = ["type"],
            childColumns = ["relatedType"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["type"]), Index(value = ["relatedType"])]
)
data class TypeRelationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val relatedType: String,
    val relationType: String  // e.g., "doubleDamageFrom", "halfDamageFrom", "noDamageFrom"
)