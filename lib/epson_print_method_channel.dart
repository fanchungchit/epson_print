import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'epson_print_platform_interface.dart';
import 'epson_printer.dart';

/// An implementation of [EpsonPrintPlatform] that uses method channels.
class MethodChannelEpsonPrint extends EpsonPrintPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('epson_print');

  @override
  Future<List<EpsonPrinter>> discovery() async {
    final List<dynamic> printers =
        await methodChannel.invokeMethod('discovery');
    return printers
        .map((e) => EpsonPrinter.fromJson(Map<String, dynamic>.from(e)))
        .toList();
  }

  @override
  Future<bool> printImage({
    required EpsonPrinter printer,
    required List<int> image,
    int copies = 1,
    bool withDrawer = false,
  }) async {
    final result = await methodChannel.invokeMethod('printImage', {
      'target': printer.target,
      'image': image,
      'copies': copies,
      'withDrawer': withDrawer,
    });
    return result as bool? ?? false;
  }
}
