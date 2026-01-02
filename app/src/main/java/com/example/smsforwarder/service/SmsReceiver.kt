package com.example.smsforwarder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.smsforwarder.data.RuleRepository

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val repository = RuleRepository(context)
            val rules = repository.getRules().filter { it.isEnabled }

            messages?.forEach { message ->
                val sender = message.originatingAddress ?: return@forEach
                val body = message.messageBody ?: ""

                Log.d("SmsReceiver", "Received SMS from $sender: $body")

                rules.forEach { rule ->
                    // Simple matching: check if sender contains the rule's incoming number
                    // or exact match. Let's do exact match or endsWith for now to be safe.
                    // Ideally, we should normalize phone numbers.
                    if (sender.endsWith(rule.incomingNumber) || rule.incomingNumber == "*") {
                        val forwardedBody = "Fwd from $sender: $body"
                        SmsSender.forwardSms(rule.forwardToNumber, forwardedBody)
                    }
                }
            }
        } else if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("SmsReceiver", "Boot completed, receiver is ready.")
        }
    }
}
