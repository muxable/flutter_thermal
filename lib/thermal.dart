import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

/// Thermal statuses of the device.
enum ThermalStatus {
  /// The device is operating nominally with no throttling.
  none,

  /// The device is throttling where UX is not impacted.
  light,

  /// The device is throttling where UX is not largely impacted. Android only.
  moderate,

  /// The device is throttling where UX is impacted.
  severe,

  /// The device is throttling where everything is being done to reduce power.
  critical,

  /// Key components in the platform are shutting down. Android only.
  emergency,

  /// The device is shutting down immediately. Android only.
  shutdown
}

class Thermal {
  static const _eventChannel = EventChannel('thermal/events');
  static const _batteryTemperatureChannel =
      EventChannel('thermal/battery_temp/events');
  static const _methodChannel = MethodChannel('thermal');

  static _parseThermalStatus(dynamic status) {
    switch (status) {
      case 1:
        return ThermalStatus.light;
      case 2:
        return ThermalStatus.moderate;
      case 3:
        return ThermalStatus.severe;
      case 4:
        return ThermalStatus.critical;
      case 5:
        return ThermalStatus.emergency;
      case 6:
        return ThermalStatus.shutdown;
      default:
        return ThermalStatus.none;
    }
  }

  Future<ThermalStatus> get thermalStatus {
    return _methodChannel
        .invokeMethod("getThermalStatus")
        .then((dynamic status) => _parseThermalStatus(status));
  }

  /// Retrieves the [ThermalStatus] of the device.
  Stream<ThermalStatus> get onThermalStatusChanged async* {
    yield await thermalStatus;
    yield* _eventChannel
        .receiveBroadcastStream()
        .map((dynamic status) => _parseThermalStatus(status));
  }

  /// Retrieves the battery temperature in Celsius. Android only.
  Stream<double> get onBatteryTemperatureChanged async* {
    if (!Platform.isAndroid) {
      return;
    }
    yield* _batteryTemperatureChannel.receiveBroadcastStream().cast();
  }
}
