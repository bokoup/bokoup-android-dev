package com.bokoup.merchantapp.domain

import com.bokoup.merchantapp.model.Device
import com.dgsd.ksol.core.model.KeyPair

interface SettingsRepo {
    suspend fun getDeviceSerial(): String?
    suspend fun getMnemonic(new: Boolean = false): List<String>
    suspend fun getDeviceOwner(): String
    suspend fun getDevice(): Device?
    suspend fun getKeyPair(): KeyPair?
    suspend fun getKeyPairFromString(bytesString: String): KeyPair


}