package com.example.dailynutri.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dailynutri.data.UserData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userData: UserData?,
    state: DashboardState,
    onNavigateToPlanner: () -> Unit,
    onNavigateToLog: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Format tanggal: "Senin, 2 Februari 2026"
    val currentDate = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // FOTO PROFIL (Tombol Navigasi ke Profil)
                        if (userData?.profilePictureUrl != null) {
                            AsyncImage(
                                model = userData.profilePictureUrl,
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigateToProfile() }, // Aksi Klik ke Profil
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Fallback jika tidak ada foto
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profil Default",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { onNavigateToProfile() }, // Aksi Klik ke Profil
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Sapaan Pengguna
                        Column {
                            Text(
                                text = "Hai, ${userData?.username?.split(" ")?.firstOrNull() ?: "Kawan"}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- SEKSI 1: RINGKASAN UTAMA (HERO) ---
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ringkasan Nutrisi",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cincin Progres Kalori
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                        // Latar Belakang Cincin (Track)
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            strokeWidth = 18.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Indikator Progres
                        val progress = if (state.plannedCalories > 0) {
                            state.actualCalories.toFloat() / state.plannedCalories.toFloat()
                        } else 0f

                        CircularProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxSize(),
                            color = if (state.isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            strokeWidth = 18.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Info Teks Tengah
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.remainingCalories}",
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (state.isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sisa Kkal",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Statistik Angka (Target vs Masuk)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            value = "${state.plannedCalories}",
                            label = "Target",
                            icon = Icons.Outlined.Flag,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Garis Pemisah Tipis
                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        StatItem(
                            value = "${state.actualCalories}",
                            label = "Masuk",
                            icon = Icons.Outlined.LocalFireDepartment,
                            color = if (state.isOverLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEKSI 2: QUICK ACTIONS (Aksi Cepat) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard(
                    title = "Rencana",
                    subtitle = "Atur Menu",
                    icon = Icons.Default.CalendarToday,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPlanner
                )

                ActionCard(
                    title = "Catat",
                    subtitle = "Input Makan",
                    icon = Icons.Default.Restaurant,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLog
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEKSI 3: KARTU MOTIVASI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (state.isOverLimit)
                            "Kalori berlebih. Kurangi camilan besok ya!"
                        else
                            "Bagus! Pertahankan pola makan sehatmu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/**
 * Komponen reusable untuk menampilkan item statistik.
 */
@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * Komponen reusable untuk kartu aksi cepat.
 */
@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = contentColor)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
        }
    }
}