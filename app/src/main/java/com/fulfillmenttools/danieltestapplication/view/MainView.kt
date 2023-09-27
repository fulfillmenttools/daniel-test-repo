package com.fulfillmenttools.danieltestapplication.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fulfillmenttools.danieltestapplication.AppStatusEnum
import com.fulfillmenttools.danieltestapplication.ConnectionTypeEnum
import com.fulfillmenttools.danieltestapplication.ui.theme.DanielTestApplicationTheme

/**
 * Created on 12.09.2023. Copyright by OC fulfillment GmbH.
 */
@Composable
fun MainScreen(
    appStatus: AppStatusEnum,
    ipAddresses: List<String>,
    onSelectPrintPDF: (connectionType: ConnectionTypeEnum) -> Unit,
    onSelectPrintCPCL: (connectionType: ConnectionTypeEnum) -> Unit,
    onSearchIPPrinter: () -> Unit,
    showPrinterDialog: Boolean,
    onSelectIPAddressInDialog: (ipAddress: String) -> Unit,
    onClosePrinterDialog: () -> Unit,
    sendZplOverTcp: () -> Unit,
    sendCpclOverTcp: () -> Unit,
    printConfigLabelUsingDnsName: () -> Unit
) {
    DanielTestApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    sendZplOverTcp()
                }) {
                    Text(text = "sendZplOverTcp")
                }
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    sendCpclOverTcp()
                }) {
                    Text(text = "sendCpclOverTcp")
                }
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    printConfigLabelUsingDnsName()
                }) {
                    Text(text = "printConfigLabelUsingDnsName")
                }




                Text(text = "ZEBRA PRINTER TESTING", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "PRINT PDF")
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    onSelectPrintPDF(ConnectionTypeEnum.BLUETOOTH)
                }) {
                    Text(text = "BLUETOOTH PRINT")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    onSelectPrintPDF(ConnectionTypeEnum.WIFI)
                }) {
                    Text(text = "WIFI PRINT")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "PRINT CPCL")
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    onSelectPrintCPCL(ConnectionTypeEnum.BLUETOOTH)
                }) {
                    Text(text = "BLUETOOTH PRINT")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    onSelectPrintCPCL(ConnectionTypeEnum.WIFI)
                }) {
                    Text(text = "WIFI PRINT")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "SEARCH")
                Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(), onClick = {
                    onSearchIPPrinter()
                }) {
                    Text(text = "SEARCH WIFI PRINTER")
                }
                Spacer(modifier = Modifier.height(64.dp))
                if (appStatus == AppStatusEnum.PRINTER_SEARCH) {
                    Text(text = "SEARCHING FOR PRINTERS...")
                } else {
                    if (ipAddresses.isEmpty()) {
                        Text(text = "NO WIFI PRINTER AVAILABLE")
                    } else {
                        Text(text = "WIFI PRINTER FOUND")

                        ipAddresses.forEach { ipAddress: String ->
                            Text(text = "IP: $ipAddress")
                        }
                    }
                }
            }

            if (showPrinterDialog) {
                AddressDialog(ipAddresses = ipAddresses, appStatus = appStatus, onSelectAddress = { ipAddress: String ->
                    onSelectIPAddressInDialog(ipAddress)
                }, onClose = {
                    onClosePrinterDialog()
                })
            }
        }
    }
}

@Composable
fun AddressDialog(
    ipAddresses: List<String>, appStatus: AppStatusEnum, onSelectAddress: (String) -> Unit, onClose: () -> Unit
) {
    Dialog(onDismissRequest = {
        onClose()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (appStatus == AppStatusEnum.PRINTER_SEARCH) {
                    Text(text = "SEARCHING FOR PRINTERS...")
                } else {
                    if (ipAddresses.isEmpty()) {
                        Text(text = "NO WIFI PRINTER AVAILABLE")
                    } else {
                        Text(text = "WIFI PRINTER FOUND")

                        ipAddresses.forEach { address: String ->
                            TextButton(onClick = {
                                onSelectAddress(address)
                            }) {
                                Text(address)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onClose() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}
