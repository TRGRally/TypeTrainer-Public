package com.example.typetrainer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.typetrainer.data.models.MoveEntity

@Dao
interface MoveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMoves(moves: List<MoveEntity>)

    @Update
    suspend fun updateMove(move: MoveEntity)

    @Delete
    suspend fun deleteMove(move: MoveEntity)

    @Query("SELECT * FROM moves_table")
    fun getAllMoves(): LiveData<List<MoveEntity>>

    @Query("SELECT * FROM moves_table WHERE id = :id")
    fun getMoveById(id: String): LiveData<MoveEntity>
}