package com.bokoup.customerapp.dom.model

import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.core.model.PrivateKey
import com.dgsd.ksol.core.model.PublicKey

data class AddressWithPrivateKey(
    val address: Address,
    val privateKey: PrivateKey,
) {

    fun asKeyPair(): KeyPair {
        return KeyPair(
            PublicKey.fromBase58(address.id),
            privateKey
        )
    }
}