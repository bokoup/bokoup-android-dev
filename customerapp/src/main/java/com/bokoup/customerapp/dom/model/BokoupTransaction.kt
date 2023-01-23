package com.bokoup.customerapp.dom.model

import com.dgsd.ksol.core.model.TransactionSignature
import java.math.BigDecimal
import java.time.OffsetDateTime

data class BokoupTransaction(
    val signature: TransactionSignature,
    val timestamp: OffsetDateTime,
    val merchantName: String,
    val orderId: String?,
    val paymentId: String?,
    val orderTotal: BigDecimal?,
    val discountValue: BigDecimal?,
    val type: Type,
    val tokenInfo: BokoupTokenInfo,
) {

    enum class Type {
        RECEIVED,
        REDEEMED
    }

}