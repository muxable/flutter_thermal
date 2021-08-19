package com.muxable.flutter.thermal

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

@RequiresApi(Build.VERSION_CODES.Q)
class ThermalPlugin : FlutterPlugin, EventChannel.StreamHandler,
    PowerManager.OnThermalStatusChangedListener {
    private lateinit var eventChannel: EventChannel
    private lateinit var methodChannel: MethodChannel
    private lateinit var powerManager: PowerManager
    private lateinit var sink: EventChannel.EventSink

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        powerManager =
            flutterPluginBinding.applicationContext.getSystemService(Context.POWER_SERVICE)
                    as PowerManager
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "thermal/events")
        eventChannel.setStreamHandler(this)
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "thermal")
        methodChannel.setMethodCallHandler({ call, result ->
            when (call.method) {
                "getThermalStatus" -> result.success(powerManager.currentThermalStatus)
                else -> result.notImplemented()
            }
        })
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        eventChannel.setStreamHandler(null)
        methodChannel.setMethodCallHandler(null)
    }

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
}
