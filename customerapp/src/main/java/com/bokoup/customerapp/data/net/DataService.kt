package com.bokoup.customerapp.data.net

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.bokoup.customerapp.*
import com.bokoup.customerapp.dom.model.Address
import com.dgsd.ksol.core.model.TransactionSignature

class DataService(
    private val apolloClient: ApolloClient
) {

    fun getTokenByMintId(
        mint: String
    ): ApolloCall<MintByIdQuery.Data> {
        return apolloClient.query(MintByIdQuery(mint))
    }

    fun getTokensOwnedByAccountSubscription(
        address: Address
    ): ApolloCall<TokenAccountListSubscription.Data> {
        return apolloClient.subscription(TokenAccountListSubscription(address.id))
    }

    fun getTransactionsByAccount(
        address: Address
    ): ApolloCall<TransactionsByAccountSubscription.Data> {
        return apolloClient.subscription(TransactionsByAccountSubscription(address.id))
    }

    fun getTradeListings(): ApolloCall<TradeListingsSubscription.Data> {
        return apolloClient.subscription(TradeListingsSubscription())
    }


    fun getTransactionBySignature(
        signature: TransactionSignature,
    ): ApolloCall<TransactionBySignatureSubscription.Data> {
        return apolloClient.subscription(TransactionBySignatureSubscription(signature))
    }
}