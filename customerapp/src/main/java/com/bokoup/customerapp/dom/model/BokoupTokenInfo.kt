package com.bokoup.customerapp.dom.model

import com.dgsd.ksol.core.model.PublicKey

data class BokoupTokenInfo(
    val address: PublicKey,
    val name: String,
    val imageUrl: String,
    val description: String?,
    val symbol: String
)