package com.bokoup.customerapp.ui.scan

import androidx.lifecycle.ViewModel
import com.bokoup.customerapp.data.net.NetworkConstants
import com.bokoup.customerapp.dom.model.ScanResult
import com.bokoup.customerapp.util.QRCodeScanner
import com.bokoup.lib.SystemClipboard
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransactionRequest
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    qrCodeScanner: QRCodeScanner,
    private val solPay: SolPay,
    private val clipboard: SystemClipboard,
) : ViewModel() {
    val scanner = qrCodeScanner.scanner

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult = _scanResult.asStateFlow()

    private fun parseValue(barcode: Barcode): ScanResult {
        val value = barcode.rawValue

        val solPayRequestDetails = if (value == null) {
            null
        } else {
            solPay.parseUrl(value)
        }

        return when (solPayRequestDetails) {
            null -> {
                // Not a SolPay request
                ScanResult.Other(value.toString(), barcode)
            }
            !is SolPayTransactionRequest -> {
                // Not a SolPay transaction request.
                ScanResult.Other(value.toString(), barcode)
            }
            else -> {
                val url = URL(solPayRequestDetails.link)
                if (url.host != NetworkConstants.TRANSACTION_API_HOST) {
                    // Not a transaction request to Bokoup
                    ScanResult.Other(value.toString(), barcode)
                } else {
                    ScanResult.BokoupUrl(
                        url = solPayRequestDetails.link,
                        barcode = barcode
                    )
                }
            }
        }
    }

    fun getScanResult(barcode: Barcode?) = _scanResult.update {
        if (barcode != null) {
            parseValue(barcode)
        } else {
            null
        }
    }

    fun copyToClipboard(contents: String?) {
        if (contents != null) {
            clipboard.copy(contents)
        }
    }
}

