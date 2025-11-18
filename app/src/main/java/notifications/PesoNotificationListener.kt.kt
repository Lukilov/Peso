package com.example.peso.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.peso.model.FakeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class PesoNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "PesoNotifListener"
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener CONNECTED")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val app = sbn.packageName

        val extras = sbn.notification.extras
        val title = (extras.getCharSequence("android.title") ?: "").toString()
        val text  = (extras.getCharSequence("android.text")  ?: "").toString()

        val date: LocalDate = Instant.ofEpochMilli(sbn.postTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        Log.d(TAG, "Notification from $app | $title | $text")

        // 1) Preberi znesek + trgovca iz besedila
        val parsed = SimpleBankParser.parse(title, text)
        val amount: BigDecimal? = parsed.amount   // SimpleBankParser naj vrača BigDecimal?
        val merchant: String = parsed.merchant ?: "Kartično plačilo"

        // (zaenkrat) vse POS nakupe štejemo kot odlive, brez posebne kategorije
        val isIncome = false
        val category: String? = null   // tu bo kasneje LocalBankClassifier

        // 2) Če imamo znesek, dodaj transakcijo v FakeData -> gre v grafe
        if (amount != null) {
            FakeData.addFromNotification(
                amount   = amount,
                merchant = merchant,
                date     = date,
                isIncome = isIncome,
                category = category
            )
        }

        // 3) Shrani za debug screen
        LastNotification.update(
            LastNotification.Data(
                app      = app,
                title    = title,
                text     = text,
                date     = date,
                amount   = amount,
                merchant = parsed.merchant,
                category = category,
                isIncome = isIncome
            )
        )
    }
}

/** Zadnja zaznana notifikacija – za debug / UI. */
object LastNotification {

    data class Data(
        val app: String,
        val title: String,
        val text: String,
        val date: LocalDate,
        val amount: BigDecimal?,
        val merchant: String?,
        val category: String?,
        val isIncome: Boolean
    )

    private val _state = MutableStateFlow<Data?>(null)
    val state = _state.asStateFlow()

    fun update(d: Data) {
        _state.value = d
    }
}

//package com.example.peso.notifications
//
//import android.service.notification.NotificationListenerService
//import android.service.notification.StatusBarNotification
//import android.util.Log
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import java.math.BigDecimal
//import java.time.Instant
//import java.time.LocalDate
//import java.time.ZoneId
//
//class PesoNotificationListener : NotificationListenerService() {
//
//    companion object {
//        private const val TAG = "PesoNotifListener"
//    }
//
//    override fun onListenerConnected() {
//        super.onListenerConnected()
//        Log.d(TAG, "Notification listener CONNECTED")
//    }
//
//    // ⬇⬇⬇ TO je tisti onNotificationPosted – NE kličeš ga sam,
//    // ampak ga kliče Android, ko pride nova notifikacija
//    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        val app = sbn.packageName
//
//        val extras = sbn.notification.extras
//        val title = (extras.getCharSequence("android.title") ?: "").toString()
//        val text  = (extras.getCharSequence("android.text")  ?: "").toString()
//
//        val date = Instant.ofEpochMilli(sbn.postTime)
//            .atZone(ZoneId.systemDefault())
//            .toLocalDate()
//
//        Log.d(TAG, "Notification from $app | $title | $text")
//
//        // Preberi znesek + trgovca (tu je že podpora za OTP SMS)
//        val parsed = SimpleBankParser.parse(title, text)
//
//        LastNotification.update(
//            LastNotification.Data(
//                app      = app,
//                title    = title,
//                text     = text,
//                date     = date,
//                amount   = parsed.amount,
//                merchant = parsed.merchant,
//                category = null,       // zaenkrat še brez klasifikatorja
//                isIncome = false       // POS nakup -> odliv
//            )
//        )
//    }
//}
//
///** Zadnja zaznana notifikacija – za debug / UI. */
//object LastNotification {
//
//    data class Data(
//        val app: String,
//        val title: String,
//        val text: String,
//        val date: LocalDate,
//        val amount: BigDecimal?,
//        val merchant: String?,
//        val category: String?,
//        val isIncome: Boolean
//    )
//
//    private val _state = MutableStateFlow<Data?>(null)
//    val state = _state.asStateFlow()
//
//    fun update(d: Data) {
//        _state.value = d
//    }
//}
