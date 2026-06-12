package com.example.typetrainer.util

import com.google.firebase.auth.FirebaseUser

interface AccountService {
    fun createAnonymousAccount(onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun firebaseAuthWithGoogle(idToken: String, onResult: (FirebaseUser?, Throwable?) -> Unit)

}