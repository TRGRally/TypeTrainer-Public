package com.example.typetrainer.util

import android.content.Context
import android.content.Intent

fun shareSheetManager(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    context.startActivity(chooserIntent)
}