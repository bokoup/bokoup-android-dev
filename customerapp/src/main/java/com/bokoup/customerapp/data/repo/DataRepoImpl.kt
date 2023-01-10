package com.bokoup.customerapp.data.repo

import com.bokoup.customerapp.data.net.DataService
import com.bokoup.customerapp.dom.repo.DataRepo

class DataRepoImpl(private val dataService: DataService): DataRepo {
    override val tokenAccountSubscriptionFlow = dataService.tokenAccountSubscription.toFlow()
}