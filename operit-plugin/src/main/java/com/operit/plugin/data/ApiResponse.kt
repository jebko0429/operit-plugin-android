package com.operit.plugin.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * Standard API response wrapper for all plugin operations
 */
data class ApiResponse(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("data") val data: JsonElement? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private val gson = Gson()
        
        fun success(data: Any? = null): ApiResponse {
            return ApiResponse(
                success = true,
                data = data?.let { gson.toJsonTree(it) },
                error = null
            )
        }
        
        fun error(message: String, data: Any? = null): ApiResponse {
            return ApiResponse(
                success = false,
                data = data?.let { gson.toJsonTree(it) },
                error = message
            )
        }
        
        fun fromJson(json: String): ApiResponse {
            return gson.fromJson(json, ApiResponse::class.java)
        }
    }
    
    fun toJson(): String = Gson().toJson(this)
}

data class DeviceInfo(
    @SerializedName("manufacturer") val manufacturer: String,
    @SerializedName("model") val model: String,
    @SerializedName("device") val device: String,
    @SerializedName("product") val product: String,
    @SerializedName("androidVersion") val androidVersion: String,
    @SerializedName("sdkInt") val sdkInt: Int,
    @SerializedName("brand") val brand: String,
    @SerializedName("hardware") val hardware: String,
    @SerializedName("serial") val serial: String,
    @SerializedName("id") val id: String,
    @SerializedName("bootloader") val bootloader: String,
    @SerializedName("board") val board: String,
    @SerializedName("fingerprint") val fingerprint: String
)

data class BatteryInfo(
    @SerializedName("level") val level: Int,
    @SerializedName("scale") val scale: Int,
    @SerializedName("percentage") val percentage: Int,
    @SerializedName("status") val status: String,
    @SerializedName("isCharging") val isCharging: Boolean,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("voltage") val voltage: Int,
    @SerializedName("technology") val technology: String,
    @SerializedName("health") val health: String,
    @SerializedName("plugged") val plugged: String
)

data class SmsMessage(
    @SerializedName("id") val id: String,
    @SerializedName("address") val address: String,
    @SerializedName("body") val body: String,
    @SerializedName("date") val date: Long,
    @SerializedName("type") val type: Int,
    @SerializedName("read") val read: Boolean
)

data class WifiInfo(
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("connected") val connected: Boolean,
    @SerializedName("ssid") val ssid: String?,
    @SerializedName("bssid") val bssid: String?,
    @SerializedName("ipAddress") val ipAddress: String?,
    @SerializedName("linkSpeed") val linkSpeed: Int,
    @SerializedName("rssi") val rssi: Int,
    @SerializedName("frequency") val frequency: Int
)

data class StorageInfo(
    @SerializedName("totalInternal") val totalInternal: Long,
    @SerializedName("availableInternal") val availableInternal: Long,
    @SerializedName("usedInternal") val usedInternal: Long,
    @SerializedName("totalExternal") val totalExternal: Long?,
    @SerializedName("availableExternal") val availableExternal: Long?
)

data class MemoryInfo(
    @SerializedName("totalRam") val totalRam: Long,
    @SerializedName("availableRam") val availableRam: Long,
    @SerializedName("lowMemory") val lowMemory: Boolean,
    @SerializedName("threshold") val threshold: Long,
    @SerializedName("usedRam") val usedRam: Long
)

data class AudioInfo(
    @SerializedName("musicVolume") val musicVolume: Int,
    @SerializedName("maxMusicVolume") val maxMusicVolume: Int,
    @SerializedName("ringVolume") val ringVolume: Int,
    @SerializedName("maxRingVolume") val maxRingVolume: Int,
    @SerializedName("notificationVolume") val notificationVolume: Int,
    @SerializedName("maxNotificationVolume") val maxNotificationVolume: Int,
    @SerializedName("alarmVolume") val alarmVolume: Int,
    @SerializedName("maxAlarmVolume") val maxAlarmVolume: Int,
    @SerializedName("ringerMode") val ringerMode: String,
    @SerializedName("isSpeakerphoneOn") val isSpeakerphoneOn: Boolean,
    @SerializedName("isMicrophoneMute") val isMicrophoneMute: Boolean
)