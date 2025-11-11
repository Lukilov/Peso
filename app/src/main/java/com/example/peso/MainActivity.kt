package com.example.peso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.peso.model.BudgetViewModel
import com.example.peso.screens.*
import com.example.peso.ui.theme.PesoTheme
import androidx.lifecycle.viewmodel.compose.viewModel

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

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_DASHBOARD,
                                onClick = {
                                    navController.navigate(ROUTE_DASHBOARD) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Pregled") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_TRANSACTIONS,
                                onClick = {
                                    navController.navigate(ROUTE_TRANSACTIONS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = { Text("Transakcije") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_ANALYSIS,
                                onClick = {
                                    navController.navigate(ROUTE_ANALYSIS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                                label = { Text("Analiza") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == ROUTE_SETTINGS,
                                onClick = {
                                    navController.navigate(ROUTE_SETTINGS) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
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
                            val vm = viewModel<BudgetViewModel>()
                            DashboardScreen()
                        }
                        composable(ROUTE_TRANSACTIONS) {
                            val vm = viewModel<BudgetViewModel>()
                            TransactionsScreen(vm)
                        }
                        composable(ROUTE_ANALYSIS) {
                            val vm = viewModel<BudgetViewModel>()
                            AnalysisScreen(vm = vm, onSetLimitClick = {
                                navController.navigate(ROUTE_SETTINGS)
                            })
                        }
                        composable(ROUTE_SETTINGS) { SettingsScreen() }
                    }
                }
            }
        }
    }
}
