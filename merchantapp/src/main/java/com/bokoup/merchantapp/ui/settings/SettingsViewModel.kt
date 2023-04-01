package com.bokoup.merchantapp.ui.settings

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bokoup.lib.QRCodeGenerator
import com.bokoup.lib.ResourceFlowConsumer
import com.bokoup.lib.resourceFlowOf
import com.bokoup.merchantapp.domain.SettingsRepo
import com.bokoup.merchantapp.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val qrCodeGenerator: QRCodeGenerator
) : ViewModel() {
    private val dispatcher = Dispatchers.IO

    val deviceConsumer = ResourceFlowConsumer<Device?>(viewModelScope)
    val mnemonicConsumer = ResourceFlowConsumer<List<String>?>(viewModelScope)
    val addressQrCodeConsumer = ResourceFlowConsumer<Bitmap>(viewModelScope)
    val serialQrCodeConsumer = ResourceFlowConsumer<Bitmap>(viewModelScope)

    fun getMnemonic(new: Boolean = false) = viewModelScope.launch(dispatcher) {
        mnemonicConsumer.collectFlow(
            resourceFlowOf {
                settingsRepo.getMnemonic(new)
            }
        )
    }
    fun getDevice() = viewModelScope.launch(dispatcher) {
        deviceConsumer.collectFlow(
            resourceFlowOf {
                settingsRepo.getDevice()
            }
        )
    }
    fun getQrCode(content: String, consumer: ResourceFlowConsumer<Bitmap>) = viewModelScope.launch(Dispatchers.IO) {
        consumer.collectFlow(
            resourceFlowOf {
                qrCodeGenerator.generateQR(content)
            }
        )

    }

}