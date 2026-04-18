package com.operit.plugin.api

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import com.operit.plugin.data.ApiResponse

/**
 * Flashlight/Torch control API
 */
class FlashlightApi(context: Context) {
    
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var cameraId: String? = null
    private var isFlashlightOn = false
    
    init {
        setupCamera()
    }
    
    private fun setupCamera() {
        try {
            cameraManager?.cameraIdList?.forEach { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                
                if (flashAvailable && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Check if flashlight is available
     */
    fun isAvailable(): ApiResponse {
        return try {
            val isFlashAvailable = cameraManager != null && cameraId != null
            ApiResponse.success(mapOf(
                "available" to isFlashAvailable,
                "enabled" to isFlashlightOn
            ))
        } catch (e: Exception) {
            ApiResponse.error("Failed to check flashlight availability: ${e.message}")
        }
    }
    
    /**
     * Turn flashlight on
     */
    fun turnOn(): ApiResponse {
        return try {
            if (cameraId == null) {
                return ApiResponse.error("Flashlight not available on this device")
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager?.setTorchMode(cameraId!!, true)
                isFlashlightOn = true
                ApiResponse.success(mapOf(
                    "enabled" to true,
                    "message" to "Flashlight turned on"
                ))
            } else {
                ApiResponse.error("Flashlight control requires Android 6.0+")
            }
        } catch (e: CameraAccessException) {
            ApiResponse.error("Camera access error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.error("Failed to turn on flashlight: ${e.message}")
        }
    }
    
    /**
     * Turn flashlight off
     */
    fun turnOff(): ApiResponse {
        return try {
            if (cameraId == null) {
                return ApiResponse.error("Flashlight not available on this device")
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager?.setTorchMode(cameraId!!, false)
                isFlashlightOn = false
                ApiResponse.success(mapOf(
                    "enabled" to false,
                    "message" to "Flashlight turned off"
                ))
            } else {
                ApiResponse.error("Flashlight control requires Android 6.0+")
            }
        } catch (e: CameraAccessException) {
            ApiResponse.error("Camera access error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.error("Failed to turn off flashlight: ${e.message}")
        }
    }
    
    /**
     * Toggle flashlight state
     */
    fun toggle(): ApiResponse {
        return if (isFlashlightOn) {
            turnOff()
        } else {
            turnOn()
        }
    }
    
    /**
     * Get current flashlight state
     */
    fun getState(): ApiResponse {
        return try {
            ApiResponse.success(mapOf(
                "available" to (cameraId != null),
                "enabled" to isFlashlightOn
            ))
        } catch (e: Exception) {
            ApiResponse.error("Failed to get flashlight state: ${e.message}")
        }
    }
}