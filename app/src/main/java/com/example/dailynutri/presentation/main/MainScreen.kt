package com.example.dailynutri.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dailynutri.core.navigation.NavGraph
import com.example.dailynutri.core.navigation.Screen
import com.example.dailynutri.data.UserData
import com.example.dailynutri.presentation.sign_in.SignInScreen
import com.example.dailynutri.presentation.sign_in.SignInState

@Composable
fun MainScreen(
    signInState: SignInState,
    userData: UserData?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val navController = rememberNavController()

    if (userData == null) {
        SignInScreen(
            state = signInState,
            onSignInClick = onSignInClick
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    // PERBAIKAN: Mengembalikan menu KAMUS dan menghapus PROFIL dari bawah
                    val items = listOf(
                        Triple(Screen.Dashboard.route, "Beranda", Icons.Default.Home),
                        Triple(Screen.MealPlanner.route, "Rencana", Icons.Default.CalendarMonth),
                        Triple(Screen.DailyNutrition.route, "Realita", Icons.Default.History),
                        Triple(Screen.FoodDatabase.route, "Kamus", Icons.Default.MenuBook) // Kamus kembali
                    )

                    items.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                signInState = signInState,
                userData = userData,
                onSignInClick = onSignInClick,
                onSignOutClick = onSignOutClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}