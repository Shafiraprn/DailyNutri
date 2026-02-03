package com.example.dailynutri.presentation.meal_planner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
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
import com.example.dailynutri.domain.model.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    foodLibrary: List<Food>,
    mealToEdit: Meal? = null,
    onSave: (String, String, Int, Double, Double, Double) -> Unit,
    onBack: () -> Unit
) {
    // State Form
    var title by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Sarapan") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    // Isi otomatis jika Mode Edit
    LaunchedEffect(mealToEdit) {
        mealToEdit?.let { meal ->
            title = meal.title
            mealType = meal.mealType
            calories = meal.calories.toString()
            protein = meal.protein.toString()
            carbs = meal.carbs.toString()
            fat = meal.fat.toString()
        }
    }

    // Filter Kamus
    val filteredSuggestions = remember(title) {
        if (title.length >= 2) {
            foodLibrary.filter { it.name.contains(title, ignoreCase = true) }.take(5)
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (mealToEdit == null) "Tambah Menu" else "Edit Menu",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // PERBAIKAN: Gunakan AutoMirrored agar tidak warning
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
                text = "Apa yang ingin kamu makan?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Nasi Goreng, Ayam...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                singleLine = true
            )

            // AUTO-COMPLETE LIST
            if (filteredSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.elevatedCardColors().let { CardDefaults.cardElevation(4.dp) }
                ) {
                    Column {
                        filteredSuggestions.forEach { suggestion ->
                            ListItem(
                                headlineContent = { Text(suggestion.name, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("${suggestion.calories} kkal") },
                                leadingContent = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                                modifier = Modifier.clickable {
                                    title = suggestion.name
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

            // --- SECTION 2: WAKTU MAKAN ---
            Text(
                text = "Kategori Waktu",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val types = listOf("Sarapan", "Makan Siang", "Makan Malam", "Camilan")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                types.forEach { type ->
                    val isSelected = mealType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { mealType = type },
                        label = {
                            Text(
                                text = if (type == "Makan Siang") "Siang" else if (type == "Makan Malam") "Malam" else type,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 3: DETAIL NUTRISI (CARD) ---
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
                    NutrientInputRow(
                        value = calories,
                        onValueChange = { calories = it },
                        label = "Kalori (kkal)",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFFF5722)
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NutrientMiniInput(
                            value = protein,
                            onChange = { protein = it },
                            label = "Protein",
                            unit = "g",
                            icon = Icons.Default.Egg,
                            modifier = Modifier.weight(1f)
                        )
                        NutrientMiniInput(
                            value = carbs,
                            onChange = { carbs = it },
                            label = "Karbo",
                            unit = "g",
                            icon = Icons.Default.Grain,
                            modifier = Modifier.weight(1f)
                        )
                        NutrientMiniInput(
                            value = fat,
                            onChange = { fat = it },
                            label = "Lemak",
                            unit = "g",
                            icon = Icons.Default.Opacity,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- SECTION 4: BUTTON ---
            Button(
                onClick = {
                    onSave(
                        title,
                        mealType,
                        calories.toIntOrNull() ?: 0,
                        protein.toDoubleOrNull() ?: 0.0,
                        carbs.toDoubleOrNull() ?: 0.0,
                        fat.toDoubleOrNull() ?: 0.0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && calories.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (mealToEdit == null) "Simpan Rencana" else "Simpan Perubahan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun NutrientInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    iconTint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun NutrientMiniInput(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(4.dp))
        // PERBAIKAN: Menghapus 'contentPadding' yang menyebabkan error
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