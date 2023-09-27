package com.fulfillmenttools.danieltestapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.TcpConnection
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 * Created on 06.09.2023. Copyright by OC fulfillment GmbH.
 */
class MainViewModel : ViewModel() {

    private val zebraPrintUseCase = ZebraPrintUseCase()

    fun wiFiPrint(ipAddress: String, pdfFile: File?) {
        Log.i("MyLog", "MainViewModel.wiFiPrint")
        viewModelScope.launch {
            zebraPrintUseCase(connectionType = ConnectionTypeEnum.WIFI, address = ipAddress, pdfFile = pdfFile)
        }
    }

    fun bluetoothPrint(macAddress: String, pdfFile: File?) {
        Log.i("MyLog", "MainViewModel.bluetoothPrint")
        viewModelScope.launch {
            zebraPrintUseCase(connectionType = ConnectionTypeEnum.BLUETOOTH, address = macAddress, pdfFile = pdfFile)
        }
    }


    @Throws(ConnectionException::class)
    fun sendZplOverTcp(theIpAddress: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Instantiate connection for ZPL TCP port at given address
                val thePrinterConn: Connection = TcpConnection(theIpAddress, TcpConnection.DEFAULT_ZPL_TCP_PORT)
                try {
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open()

                    // This example prints "This is a ZPL test." near the top of the label.
                    val zplData = "^XA" +
                            "^FX Top section with logo, name and address." +
                            "^CF0,60" +
                            "^FO50,50^GB100,100,100^FS" +
                            "^FO75,75^FR^GB100,100,100^FS" +
                            "^FO93,93^GB40,40,40^FS" +
                            "^FO220,50^FDHubi, Inc.^FS" +
                            "^CF0,30" +
                            "^FO220,115^FDGinsterfpad 12^FS" +
                            "^FO220,155^FD50737 Koeln^FS" +
                            "^FO220,195^FDGermany^FS" +
                            "^FO50,250^GB700,3,3^FS" +
                            "" +
                            "^FX Second section with recipient address and permit information." +
                            "^CFA,30" +
                            "^FO50,300^FDMark Byerly^FS" +
                            "^FO50,340^FDDomstr. 20^FS" +
                            "^FO50,380^FD72055 Markhausen^FS" +
                            "^FO50,420^FDGermany^FS" +
                            "^CFA,15" +
                            "^FO600,300^GB150,150,3^FS" +
                            "^FO638,340^FDPermit^FS" +
                            "^FO638,390^FD123456^FS" +
                            "^FO50,500^GB700,3,3^FS" +
                            "" +
                            "^FX Third section with bar code." +
                            "^BY5,2,270" +
                            "^FO100,550^BC^FD20t45678^FS" +
                            "" +
                            "^FX Fourth section (the two boxes on the bottom)." +
                            "^FO50,900^GB700,250,3^FS" +
                            "^FO400,900^GB3,250,3^FS" +
                            "^CF0,40" +
                            "^FO100,960^FDCtr. X34B-1^FS" +
                            "^FO100,1010^FDREF1 F00B47^FS" +
                            "^FO100,1060^FDREF2 BL4H8^FS" +
                            "^CF0,190" +
                            "^FO470,955^FDCA^FS" +
                            "" +
                            "^XZ"

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(zplData.toByteArray())
                } catch (e: ConnectionException) {
                    // Handle communications error here.
                    e.printStackTrace()
                } finally {
                    // Close the connection to release resources.
                    thePrinterConn.close()
                }
            }
        }
    }

    @Throws(ConnectionException::class)
    fun sendCpclOverTcp(theIpAddress: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
// Instantiate connection for CPCL TCP port at given address
                val thePrinterConn: Connection = TcpConnection(theIpAddress, TcpConnection.DEFAULT_CPCL_TCP_PORT)
                try {
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open()

                    // This example prints "This is a CPCL test." near the top of the label.
                    val cpclData = """
            ! 0 200 200 210 1
            TEXT 4 0 30 40 This is a CPCL test.
            FORM
            PRINT
            
            """.trimIndent()

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(cpclData.toByteArray())
                } catch (e: ConnectionException) {
                    // Handle communications error here.
                    e.printStackTrace()
                } finally {
                    // Close the connection to release resources.
                    thePrinterConn.close()
                }
            }
        }
    }

    @Throws(ConnectionException::class)
    fun printConfigLabelUsingDnsName(dnsName: String) {
        val connection: Connection = TcpConnection(dnsName, 9100)
        try {
            connection.open()
            val p: ZebraPrinter = ZebraPrinterFactory.getInstance(connection)
            p.printConfigurationLabel()
        } catch (e: ConnectionException) {
            e.printStackTrace()
        } catch (e: ZebraPrinterLanguageUnknownException) {
            e.printStackTrace()
        } finally {
            // Close the connection to release resources.
            connection.close()
        }
    }

}
