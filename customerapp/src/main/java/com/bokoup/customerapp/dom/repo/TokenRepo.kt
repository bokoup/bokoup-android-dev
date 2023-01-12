package com.bokoup.customerapp.dom.repo

import com.bokoup.customerapp.dom.model.Token
import com.bokoup.lib.Resource
import com.dgsd.ksol.solpay.model.SolPayTransactionInfo
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails
import kotlinx.coroutines.flow.Flow

interface TokenRepo {
    fun getTokensFromRoom(): Flow<List<Token>>
    fun getApiId(
        url: String,
    ): Flow<Resource<SolPayTransactionRequestDetails>>

    fun getTokenTransaction(
        url: String,
        address: String
    ): Flow<Resource<SolPayTransactionInfo>>

}