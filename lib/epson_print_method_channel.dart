import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'epson_print_platform_interface.dart';

/// An implementation of [EpsonPrintPlatform] that uses method channels.
class MethodChannelEpsonPrint extends EpsonPrintPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('epson_print');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
