package com.bokoup.merchantapp.domain

import android.content.SharedPreferences
import com.bokoup.merchantapp.model.Device
import com.bokoup.merchantapp.model.SharedPrefKeys
import com.bokoup.merchantapp.model.key
import com.bokoup.merchantapp.net.DataService
import com.bokoup.merchantapp.net.MerchantService
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength

class SettingsRepoImpl(
    private val keyFactory: KeyFactory,
    private val sharedPref: SharedPreferences,
    private val merchantService: MerchantService,
    private val dataService: DataService
) : SettingsRepo {
    override suspend fun getDeviceSerial() : String? {
        var deviceSerial = sharedPref.getString(SharedPrefKeys.DeviceSerial.key, null)
        if (deviceSerial == null) {
            deviceSerial = merchantService.getDevice().serial
            with(sharedPref.edit()) {
                putString(SharedPrefKeys.DeviceSerial.key, deviceSerial)
                commit()
            }
        }
        return deviceSerial
    }

    override suspend fun getMnemonic(new: Boolean) : List<String> {
        var mnemonic = sharedPref.getString(SharedPrefKeys.MnemonicString.key, null)?.split(",")
        if (mnemonic == null || new) {
            mnemonic = keyFactory.createMnemonic(MnemonicPhraseLength.TWELVE)
            with(sharedPref.edit()) {
                putString(SharedPrefKeys.MnemonicString.key, mnemonic.joinToString())
                commit()
            }
        }
        return mnemonic
    }
    override suspend fun getDeviceOwner(): String {
        var deviceOwner = sharedPref.getString(SharedPrefKeys.DeviceOwner.key, null)
        if (deviceOwner == null) {
            deviceOwner = getKeyPair()?.publicKey.toString()
            with(sharedPref.edit()) {
                putString(SharedPrefKeys.DeviceOwner.key, deviceOwner)
                commit()
            }
        }
        return deviceOwner
    }
    override suspend fun getDevice(): Device? {
        val deviceSerial = sharedPref.getString(SharedPrefKeys.DeviceSerial.key, null) ?: getDeviceSerial()
        val deviceOwner = sharedPref.getString(SharedPrefKeys.DeviceOwner.key, null) ?: getDeviceOwner()
        if (deviceSerial != null && deviceOwner != null) {
            val device = dataService.fetchDevice(deviceOwner, deviceSerial)
            if (device != null) {
                return Device(
                    owner = deviceOwner,
                    name = deviceSerial,
                    device = device.id,
                    location = device.location
                )
            }
        }
        return null
    }
    override suspend fun getKeyPair() : KeyPair? {
        val mnemonic = getMnemonic()
        if (mnemonic != null) {
            return keyFactory.createKeyPairFromMnemonic(mnemonic, "")
        }
        return null
    }
    override suspend fun getKeyPairFromString(bytesString: String) : KeyPair {
        val bytesList = bytesString.split(",").map {
            it.toInt()
        }
        if (bytesList.size != 64 && bytesList.any{ it < 0 || it > 255 }) {
            throw Exception("Invalid bytesString")
        }

        val privateKey = PrivateKey.fromByteArray(bytesList.map{ it.toByte()}.toByteArray())

        return keyFactory.createKeyPairFromPrivateKey(privateKey)
    }


}