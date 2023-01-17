package com.bokoup.merchantapp.model

data class DelegateMemo(val orderId: String, val timestamp: Int, val orderTotal: Long, val discountValue: Long)
