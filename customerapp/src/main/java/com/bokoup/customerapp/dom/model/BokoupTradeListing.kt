package com.bokoup.customerapp.dom.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class BokoupTradeListing(
    val id: String,
    val tokenInfo: BokoupTokenInfo,
    val tokenSize: Int,
    val sellerAddress: String,
    val price: BigDecimal,
    val timestamp: OffsetDateTime,
) {
}