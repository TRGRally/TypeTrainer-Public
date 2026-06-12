package com.example.typetrainer.util

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ImageCacheManager {
    private val _imageCache = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val imageCache: StateFlow<Map<String, Bitmap>> = _imageCache

    fun getImage(url: String): Bitmap? {
        //Log.d("ImageCacheManager", "Retrieving from cache: $url")
        return imageCache.value[url]
    }

    fun putImage(url: String, bitmap: Bitmap) {
        //Log.d("ImageCacheManager", "Caching: $url")
        val updatedCache = _imageCache.value.toMutableMap()
        updatedCache[url] = bitmap
        _imageCache.value = updatedCache
    }
}

