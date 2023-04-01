package com.bokoup.merchantapp.model

sealed class SharedPrefKeys {
    object DeviceSerial : SharedPrefKeys()
    object MnemonicString : SharedPrefKeys()
    object DeviceOwner : SharedPrefKeys()

}
val SharedPrefKeys.key: String
    get() = this.javaClass.simpleName.replaceFirstChar { it.lowercase() }
