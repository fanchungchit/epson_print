import 'package:flutter/material.dart';
import 'package:epson_print/epson_print.dart';

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
                onTap: () async {},
              );
            },
          );
        },
      ),
    );
  }
}
