package com.example.smsforwarder.data

data class Rule(
    val id: String = java.util.UUID.randomUUID().toString(),
    val incomingNumber: String,
    val forwardToNumber: String,
    var isEnabled: Boolean = true
)
