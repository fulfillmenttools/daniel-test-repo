package com.fulfillmenttools.danieltestapplication

import android.os.Looper
import android.util.Log
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.BluetoothConnectionInsecure
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.PrinterStatus
import com.zebra.sdk.printer.SGD
import com.zebra.sdk.printer.ZebraPrinterFactory
import com.zebra.sdk.printer.ZebraPrinterLinkOs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created on 06.09.2023. Copyright by OC fulfillment GmbH.
 */
@Deprecated("Use the ZebraPrintUseCase class.")
class ZebraBluetoothPrintUseCase {

    suspend operator fun invoke(macAddress: String, pdfFile: File?): Unit = withContext(Dispatchers.IO) {
        val zebraPrinterConnection: Connection = connectToPrinter(macAddress = macAddress)

        if (pdfFile == null) {
            //printCPCL(zebraPrinterConnection = zebraPrinterConnection)
        } else {
            //printPDF(zebraPrinterConnection = zebraPrinterConnection, pdfFile = pdfFile)
        }
    }

    private fun connectToPrinter(macAddress: String): Connection {
        val connection: Connection = BluetoothConnection(macAddress)

        try {
            connection.open()

            if (connection.isConnected.not()) {
                connection.open()
            }
        } catch (e: ConnectionException) {
            e.printStackTrace()
        }

        return connection
    }


    @Throws(ConnectionException::class)
    private fun zebraPrinterSupportsPDF(connection: Connection): Boolean {
        val printerInfo: String = SGD.GET("apl.enable", connection)

        Log.e("MyLog", "INFO: " + printerInfo)

        return printerInfo == "pdf"
    }

    private fun printImage(macAddress: String, pdfFile: File) {
        val connection: Connection = BluetoothConnection(macAddress)

        try {
            // Open Connection
            connection.open()

            if (connection.isConnected.not()) {
                connection.open()
            }

            Log.d("MyLog", ">>> PDF Support Status: " + zebraPrinterSupportsPDF(connection))

            val printer: ZebraPrinterLinkOs = ZebraPrinterFactory.getLinkOsPrinter(connection)
            val printerStatus: PrinterStatus = printer.currentStatus

            Log.d("MyLog", ">>> Printer Status: " + printerStatus)

            if (printerStatus.isReadyToPrint) {
                // Send the data to printer as a byte array.

                Log.d("MyLog", ">>> PATH: " + pdfFile.absolutePath)

                printer.sendFileContents(pdfFile.absolutePath) { bytesWritten, totalBytes ->
                    // Calc Progress
                    val rawProgress = (bytesWritten * 100 / totalBytes).toDouble()
                    val progress = Math.round(rawProgress).toInt()

                    Log.d("MyLog", ">>> progress: " + progress)
                }

                // Make sure the data got to the printer before closing the connection
                Thread.sleep(500)

                // TODO: Notify UI that print job completed successfully...
            } else {
                if (printerStatus.isPaused) {
                    Log.e("MyLog", "Printer paused")
                } else if (printerStatus.isHeadOpen) {
                    Log.e("MyLog", "Printer head open")
                } else if (printerStatus.isPaperOut) {
                    Log.e("MyLog", "Printer is out of paper")
                } else {
                    Log.e("MyLog", "Unknown error occurred")
                }
            }


            /*
            // Verify Printer Supports PDF
            if (zebraPrinterSupportsPDF(connection)) {
                // TODO: Send PDF to Printer

                Log.e("MyLog", "Printer FOUND")
            } else {
                Log.e("MyLog", "Printer does not support PDF Printing")

                // Close Connection
                connection.close()
            }
             */
        } catch (e: ConnectionException) {
            e.printStackTrace()
            Log.e("MyLog", "Connection Failed: " + e.message)
        }
    }

    private fun printCPCL(macAddress: String) {
        try {

            // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
            val thePrinterConn: Connection = BluetoothConnectionInsecure(macAddress)

            // Initialize
            Looper.prepare()

            // Open the connection - physical connection is established here.
            thePrinterConn.open()

            // This example prints "This is a CPCL test." near the top of the label.

            /*
            A label file always begins with the “!” character
            followed by an “x” offset parameter,
            “x” and “y” axis resolutions,
            a label length
            and  a quantity of labels to print.
             */

            val cpclData = """
                ! 0 200 200 210 1
                TEXT 4 0 30 40 Hallo Mark,
                TEXT 4 0 30 80 du süßer Schmakkofatz!
                TEXT 4 0 30 120      Daniel <3
                FORM
                PRINT
                
                """.trimIndent()

            // Send the data to printer as a byte array.
            thePrinterConn.write(cpclData.toByteArray())

            // Make sure the data got to the printer before closing the connection
            Thread.sleep(500)

            // Close the insecure connection to release resources.
            thePrinterConn.close()
            Looper.myLooper()!!.quit()
        } catch (e: Exception) {
            // Handle communications error here.
            e.printStackTrace()
        }
    }
}
