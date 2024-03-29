package com.bokoup.customerapp.dom.repo

import com.bokoup.customerapp.MintByIdQuery
import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.TradeListingsSubscription
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.model.BokoupTradeListing
import com.bokoup.customerapp.dom.model.BokoupTransaction
import com.bokoup.lib.Resource
import com.dgsd.ksol.core.model.PublicKey
import com.dgsd.ksol.core.model.TransactionSignature
import kotlinx.coroutines.flow.Flow

interface DataRepo {

    fun getMintTokenInfo(
        mint: PublicKey
    ): Flow<Resource<MintByIdQuery.Mint>>

    fun getTokensOwnedByAccountSubscription(
        address: Address
    ): Flow<Resource<List<TokenAccountListSubscription.TokenAccount>>>

    fun getTransactionsByAccount(
        address: Address
    ): Flow<Resource<List<BokoupTransaction>>>

    fun getTradeListings(): Flow<Resource<List<BokoupTradeListing>>>

    fun getTransaction(
        transactionSignature: TransactionSignature
    ): Flow<Resource<BokoupTransaction>>

    fun getTransactionByAddressAndToken(
        address: Address,
        token: PublicKey,
    ): Flow<Resource<List<BokoupTransaction>>>
}