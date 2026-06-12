package com.example.typetrainer.data.db



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.typetrainer.data.models.MoveEntity
import com.example.typetrainer.data.models.PokemonEntity
import com.example.typetrainer.data.models.TypeEntity
import com.example.typetrainer.data.models.TypeRelationEntity
import com.example.typetrainer.data.models.*


import com.example.typetrainer.data.models.WeatherBoostEntity

@Database(
    entities = [
        PokemonEntity::class,
        TypeEntity::class,
        TypeRelationEntity::class,
        MoveEntity::class,
        WeatherBoostEntity::class,
        QuizEntity::class,
        QuestionEntity::class,
        QuestionTypeRelationEntity::class,
        QuizQuestionJoinEntity::class,
        UserAnswerEntity::class,
        LocalHashEntity::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonTypeDao(): TypeDao
    abstract fun moveDao(): MoveDao
    abstract fun quizDao(): QuizDao

    abstract fun localHashDao(): LocalHashDao

    companion object {
        @Volatile
        private var INSTANCE: PokemonDatabase? = null

        fun getDatabase(context: Context): PokemonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    "pokemon_database"
                )
                    .fallbackToDestructiveMigration() // literally a requirement for development at this point
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}