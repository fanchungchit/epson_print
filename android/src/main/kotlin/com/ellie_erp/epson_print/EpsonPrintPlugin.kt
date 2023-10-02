package com.ellie_erp.epson_print

import android.content.Context
import android.graphics.BitmapFactory
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.FilterOption
import com.epson.epos2.printer.Printer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** EpsonPrintPlugin */
class EpsonPrintPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "epson_print")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "discovery" -> discovery(result)
      "printImage" -> printImage(call, result)
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun discovery(result: Result) {
    val filterOption = FilterOption()

     val discoveryListener = DiscoveryListener { deviceInfo ->
       val printers = mutableListOf<Map<String, Any>>()

         val printer = mapOf(
            "target" to deviceInfo.target,
            "deviceName" to deviceInfo.deviceName,
            "deviceType" to deviceInfo.deviceType,
            "ipAddress" to deviceInfo.ipAddress,
            "macAddress" to deviceInfo.macAddress,
            "bdAddress" to deviceInfo.bdAddress,
         )

         printers.add(printer)

         result.success(printers)
     }

    Discovery.start(context, filterOption, discoveryListener)
  }

  private fun printImage(call: MethodCall, result: Result) {
    val target = call.argument<String>("target")
    val copies = call.argument<Int>("copies")!!
    val image = call.argument<ByteArray>("image")!!
    val imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

    val printer = Printer(Printer.TM_T82, Printer.LANG_ZH_TW, context)
    printer.connect(target, Printer.PARAM_DEFAULT)
    printer.beginTransaction()
    printer.sendData(Printer.PARAM_DEFAULT)
    printer.addTextAlign(Printer.ALIGN_CENTER)
    for (i in 1..copies) {
      printer.addImage(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, Printer.COLOR_1, Printer.MODE_MONO, Printer.HALFTONE_DITHER,
        Printer.PARAM_DEFAULT.toDouble(), Printer.COMPRESS_AUTO)
      printer.addCut(Printer.CUT_FEED)
    }
    printer.endTransaction()
    printer.disconnect()
    result.success(null)
  }
}
