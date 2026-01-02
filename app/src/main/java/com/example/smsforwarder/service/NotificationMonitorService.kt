package com.example.smsforwarder.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.smsforwarder.data.RuleRepository

class NotificationMonitorService : NotificationListenerService() {

    private val TAG = "NotificationMonitor"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        // Filter for common SMS/RCS apps. 
        // com.samsung.android.messaging (Samsung Messages)
        // com.google.android.apps.messaging (Google Messages)
        // com.android.mms (Generic)
        if (packageName == "com.google.android.apps.messaging" || 
            packageName == "com.samsung.android.messaging" ||
            packageName == "com.android.mms") {
            
            val extras = sbn.notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            
            Log.d(TAG, "Notification from $packageName: Title=$title, Text=$text")

            if (title.isNotEmpty() && text.isNotEmpty()) {
                processMessage(title, text)
            }
        }
    }

    private fun processMessage(sender: String, body: String) {
        val repository = RuleRepository(this)
        val rules = repository.getRules().filter { it.isEnabled }

        rules.forEach { rule ->
            // Title usually contains the sender name or number in notifications
            if (sender.contains(rule.incomingNumber) || rule.incomingNumber == "*") {
                 val forwardedBody = "Fwd(RCS) from $sender: $body"
                 SmsSender.forwardSms(rule.forwardToNumber, forwardedBody)
            }
        }
    }
}
