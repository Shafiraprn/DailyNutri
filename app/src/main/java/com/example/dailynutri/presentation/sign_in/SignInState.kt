package com.example.dailynutri.presentation.sign_in

/**
 * Menyimpan status proses login.
 */
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)