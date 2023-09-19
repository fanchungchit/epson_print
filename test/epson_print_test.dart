import 'package:flutter_test/flutter_test.dart';
import 'package:epson_print/epson_print.dart';
import 'package:epson_print/epson_print_platform_interface.dart';
import 'package:epson_print/epson_print_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockEpsonPrintPlatform
    with MockPlatformInterfaceMixin
    implements EpsonPrintPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final EpsonPrintPlatform initialPlatform = EpsonPrintPlatform.instance;

  test('$MethodChannelEpsonPrint is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelEpsonPrint>());
  });

  test('getPlatformVersion', () async {
    EpsonPrint epsonPrintPlugin = EpsonPrint();
    MockEpsonPrintPlatform fakePlatform = MockEpsonPrintPlatform();
    EpsonPrintPlatform.instance = fakePlatform;

    expect(await epsonPrintPlugin.getPlatformVersion(), '42');
  });
}
