package com.operit.plugin.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * BroadcastReceiver for incoming SMS messages
 */
class SmsReceivedReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "SmsReceivedReceiver"
        val receivedMessages = mutableListOf<Map<String, Any>>()
        private const val MAX_MESSAGES = 100
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                messages?.forEach { smsMessage ->
                    val messageData = mapOf(
                        "address" to (smsMessage.originatingAddress ?: "unknown"),
                        "body" to (smsMessage.messageBody ?: ""),
                        "timestamp" to System.currentTimeMillis(),
                        "date" to smsMessage.timestampMillis
                    )
                    
                    synchronized(receivedMessages) {
                        receivedMessages.add(0, messageData)
                        // Keep only last MAX_MESSAGES
                        while (receivedMessages.size > MAX_MESSAGES) {
                            receivedMessages.removeAt(receivedMessages.size - 1)
                        }
                    }
                    
                    Log.d(TAG, "SMS received from ${smsMessage.originatingAddress}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing SMS: ${e.message}")
            }
        }
    }
    
    fun getPendingMessages(clear: Boolean = true): List<Map<String, Any>> {
        return synchronized(receivedMessages) {
            val messages = receivedMessages.toList()
            if (clear) {
                receivedMessages.clear()
            }
            messages
        }
    }
}