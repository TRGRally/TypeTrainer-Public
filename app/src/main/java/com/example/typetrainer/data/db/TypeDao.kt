package com.example.typetrainer.data.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.typetrainer.data.models.AggregatedTypeInfo
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.data.models.TypeRelationEntity
import com.example.typetrainer.data.models.WeatherBoostEntity

@Dao
interface TypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTypes(types: List<TypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherBoost(weatherBoost: WeatherBoostEntity)

    @Query("SELECT COUNT(*) FROM type_table")
    suspend fun getCount(): Int

    @Query("SELECT * FROM type_table")
    fun getAllTypes(): LiveData<List<TypeEntity>>

    @Query("SELECT * FROM type_relation_table")
    suspend fun getAllTypeRelations(): List<TypeRelationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeRelation(typeRelation: TypeRelationEntity)


    @Transaction
    @Query("""
        SELECT 
            type_table.type as type,
            IFNULL(GROUP_CONCAT(CASE WHEN type_relation_table.relationType = 'doubleDamageFrom' THEN type_relation_table.relatedType END), '') AS doubleDamageFrom,
            IFNULL(GROUP_CONCAT(CASE WHEN type_relation_table.relationType = 'halfDamageFrom' THEN type_relation_table.relatedType END), '') AS halfDamageFrom,
            IFNULL(GROUP_CONCAT(CASE WHEN type_relation_table.relationType = 'noDamageFrom' THEN type_relation_table.relatedType END), '') AS noDamageFrom
        FROM type_table
        LEFT JOIN type_relation_table ON type_table.type = type_relation_table.type
        GROUP BY type_table.type
    """)
    fun getAggregatedTypeInfo(): LiveData<List<AggregatedTypeInfo>>


    //cursor methods for content provider
    @Query("SELECT * FROM type_table")
    fun getAllTypesCursor(): Cursor

    @Query("SELECT * FROM type_table WHERE type = :name")
    fun getTypeItemCursor(name: String): Cursor


    @Query("SELECT * FROM type_table WHERE type LIKE :query || '%' LIMIT :limit")
    suspend fun searchTypeByNameStartsWith(query: String, limit: Int): List<TypeEntity>

    @Query("SELECT * FROM type_table WHERE type LIKE '%' || :query || '%' AND type NOT LIKE :query || '%' LIMIT :limit")
    suspend fun searchTypeByNameContains(query: String, limit: Int): List<TypeEntity>

    @Query("SELECT * FROM type_table WHERE type = :id")
    suspend fun getTypeById(id: String): TypeEntity


    //list methods for testing
    @Query("SELECT * FROM type_table")
    suspend fun getAllTypesList(): List<TypeEntity>

    @Query("SELECT * FROM weather_boost_table")
    fun getAllWeatherBoosts(): LiveData<List<WeatherBoostEntity>>

    @Query("SELECT * FROM weather_boost_table")
    suspend fun getAllWeatherBoostsList(): List<WeatherBoostEntity>

}