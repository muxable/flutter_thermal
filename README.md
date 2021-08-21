# thermal

[![pub package](https://img.shields.io/pub/v/thermal.svg)](https://pub.dev/packages/thermal)

## Usage

To use this plugin, add `thermal` as a [dependency in your pubspec.yaml file](https://flutter.dev/docs/development/platform-integration/platform-channels).

### Example

```dart
// Import package
import 'package:thermal/thermal.dart';

// Instantiate it
var _thermal = Thermal();

// Access current thermal status
print(await _thermal.thermalStatus);

// Be informed when the status changes
_thermal.onThermalStatusChanged.listen((ThermalStatus state) {
  // Do something with new status
});

// Watch the battery temperature
_thermal.onBatteryTemperatureChanged.listen((double temperature) {
  // Do something wth the temperature
});
```
