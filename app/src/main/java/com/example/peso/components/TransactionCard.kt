package com.example.peso.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.peso.model.Transaction
import com.example.peso.model.displayTitle
import com.example.peso.model.dateLabel
import com.example.peso.model.category
import java.math.RoundingMode

@Composable
fun TransactionCard(
    tx: Transaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.Payment, contentDescription = "transaction")

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = tx.dateLabel, style = MaterialTheme.typography.bodySmall)
                Text(text = tx.category, style = MaterialTheme.typography.bodySmall)
            }

            val sign = if (tx.isIncome) "+" else "-"
            val pretty = tx.amount.abs().setScale(2, RoundingMode.HALF_UP)
            Text(
                text = "$sign$pretty €",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
