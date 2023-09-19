import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'epson_print_method_channel.dart';

abstract class EpsonPrintPlatform extends PlatformInterface {
  /// Constructs a EpsonPrintPlatform.
  EpsonPrintPlatform() : super(token: _token);

  static final Object _token = Object();

  static EpsonPrintPlatform _instance = MethodChannelEpsonPrint();

  /// The default instance of [EpsonPrintPlatform] to use.
  ///
  /// Defaults to [MethodChannelEpsonPrint].
  static EpsonPrintPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [EpsonPrintPlatform] when
  /// they register themselves.
  static set instance(EpsonPrintPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
