package com.bokoup.merchantapp.model

import com.bokoup.merchantapp.TokenAccountListQuery

data class TokenAccountWithMetadata(
    val tokenAccount: TokenAccountListQuery.TokenAccount,
    val name: String,
    val campaign: String,
    val image: String,
    val description: String,
    val attributes: Map<String, String>,
    var orderTotal: Long = 0,
    var discountValue: Long = 0,
    var selected: Boolean = false
)
