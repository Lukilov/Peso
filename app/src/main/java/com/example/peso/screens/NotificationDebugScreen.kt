package com.example.peso.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peso.notifications.LastNotification

@Composable
fun NotificationDebugScreen() {
    val last by LastNotification.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "Debug bančnih notifikacij",
            fontSize = 20.sp,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))

        if (last == null) {
            Text(
                text = "Še ni zaznane nobene notifikacije.",
                color = Color.Gray
            )
        } else {
            val d = last!!

            Text("App: ${d.app}", color = Color.White)
            Text("Datum: ${d.date}", color = Color.White)
            Spacer(Modifier.height(8.dp))

            Text("Naslov:", color = Color.Gray, fontSize = 12.sp)
            Text(d.title, color = Color.White)
            Spacer(Modifier.height(8.dp))

            Text("Besedilo:", color = Color.Gray, fontSize = 12.sp)
            Text(d.text, color = Color.White)
            Spacer(Modifier.height(8.dp))

            Text("Znesek: ${d.amount?.toPlainString() ?: "-"} €", color = Color.White)
            Text("Trgovec: ${d.merchant ?: "-"}", color = Color.White)
            Text("Kategorija: ${d.category ?: "-"}", color = Color.White)
            Text("Tip: ${if (d.isIncome) "Priliv" else "Odliv"}", color = Color.White)
        }
    }
}
