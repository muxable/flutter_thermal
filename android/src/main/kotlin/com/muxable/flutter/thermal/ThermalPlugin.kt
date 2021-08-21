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

@RequiresApi(Build.VERSION_CODES.Q)
class ThermalPlugin : FlutterPlugin {
    private lateinit var stateEventChannel: EventChannel
    private lateinit var methodChannel: MethodChannel
    private lateinit var batteryTemperatureEventChannel: EventChannel
    private lateinit var powerManager: PowerManager

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        powerManager =
            flutterPluginBinding.applicationContext.getSystemService(Context.POWER_SERVICE)
                    as PowerManager
        stateEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "thermal/events")
        stateEventChannel.setStreamHandler(object : EventChannel.StreamHandler,
            PowerManager.OnThermalStatusChangedListener {
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
        batteryTemperatureEventChannel =
            EventChannel(flutterPluginBinding.binaryMessenger, "thermal/battery_temp/events")
        batteryTemperatureEventChannel.setStreamHandler(object : EventChannel.StreamHandler,
            BroadcastReceiver() {
            private lateinit var sink: EventChannel.EventSink

            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                sink = events
                flutterPluginBinding.applicationContext.registerReceiver(
                    this,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                )
            }

            override fun onCancel(arguments: Any?) {
                flutterPluginBinding.applicationContext.unregisterReceiver(this)
            }

            override fun onReceive(context: Context?, intent: Intent) {
                sink.success(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toDouble() / 10)
            }
        })
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "thermal")
        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "getThermalStatus" -> result.success(powerManager.currentThermalStatus)
                else -> result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        stateEventChannel.setStreamHandler(null)
        batteryTemperatureEventChannel.setStreamHandler(null)
        methodChannel.setMethodCallHandler(null)
    }
}
