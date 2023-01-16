package com.bokoup.customerapp.dom.repo

import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.lib.Resource
import com.dgsd.ksol.core.model.TransactionSignature
import kotlinx.coroutines.flow.Flow

interface DataRepo {

    fun getTokensOwnedByAccountSubscription(
        address: Address
    ): Flow<Resource<List<TokenAccountListSubscription.TokenAccount>>>

    fun getTransactionsByAccount(
        address: Address
    ): Flow<Resource<List<BokoupTransaction>>>

    fun getTransaction(
        transactionSignature: TransactionSignature
    ): Flow<Resource<BokoupTransaction>>
}