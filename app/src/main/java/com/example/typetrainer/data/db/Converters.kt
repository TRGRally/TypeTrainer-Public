package com.example.typetrainer.data.db

import androidx.room.TypeConverter
import com.example.typetrainer.data.models.Buffs
import com.example.typetrainer.data.models.Combat
import com.example.typetrainer.data.models.Names
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                Json.decodeFromString(value)
            } catch (e: Exception) {
                // Handle cases where the input is not in JSON array format
                value.split(",").map { it.trim() }
            }
        }
    }
    @TypeConverter
    fun fromNames(value: Names): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toNames(value: String): Names {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromCombat(value: Combat): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toCombat(value: String): Combat {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromBuffs(value: Buffs): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toBuffs(value: String): Buffs {
        return Json.decodeFromString(value)
    }
}