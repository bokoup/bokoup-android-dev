package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.TokenAccountListSubscription
import com.bokoup.customerapp.data.net.DataService
import com.bokoup.customerapp.dom.model.Address
import com.bokoup.customerapp.dom.repo.DataRepo
import com.bokoup.lib.Resource
import com.bokoup.lib.asResourceFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepoImpl(private val dataService: DataService) : DataRepo {

    override fun getTokensOwnedByAccountSubscription(
        address: Address
    ): Flow<Resource<List<TokenAccountListSubscription.TokenAccount>>> {
        return dataService
            .getTokensOwnedByAccountSubscription(address)
            .toFlow()
            .map { it.dataAssertNoErrors.tokenAccount }
            .asResourceFlow()
    }
}