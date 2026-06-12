package com.example.typetrainer.data.models
import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

//this is utterly crazy


object MapOrEmptyListSerializer : KSerializer<Map<String, PokemonMove>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MapOrEmptyList") {
        element<Map<String, PokemonMove>>("mapOrEmptyList")
    }

    override fun deserialize(decoder: Decoder): Map<String, PokemonMove> {
        val input = decoder as JsonDecoder
        val jsonElement = input.decodeJsonElement()

        return when (jsonElement) {
            is JsonObject -> input.json.decodeFromJsonElement(MapSerializer(String.serializer(), PokemonMove.serializer()), jsonElement)
            is JsonArray -> emptyMap() // Handle the case where the JSON element is an empty array
            else -> throw IllegalStateException("Unexpected JSON type: ${jsonElement::class}")
        }
    }

    override fun serialize(encoder: Encoder, value: Map<String, PokemonMove>) {
        throw UnsupportedOperationException("Serialization is not supported")
    }
}

object MegaEvolutionMapOrEmptyListSerializer : KSerializer<Map<String, MegaEvolution>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MegaEvolutionMapOrEmptyList") {
        element<Map<String, MegaEvolution>>("mapOrEmptyList")
    }

    override fun deserialize(decoder: Decoder): Map<String, MegaEvolution> {
        val input = decoder as JsonDecoder
        val jsonElement = input.decodeJsonElement()

        return when (jsonElement) {
            is JsonObject -> input.json.decodeFromJsonElement(MapSerializer(String.serializer(), MegaEvolution.serializer()), jsonElement)
            is JsonArray -> emptyMap() // Handle the case where the JSON element is an empty array
            else -> throw IllegalStateException("Unexpected JSON type: ${jsonElement::class}")
        }
    }

    override fun serialize(encoder: Encoder, value: Map<String, MegaEvolution>) {
        throw UnsupportedOperationException("Serialization is not supported")
    }
}

object RegionFormsMapOrEmptyListSerializer : KSerializer<Map<String, RegionPokemon>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RegionFormsMapOrEmptyList") {
        element<Map<String, RegionPokemon>>("mapOrEmptyList")
    }

    override fun deserialize(decoder: Decoder): Map<String, RegionPokemon> {
        val input = decoder as JsonDecoder
        val jsonElement = input.decodeJsonElement()

        return when (jsonElement) {
            is JsonObject -> input.json.decodeFromJsonElement(MapSerializer(String.serializer(), RegionPokemon.serializer()), jsonElement)
            is JsonArray -> emptyMap() // Handle the case where the JSON element is an empty array
            else -> throw IllegalStateException("Unexpected JSON type: ${jsonElement::class}")
        }
    }

    override fun serialize(encoder: Encoder, value: Map<String, RegionPokemon>) {
        throw UnsupportedOperationException("Serialization is not supported")
    }
}

@Serializable
data class PokemonResponse(
    val id: String,
    val formId: String,
    val dexNr: Int,
    val generation: Int,
    val names: Names,
    val stats: PokemonStats?,
    val primaryType: Type,
    val secondaryType: Type?,
    val pokemonClass: String?,
    @Serializable(with = MapOrEmptyListSerializer::class) val quickMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val cinematicMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val eliteQuickMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val eliteCinematicMoves: Map<String, PokemonMove> = emptyMap(),
    val assets: Assets?,
    val assetForms: List<AssetForm> = emptyList(),
    @Serializable(with = RegionFormsMapOrEmptyListSerializer::class) val regionForms: Map<String, RegionPokemon> = emptyMap(),
    val evolutions: List<Evolution> = emptyList(),
    val hasMegaEvolution: Boolean,
    @Serializable(with = MegaEvolutionMapOrEmptyListSerializer::class) val megaEvolutions: Map<String, MegaEvolution> = emptyMap()
)

@Serializable
data class Names(
    val English: String,
    val German: String,
    val French: String? = null,
    val Italian: String? = null,
    val Japanese: String? = null,
    val Korean: String? = null,
    val Spanish: String? = null
)

@Serializable
data class PokemonStats(
    val stamina: Int,
    val attack: Int,
    val defense: Int
)

@Serializable
data class Type(
    val type: String,
    val names: Names
)

@Serializable
data class PokemonMove(
    val id: String,
    val power: Double,
    val energy: Double,
    val durationMs: Double,
    val type: Type,
    val names: Names,
    val combat: Combat? = null
)

@Serializable
data class Combat(
    val energy: Double,
    val power: Double,
    val turns: Double,
    val buffs: Buffs? = null
)

@Serializable
data class Buffs(
    val activationChance: Int,
    val attackerAttackStatsChange: Int? = null,
    val attackerDefenseStatsChange: Int? = null,
    val targetAttackStatsChange: Int? = null,
    val targetDefenseStatsChange: Int? = null
)

@Serializable
data class Assets(
    val image: String,
    val shinyImage: String
)

@Serializable
data class AssetForm(
    val image: String,
    val shinyImage: String,
    val form: String? = null,
    val costume: String? = null,
    val isFemale: Boolean
)

@Serializable
data class RegionPokemon(
    val id: String,
    val formId: String,
    val dexNr: Int,
    val generation: Int,
    val names: Names,
    val stats: PokemonStats?,
    val primaryType: Type,
    val secondaryType: Type?,
    val pokemonClass: String?,
    @Serializable(with = MapOrEmptyListSerializer::class) val quickMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val cinematicMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val eliteQuickMoves: Map<String, PokemonMove> = emptyMap(),
    @Serializable(with = MapOrEmptyListSerializer::class) val eliteCinematicMoves: Map<String, PokemonMove> = emptyMap(),
    val assets: Assets?,
    @Serializable(with = RegionFormsMapOrEmptyListSerializer::class) val regionForms: Map<String, RegionPokemon> = emptyMap(),
    val evolutions: List<Evolution> = emptyList(),
    val hasMegaEvolution: Boolean,
    @Serializable(with = MegaEvolutionMapOrEmptyListSerializer::class) val megaEvolutions: Map<String, MegaEvolution> = emptyMap()
)
@Serializable
data class Evolution(
    val id: String,
    val formId: String,
    val candies: Int,
    val item: Item? = null,
    val quests: List<Quest>
)

@Serializable
data class Item(
    val id: String,
    val names: Names
)

@Serializable
data class Quest(
    val id: String,
    val type: String,
    val names: Names
)

@Serializable
data class MegaEvolution(
    val id: String,
    val names: Names,
    val stats: PokemonStats,
    val primaryType: Type,
    val secondaryType: Type?,
    val assets: Assets
)

@Serializable
data class PokemonType(
    val type: String,
    val names: Names,
    val doubleDamageFrom: List<String>,
    val halfDamageFrom: List<String>,
    val noDamageFrom: List<String>,
    val weatherBoost: WeatherBoost?
)

@Serializable
data class WeatherBoost(
    val id: String,
    val names: Names,
    val assetName: String
)


//hashes.json
/*
{
    "sha512": {
        "raidboss.json": "64715cab77ffacb5005cb9b9911349f453338de3ccc0331cc8bd93a00a4a20c56a502b26f27a2810e661ff578db61711ede708f172b1461dba5fa767b8b46303",
        "pokedex.json": "9a1abd3f33dd5fef0fc62e3383d27a3edcf3be613c555d0b1c853248054c1730dccb40e171314b8161dcbd4e75bb12a1e259106c390a82fd1244d47628ed75f0",
        "quests.json": "b25b294cb4deb69ea00a4c3cf3113904801b6015e5956bd019a8570b1fe1d6040e944ef3cdee16d0a46503ca6e659a25f21cf9ceddc13f352a3c98138c15d6af"
    }
}
*/

@Serializable
data class Hashes(
    val sha512: Map<String, String>
)



