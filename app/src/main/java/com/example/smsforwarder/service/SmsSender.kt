package com.example.smsforwarder.service

import android.telephony.SmsManager
import android.util.Log

object SmsSender {
    private const val TAG = "SmsSender"

    fun forwardSms(destinationAddress: String, messageBody: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(messageBody)
            smsManager.sendMultipartTextMessage(destinationAddress, null, parts, null, null)
            Log.d(TAG, "Forwarded message to $destinationAddress")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to forward message", e)
        }
    }
}
