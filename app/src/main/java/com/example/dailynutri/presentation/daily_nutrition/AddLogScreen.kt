package com.example.dailynutri.presentation.daily_nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.model.NutritionLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogScreen(
    foodLibrary: List<Food>,
    logToEdit: NutritionLog? = null, // PARAMETER BARU: Mode Edit
    onSave: (String, Int, Double, Double, Double) -> Unit,
    onBack: () -> Unit
) {
    // State Form
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    // Isi Otomatis jika Mode Edit
    LaunchedEffect(logToEdit) {
        logToEdit?.let { log ->
            name = log.foodName
            calories = log.actualCalories.toString()
            protein = log.actualProtein.toString()
            carbs = log.actualCarbs.toString()
            fat = log.actualFat.toString()
        }
    }

    // Filter Kamus
    val filteredSuggestions = remember(name) {
        if (name.length >= 2) {
            foodLibrary.filter { it.name.contains(name, ignoreCase = true) }.take(5)
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (logToEdit == null) "Catat Makanan" else "Edit Catatan",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // --- SECTION 1: NAMA MAKANAN ---
            Text(
                text = "Apa yang baru saja kamu makan?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Roti Bakar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            // AUTO-COMPLETE LIST
            if (filteredSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        filteredSuggestions.forEach { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion.name, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("${suggestion.calories} kkal") },
                                leadingContent = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                                modifier = Modifier.clickable {
                                    name = suggestion.name
                                    calories = suggestion.calories.toString()
                                    protein = suggestion.protein.toString()
                                    carbs = suggestion.carbs.toString()
                                    fat = suggestion.fat.toString()
                                }
                            )
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 2: DETAIL NUTRISI (CARD) ---
            Text(
                text = "Detail Nutrisi",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Kalori
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("Kalori (kkal)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Makro (Menggunakan Composable Lokal agar rapi)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MiniNutrientInput(protein, { protein = it }, "Protein", "g", Icons.Default.Egg, Modifier.weight(1f))
                        MiniNutrientInput(carbs, { carbs = it }, "Karbo", "g", Icons.Default.Grain, Modifier.weight(1f))
                        MiniNutrientInput(fat, { fat = it }, "Lemak", "g", Icons.Default.Opacity, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- SECTION 3: TOMBOL SIMPAN ---
            Button(
                onClick = {
                    onSave(
                        name,
                        calories.toIntOrNull() ?: 0,
                        protein.toDoubleOrNull() ?: 0.0,
                        carbs.toDoubleOrNull() ?: 0.0,
                        fat.toDoubleOrNull() ?: 0.0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank() && calories.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (logToEdit == null) "Simpan Catatan" else "Simpan Perubahan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Helper Composable untuk Input Kecil
@Composable
private fun MiniNutrientInput(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text("0") },
            suffix = { Text(unit, fontSize = 10.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}