package com.example.typetrainer.util

import com.example.typetrainer.util.AccountService
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountServiceImpl : AccountService {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun createAnonymousAccount(onResult: (Throwable?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener { onResult(it.exception) }
    }

    override fun firebaseAuthWithGoogle(idToken: String, onResult: (FirebaseUser?, Throwable?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(auth.currentUser, null)
                } else {
                    onResult(null, task.exception)
                }
            }
    }
}
