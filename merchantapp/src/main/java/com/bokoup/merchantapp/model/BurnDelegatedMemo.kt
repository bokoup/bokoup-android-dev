package com.bokoup.merchantapp.model

data class BurnDelegatedMemo(val orderId: String, val orderTotal: Int, val discountValue: Int, val paymentId: String, val delegateSignature: String)
