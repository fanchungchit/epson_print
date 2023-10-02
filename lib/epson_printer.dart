class EpsonPrinter {
  EpsonPrinter({
    required this.name,
    required this.type,
    required this.ipAddress,
    required this.macAddress,
    required this.bdAddress,
    required this.target,
  });

  String name;
  int type;
  String ipAddress;
  String macAddress;
  String bdAddress;
  String target;

  factory EpsonPrinter.fromJson(Map<String, dynamic> json) => EpsonPrinter(
        name: json["deviceName"],
        type: json["deviceType"],
        ipAddress: json["ipAddress"],
        macAddress: json["macAddress"],
        bdAddress: json["bdAddress"],
        target: json["target"],
      );

  Map<String, dynamic> toJson() => {
        "deviceName": name,
        "deviceType": type,
        "ipAddress": ipAddress,
        "macAddress": macAddress,
        "bdAddress": bdAddress,
        "target": target,
      };
}
