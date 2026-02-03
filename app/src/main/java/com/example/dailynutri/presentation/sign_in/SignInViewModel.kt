package com.example.dailynutri.presentation.sign_in

import androidx.lifecycle.ViewModel
import com.example.dailynutri.data.SignInResult // Import disesuaikan ke lokasi baru
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel untuk mengelola status autentikasi pengguna.
 * Perbaikan: Menggunakan model SignInResult dari paket data.
 */
class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    /**
     * Memperbarui state berdasarkan hasil yang diterima dari GoogleAuthUiClient.
     */
    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    /**
     * Mengembalikan status ke awal (misal saat logout).
     */
    fun resetState() {
        _state.update { SignInState() }
    }
}