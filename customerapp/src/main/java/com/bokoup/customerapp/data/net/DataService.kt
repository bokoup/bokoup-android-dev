package com.bokoup.customerapp.data.net

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.bokoup.customerapp.MintByIdQuery
import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.TransactionBySignatureQuery
import com.bokoup.customerapp.TransactionsByAccountQuery
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
    ): ApolloCall<TransactionsByAccountQuery.Data> {
        return apolloClient.query(TransactionsByAccountQuery(address.id))
    }

    fun getTransactionBySignature(
        signature: TransactionSignature,
    ): ApolloCall<TransactionBySignatureQuery.Data> {
        return apolloClient.query(TransactionBySignatureQuery(signature))
    }
}