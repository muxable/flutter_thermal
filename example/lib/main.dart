import 'package:flutter/material.dart';
import 'package:thermal/thermal.dart';

void main() {
  runApp(const App());
}

class App extends StatelessWidget {
  const App({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Thermal example app'),
        ),
        body: Center(
          child: Column(children: [
            FutureBuilder<ThermalStatus>(
                future: Thermal().thermalStatus,
                builder: (context, snapshot) {
                  return Text("Thermal status: ${snapshot.data}");
                }),
            StreamBuilder<ThermalStatus>(
                stream: Thermal().onThermalStatusChanged,
                builder: (context, snapshot) {
                  return Text("Live thermal status: ${snapshot.data}");
                }),
            StreamBuilder<double>(
                stream: Thermal().onBatteryTemperatureChanged,
                builder: (context, snapshot) {
                  return Text("Battery temperature: ${snapshot.data}°C");
                })
          ]),
        ),
      ),
    );
  }
}
