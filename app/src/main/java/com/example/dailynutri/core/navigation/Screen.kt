package com.example.dailynutri.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Beranda", Icons.Default.Home)
    object FoodDatabase : Screen("food_database", "Kamus", Icons.Default.MenuBook)
    object DailyNutrition : Screen("daily_nutrition", "Ringkasan", Icons.Default.BarChart)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)

    // Rute tambahan tanpa icon bawah
    object AddMeal : Screen("add_meal", "Tambah Niat", Icons.Default.Add)
    object AddLog : Screen("add_log", "Tambah Realita", Icons.Default.Add)
    object AddFood : Screen("add_food", "Tambah Kamus", Icons.Default.Add)
    object MealPlanner : Screen("meal_planner", "Rencana", Icons.Default.EventNote)
    object SignIn : Screen("sign_in", "Login", Icons.Default.Login)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.FoodDatabase,
    Screen.DailyNutrition,
    Screen.Profile
)