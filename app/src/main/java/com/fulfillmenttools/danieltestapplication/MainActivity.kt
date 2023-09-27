package com.fulfillmenttools.danieltestapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fulfillmenttools.danieltestapplication.view.MainScreen
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryException
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import com.zebra.sdk.printer.discovery.NetworkDiscoverer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created on 12.09.2023. Copyright by OC fulfillment GmbH.
 */
class MainActivity : ComponentActivity() {

    companion object {
        // HANDHELD "AC3FA4DD68EF"
        // ZD620 "48A49362EC9A"
        const val PRINTER_BT_MAC_ADDRESS = "AC:3F:A4:DD:68:EF"
    }

    private val mainViewModel: MainViewModel = MainViewModel()
    private var ipAddresses by mutableStateOf(emptyList<String>())
    private var appStatus by mutableStateOf(AppStatusEnum.NEUTRAL)
    private var showPrinterDialog by mutableStateOf(false)

    private var selectedPDFUri: Uri? = null
    private var selectedConnectionType: ConnectionTypeEnum? = null
    private var selectedAddress: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && data.data != null) {
            selectedPDFUri = data.data

            when (selectedConnectionType) {
                ConnectionTypeEnum.BLUETOOTH -> {
                    printWithZebra()
                }

                ConnectionTypeEnum.WIFI -> {
                    searchPrinter()
                    showPrinterDialog = true
                }

                else -> {
                    selectedPDFUri = null
                    selectedAddress = null
                    selectedConnectionType = null
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()

        setContent {
            MainScreen(
                appStatus = appStatus,
                ipAddresses = ipAddresses,
                showPrinterDialog = showPrinterDialog,
                onSelectPrintPDF = { connectionType: ConnectionTypeEnum ->
                    if (connectionType == ConnectionTypeEnum.BLUETOOTH) {
                        selectedAddress = PRINTER_BT_MAC_ADDRESS
                    }

                    selectedConnectionType = connectionType
                    selectedPDFUri = null

                    val selectPdfIntent = Intent()
                    selectPdfIntent.type = "application/pdf"
                    selectPdfIntent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(selectPdfIntent, "SELECT AND PRINT PDF"), 0)
                },
                onSelectPrintCPCL = { connectionType: ConnectionTypeEnum ->
                    if (connectionType == ConnectionTypeEnum.BLUETOOTH) {
                        selectedAddress = PRINTER_BT_MAC_ADDRESS
                        Toast.makeText(applicationContext, "Print executed, wait...", Toast.LENGTH_SHORT).show()
                    }

                    selectedConnectionType = connectionType
                    selectedPDFUri = null

                    if (connectionType == ConnectionTypeEnum.BLUETOOTH) {
                        printWithZebra()
                    } else if (connectionType == ConnectionTypeEnum.WIFI) {
                        searchPrinter()
                        showPrinterDialog = true
                    }
                },
                onSelectIPAddressInDialog = { ipAddress: String ->
                    showPrinterDialog = false
                    selectedAddress = ipAddress

                    printWithZebra()
                },
                onClosePrinterDialog = {
                    selectedConnectionType = null
                    selectedPDFUri = null
                    selectedAddress = null

                    showPrinterDialog = false
                },
                onSearchIPPrinter = {
                    searchPrinter()
                },
                sendZplOverTcp = {
                    mainViewModel.sendZplOverTcp("192.168.244.103")
                },
                sendCpclOverTcp = {
                    mainViewModel.sendCpclOverTcp("192.168.244.103")
                },
                printConfigLabelUsingDnsName = {

                }
            )
        }
    }

    private fun printWithZebra() {
        selectedAddress?.let { address: String ->
            selectedConnectionType?.let { connectionType: ConnectionTypeEnum ->
                if (connectionType == ConnectionTypeEnum.WIFI) {
                    mainViewModel.wiFiPrint(ipAddress = address, pdfFile = getFileFromUri(contentUri = selectedPDFUri))
                } else if (connectionType == ConnectionTypeEnum.BLUETOOTH) {
                    mainViewModel.bluetoothPrint(macAddress = address, pdfFile = getFileFromUri(contentUri = selectedPDFUri))
                }
            } ?: kotlin.run {
                Log.i("MyLog", "No selected connection tpye found.")
            }
        } ?: kotlin.run {
            Log.i("MyLog", "No selected address found.")
        }

        selectedAddress = null
        selectedConnectionType = null
        selectedPDFUri = null
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            Log.i(" >>> MyLog", " >>> A")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.i(" >>> MyLog", " >>> B")

                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_SCAN), 2
                )
            }
        } else if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            Log.i(" >>> MyLog", " >>> A")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(" >>> MyLog", " >>> B")

                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2
                )
            }
        } else {
            Log.d(" >>> MyLog", " >>> C")
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                Log.i(" >>> MyLog", " >>> Device has no Bluetooth.")
            }

            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceMACAddress = device.address

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.i(" >>> MyLog", " >>> BT DEVICE: $deviceName - $deviceMACAddress - ${device.alias}")
                } else {
                    Log.i(" >>> MyLog", " >>> BT DEVICE: $deviceName - $deviceMACAddress")
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getFileFromUri(contentUri: Uri?): File? {
        if (contentUri == null) {
            return null
        }

        var pdfFile: File? = null
        val inputStream: InputStream? = applicationContext.contentResolver.openInputStream(contentUri)

        if (inputStream != null) {
            try {
                pdfFile = File.createTempFile("fft-image", ".pdf", applicationContext.cacheDir)
                FileOutputStream(pdfFile).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }

                    output.flush()
                }
            } finally {
                inputStream.close()
            }
        }

        return pdfFile
    }

    private fun searchPrinter() {
        appStatus = AppStatusEnum.PRINTER_SEARCH

        val discoveryHandler: DiscoveryHandler = object : DiscoveryHandler {
            var printers: MutableList<DiscoveredPrinter> = ArrayList()

            override fun foundPrinter(printer: DiscoveredPrinter) {
                Log.i(" >>> MyLog", "### NEW PRINTER ####################################")
                printer.discoveryDataMap.forEach { (key, value) ->
                    Log.e(" >>> MyLog", "$key")
                    Log.e(" >>> MyLog", "    $value")
                }
                Log.i(" >>> MyLog", "####################################################")

                printers.add(printer)
            }

            override fun discoveryFinished() {
                ipAddresses = printers.map {
                    it.address
                }

                appStatus = AppStatusEnum.NEUTRAL
                Log.i(" >>> MyLog", "Discovered " + printers.size + " printers.")
            }

            override fun discoveryError(message: String) {
                appStatus = AppStatusEnum.NEUTRAL

                Log.e(" >>> MyLog", "An error occurred during discovery : $message")
            }
        }

        try {
            Log.i(" >>> MyLog", "Starting printer discovery.")
            NetworkDiscoverer.findPrinters(discoveryHandler)
        } catch (e: DiscoveryException) {
            e.printStackTrace()
        }
    }
}
