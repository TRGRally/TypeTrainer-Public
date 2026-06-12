package com.example.typetrainer.provider

import android.net.Uri
import android.provider.BaseColumns

object PokemonTypeContract {
    const val AUTHORITY = "com.example.typetrainer.provider"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

    object PokemonTypes : BaseColumns {
        const val PATH_TYPES = "types"
        val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TYPES)
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_TYPES"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_TYPES"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DOUBLE_DAMAGE_FROM = "doubleDamageFrom"
        const val COLUMN_HALF_DAMAGE_FROM = "halfDamageFrom"
        const val COLUMN_NO_DAMAGE_FROM = "noDamageFrom"
        const val COLUMN_WEATHER_BOOST = "weatherBoost"
    }
}