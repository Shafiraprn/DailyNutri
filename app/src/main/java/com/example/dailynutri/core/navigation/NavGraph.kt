package com.example.dailynutri.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dailynutri.data.UserData
import com.example.dailynutri.data.repository.FoodRepositoryImpl
import com.example.dailynutri.data.repository.MealRepositoryImpl
import com.example.dailynutri.data.repository.NutritionRepositoryImpl
import com.example.dailynutri.domain.model.Food
import com.example.dailynutri.domain.model.Meal
import com.example.dailynutri.domain.model.NutritionLog
import com.example.dailynutri.presentation.dashboard.DashboardScreen
import com.example.dailynutri.presentation.dashboard.DashboardViewModel
import com.example.dailynutri.presentation.dashboard.DashboardViewModelFactory
import com.example.dailynutri.presentation.daily_nutrition.AddLogScreen
import com.example.dailynutri.presentation.daily_nutrition.DailyNutritionScreen
import com.example.dailynutri.presentation.daily_nutrition.NutritionViewModel
import com.example.dailynutri.presentation.daily_nutrition.NutritionViewModelFactory
import com.example.dailynutri.presentation.food_database.AddFoodScreen
import com.example.dailynutri.presentation.food_database.FoodDatabaseScreen
import com.example.dailynutri.presentation.food_database.FoodViewModel
import com.example.dailynutri.presentation.food_database.FoodViewModelFactory
import com.example.dailynutri.presentation.meal_planner.AddMealScreen
import com.example.dailynutri.presentation.meal_planner.MealPlannerScreen
import com.example.dailynutri.presentation.meal_planner.MealPlannerViewModel
import com.example.dailynutri.presentation.meal_planner.MealPlannerViewModelFactory
import com.example.dailynutri.presentation.profile.ProfileScreen
import com.example.dailynutri.presentation.sign_in.SignInScreen
import com.example.dailynutri.presentation.sign_in.SignInState

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    signInState: SignInState,
    userData: UserData?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- 1. Login ---
        composable(Screen.SignIn.route) {
            SignInScreen(state = signInState, onSignInClick = onSignInClick)
        }

        // --- 2. Dashboard ---
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(MealRepositoryImpl(), NutritionRepositoryImpl())
            )
            val state by dashboardViewModel.dashboardState.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> dashboardViewModel.getDashboardData(uid) }
            }

            DashboardScreen(
                userData = userData,
                state = state,
                onNavigateToPlanner = { navController.navigate(Screen.MealPlanner.route) },
                onNavigateToLog = { navController.navigate(Screen.DailyNutrition.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        // --- 3. Meal Planner (Rencana) ---
        composable(Screen.MealPlanner.route) {
            val plannerViewModel: MealPlannerViewModel = viewModel(
                factory = MealPlannerViewModelFactory(
                    MealRepositoryImpl(),
                    FoodRepositoryImpl(),
                    NutritionRepositoryImpl()
                )
            )
            val state by plannerViewModel.state.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> plannerViewModel.observeMeals(uid) }
            }

            MealPlannerScreen(
                state = state,
                onDateSelected = { plannerViewModel.onDateSelected(it) },
                onToggleCompletion = { plannerViewModel.toggleMealCompletion(it) },
                onAddMealClick = {
                    val targetDate = state.selectedDate
                    navController.navigate(Screen.AddMeal.route + "/add/$targetDate")
                },
                onEditMeal = { mealId ->
                    navController.navigate(Screen.AddMeal.route + "/edit/$mealId")
                },
                onDeleteMeal = { plannerViewModel.deleteMeal(it) }
            )
        }

        // --- 4a. Add Meal (TAMBAH) ---
        composable(
            route = Screen.AddMeal.route + "/add/{date}",
            arguments = listOf(navArgument("date") { type = NavType.LongType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getLong("date") ?: System.currentTimeMillis()
            val plannerViewModel: MealPlannerViewModel = viewModel(
                factory = MealPlannerViewModelFactory(MealRepositoryImpl(), FoodRepositoryImpl(), NutritionRepositoryImpl())
            )
            val state by plannerViewModel.state.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> plannerViewModel.loadFoodLibrary(uid) }
            }

            AddMealScreen(
                foodLibrary = state.foodLibrary,
                mealToEdit = null,
                onSave = { title, type, cal, prot, carb, fat ->
                    val newMeal = Meal(
                        userId = userData?.userId ?: "",
                        title = title,
                        mealType = type,
                        calories = cal,
                        protein = prot,
                        carbs = carb,
                        fat = fat,
                        scheduledDate = selectedDate,
                        isCompleted = false
                    )
                    plannerViewModel.addMeal(newMeal)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 4b. Add Meal (EDIT) ---
        composable(
            route = Screen.AddMeal.route + "/edit/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            val plannerViewModel: MealPlannerViewModel = viewModel(
                factory = MealPlannerViewModelFactory(MealRepositoryImpl(), FoodRepositoryImpl(), NutritionRepositoryImpl())
            )
            val state by plannerViewModel.state.collectAsState()
            var mealToEdit by remember { mutableStateOf<Meal?>(null) }

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid ->
                    plannerViewModel.loadFoodLibrary(uid)
                    if (mealId.isNotEmpty()) mealToEdit = plannerViewModel.getMealById(mealId)
                }
            }

            AddMealScreen(
                foodLibrary = state.foodLibrary,
                mealToEdit = mealToEdit,
                onSave = { title, type, cal, prot, carb, fat ->
                    mealToEdit?.let { existingMeal ->
                        val updatedMeal = existingMeal.copy(
                            title = title,
                            mealType = type,
                            calories = cal,
                            protein = prot,
                            carbs = carb,
                            fat = fat
                        )
                        plannerViewModel.updateMeal(updatedMeal)
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 5. Daily Nutrition (Realita) ---
        composable(Screen.DailyNutrition.route) {
            val nutritionViewModel: NutritionViewModel = viewModel(
                factory = NutritionViewModelFactory(
                    NutritionRepositoryImpl(),
                    FoodRepositoryImpl()
                )
            )
            val state by nutritionViewModel.state.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> nutritionViewModel.observeLogs(uid) }
            }

            DailyNutritionScreen(
                state = state,
                onDateSelected = { nutritionViewModel.onDateSelected(it) },
                onAddLogClick = {
                    // Mode TAMBAH Log (Kirim Tanggal)
                    val targetDate = state.selectedDate
                    navController.navigate(Screen.AddLog.route + "/add/$targetDate")
                },
                onEditLog = { logId ->
                    // Mode EDIT Log (Kirim ID)
                    navController.navigate(Screen.AddLog.route + "/edit/$logId")
                },
                onDeleteLog = { nutritionViewModel.deleteLog(it) }
            )
        }

        // --- 6a. Add Log Form (MODE TAMBAH) ---
        composable(
            route = Screen.AddLog.route + "/add/{date}",
            arguments = listOf(navArgument("date") { type = NavType.LongType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getLong("date") ?: System.currentTimeMillis()
            val nutritionViewModel: NutritionViewModel = viewModel(
                factory = NutritionViewModelFactory(NutritionRepositoryImpl(), FoodRepositoryImpl())
            )
            val state by nutritionViewModel.state.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> nutritionViewModel.loadFoodLibrary(uid) }
            }

            AddLogScreen(
                foodLibrary = state.foodLibrary,
                logToEdit = null, // Form Kosong
                onSave = { name, cal, prot, carb, fat ->
                    val newLog = NutritionLog(
                        userId = userData?.userId ?: "",
                        foodName = name,
                        actualCalories = cal,
                        actualProtein = prot,
                        actualCarbs = carb,
                        actualFat = fat,
                        consumptionTime = selectedDate
                    )
                    nutritionViewModel.addLog(newLog)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 6b. Add Log Form (MODE EDIT) ---
        composable(
            route = Screen.AddLog.route + "/edit/{logId}",
            arguments = listOf(navArgument("logId") { type = NavType.StringType })
        ) { backStackEntry ->
            val logId = backStackEntry.arguments?.getString("logId") ?: ""
            val nutritionViewModel: NutritionViewModel = viewModel(
                factory = NutritionViewModelFactory(NutritionRepositoryImpl(), FoodRepositoryImpl())
            )
            val state by nutritionViewModel.state.collectAsState()
            var logToEdit by remember { mutableStateOf<NutritionLog?>(null) }

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid ->
                    nutritionViewModel.loadFoodLibrary(uid)
                    if (logId.isNotEmpty()) logToEdit = nutritionViewModel.getLogById(logId)
                }
            }

            AddLogScreen(
                foodLibrary = state.foodLibrary,
                logToEdit = logToEdit, // Isi Form dengan Data Lama
                onSave = { name, cal, prot, carb, fat ->
                    logToEdit?.let { existingLog ->
                        val updatedLog = existingLog.copy(
                            foodName = name,
                            actualCalories = cal,
                            actualProtein = prot,
                            actualCarbs = carb,
                            actualFat = fat
                        )
                        nutritionViewModel.updateLog(updatedLog)
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 7. Food Database ---
        composable(Screen.FoodDatabase.route) {
            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(FoodRepositoryImpl()))
            val state by foodViewModel.state.collectAsState()

            LaunchedEffect(key1 = userData?.userId) {
                userData?.userId?.let { uid -> foodViewModel.observeFoodLibrary(uid) }
            }

            FoodDatabaseScreen(
                state = state,
                onAddFoodClick = { navController.navigate(Screen.AddFood.route) },
                onDeleteFood = { foodViewModel.deleteFood(it) }
            )
        }

        // --- 8. Add Food Form ---
        composable(Screen.AddFood.route) {
            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(FoodRepositoryImpl()))
            AddFoodScreen(
                onSave = { n, cat, s, cal, p, c, f ->
                    val newFood = Food(userData?.userId ?: "", n, cat, s, cal, p, c, f)
                    foodViewModel.addFood(newFood)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- 9. Profile ---
        composable(Screen.Profile.route) {
            ProfileScreen(userData = userData, onSignOut = onSignOutClick)
        }
    }
}