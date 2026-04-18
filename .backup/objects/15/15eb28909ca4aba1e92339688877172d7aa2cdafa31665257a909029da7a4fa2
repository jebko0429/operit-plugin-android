package com.operit.plugin.api

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import com.operit.plugin.data.ApiResponse
import com.operit.plugin.data.SmsMessage

/**
 * SMS message API
 */
class SmsApi(private val context: Context) {
    
    companion object {
        private val SMS_URI = Uri.parse("content://sms/")
        private const val COLUMN_ID = "_id"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_BODY = "body"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_READ = "read"
    }
    
    /**
     * Send SMS message
     */
    fun sendSms(phoneNumber: String, message: String, simSlot: Int = -1): ApiResponse {
        return try {
            val smsManager = if (simSlot >= 0 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
                if (subscriptionInfoList != null && simSlot < subscriptionInfoList.size) {
                    SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList[simSlot].subscriptionId)
                } else {
                    SmsManager.getDefault()
                }
            } else {
                SmsManager.getDefault()
            }
            
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    parts,
                    null,
                    null
                )
            } else {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
            }
            
            ApiResponse.success(mapOf(
                "sent" to true,
                "recipient" to phoneNumber,
                "messageLength" to message.length,
                "parts" to parts.size
            ))
        } catch (e: Exception) {
            ApiResponse.error("Failed to send SMS: ${e.message}")
        }
    }
    
    /**
     * Read SMS inbox
     */
    @SuppressLint("Recycle")
    fun readInbox(limit: Int = 50): ApiResponse {
        return try {
            val messages = mutableListOf<SmsMessage>()
            val projection = arrayOf(COLUMN_ID, COLUMN_ADDRESS, COLUMN_BODY, COLUMN_DATE, COLUMN_TYPE, COLUMN_READ)
            val cursor = context.contentResolver.query(
                SMS_URI,
                projection,
                "type = ?",
                arrayOf("1"), // Inbox messages
                "$COLUMN_DATE DESC LIMIT $limit"
            )
            
            cursor?.use {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val addressIndex = it.getColumnIndex(COLUMN_ADDRESS)
                val bodyIndex = it.getColumnIndex(COLUMN_BODY)
                val dateIndex = it.getColumnIndex(COLUMN_DATE)
                val typeIndex = it.getColumnIndex(COLUMN_TYPE)
                val readIndex = it.getColumnIndex(COLUMN_READ)
                
                while (it.moveToNext()) {
                    messages.add(SmsMessage(
                        id = it.getString(idIndex),
                        address = it.getString(addressIndex) ?: "",
                        body = it.getString(bodyIndex) ?: "",
                        date = it.getLong(dateIndex),
                        type = it.getInt(typeIndex),
                        read = it.getInt(readIndex) == 1
                    ))
                }
            }
            
            ApiResponse.success(mapOf(
                "messages" to messages,
                "count" to messages.size
            ))
        } catch (e: SecurityException) {
            ApiResponse.error("SMS permission denied. Grant READ_SMS permission.")
        } catch (e: Exception) {
            ApiResponse.error("Failed to read SMS inbox: ${e.message}")
        }
    }
    
    /**
     * Read SMS sent messages
     */
    @SuppressLint("Recycle")
    fun readSent(limit: Int = 50): ApiResponse {
        return try {
            val messages = mutableListOf<SmsMessage>()
            val projection = arrayOf(COLUMN_ID, COLUMN_ADDRESS, COLUMN_BODY, COLUMN_DATE, COLUMN_TYPE, COLUMN_READ)
            val cursor = context.contentResolver.query(
                SMS_URI,
                projection,
                "type = ?",
                arrayOf("2"), // Sent messages
                "$COLUMN_DATE DESC LIMIT $limit"
            )
            
            cursor?.use {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val addressIndex = it.getColumnIndex(COLUMN_ADDRESS)
                val bodyIndex = it.getColumnIndex(COLUMN_BODY)
                val dateIndex = it.getColumnIndex(COLUMN_DATE)
                val typeIndex = it.getColumnIndex(COLUMN_TYPE)
                val readIndex = it.getColumnIndex(COLUMN_READ)
                
                while (it.moveToNext()) {
                    messages.add(SmsMessage(
                        id = it.getString(idIndex),
                        address = it.getString(addressIndex) ?: "",
                        body = it.getString(bodyIndex) ?: "",
                        date = it.getLong(dateIndex),
                        type = it.getInt(typeIndex),
                        read = it.getInt(readIndex) == 1
                    ))
                }
            }
            
            ApiResponse.success(mapOf(
                "messages" to messages,
                "count" to messages.size
            ))
        } catch (e: SecurityException) {
            ApiResponse.error("SMS permission denied. Grant READ_SMS permission.")
        } catch (e: Exception) {
            ApiResponse.error("Failed to read sent SMS: ${e.message}")
        }
    }
    
    /**
     * Get SMS conversations (grouped by contact)
     */
    @SuppressLint("Recycle")
    fun getConversations(limit: Int = 20): ApiResponse {
        return try {
            val conversationsUri = Uri.parse("content://sms/conversations")
            val cursor = context.contentResolver.query(
                conversationsUri,
                null,
                null,
                null,
                "date DESC LIMIT $limit"
            )
            
            val conversations = mutableListOf<Map<String, Any>>()
            
            cursor?.use {
                val threadIdIndex = it.getColumnIndex("thread_id")
                val msgCountIndex = it.getColumnIndex("msg_count")
                val snippetIndex = it.getColumnIndex("snippet")
                val dateIndex = it.getColumnIndex("date")
                
                while (it.moveToNext()) {
                    conversations.add(mapOf(
                        "threadId" to (it.getLong(threadIdIndex)),
                        "messageCount" to (it.getInt(msgCountIndex)),
                        "snippet" to (it.getString(snippetIndex) ?: ""),
                        "date" to (it.getLong(dateIndex))
                    ))
                }
            }
            
            ApiResponse.success(mapOf(
                "conversations" to conversations,
                "count" to conversations.size
            ))
        } catch (e: Exception) {
            ApiResponse.error("Failed to get conversations: ${e.message}")
        }
    }
    
    /**
     * Delete SMS message by ID
     */
    fun deleteMessage(messageId: String): ApiResponse {
        return try {
            val uri = Uri.parse("$SMS_URI/$messageId")
            val rowsDeleted = context.contentResolver.delete(uri, null, null)
            
            if (rowsDeleted > 0) {
                ApiResponse.success(mapOf(
                    "deleted" to true,
                    "messageId" to messageId
                ))
            } else {
                ApiResponse.error("Message not found")
            }
        } catch (e: SecurityException) {
            ApiResponse.error("SMS permission denied")
        } catch (e: Exception) {
            ApiResponse.error("Failed to delete message: ${e.message}")
        }
    }
    
    /**
     * Mark SMS as read
     */
    fun markAsRead(messageId: String): ApiResponse {
        return try {
            val uri = Uri.parse("$SMS_URI/$messageId")
            val values = android.content.ContentValues().apply {
                put("read", 1)
            }
            val rowsUpdated = context.contentResolver.update(uri, values, null, null)
            
            if (rowsUpdated > 0) {
                ApiResponse.success(mapOf(
                    "marked" to true,
                    "messageId" to messageId
                ))
            } else {
                ApiResponse.error("Message not found")
            }
        } catch (e: SecurityException) {
            ApiResponse.error("SMS permission denied")
        } catch (e: Exception) {
            ApiResponse.error("Failed to mark as read: ${e.message}")
        }
    }
}