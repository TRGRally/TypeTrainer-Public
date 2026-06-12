package com.example.typetrainer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.typetrainer.data.models.LocalHashEntity

@Dao
interface LocalHashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<LocalHashEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hashEntity: LocalHashEntity)

    @Update
    suspend fun updateHashForFilename(routeHash: LocalHashEntity)

    @Delete
    suspend fun deleteHashForFilename(routeHash: LocalHashEntity)

    @Query("SELECT * FROM local_hash_table")
    fun getAllLocalHashes(): LiveData<List<LocalHashEntity>>

    @Query("SELECT * FROM local_hash_table WHERE filename = :filename")
    fun getLocalHashByFilename(filename: String): LocalHashEntity?

}