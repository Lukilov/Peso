package com.example.peso.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.peso.model.BudgetViewModel
import java.math.BigDecimal

@Composable
fun SettingsScreen(vm: BudgetViewModel) {
    val currentLimit = vm.monthlyLimit ?: BigDecimal("800.00")

    var text by remember { mutableStateOf(currentLimit.toPlainString()) }
    var showSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nastavitve", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Mesečni limit (€)")
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = text,
            onValueChange = { input ->
                val cleaned = input.filter { it.isDigit() || it == '.' || it == ',' }
                text = cleaned
                showSaved = false
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val normalized = text.replace(',', '.')
                try {
                    val value = BigDecimal(normalized)
                    if (value > BigDecimal.ZERO) {
                        vm.monthlyLimit = value
                        showSaved = true
                    }
                } catch (_: Exception) {
                    // nevaliden vnos – ignoriraj
                }
            }
        ) {
            Text("Shrani")
        }

        if (showSaved) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Limit posodobljen.",
                color = Color(0xFF4CAF50),
                fontSize = 12.sp
            )
        }
    }
}
