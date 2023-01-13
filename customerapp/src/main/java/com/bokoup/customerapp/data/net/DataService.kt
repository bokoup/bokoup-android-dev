package com.bokoup.customerapp.data.net

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.dom.model.Address

class DataService(
    private val apolloClient: ApolloClient
) {

    fun getTokensOwnedByAccountSubscription(
        address: Address
    ): ApolloCall<TokenAccountListSubscription.Data> {
        return apolloClient.subscription(TokenAccountListSubscription(address.id))
    }
}