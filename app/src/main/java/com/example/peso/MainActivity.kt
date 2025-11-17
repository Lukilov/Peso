package com.example.peso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.peso.model.BudgetViewModel
import com.example.peso.screens.AnalysisScreen
import com.example.peso.screens.DashboardScreen
import com.example.peso.screens.SettingsScreen
import com.example.peso.screens.TransactionsScreen
import com.example.peso.ui.theme.PesoTheme

private const val ROUTE_DASHBOARD = "dashboard"
private const val ROUTE_TRANSACTIONS = "transactions"
private const val ROUTE_ANALYSIS = "analysis"
private const val ROUTE_SETTINGS = "settings"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PesoTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // EN SAM ViewModel za cel app
                val vm: BudgetViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_DASHBOARD,
                                onClick = {
                                    navController.navigate(ROUTE_DASHBOARD) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, null) },
                                label = { Text("Pregled") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_TRANSACTIONS,
                                onClick = {
                                    navController.navigate(ROUTE_TRANSACTIONS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.List, null) },
                                label = { Text("Transakcije") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_ANALYSIS,
                                onClick = {
                                    navController.navigate(ROUTE_ANALYSIS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Assessment, null) },
                                label = { Text("Analiza") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_SETTINGS,
                                onClick = {
                                    navController.navigate(ROUTE_SETTINGS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, null) },
                                label = { Text("Nastavitve") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_DASHBOARD,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(ROUTE_DASHBOARD) {
                            DashboardScreen(vm = vm)
                        }
                        composable(ROUTE_TRANSACTIONS) {
                            TransactionsScreen(vm = vm)
                        }
                        composable(ROUTE_ANALYSIS) {
                            AnalysisScreen(vm = vm)
                        }
                        composable(ROUTE_SETTINGS) {
                            SettingsScreen(vm = vm)
                        }
                    }
                }
            }
        }
    }
}
