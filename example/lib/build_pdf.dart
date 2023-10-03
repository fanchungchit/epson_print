import 'dart:typed_data';

import 'package:pdf/pdf.dart';
import 'package:pdf/widgets.dart';

Future<Uint8List> buildPdf() async {
  final pdf = Document();

  pdf.addPage(
    Page(
      pageFormat: PdfPageFormat.roll80,
      build: (context) {
        return Center(
          child: Text('Hello World!'),
        );
      },
    ),
  );

  return pdf.save();
}
