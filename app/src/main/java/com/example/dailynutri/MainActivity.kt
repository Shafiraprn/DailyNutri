package com.example.dailynutri

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailynutri.data.GoogleAuthUiClient
import com.example.dailynutri.presentation.main.MainScreen
import com.example.dailynutri.presentation.sign_in.SignInViewModel
import com.example.dailynutri.ui.theme.DailyNutriTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

/**
 * MainActivity diperbarui untuk menggunakan GoogleAuthUiClient di paket data.
 * Mengelola alur login Firebase dan transisi ke MainScreen.
 */
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyNutriTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SignInViewModel = viewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    // Menangani hasil intent dari Google One Tap
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    // Sinkronisasi status login: Jika sukses, arahkan ke dashboard di dalam MainScreen
                    val currentUser = googleAuthUiClient.getSignedInUser()

                    MainScreen(
                        signInState = state,
                        userData = currentUser,
                        onSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        },
                        onSignOutClick = {
                            lifecycleScope.launch {
                                googleAuthUiClient.signOut()
                                viewModel.resetState() // Pastikan state ViewModel kembali ke awal
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil Keluar",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}