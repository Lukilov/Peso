package com.example.peso.screens
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var monthlyLimit by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Nastavitve", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = monthlyLimit,
            onValueChange = { monthlyLimit = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' } },
            label = { Text("Mesečni limit (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Text("To je demo polje – dejansko povezavo z analizo bomo dodali, ko boš želel shranjevanje (npr. DataStore).")
        Button(onClick = { /* tukaj bi shranil v DataStore */ }) {
            Text("Shrani")
        }
    }
}
