package com.example.typetrainer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.typetrainer.data.models.PokemonEntity

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonEntity>)

    @Query("SELECT COUNT(*) FROM pokemon_table")
    suspend fun getCount(): Int

    @Update
    suspend fun updatePokemon(pokemon: PokemonEntity)

    @Delete
    suspend fun deletePokemon(pokemon: PokemonEntity)



    @Query("SELECT * FROM pokemon_table WHERE name LIKE :query || '%' LIMIT :limit")
    suspend fun searchPokemonByNameStartsWith(query: String, limit: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon_table WHERE name LIKE '%' || :query || '%' AND name NOT LIKE :query || '%' LIMIT :limit")
    suspend fun searchPokemonByNameContains(query: String, limit: Int): List<PokemonEntity>


    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemon(): LiveData<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table WHERE id = :id AND formId = :formId")
    suspend fun getPokemonByIdAndForm(id: String, formId: String): PokemonEntity?

}