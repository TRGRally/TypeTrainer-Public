package com.example.typetrainer.data.models

import kotlinx.serialization.Serializable

//ChatGPT goated for this

// Root Structure
@Serializable
data class PokemonGoEvents(
    val events: List<Event>
)

@Serializable
data class Event(
    val eventID: String,           // The unique ID of the event
    val name: String,              // The name of the event
    val eventType: String,         // The type/category of the event (e.g., "city-safari", "research", "event")
    val heading: String,           // The heading/title category of the event
    val link: String,              // URL link to the event's page
    val image: String,             // Image URL representing the event
    val start8601: String,         // Start date and time of the event in ISO 8601 format
    val startDate: String,         // Start date of the event (human readable)
    val startTime: String,         // Start time of the event (human readable)
    val end8601: String,           // End date and time of the event in ISO 8601 format
    val endDate: String,           // End date of the event (human readable)
    val endTime: String,           // End time of the event (human readable)
    val extraData: ExtraData       // Additional data related to the event
)

// ExtraData Structure
@Serializable
data class ExtraData(
    val generic: GenericData? = null,         // Generic data applicable to multiple event types
    val spotlight: SpotlightData? = null,     // Data specific to Spotlight Hour events
    val raidbattles: RaidBattlesData? = null, // Data specific to raid battles
    val communityday: CommunityDayData? = null // Data specific to Community Day events
)

// GenericData Structure
@Serializable
data class GenericData(
    val hasSpawns: Boolean,               // Indicates if the event has Pokémon spawns
    val hasFieldResearchTasks: Boolean    // Indicates if the event has field research tasks
)

// SpotlightData Structure
@Serializable
data class SpotlightData(
    val name: String,                     // The Pokémon featured in the Spotlight Hour
    val canBeShiny: Boolean,              // Indicates if the Pokémon can be shiny
    val image: String,                    // Image URL of the featured Pokémon
    val bonus: String,                    // Bonus active during the Spotlight Hour
    val list: List<SpotlightPokemon>      // List of Pokémon in the Spotlight Hour
)

@Serializable
data class SpotlightPokemon(
    val name: String,                     // Name of the Pokémon
    val canBeShiny: Boolean,              // Indicates if this Pokémon can be shiny
    val image: String                     // Image URL of the Pokémon
)

// RaidBattlesData Structure
@Serializable
data class RaidBattlesData(
    val bosses: List<RaidBoss>,           // List of raid bosses
    val shinies: List<ShinyPokemon>       // List of shiny Pokémon available in raids
)

@Serializable
data class RaidBoss(
    val name: String,                     // Name of the raid boss
    val image: String,                    // Image URL of the raid boss
    val canBeShiny: Boolean               // Indicates if the raid boss can be shiny
)

@Serializable
data class ShinyPokemon(
    val name: String,                     // Name of the shiny Pokémon
    val image: String                     // Image URL of the shiny Pokémon
)

// CommunityDayData Structure
@Serializable
data class CommunityDayData(
    val spawns: List<SpawnPokemon>,           // List of Pokémon spawns during the Community Day
    val bonuses: List<CommunityDayBonus>,     // List of bonuses during Community Day
    val bonusDisclaimers: List<String>,       // List of disclaimers related to bonuses
    val shinies: List<ShinyPokemon>,          // List of shiny Pokémon available during the event
    val specialresearch: List<SpecialResearch> // List of special research tasks available
)

@Serializable
data class SpawnPokemon(
    val name: String,                     // Name of the Pokémon spawning
    val image: String                     // Image URL of the Pokémon spawning
)

@Serializable
data class CommunityDayBonus(
    val text: String,                     // Description of the bonus
    val image: String                     // Image URL representing the bonus
)

@Serializable
data class SpecialResearch(
    val name: String,                     // Name/Title of the special research
    val step: Int,                        // The step number of the research
    val tasks: List<ResearchTask>,        // List of tasks in this step
    val rewards: List<ResearchReward>     // List of rewards for completing the step
)

@Serializable
data class ResearchTask(
    val text: String,                     // Description of the research task
    val reward: ResearchReward            // Reward for completing the task
)

@Serializable
data class ResearchReward(
    val text: String,                     // Description of the reward
    val image: String                     // Image URL of the reward
)
