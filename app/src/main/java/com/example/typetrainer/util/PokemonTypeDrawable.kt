package com.example.typetrainer.util

import com.example.typetrainer.R

fun PokemonTypeDrawable(typeId: String): Int {
    return when (typeId) {
        "Bug" -> R.drawable.bug
        "Dark" -> R.drawable.dark
        "Dragon" -> R.drawable.dragon
        "Electric" -> R.drawable.electric
        "Fairy" -> R.drawable.fairy
        "Fighting" -> R.drawable.fighting
        "Fire" -> R.drawable.fire
        "Flying" -> R.drawable.flying
        "Ghost" -> R.drawable.ghost
        "Grass" -> R.drawable.grass
        "Ground" -> R.drawable.ground
        "Ice" -> R.drawable.ice
        "Normal" -> R.drawable.normal
        "Poison" -> R.drawable.poison
        "Psychic" -> R.drawable.psychic
        "Rock" -> R.drawable.rock
        "Steel" -> R.drawable.steel
        "Water" -> R.drawable.water
        else -> R.drawable.unknown
    }
}