package com.operit.plugin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.operit.plugin.server.OperitApiService
import com.operit.plugin.ui.theme.OperitPluginTheme

class MainActivity : ComponentActivity() {
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            startApiService()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request permissions
        checkAndRequestPermissions()
        
        setContent {
            OperitPluginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onStartService = { startApiService() },
                        onStopService = { stopApiService() },
                        isServiceRunning = OperitApiService.isRunning
                    )
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != 
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SEND_SMS)
            permissions.add(Manifest.permission.READ_SMS)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 
                PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                    PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            // All permissions granted, can auto-start service
        }
    }
    
    private fun startApiService() {
        val intent = Intent(this, OperitApiService::class.java).apply {
            action = OperitApiService.ACTION_START
        }
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Operit API Service started on port 8765", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopApiService() {
        val intent = Intent(this, OperitApiService::class.java).apply {
            action = OperitApiService.ACTION_STOP
        }
        startService(intent)
        Toast.makeText(this, "Operit API Service stopped", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MainScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    isServiceRunning: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Operit Plugin",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Android API Bridge",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isServiceRunning) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isServiceRunning) "● Service Running" else "○ Service Stopped",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isServiceRunning) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onErrorContainer
                )
                
                if (isServiceRunning) {
                    Text(
                        text = "API available at: http://localhost:8765",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onStartService,
                enabled = !isServiceRunning
            ) {
                Text("Start Service")
            }
            
            Button(
                onClick = onStopService,
                enabled = isServiceRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Stop Service")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Available APIs:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val apis = listOf(
            "/api/device/info", "/api/device/battery", "/api/device/brightness",
            "/api/flashlight/on", "/api/flashlight/off", "/api/flashlight/toggle",
            "/api/sms/send", "/api/sms/inbox",
            "/api/audio/info", "/api/audio/volume/music",
            "/api/audio/ringer", "/api/audio/play/notification"
        )
        
        apis.forEach { api ->
            Text(
                text = api,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}