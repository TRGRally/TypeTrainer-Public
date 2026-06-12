package com.example.typetrainer.ui.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.typetrainer.R
import com.example.typetrainer.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun URLImage(url: String, onImageLoaded: () -> Unit) {
    val onImageLoadedState by rememberUpdatedState(onImageLoaded)
    val bitmapState = remember(url) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                bitmapState.value = bitmap
                withContext(Dispatchers.Main) {
                    onImageLoadedState()
                }
            } catch (e: Exception) {
                //TODO:
            }
        }
    }

    bitmapState.value?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "My network image",
            modifier = Modifier
                .padding(4.dp, 4.dp, 8.dp, 4.dp)
                .size(72.dp, 72.dp),
            contentScale = ContentScale.Fit
        )
    } ?: run {
        // Placeholder for the image
        Image(
            painter = painterResource(id = Constants.IMAGE_PLACEHOLDER),
            contentDescription = "Placeholder Image",
            modifier = Modifier
                .padding(4.dp, 4.dp, 8.dp, 4.dp)
                .size(72.dp, 72.dp),
            contentScale = ContentScale.Fit
        )
    }
}