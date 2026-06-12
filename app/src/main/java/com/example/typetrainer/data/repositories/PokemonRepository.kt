package com.example.typetrainer.data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.typetrainer.data.db.LocalHashDao
import com.example.typetrainer.data.db.MoveDao
import com.example.typetrainer.data.db.PokemonDao
import com.example.typetrainer.data.db.TypeDao
import com.example.typetrainer.data.models.CommunityDayBonus
import com.example.typetrainer.data.models.CommunityDayData
import com.example.typetrainer.data.models.Event
import com.example.typetrainer.data.models.EventRecommendation
import com.example.typetrainer.data.models.ExtraData
import com.example.typetrainer.data.models.GenericData
import com.example.typetrainer.data.models.Hashes
import com.example.typetrainer.data.models.LocalHashEntity
import com.example.typetrainer.data.models.MoveEntity
import com.example.typetrainer.data.models.PokemonEntity
import com.example.typetrainer.data.models.PokemonMove
import com.example.typetrainer.data.models.PokemonResponse
import com.example.typetrainer.data.models.RaidBattlesData
import com.example.typetrainer.data.models.RaidBoss
import com.example.typetrainer.data.models.ResearchReward
import com.example.typetrainer.data.models.ResearchTask
import com.example.typetrainer.data.models.ShinyPokemon
import com.example.typetrainer.data.models.SpawnPokemon
import com.example.typetrainer.data.models.SpecialResearch
import com.example.typetrainer.data.models.SpotlightData
import com.example.typetrainer.data.models.SpotlightPokemon
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.data.models.TypeRelationEntity
import com.example.typetrainer.util.isInternetConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Integer.parseInt
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.sql.Time
import java.util.Date
import javax.net.ssl.HttpsURLConnection
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

class PokemonRepository(
    private val context: Context,
    private val pokemonDao: PokemonDao,
    private val moveDao: MoveDao,
    private val typeDao: TypeDao,
    private val localHashDao: LocalHashDao
) {
    val allPokemon: LiveData<List<PokemonEntity>> = pokemonDao.getAllPokemon()
    val allMoves: LiveData<List<MoveEntity>> = moveDao.getAllMoves()
    val allTypes: LiveData<List<TypeEntity>> = typeDao.getAllTypes()
    val allLocalHashes: LiveData<List<LocalHashEntity>> = localHashDao.getAllLocalHashes()


    suspend fun getPokemonCount(): Int {
        return pokemonDao.getCount()
    }


    suspend fun getPokemonByIdAndForm(id: String, form: String): PokemonEntity? {
        return pokemonDao.getPokemonByIdAndForm(id, form)
    }

    suspend fun insert(pokemon: PokemonEntity) {
        pokemonDao.insertAll(listOf(pokemon))
    }

    suspend fun update(pokemon: PokemonEntity) {
        pokemonDao.updatePokemon(pokemon)
    }

    suspend fun delete(pokemon: PokemonEntity) {
        pokemonDao.deletePokemon(pokemon)
    }

    suspend fun insertAllMoves(moves: List<MoveEntity>) {
        moveDao.insertAllMoves(moves)
    }


    suspend fun searchPokemonByNameStartsWith(query: String, limit: Int): List<PokemonEntity> {
        return pokemonDao.searchPokemonByNameStartsWith(query, limit)
    }

    suspend fun searchPokemonByNameContains(query: String, limit: Int): List<PokemonEntity> {
        return pokemonDao.searchPokemonByNameContains(query, limit)
    }



    // Function to fetch all type relations
    private suspend fun getAllTypeRelations(): List<TypeRelationEntity> {
        return typeDao.getAllTypeRelations()
    }

    // Function to calculate type effectiveness
    suspend fun calculateTypeEffectiveness(attackingType: String, defendingTypes: List<String>): Double {
        val typeRelations = getAllTypeRelations()
        return calculateTypeEffectiveness(attackingType, defendingTypes, typeRelations)
    }

    // Helper function to calculate type effectiveness
    private fun calculateTypeEffectiveness(attackingType: String, defendingTypes: List<String>, typeRelations: List<TypeRelationEntity>): Double {
        var effectiveness = 1.0
        defendingTypes.forEach { defendingType ->
            typeRelations.filter { it.type == defendingType }.forEach { relation ->
                when (relation.relationType) {
                    "doubleDamageFrom" -> if (relation.relatedType == attackingType) effectiveness *= 2
                    "halfDamageFrom" -> if (relation.relatedType == attackingType) effectiveness *= 0.5
                    "noDamageFrom" -> if (relation.relatedType == attackingType) effectiveness *= 0
                }
            }
        }
        return effectiveness
    }

    private fun saveJsonFile(context: Context, jsonString: String, fileName: String) {

        val file = File(context.filesDir, fileName)

        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    suspend fun updateLocalJsonCache() {
        val internetAccess = isInternetConnected(context)
        if (internetAccess) {
            val url = "https://pokemon-go-api.github.io/pokemon-go-api/api/pokedex.json"
            val jsonString = withContext(Dispatchers.IO) { getJSONFromApi(url) }
            if (jsonString.isNotBlank()) {
                saveJsonFile(context, jsonString, "pokedex.json")
                Log.d("PokemonRepository", "Pokemon data saved successfully.")
            } else {
                Log.e("PokemonRepository", "Failed to fetch Pokemon data.")
            }
        } else {
            Log.e("PokemonRepository", "No internet connection.")
        }
    }

    suspend fun loadPokemonDataFromJson() {
        val timeMillis = measureTimeMillis {
            withContext(Dispatchers.IO) {
                
                //check if they can access the internet
                val internetAccess = isInternetConnected(context)

                var computeHash = false
                
                if(internetAccess) {
                    //check if pokedex.json has a hash in the database
                    val localHash: LocalHashEntity? = localHashDao.getLocalHashByFilename("pokedex.json")
                    //check if the hash is stale (1 hour ago)
                    if (localHash != null && System.currentTimeMillis() - localHash.timestamp < 1000 * 60 * 60) {
                        Log.d("PokemonRepository", "Local pokedex.json hash is still fresh.")
                        return@withContext
                    }

                    //if the hash is stale, fetch the hashes.json file
                    val hashUrl = "https://pokemon-go-api.github.io/pokemon-go-api/api/hashes.json"
                    val hashJsonString = getJSONFromApi(hashUrl)
                    val hashes: Hashes = Json.decodeFromString(hashJsonString)

                    //check if the pokedex.json hash is the same as the one in the hashes.json file
                    if (localHash != null && hashes.sha512["pokedex.json"] == localHash.hash) {
                        Log.d("PokemonRepository", "Local pokedex.json hash is still fresh.")
                        //update the timestamp
                        localHashDao.updateHashForFilename(
                            LocalHashEntity(
                                filename = "pokedex.json",
                                hash = localHash.hash,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        return@withContext
                    }


                    //get json from api
                    val url = "https://pokemon-go-api.github.io/pokemon-go-api/api/pokedex.json"
                    val jsonString = getJSONFromApi(url)
                    //save this to a json file
                    saveJsonFile(context, jsonString, "pokedex.json")
                    Log.d("PokemonRepository", "pokedex.json data saved successfully.")
                    
                    computeHash = true

                } else {
                    //cry about it
                    Log.e("PokemonRepository", "No internet connection.")
                }

                var file = File(context.filesDir, "pokedex.json")
                if (!file.exists()) {
                    Log.e("PokemonRepository", "No local JSON file found.")
                    //create the file from assets pokedex.json (first time)
                    val assetStream = context.assets.open("pokedex.json")
                    val assetString = InputStreamReader(assetStream).readText()
                    saveJsonFile(context, assetString, "pokedex.json")

                }

                val jsonStream = context.openFileInput("pokedex.json")
                val jsonString = InputStreamReader(jsonStream).readText()
                
                if (computeHash) { //compute the SHA-512 hash of the file content
                    val timeMillis = measureTimeMillis {
                        val messageDigest = MessageDigest.getInstance("SHA-512")
                        messageDigest.update(jsonString.toByteArray(Charsets.UTF_8))
                        val digest = messageDigest.digest()
                        val hash = BigInteger(1, digest).toString(16).padStart(128, '0')
                        
                        localHashDao.insert(
                            hashEntity = LocalHashEntity(
                                filename = "pokedex.json",
                                hash = hash,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    Log.d("PokemonRepository debug", "Hashing took $timeMillis ms")
                }
                
                //custom JSON config for leniency
                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }

                val pokemonList: List<PokemonResponse> = json.decodeFromString(jsonString)
                val moveEntities = mutableListOf<MoveEntity>()
                val pokemonEntities = mutableListOf<PokemonEntity>()

                for (pokemon in pokemonList) {
                    //Log.d("PokemonRepository", "Found Pokemon: ${pokemon.id}/${pokemon.formId}")

                    moveEntities.addAll(pokemon.quickMoves.values.map { it.toMoveEntity() })
                    moveEntities.addAll(pokemon.cinematicMoves.values.map { it.toMoveEntity() })
                    moveEntities.addAll(pokemon.eliteQuickMoves.values.map { it.toMoveEntity() })
                    moveEntities.addAll(pokemon.eliteCinematicMoves.values.map { it.toMoveEntity() })

                    //massive large serialisation to add pokemon
                    pokemonEntities.add(
                        PokemonEntity(
                            id = pokemon.id,
                            formId = pokemon.formId,
                            dexNr = pokemon.dexNr,
                            generation = pokemon.generation,
                            name = pokemon.names.English,
                            primaryType = pokemon.primaryType.names.English,
                            secondaryType = pokemon.secondaryType?.names?.English,
                            image = pokemon.assets?.image,
                            quickMoveIds = pokemon.quickMoves.keys.toList(),
                            cinematicMoveIds = pokemon.cinematicMoves.keys.toList(),
                            eliteQuickMoveIds = pokemon.eliteQuickMoves.keys.toList(),
                            eliteCinematicMoveIds = pokemon.eliteCinematicMoves.keys.toList()
                        )
                    )

                    //regional form
                    for ((_, regionForm) in pokemon.regionForms) {
                        //Log.d(
                        //    "PokemonRepository",
                        //    "Found Region Form: ${regionForm.id}/${regionForm.formId}"
                        //)


                        moveEntities.addAll(regionForm.quickMoves.values.map { it.toMoveEntity() })
                        moveEntities.addAll(regionForm.cinematicMoves.values.map { it.toMoveEntity() })
                        moveEntities.addAll(regionForm.eliteQuickMoves.values.map { it.toMoveEntity() })
                        moveEntities.addAll(regionForm.eliteCinematicMoves.values.map { it.toMoveEntity() })


                        pokemonEntities.add(
                            PokemonEntity(
                                id = regionForm.id,
                                formId = regionForm.formId,
                                dexNr = regionForm.dexNr,
                                generation = regionForm.generation,
                                name = regionForm.names.English,
                                primaryType = regionForm.primaryType.names.English,
                                secondaryType = regionForm.secondaryType?.names?.English,
                                image = regionForm.assets?.image,
                                quickMoveIds = regionForm.quickMoves.keys.toList(),
                                cinematicMoveIds = regionForm.cinematicMoves.keys.toList(),
                                eliteQuickMoveIds = regionForm.eliteQuickMoves.keys.toList(),
                                eliteCinematicMoveIds = regionForm.eliteCinematicMoves.keys.toList()
                            )
                        )
                    }

                    //mega evolutions
                    for ((megaId, megaEvolution) in pokemon.megaEvolutions) {
                        //Log.d("PokemonRepository", "Found Mega Evolution: ${pokemon.id}/${megaId}")

                       //uses data from the base pokemon where missing from the API
                        pokemonEntities.add(
                            PokemonEntity(
                                id = pokemon.id,
                                formId = megaId,
                                dexNr = pokemon.dexNr,
                                generation = pokemon.generation,
                                name = megaEvolution.names.English,
                                primaryType = megaEvolution.primaryType.names.English,
                                secondaryType = megaEvolution.secondaryType?.names?.English,
                                image = megaEvolution.assets.image,
                                quickMoveIds = pokemon.quickMoves.keys.toList(),
                                cinematicMoveIds = pokemon.cinematicMoves.keys.toList(),
                                eliteQuickMoveIds = pokemon.eliteQuickMoves.keys.toList(),
                                eliteCinematicMoveIds = pokemon.eliteCinematicMoves.keys.toList()
                            )
                        )
                    }
                }

                pokemonDao.insertAll(pokemonEntities)
                moveDao.insertAllMoves(moveEntities)
                Log.d("PokemonRepository", "Inserted ${pokemonEntities.size} Pokemon into the database.")
            }
        }
        Log.d("PokemonRepository debug", "Loading Pokemon data took $timeMillis ms")

    }

    suspend fun loadRaidRotation(): List<EventRecommendation> {
        var items = mutableListOf<EventRecommendation>()

        val timeMillis = measureTimeMillis {
            Log.d("PokemonRepository", "checking if raid rotation is fresh")


            withContext(Dispatchers.IO) {
                val internetAccess = isInternetConnected(context)
                var computeHash = false

                // Check if the local hash exists and is fresh
                val localHash: LocalHashEntity? =
                    localHashDao.getLocalHashByFilename("raidboss.json")
                val oneHourAgo = System.currentTimeMillis() - 1000 * 60 * 60

                if (localHash != null && localHash.timestamp > oneHourAgo) {
                    Log.d("PokemonRepository debug", "Local raidboss.json hash is still fresh, returning local data.")
                    items = loadLocalRaidRotation()
                    return@withContext
                }

                if (internetAccess) {
                    // Fetch hashes.json to validate the hash for raidboss.json
                    val hashUrl = "https://pokemon-go-api.github.io/pokemon-go-api/api/hashes.json"
                    val hashJsonString = getJSONFromApi(hashUrl)
                    val hashes: Hashes = Json.decodeFromString(hashJsonString)

                    // Compare hash of local raidboss.json with remote hash
                    if (localHash != null && hashes.sha512["raidboss.json"] == localHash.hash) {
                        Log.d("PokemonRepository", "Hashes match, updating timestamp and returning local data.")
                        localHashDao.updateHashForFilename(
                            LocalHashEntity(
                                filename = "raidboss.json",
                                hash = localHash.hash,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        items = loadLocalRaidRotation()
                        return@withContext
                    }

                    //fetch raidboss.json from the internet and save it
                    val raidUrl =
                        "https://pokemon-go-api.github.io/pokemon-go-api/api/raidboss.json"
                    val raidJsonString = getJSONFromApi(raidUrl)
                    saveJsonFile(context, raidJsonString, "raidboss.json")
                    computeHash = true
                    Log.d("PokemonRepository debug", "Raid rotation data fetched from endpoint.")

                } else {
                    Log.e(
                        "PokemonRepository debug",
                        "No internet connection. Attempting to load local file."
                    )
                }

                //load data from the local file if available
                val file = File(context.filesDir, "raidboss.json")
                if (!file.exists()) {
                    Log.e("PokemonRepository debug", "No local JSON file found, returning empty list.")
                    return@withContext
                }

                val jsonStream = context.openFileInput("raidboss.json")
                val jsonString = InputStreamReader(jsonStream).readText()

                //items calculated before hash so it doesn't block this thread from returning
                items = raidRotationFromJSON(jsonString)

                if (computeHash) {
                    val hashTime = measureTimeMillis {
                        launch(Dispatchers.IO) { //separate coroutine for hash computation
                            val messageDigest = MessageDigest.getInstance("SHA-512")
                            val digest = messageDigest.digest(jsonString.toByteArray(Charsets.UTF_8))
                            val hash = BigInteger(1, digest).toString(16).padStart(128, '0')

                            localHashDao.insert(
                                LocalHashEntity(
                                    filename = "raidboss.json",
                                    hash = hash,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    Log.d("PokemonRepository debug", "Hashing new raidboss.json took $hashTime ms")

                }
            }
        }

        Log.d("PokemonRepository debug", "Loading raid rotation took $timeMillis ms")
        return items

    }

    private suspend fun loadLocalRaidRotation(): MutableList<EventRecommendation> {
        val jsonStream = context.openFileInput("raidboss.json")
        val jsonString = InputStreamReader(jsonStream).readText()
        return raidRotationFromJSON(jsonString)
    }


    fun raidRotationFromJSON(jsonString: String): MutableList<EventRecommendation> {
        val json = jsonString
        val currentList = "currentList"
        val jsonObject = JSONObject(json)
        val currentListJson = jsonObject.getJSONObject(currentList)

        val items = mutableListOf<EventRecommendation>()
        items.clear()

        val keys = currentListJson.keys()
        Log.d("PokemonRepository", "Keys: $keys")
        for (key in keys) {
            val tier = currentListJson.getJSONArray(key)

            val tierItems = mutableListOf<EventRecommendation>()
            tierItems.clear()

            for (i in 0 until tier.length()) {
                val item = tier.getJSONObject(i)
                val id = item.getString("id")
                val form = item.getString("form")
                val namesObj = item.getJSONObject("names")
                val name = namesObj.getString("English")
                val assets = item.getJSONObject("assets")
                val image = assets.getString("image")

                val level = item.getString("level")
                val types = item.getJSONArray("types")
                val typesList = mutableListOf<String>()

                for (j in 0 until types.length()) {
                    typesList.add(types.getString(j))
                }

                val counters = item.getJSONObject("counter")
                //counters is a json object of type -> double
                val countersMap = mutableMapOf<String, Double>()
                val countersKeys = counters.keys()
                for (counterKey in countersKeys) {
                    countersMap[counterKey] = counters.getDouble(counterKey)
                }

                val battles = item.getJSONObject("battleResult")
                //battles is a json object of difficulty -> totalEstimator and has other keys inside.
                //need to find the min and max of totalEstimator across all difficulties
                val battlesKeys = battles.keys() //easy, medium, hard
                var min = Double.MAX_VALUE
                var max = Double.MIN_VALUE

                for (battleKey in battlesKeys) {
                    val battle = battles.getJSONObject(battleKey)
                    val totalEstimator = battle.getDouble("totalEstimator")
                    if (totalEstimator < min) {
                        min = totalEstimator
                    }
                    if (totalEstimator > max) {
                        max = totalEstimator
                    }
                }

                //round min and max up to nearest whole number then cast to int
                val minInt = ceil(min).toInt()
                val maxInt = ceil(max).toInt()

                items.add(EventRecommendation(id, form, name, image, level, typesList, countersMap, minInt, maxInt))

                Log.d("PokemonRepository", "Added raid boss: $name")
//                    Log.d("PokemonRepository", "key: $key")
//                    Log.d("PokemonRepository", "min,max: $min, $max")
//                    Log.d("PokemonRepository", "min,max Int: $minInt, $maxInt")
            }
        }

        return items
    }

    suspend fun loadEvents(): List<Event> {
        var items = mutableListOf<Event>()

        val timeMillis = measureTimeMillis {
            Log.d("PokemonRepository", "checking if local events is fresh")

            withContext(Dispatchers.IO) {
                val internetAccess = isInternetConnected(context)
                var computeHash = false

                //check if the last time events was fetched was under 6 hours ago
                //last fetch time stored as timestamp in shared preferences

                val sixHoursAgo = System.currentTimeMillis() - 1000 * 60 * 60 * 6
                val lastFetched = context.getSharedPreferences("events", Context.MODE_PRIVATE).getString("lastFetched", null)
                val lastFetchedLong = lastFetched?.toLongOrNull()

                if (lastFetchedLong != null && lastFetchedLong > sixHoursAgo) {
                    Log.d("PokemonRepository debug", "Local events.json is still fresh, returning local data.")
                    items = loadLocalEvents()
                    return@withContext
                }

                if (internetAccess) {
                    //fetch events.json from the internet and save it
                    val url = "https://raw.githubusercontent.com/bigfoott/ScrapedDuck/refs/heads/data/events.json"
                    val json = getJSONFromApi(url)
                    saveJsonFile(context, json, "events.json")

                    //updates the last fetched time
                    context.getSharedPreferences("events", Context.MODE_PRIVATE).edit()
                        .putString("lastFetched", System.currentTimeMillis().toString()).apply()
                    Log.d("PokemonRepository debug", "Events data fetched from endpoint.")

                } else {
                    Log.e(
                        "PokemonRepository debug",
                        "No internet connection. Attempting to load local file."
                    )
                }

                //load data from the local file if available
                val file = File(context.filesDir, "events.json")
                if (!file.exists()) {
                    Log.e("PokemonRepository debug", "No local JSON file found, returning empty list.")
                    return@withContext
                }

                val jsonStream = context.openFileInput("events.json")
                val jsonString = InputStreamReader(jsonStream).readText()

                //items calculated before hash so it doesn't block this thread from returning
                items = eventsFromJSON(jsonString)

            }
        }

        Log.d("PokemonRepository debug", "Loading events took $timeMillis ms")
        return items

    }

    private suspend fun loadLocalEvents(): MutableList<Event> {
        val jsonStream = context.openFileInput("events.json")
        val jsonString = InputStreamReader(jsonStream).readText()
        return eventsFromJSON(jsonString)
    }

    private fun eventsFromJSON(jsonString: String): MutableList<Event> {
        val json = jsonString
        //the json is an array of event objects
        val jsonArray = JSONArray(json)
        val events = mutableListOf<Event>()
        events.clear()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString("eventID")
            val name = item.getString("name")
            val eventType = item.getString("eventType")
            val heading = item.getString("heading")
            val link = item.getString("link")
            val image = item.getString("image")
            val start = item.getString("start")
            val end = item.getString("end")
            val extraData = item.getJSONObject("extraData")
            //extraData may have generic spotlight, raidbattles, communityday but these may not be present
            //so we need to check if they are present before trying to get them
            val generic = extraData.optJSONObject("generic")
            val genericData = if (generic != null) {
                val hasSpawns = generic.getBoolean("hasSpawns")
                val hasFieldResearchTasks = generic.getBoolean("hasFieldResearchTasks")
                GenericData(hasSpawns, hasFieldResearchTasks)
            } else {
                null
            }

            val spotlight = extraData.optJSONObject("spotlight")
            val spotlightData = if (spotlight != null) {
                val name = spotlight.getString("name")
                val canBeShiny = spotlight.getBoolean("canBeShiny")
                val image = spotlight.getString("image")
                val bonus = spotlight.getString("bonus")
                val list = spotlight.getJSONArray("list")
                val spotlightPokemonList = mutableListOf<SpotlightPokemon>()
                for (j in 0 until list.length()) {
                    val spotlightPokemon = list.getJSONObject(j)
                    val spotlightPokemonName = spotlightPokemon.getString("name")
                    val spotlightPokemonCanBeShiny = spotlightPokemon.getBoolean("canBeShiny")
                    val spotlightPokemonImage = spotlightPokemon.getString("image")
                    spotlightPokemonList.add(SpotlightPokemon(spotlightPokemonName, spotlightPokemonCanBeShiny, spotlightPokemonImage))
                }
                SpotlightData(name, canBeShiny, image, bonus, spotlightPokemonList)
            } else {
                null
            }

            val raidbattles = extraData.optJSONObject("raidbattles")
            val raidBattlesData = if (raidbattles != null) {
                val bosses = raidbattles.getJSONArray("bosses")
                val raidBosses = mutableListOf<RaidBoss>()
                for (j in 0 until bosses.length()) {
                    val raidBoss = bosses.getJSONObject(j)
                    val raidBossName = raidBoss.getString("name")
                    val raidBossImage = raidBoss.getString("image")
                    val raidBossyCanBeShiny = raidBoss.getBoolean("canBeShiny")
                    raidBosses.add(RaidBoss(raidBossName, raidBossImage, raidBossyCanBeShiny))
                }

                val shinies = raidbattles.getJSONArray("shinies")
                val shinyPokemon = mutableListOf<ShinyPokemon>()
                for (j in 0 until shinies.length()) {
                    val shiny = shinies.getJSONObject(j)
                    val shinyName = shiny.getString("name")
                    val shinyImage = shiny.getString("image")
                    shinyPokemon.add(ShinyPokemon(shinyName, shinyImage))
                }

                RaidBattlesData(raidBosses, shinyPokemon)

            } else {
                null
            }

            val communityDay = extraData.optJSONObject("communityday")
            val communityDayData = if (communityDay != null) {
                val spawns = communityDay.getJSONArray("spawns")
                val spawnPokemon = mutableListOf<SpawnPokemon>()
                for (j in 0 until spawns.length()) {
                    val spawn = spawns.getJSONObject(j)
                    val spawnName = spawn.getString("name")
                    val spawnImage = spawn.getString("image")
                    spawnPokemon.add(SpawnPokemon(spawnName, spawnImage))
                }

                val bonuses = communityDay.getJSONArray("bonuses")
                val communityDayBonuses = mutableListOf<CommunityDayBonus>()
                for (j in 0 until bonuses.length()) {
                    val bonus = bonuses.getJSONObject(j)
                    val bonusText = bonus.getString("text")
                    val bonusImage = bonus.getString("image")
                    communityDayBonuses.add(CommunityDayBonus(bonusText, bonusImage))
                }

                val bonusDisclaimers = communityDay.getJSONArray("bonusDisclaimers")
                val bonusDisclaimersList = mutableListOf<String>()
                for (j in 0 until bonusDisclaimers.length()) {
                    bonusDisclaimersList.add(bonusDisclaimers.getString(j))
                }

                val shinies = communityDay.getJSONArray("shinies")
                val shinyPokemon = mutableListOf<ShinyPokemon>()
                for (j in 0 until shinies.length()) {
                    val shiny = shinies.getJSONObject(j)
                    val shinyName = shiny.getString("name")
                    val shinyImage = shiny.getString("image")
                    shinyPokemon.add(ShinyPokemon(shinyName, shinyImage))
                }

                val specialResearch = communityDay.getJSONArray("specialresearch")
                val specialResearchTasks = mutableListOf<SpecialResearch>()
                for (j in 0 until specialResearch.length()) {
                    val research = specialResearch.getJSONObject(j)
                    val researchName = research.getString("name")
                    val researchStep = research.getInt("step")
                    val tasks = research.getJSONArray("tasks")
                    val researchTasks = mutableListOf<ResearchTask>()
                    for (k in 0 until tasks.length()) {
                        val task = tasks.getJSONObject(k)
                        val taskText = task.getString("text")
                        val taskReward = task.getJSONObject("reward")
                        val taskRewardText = taskReward.getString("text")
                        val taskRewardImage = taskReward.getString("image")
                        researchTasks.add(ResearchTask(taskText, ResearchReward(taskRewardText, taskRewardImage)))
                    }
                    val rewards = research.getJSONArray("rewards")
                    val researchRewards = mutableListOf<ResearchReward>()
                    for (k in 0 until rewards.length()) {
                        val reward = rewards.getJSONObject(k)
                        val rewardText = reward.getString("text")
                        val rewardImage = reward.getString("image")
                        researchRewards.add(ResearchReward(rewardText, rewardImage))
                    }
                    specialResearchTasks.add(SpecialResearch(researchName, researchStep, researchTasks, researchRewards))
                }

                CommunityDayData(spawnPokemon, communityDayBonuses, bonusDisclaimersList, shinyPokemon, specialResearchTasks)
            } else {
                null
            }

            val extraDataInsert = ExtraData(
                genericData,
                spotlightData,
                raidBattlesData,
                communityDayData
            )

            //splitting into date and time
            val startDate = start.substring(0, start.indexOf("T"))
            val startTimeFull = start.substring(start.indexOf("T") + 1)
            val startTime = startTimeFull.substring(0, 5)
            val endDate = end.substring(0, end.indexOf("T"))
            val endTimeFull = end.substring(end.indexOf("T") + 1)
            val endTime = endTimeFull.substring(0, 5)



            val event = Event(
                id,
                name,
                eventType,
                heading,
                link,
                image,
                start,
                startDate,
                startTime,
                end,
                endDate,
                endTime,
                extraDataInsert
            )

            events.add(event)
            Log.d("SearchViewModel", "Added event: $name")

        }

        return events
    }





    //reference: helper function from lab exercise
    private fun getJSONFromApi(url: String): String {
        var result = ""
        var conn: HttpsURLConnection? = null
        try {
            val request = URL(url)
            conn = request.openConnection() as HttpsURLConnection
            conn.connect()
            val inStream: InputStream = conn.inputStream
            result = convertInputStreamToString(inStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return result
    }

    //reference: helper function from lab exercise
    @Throws(IOException::class)
    private fun convertInputStreamToString(inS: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inS))
        val result = StringBuilder()
        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            result.append(line)
        }
        inS.close()
        return result.toString()
    }

}

private fun PokemonMove.toMoveEntity(): MoveEntity {
    return MoveEntity(
        id = this.id,
        name = this.names.English,
        power = this.power,
        energy = this.energy,
        durationMs = this.durationMs,
        type = this.type.names.English,
        combat = this.combat
    )
}