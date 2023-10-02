import 'epson_print_platform_interface.dart';
import 'epson_printer.dart';

export 'epson_printer.dart';

class EpsonPrint {
  Future<List<EpsonPrinter>> discovery() {
    return EpsonPrintPlatform.instance.discovery();
  }

  Future<void> printImage({
    required EpsonPrinter printer,
    required List<int> image,
    int copies = 1,
  }) {
    return EpsonPrintPlatform.instance.printImage(
      printer: printer,
      image: image,
      copies: copies,
    );
  }
}
