import 'package:flutter/material.dart';
import 'package:epson_print/epson_print.dart';
import 'package:printing/printing.dart';

import 'build_pdf.dart';

void main() => runApp(const App());

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: HomeView(),
    );
  }
}

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  final epsonPrint = EpsonPrint();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Epson Print Example')),
      body: FutureBuilder(
        future: epsonPrint.discovery(),
        builder: (context, snapshot) {
          if (!snapshot.hasData) {
            return const Center(
              child: CircularProgressIndicator.adaptive(),
            );
          }

          final printers = snapshot.data!;
          return ListView.builder(
            itemCount: printers.length,
            itemBuilder: (context, index) {
              final printer = printers[index];
              return ListTile(
                title: Text(printer.name),
                subtitle: Text(printer.ipAddress),
                onTap: () async {
                  final pdf = await buildPdf();
                  final raster = Printing.raster(pdf);
                  final image = await (await raster.first).toPng();
                  await epsonPrint.printImage(
                      printer: printer, image: image, withDrawer: true);
                },
              );
            },
          );
        },
      ),
    );
  }
}
