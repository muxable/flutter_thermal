#import "ThermalPlugin.h"
#if __has_include(<thermal/thermal-Swift.h>)
#import <thermal/thermal-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "thermal-Swift.h"
#endif

@implementation ThermalPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftThermalPlugin registerWithRegistrar:registrar];
}
@end
