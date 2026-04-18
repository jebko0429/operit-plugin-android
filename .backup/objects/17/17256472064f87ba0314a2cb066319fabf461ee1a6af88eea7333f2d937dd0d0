package com.operit.plugin.api

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.operit.plugin.data.ApiResponse

/**
 * Phone Call API for making and managing calls
 */
class CallApi(private val context: Context) {
    
    companion object {
        private const val TAG = "CallApi"
    }
    
    /**
     * Initiate a phone call
     */
    fun makeCall(phoneNumber: String): ApiResponse {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != 
                PackageManager.PERMISSION_GRANTED) {
                return ApiResponse.error("CALL_PHONE permission not granted")
            }
            
            val normalizedNumber = phoneNumber.replace(" ", "").replace("-", "")
            
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$normalizedNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(intent)
            
            ApiResponse.success(mapOf(
                "status" to "call_initiated",
                "number" to normalizedNumber,
                "message" to "Dialer opened with number"
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to make call: ${e.message}")
            ApiResponse.error("Failed to make call: ${e.message}")
        }
    }
    
    /**
     * Open dialer with number (doesn't require permission)
     */
    fun openDialer(phoneNumber: String): ApiResponse {
        return try {
            val normalizedNumber = phoneNumber.replace(" ", "").replace("-", "")
            
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$normalizedNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(intent)
            
            ApiResponse.success(mapOf(
                "status" to "dialer_opened",
                "number" to normalizedNumber
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open dialer: ${e.message}")
            ApiResponse.error("Failed to open dialer: ${e.message}")
        }
    }
    
    /**
     * Get telephony info
     */
    fun getTelephonyInfo(): ApiResponse {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                ?: return ApiResponse.error("Telephony service not available")
            
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != 
                PackageManager.PERMISSION_GRANTED) {
                return ApiResponse.error("READ_PHONE_STATE permission not granted")
            }
            
            ApiResponse.success(mapOf(
                "simState" to telephonyManager.simState,
                "simOperatorName" to telephonyManager.simOperatorName,
                "networkOperatorName" to telephonyManager.networkOperatorName,
                "networkType" to telephonyManager.networkType,
                "phoneType" to telephonyManager.phoneType,
                "isSmsCapable" to telephonyManager.isSmsCapable,
                "lineNumber" to (if (telephonyManager.line1Number != null) telephonyManager.line1Number else "unavailable")
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get telephony info: ${e.message}")
            ApiResponse.error("Failed to get telephony info: ${e.message}")
        }
    }
    
    /**
     * End current call (requires accessibility service or root on stock Android)
     * Returns info about alternatives
     */
    fun endCallInfo(): ApiResponse {
        return ApiResponse.success(mapOf(
            "status" to "info",
            "message" to "Ending calls requires either: 1) Accessibility service (requires user setup), 2) Root access, or 3) Device admin app. Consider using am start -a android.intent.action.CALL_END if your device supports it."
        ))
    }
}