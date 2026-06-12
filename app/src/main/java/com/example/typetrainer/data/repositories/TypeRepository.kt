package com.example.typetrainer.data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.typetrainer.data.db.TypeDao
import com.example.typetrainer.data.models.AggregatedTypeInfo
import com.example.typetrainer.data.models.PokemonType
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.data.models.TypeRelationEntity
import com.example.typetrainer.data.models.WeatherBoostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

class TypeRepository(private val context: Context, private val typeDao: TypeDao) {

    val allTypes: LiveData<List<TypeEntity>> = typeDao.getAllTypes()
    val allWeatherBoosts: LiveData<List<WeatherBoostEntity>> = typeDao.getAllWeatherBoosts()

    suspend fun getTypeCount(): Int {
        return typeDao.getCount()
    }

    suspend fun getAllTypes(): List<TypeEntity> {
        return typeDao.getAllTypesList()
    }

    suspend fun getTypeById(id: String): TypeEntity {
        return typeDao.getTypeById(id)
    }

    suspend fun getAllTypeRelations(): List<TypeRelationEntity> {
        return typeDao.getAllTypeRelations()
    }

    suspend fun getAllWeatherBoosts(): List<WeatherBoostEntity> {
        return typeDao.getAllWeatherBoosts().value ?: emptyList()
    }


    suspend fun searchTypeByNameStartsWith(query: String, limit: Int): List<TypeEntity> {
        return typeDao.searchTypeByNameStartsWith(query, limit)
    }

    suspend fun searchTypeByNameContains(query: String, limit: Int): List<TypeEntity> {
        return typeDao.searchTypeByNameContains(query, limit)
    }



    fun getAggregatedTypeInfo(): LiveData<List<AggregatedTypeInfo>> {
        return typeDao.getAggregatedTypeInfo()
    }

    suspend fun loadTypeData() {
        try {
            val timeMillis = measureTimeMillis {
                withContext(Dispatchers.IO) {
                    try {
                        val jsonStream = context.assets.open("types.json")
                        val jsonString = InputStreamReader(jsonStream).readText()
                        val typeList: List<PokemonType> = Json.decodeFromString(jsonString)

                        val typeEntities = typeList.map { type ->
                            Log.d("TypeRepository", "Found Type: ${type.type}")
                            TypeEntity(
                                type = type.type
                            )
                        }

                        val typeRelations = typeList.flatMap { type ->
                            val doubleDamageFromRelations = type.doubleDamageFrom.map { relatedType ->
                                TypeRelationEntity(
                                    type = type.type,
                                    relatedType = relatedType,
                                    relationType = "doubleDamageFrom"
                                )
                            }
                            val halfDamageFromRelations = type.halfDamageFrom.map { relatedType ->
                                TypeRelationEntity(
                                    type = type.type,
                                    relatedType = relatedType,
                                    relationType = "halfDamageFrom"
                                )
                            }
                            val noDamageFromRelations = type.noDamageFrom.map { relatedType ->
                                TypeRelationEntity(
                                    type = type.type,
                                    relatedType = relatedType,
                                    relationType = "noDamageFrom"
                                )
                            }
                            doubleDamageFromRelations + halfDamageFromRelations + noDamageFromRelations
                        }

                        val weatherBoostEntities = typeList.mapNotNull { type ->
                            type.weatherBoost?.let {
                                WeatherBoostEntity(
                                    type = type.type,
                                    weatherBoost = it.id
                                )
                            }
                        }

                        Log.d("TypeRepository", "Attempting to insert ${typeEntities.size} types into the database.")
                        typeDao.insertAllTypes(typeEntities)
                        Log.d("TypeRepository", "Types inserted successfully.")

                        typeRelations.forEach {
                            typeDao.insertTypeRelation(it)
                            Log.d("TypeRepository", "Type relation inserted: ${it.type} -> ${it.relatedType} (${it.relationType})")
                        }

                        weatherBoostEntities.forEach {
                            typeDao.insertWeatherBoost(it)
                            Log.d("TypeRepository", "Weather boost inserted: ${it.id}")
                        }

                        Log.d("TypeRepository", "Inserted ${typeEntities.size} types, ${typeRelations.size} type relations, and ${weatherBoostEntities.size} weather boosts into the database.")
                    } catch (e: Exception) {
                        Log.e("TypeRepository", "Error during database operation: ${e.message}", e)
                    }
                }
            }
            Log.d("TypeRepository debug", "Loading type data took $timeMillis ms")
        } catch (e: Exception) {
            Log.e("TypeRepository debug", "Error loading type data: ${e.message}", e)
        }
    }
}


