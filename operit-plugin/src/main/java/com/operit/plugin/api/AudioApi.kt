package com.operit.plugin.api

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import com.operit.plugin.data.ApiResponse
import com.operit.plugin.data.AudioInfo

/**
 * Audio and volume control API
 */
class AudioApi(context: Context) {
    
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    
    /**
     * Get current audio/volume information
     */
    fun getAudioInfo(): ApiResponse {
        return try {
            audioManager?.let { am ->
                val ringerMode = when (am.ringerMode) {
                    AudioManager.RINGER_MODE_NORMAL -> "normal"
                    AudioManager.RINGER_MODE_SILENT -> "silent"
                    AudioManager.RINGER_MODE_VIBRATE -> "vibrate"
                    else -> "unknown"
                }
                
                ApiResponse.success(AudioInfo(
                    musicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC),
                    maxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    ringVolume = am.getStreamVolume(AudioManager.STREAM_RING),
                    maxRingVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING),
                    notificationVolume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
                    maxNotificationVolume = am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
                    alarmVolume = am.getStreamVolume(AudioManager.STREAM_ALARM),
                    maxAlarmVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                    ringerMode = ringerMode,
                    isSpeakerphoneOn = am.isSpeakerphoneOn,
                    isMicrophoneMute = am.isMicrophoneMute
                ))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to get audio info: ${e.message}")
        }
    }
    
    /**
     * Set music/media volume
     */
    fun setMusicVolume(level: Int): ApiResponse {
        return try {
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val volume = level.coerceIn(0, maxVolume)
                am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
                ApiResponse.success(mapOf(
                    "volume" to volume,
                    "max" to maxVolume,
                    "type" to "music"
                ))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to set music volume: ${e.message}")
        }
    }
    
    /**
     * Set ringtone volume
     */
    fun setRingVolume(level: Int): ApiResponse {
        return try {
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING)
                val volume = level.coerceIn(0, maxVolume)
                am.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI)
                ApiResponse.success(mapOf(
                    "volume" to volume,
                    "max" to maxVolume,
                    "type" to "ring"
                ))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to set ring volume: ${e.message}")
        }
    }
    
    /**
     * Set notification volume
     */
    fun setNotificationVolume(level: Int): ApiResponse {
        return try {
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
                val volume = level.coerceIn(0, maxVolume)
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, AudioManager.FLAG_SHOW_UI)
                ApiResponse.success(mapOf(
                    "volume" to volume,
                    "max" to maxVolume,
                    "type" to "notification"
                ))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to set notification volume: ${e.message}")
        }
    }
    
    /**
     * Set alarm volume
     */
    fun setAlarmVolume(level: Int): ApiResponse {
        return try {
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                val volume = level.coerceIn(0, maxVolume)
                am.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_SHOW_UI)
                ApiResponse.success(mapOf(
                    "volume" to volume,
                    "max" to maxVolume,
                    "type" to "alarm"
                ))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to set alarm volume: ${e.message}")
        }
    }
    
    /**
     * Set ringer mode (normal/silent/vibrate)
     */
    fun setRingerMode(mode: String): ApiResponse {
        return try {
            val ringerMode = when (mode.lowercase()) {
                "normal" -> AudioManager.RINGER_MODE_NORMAL
                "silent" -> AudioManager.RINGER_MODE_SILENT
                "vibrate" -> AudioManager.RINGER_MODE_VIBRATE
                else -> return ApiResponse.error("Invalid mode. Use: normal, silent, vibrate")
            }
            
            audioManager?.ringerMode = ringerMode
            ApiResponse.success(mapOf(
                "mode" to mode,
                "ringerMode" to ringerMode
            ))
        } catch (e: Exception) {
            ApiResponse.error("Failed to set ringer mode: ${e.message}")
        }
    }
    
    /**
     * Toggle speakerphone
     */
    fun setSpeakerphone(enabled: Boolean): ApiResponse {
        return try {
            audioManager?.isSpeakerphoneOn = enabled
            ApiResponse.success(mapOf("speakerphone" to enabled))
        } catch (e: Exception) {
            ApiResponse.error("Failed to set speakerphone: ${e.message}")
        }
    }
    
    /**
     * Toggle microphone mute
     */
    fun setMicrophoneMute(muted: Boolean): ApiResponse {
        return try {
            audioManager?.isMicrophoneMute = muted
            ApiResponse.success(mapOf("microphoneMuted" to muted))
        } catch (e: Exception) {
            ApiResponse.error("Failed to set microphone mute: ${e.message}")
        }
    }
    
    /**
     * Play notification sound
     */
    fun playNotification(): ApiResponse {
        return try {
            audioManager?.let { am ->
                val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(null, notificationUri)
                ringtone?.play()
                ApiResponse.success(mapOf("played" to true))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to play notification: ${e.message}")
        }
    }
    
    /**
     * Play ringtone
     */
    fun playRingtone(): ApiResponse {
        return try {
            audioManager?.let { am ->
                val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                val ringtone = RingtoneManager.getRingtone(null, ringtoneUri)
                ringtone?.play()
                ApiResponse.success(mapOf("played" to true))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to play ringtone: ${e.message}")
        }
    }
    
    /**
     * Play alarm sound
     */
    fun playAlarm(): ApiResponse {
        return try {
            audioManager?.let { am ->
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val ringtone = RingtoneManager.getRingtone(null, alarmUri)
                ringtone?.play()
                ApiResponse.success(mapOf("played" to true))
            } ?: ApiResponse.error("Audio service not available")
        } catch (e: Exception) {
            ApiResponse.error("Failed to play alarm: ${e.message}")
        }
    }
}