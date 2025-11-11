package com.example.peso.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.peso.components.TransactionCard
import com.example.peso.model.BudgetViewModel
import com.example.peso.model.Transaction

@Composable
fun TransactionsScreen(vm: BudgetViewModel) {
    val transactions: List<Transaction> = vm.transactions  // naj bo List<Transaction>
    val sorted = transactions.sortedByDescending { it.date } // ali .sortedByDescending { it.amount }

    LazyColumn {
        items(sorted) { tx ->
            TransactionCard(tx = tx)
        }
    }
}
