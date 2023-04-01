package com.bokoup.merchantapp.net

import com.clover.sdk.v3.device.Device
import com.clover.sdk.v3.merchant.MerchantDevicesV2Connector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MerchantService(private val connector: MerchantDevicesV2Connector) {
    private val dispatcher = Dispatchers.IO

    suspend fun getDevice(): Device {
        return withContext(dispatcher) {
            connector.device
        }
    }
}
