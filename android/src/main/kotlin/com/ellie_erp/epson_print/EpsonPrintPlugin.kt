package com.ellie_erp.epson_print

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.epson.epos2.Epos2Exception
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
  private var printer: Printer? = null

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
    val target = call.argument<String>("target")!!
    val copies = call.argument<Int>("copies")!!
    val withDrawer = call.argument<Boolean>("withDrawer")!!
    val image = call.argument<ByteArray>("image")!!

    if (!connect(target)) {
      Log.d("EpsonPrintPlugin", "Failed to connect to printer")
      result.error("connect", "Failed to connect to printer", null)
      if (printer != null) {
        printer!!.clearCommandBuffer()
      }
      return
    }

    if (!createData(image, copies, withDrawer)) {
      Log.d("EpsonPrintPlugin", "Failed to create data")
      result.error("createData", "Failed to create data", null)
      if (printer != null) {
        printer!!.clearCommandBuffer()
      }
      return
    }

    try {
      Log.d("EpsonPrintPlugin", "Printer status: ${printer!!.status}")
      val status = printer!!.status
      Log.d("EpsonPrintPlugin", "Printer connection: ${status.connection} online: ${status.online} coverOpen: ${status.coverOpen} paper: ${status.paper} paperFeed: ${status.paperFeed} panelSwitch: ${status.panelSwitch}")
      printer!!.sendData(Printer.PARAM_DEFAULT)
      result.success(true)
    } catch (e: Epos2Exception) {
      e.printStackTrace()
      Log.e("EpsonPrintPlugin", "Failed to print: $e")
      disconnect()
    }
  }

  private fun connect(target: String): Boolean {
    if (printer == null) {
      printer = Printer(Printer.TM_T82, Printer.MODEL_ANK, context)
    }
    try {
      val status = printer!!.status
      if (status.online != Printer.TRUE) {
        printer!!.connect(target, Printer.PARAM_DEFAULT)
      }
      printer!!.clearCommandBuffer()
    } catch (e: Epos2Exception) {
      disconnect()
      Log.d("EpsonPrintPlugin", "Failed to connect: $e")
      return false
    }
    return true
  }

  private fun disconnect() {
    if (printer == null) {
      return
    }
    while (true) {
      try {
        printer!!.disconnect()
        printer = null
        break
      } catch (e: Epos2Exception) {
        Log.d("EpsonPrintPlugin", "Failed to disconnect: ${e.errorStatus}")
        printer!!.clearCommandBuffer()
//        throw e
      }
    }
  }

  private fun createData(image: ByteArray, copies: Int, withDrawer: Boolean): Boolean {
    if (printer == null) {
      return false
    }
    try {
      if (withDrawer) {
        printer!!.addPulse(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT)
      }

      val imageData = BitmapFactory.decodeByteArray(image, 0, image.size)
      for (i in 0 until copies) {
        printer!!.addImage(imageData, 0, 0, imageData.width, imageData.height, Printer.COLOR_1, Printer.MODE_MONO, Printer.HALFTONE_DITHER,
          Printer.PARAM_DEFAULT.toDouble(), Printer.COMPRESS_AUTO)
        printer!!.addCut(Printer.CUT_FEED)
      }
    } catch (e: Exception) {
      Log.d("EpsonPrintPlugin", "Failed to create data: $e")
      return false
    }
    return true
  }
}
