package com.bokoup.customerapp.dom.model

import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import java.text.DateFormatSymbols
import java.time.OffsetDateTime

data class BokoupTransaction(
    val signature: TransactionSignature,
    val timestamp: OffsetDateTime,
    val merchantName: String,
    val orderId: String?,
    val paymentId: String?,
    val orderTotal: Int?,
    val discountValue: Float?,
    val type: Type,
    val tokenInfo: TokenInfo,
) {

    enum class Type {
        RECEIVED,
        REDEEMED
    }

    data class TokenInfo(
        val address: PublicKey,
        val name: String,
        val imageUrl: String,
        val symbol: String
    )
}