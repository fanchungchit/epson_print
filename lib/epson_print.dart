
import 'epson_print_platform_interface.dart';

class EpsonPrint {
  Future<String?> getPlatformVersion() {
    return EpsonPrintPlatform.instance.getPlatformVersion();
  }
}
