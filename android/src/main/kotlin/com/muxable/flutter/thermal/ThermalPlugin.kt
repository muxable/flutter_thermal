package com.muxable.flutter.thermal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class ThermalPlugin : FlutterPlugin {
    private lateinit var stateEventChannel: EventChannel
    private lateinit var methodChannel: MethodChannel
    private lateinit var batteryTemperatureEventChannel: EventChannel
    
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Set up battery temperature event channel (works on all Android versions)
        batteryTemperatureEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "thermal/battery_temp/events")
        batteryTemperatureEventChannel.setStreamHandler(object : EventChannel.StreamHandler, BroadcastReceiver() {
            private lateinit var sink: EventChannel.EventSink
            
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                sink = events
                flutterPluginBinding.applicationContext.registerReceiver(
                    this, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                )
            }
            
            override fun onCancel(arguments: Any?) {
                flutterPluginBinding.applicationContext.unregisterReceiver(this)
            }
            
            override fun onReceive(context: Context?, intent: Intent) {
                sink.success(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toDouble() / 10)
            }
        })
        
        // Set up thermal status channels only for Android 10+ (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setupThermalStatusHandlers(flutterPluginBinding)
        } else {
            // For older Android versions, set up dummy handlers that report unsupported
            setupLegacyThermalStatusHandlers(flutterPluginBinding)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupThermalStatusHandlers(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val powerManager = flutterPluginBinding.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        
        // Thermal status event channel
        stateEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "thermal/events")
        stateEventChannel.setStreamHandler(object : EventChannel.StreamHandler, PowerManager.OnThermalStatusChangedListener {
            private lateinit var sink: EventChannel.EventSink
            
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                sink = events
                powerManager.addThermalStatusListener(this)
            }
            
            override fun onCancel(arguments: Any?) {
                powerManager.removeThermalStatusListener(this)
            }
            
            override fun onThermalStatusChanged(status: Int) {
                sink.success(status)
            }
        })
        
        // Method channel with thermal status methods
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "thermal")
        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "getThermalStatus" -> result.success(powerManager.currentThermalStatus)
                else -> result.notImplemented()
            }
        }
    }
    
    private fun setupLegacyThermalStatusHandlers(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Provide dummy implementation for thermal status event channel
        stateEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "thermal/events")
        stateEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                // Send an initial THERMAL_STATUS_NONE (0) status for Android 9 and below
                events.success(PowerManager.THERMAL_STATUS_NONE)
            }
            
            override fun onCancel(arguments: Any?) {
                // Nothing to clean up
            }
        })
        
        // Method channel with appropriate responses for unsupported devices
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "thermal")
        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "getThermalStatus" -> result.success(PowerManager.THERMAL_STATUS_NONE) // Always return THERMAL_STATUS_NONE for unsupported devices
                else -> result.notImplemented()
            }
        }
    }
    
    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        stateEventChannel.setStreamHandler(null)
        batteryTemperatureEventChannel.setStreamHandler(null)
        
        if (::methodChannel.isInitialized) {
            methodChannel.setMethodCallHandler(null)
        }
    }
}