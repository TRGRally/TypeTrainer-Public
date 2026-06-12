package com.example.typetrainer.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.typetrainer.data.repositories.PreferencesRepository
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PreferencesViewModel(
    private val repository: PreferencesRepository
) : ViewModel() {

    val pokemonGoTheme: Flow<Boolean> = repository.pokemonGoTheme
    //info dialog state flow
    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: Flow<Boolean> = _showInfoDialog

    fun setPokemonGoTheme(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPokemonGoTheme(enabled)
        }
    }

    fun onGoogleSignInSuccess(user: FirebaseUser) {
        Log.d("PreferencesViewModel", "Google sign in success: ${user.displayName}")
    }
    //opens github issues page in browser
    fun openBugReport(context: Context) {
        val url = "https://github.com/trgrally/TypeTrainer/issues"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

    }

    fun showInfoDialog() {
        _showInfoDialog.value = true
    }

    fun hideInfoDialog() {
        _showInfoDialog.value = false
    }


}