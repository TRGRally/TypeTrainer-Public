package com.example.typetrainer.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.typetrainer.data.db.PokemonDatabase
import com.example.typetrainer.data.db.TypeDao

class PokemonTypeProvider : ContentProvider() {

    private lateinit var typeDao: TypeDao

    private val POKEMON_TYPES = 100
    private val TYPE_NAME = 101

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(PokemonTypeContract.AUTHORITY, PokemonTypeContract.PokemonTypes.PATH_TYPES, POKEMON_TYPES)
        addURI(PokemonTypeContract.AUTHORITY, "${PokemonTypeContract.PokemonTypes.PATH_TYPES}/#", TYPE_NAME)
    }

    override fun onCreate(): Boolean {
        typeDao = PokemonDatabase.getDatabase(context!!).pokemonTypeDao()
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs:
    Array<String>?, sortOrder: String?): Cursor? {
        val match = uriMatcher.match(uri)
        return when (match) {
            POKEMON_TYPES -> typeDao.getAllTypesCursor()
            TYPE_NAME -> {
                val  name = ContentUris.parseId(uri)
                typeDao.getTypeItemCursor(name.toString())
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            POKEMON_TYPES -> PokemonTypeContract.PokemonTypes.CONTENT_TYPE
            TYPE_NAME -> PokemonTypeContract.PokemonTypes.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Insert operation is not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Delete operation is not supported")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Update operation is not supported")
    }
}