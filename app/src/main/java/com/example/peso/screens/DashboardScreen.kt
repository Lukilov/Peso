package com.example.peso.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.peso.components.TransactionCard
import com.example.peso.model.FakeData
import com.example.peso.model.Transaction
import com.example.peso.model.dateLabel

@Composable
fun DashboardScreen() {
    val all: List<Transaction> = FakeData.transactions

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Zadnje transakcije", style = MaterialTheme.typography.titleLarge)

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(all.sortedByDescending { it.ts }.take(8)) { tx ->
                TransactionCard(tx = tx)
            }
        }

        Text(
            text = "Najnovejša: " + (all.maxByOrNull { it.ts }?.dateLabel ?: ""),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
