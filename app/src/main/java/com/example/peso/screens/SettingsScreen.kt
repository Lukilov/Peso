package com.example.peso.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var monthlyLimit by remember { mutableStateOf(800.0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nastavitve")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Mesečni Limit")
        TextField(value = monthlyLimit.toString(), onValueChange = {
            monthlyLimit = it.toDoubleOrNull() ?: monthlyLimit
        })

        Button(onClick = { /* Save Limit */ }) {
            Text("Shrani")
        }
    }
}
