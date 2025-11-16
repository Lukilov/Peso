package com.example.peso.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peso.model.BudgetViewModel
import com.example.peso.model.Transaction
import java.math.BigDecimal

@Composable
fun TransactionsScreen(vm: BudgetViewModel) {
    val transactions: List<Transaction> = vm.transactions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Zadnje transakcije",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(transactions) { tx ->
                TransactionCard(tx = tx)
            }
        }
    }
}

@Composable
fun TransactionCard(tx: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tx.name,
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "${tx.amount.toPlainString()} €",
                color = if (tx.amount < BigDecimal.ZERO) Color.Red else Color.Green,
                fontSize = 16.sp
            )
        }
    }
}
