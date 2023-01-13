package com.bokoup.customerapp.dom.repo

import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.lib.Resource
import kotlinx.coroutines.flow.Flow

interface DataRepo {

    fun getTokensOwnedByAccountSubscription(
        address: Address
    ): Flow<Resource<List<TokenAccountListSubscription.TokenAccount>>>
}