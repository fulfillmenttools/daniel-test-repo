package com.fulfillmenttools.danieltestapplication

import android.util.Log
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.TcpConnection
import com.zebra.sdk.printer.PrinterStatus
import com.zebra.sdk.printer.SGD
import com.zebra.sdk.printer.ZebraPrinterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/**
 * Created on 06.09.2023. Copyright by OC fulfillment GmbH.
 */
class ZebraPrintUseCase {

    private val cpclRepository = CPCLRepository()

    suspend operator fun invoke(connectionType: ConnectionTypeEnum, address: String, pdfFile: File?): Unit = withContext(Dispatchers.IO) {
        val zebraPrinterConnection: Connection = connectToPrinter(connectionType = connectionType, address = address)

        if (pdfFile == null) {
            printCPCL(zebraPrinterConnection = zebraPrinterConnection)
        } else {
            val printerInfo: String = SGD.GET("apl.enable", zebraPrinterConnection)
            val printerSupportsPDF: Boolean = (printerInfo == "pdf")

            Log.e("MyLog", "Printer info: $printerInfo (supports PDF: $printerSupportsPDF)")

            if (printerSupportsPDF) {
                printPDF(zebraPrinterConnection = zebraPrinterConnection, pdfFile = pdfFile)
            }
        }
    }

    private fun connectToPrinter(connectionType: ConnectionTypeEnum, address: String): Connection {
        val connection: Connection = if (connectionType == ConnectionTypeEnum.WIFI) {
            Log.i("MyLog", "Try connect with WIFI $address")
            TcpConnection(address, TcpConnection.DEFAULT_CPCL_TCP_PORT)
        } else {
            Log.i("MyLog", "Try connect with BLUETOOTH $address")
            BluetoothConnection(address)
        }

        try {
            connection.open()

            if (connection.isConnected.not()) {
                connection.open()
            }
        } catch (e: ConnectionException) {
            e.printStackTrace()
        }

        Log.i("MyLog", "CONNECTION " + connection.isConnected)

        return connection
    }

    private fun printPDF(zebraPrinterConnection: Connection, pdfFile: File) {
        try {
            val zebraPrinter = ZebraPrinterFactory.getLinkOsPrinter(zebraPrinterConnection)
            val printerStatus: PrinterStatus = zebraPrinter.currentStatus

            if (printerStatus.isReadyToPrint) {
                Log.i("MyLog", "Printer ready to print")
                Log.i("MyLog", ">>> PATH: " + pdfFile.absolutePath)

                zebraPrinter.sendFileContents(pdfFile.absolutePath) { bytesWritten, totalBytes ->
                    val rawProgress = (bytesWritten * 100 / totalBytes).toDouble()
                    val progress = rawProgress.roundToInt()

                    Log.i("MyLog", ">>> Progress: $progress")
                }

                // Make sure the data got to the printer before closing the connection
                Thread.sleep(800)
            } else {
                if (printerStatus.isPaused) {
                    Log.i("MyLog", "Printer paused")
                } else if (printerStatus.isHeadOpen) {
                    Log.i("MyLog", "Printer head open")
                } else if (printerStatus.isPaperOut) {
                    Log.i("MyLog", "Printer is out of paper")
                } else {
                    Log.i("MyLog", "Unknown error occurred")
                }
            }
        } catch (e: ConnectionException) {
            e.printStackTrace()
        } finally {
            zebraPrinterConnection.close()
        }
    }

    private fun printCPCL(zebraPrinterConnection: Connection) {
        Log.i("MyLog", "Try to print CPCL.")

        try {
            val cpclData = cpclRepository.getLabel()

            zebraPrinterConnection.write(cpclData.toByteArray())

            // Make sure the data got to the printer before closing the connection
            Thread.sleep(500)

            zebraPrinterConnection.close()
        } catch (e: ConnectionException) {
            e.printStackTrace()
            Log.i("MyLog", "Error with Bluetooth printer.")
        }
    }
}
