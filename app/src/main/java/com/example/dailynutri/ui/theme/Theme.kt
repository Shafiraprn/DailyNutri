package com.example.dailynutri.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Gelap (Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.Black, // Tulisan hitam di atas tombol hijau terang
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = PrimaryGreenLight,
    secondary = CaloriesOrange,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

// Skema Warna Terang (Light Mode) - INI YANG UTAMA
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreenDark, // Tombol & Header menggunakan Hijau yang enak dibaca
    onPrimary = Color.White,    // Tulisan putih di tombol hijau

    primaryContainer = GreenContainer, // Latar belakang kartu yang aktif
    onPrimaryContainer = OnGreenContainer, // Teks di atas latar belakang kartu

    secondary = CaloriesOrange, // Untuk FAB atau tombol aksi sekunder
    onSecondary = Color.White,

    tertiary = FatBlue, // Warna pelengkap

    background = BackgroundWhite, // Latar belakang layar
    surface = SurfaceWhite,       // Latar belakang kartu
    onSurface = Color.Black,

    outline = OutlineGray
)

@Composable
fun DailyNutriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color tersedia di Android 12+, tapi kita matikan agar branding HIJAU tetap kuat
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Mengubah warna Status Bar (ikon sinyal/baterai) agar serasi
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan file Typography.kt ada (bawaan project)
        content = content
    )
}