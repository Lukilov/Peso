package com.example.peso.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preprost listener, ki iz vsake nove notifikacije prebere naslov in besedilo.
 * TU kasneje dodaš natančnejši parser za tvojo banko.
 */
class PesoNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = (extras.getCharSequence("android.title") ?: "").toString()
        val text = (extras.getCharSequence("android.text") ?: "").toString()
        val app = sbn.packageName

        val parsed = SimpleBankParser.parse(title, text)

        LastNotification.update(
            LastNotification.Data(
                app = app,
                title = title,
                text = text,
                amount = parsed.amount,
                merchant = parsed.merchant
            )
        )
    }
}

/** Zadnja notifikacija za prikaz v UI. */
object LastNotification {
    data class Data(
        val app: String,
        val title: String,
        val text: String,
        val amount: String? = null,
        val merchant: String? = null
    )
    private val _state = MutableStateFlow<Data?>(null)
    val state = _state.asStateFlow()
    fun update(d: Data) { _state.value = d }
}

/**
 * Zelo preprost (demo) parser: išče € ali EUR in številke, ter možnega trgovca.
 * Prilagodi regexe glede na dejanski format tvoje banke.
 */
object SimpleBankParser {
    private val amountRegex = Regex("""(?i)(?:€\s*|EUR\s*)(\d+(?:[.,]\d{1,2})?)""")
    private val merchantRegex = Regex("""(?i)(?:pri|at|merchant|trgovec)\s*[:\-]?\s*([A-Z0-9\-\s&\.]{3,})""")

    data class Parsed(val amount: String?, val merchant: String?)
    fun parse(title: String, text: String): Parsed {
        val joined = "$title $text"
        val amount = amountRegex.find(joined)?.groupValues?.getOrNull(1)?.replace(',', '.')
        val merchant = merchantRegex.find(joined)?.groupValues?.getOrNull(1)?.trim()
        return Parsed(amount, merchant)
    }
}
